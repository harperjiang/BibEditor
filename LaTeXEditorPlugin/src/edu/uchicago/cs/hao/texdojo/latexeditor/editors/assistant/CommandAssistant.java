/*******************************************************************************
 * Copyright (c) 2016 Hao Jiang.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hao Jiang - initial API and implementation and/or initial documentation
 *******************************************************************************/

package edu.uchicago.cs.hao.texdojo.latexeditor.editors.assistant;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Point;

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.assistant.model.AssistEntry;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.assistant.model.AssistIndex;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.assistant.model.BibCache;
import edu.uchicago.cs.hao.texdojo.latexeditor.inmem.DependencyMap;
import edu.uchicago.cs.hao.texdojo.latexeditor.util.PlatformUtil;

public class CommandAssistant implements IContentAssistProcessor {

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		try {
			IDocument doc = viewer.getDocument();

			Point selectedRange = viewer.getSelectedRange();

			if (selectedRange.y > 0) {
				// Have selection, current implementation try to generate table
				// or
				// itemize

				String selected = doc.get(selectedRange.x, selectedRange.y);
				ICompletionProposal[] proposals = findSelectionProposals(selected, selectedRange.x, selectedRange.y);
				return proposals;

			} else {
				// Retrieve qualifier
				String qualifier = getQualifier(doc, offset);

				// proposal for empty string
				if (qualifier == null || qualifier.length() == 0)
					return noInputProposals(offset);
				if (qualifier.startsWith("\\begin{"))
					return envProposals(qualifier.substring(7), offset);
				if (qualifier.startsWith("\\cite{")) {
					return citeProposals(qualifier.substring(6), offset);
				}
				return findProposals(qualifier, offset);
			}
		} catch (BadLocationException e) {
			// Do nothing
			return new ICompletionProposal[0];
		}
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '\\' };
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	private String getQualifier(IDocument doc, int documentOffset) {
		// Use string buffer to collect characters
		StringBuffer buf = new StringBuffer();
		while (true) {
			try {
				// Read character backwards
				char c = doc.getChar(--documentOffset);

				// Start of tag. Return qualifier
				if (c == '\\') {
					buf.append('\\');
					return buf.reverse().toString();
				}
				// No cross line
				if (c == '\r' || c == '\n')
					return "";
				// Collect character
				buf.append(c);
			} catch (BadLocationException e) {
				// Document start reached, no tag found
				return "";
			}
		}
	}

	BibCache cache = new BibCache();

	/**
	 * Return available cite names as proposal
	 * 
	 * @param offset
	 * @return
	 */
	private ICompletionProposal[] citeProposals(String qualifier, int offset) {
		IPath file = PlatformUtil.getOpenFile();
		IProject project = PlatformUtil.getOpenProject();
		DependencyMap map = DependencyMap.load(project);
		// Get bib file
		String relFile = file.makeRelativeTo(project.getLocation()).removeFileExtension().toOSString();
		String bib = map.ref(relFile);
		IFile bibfile = project.getFile(bib + ".bib");
		if (!bibfile.exists()) {
			return new ICompletionProposal[0];
		}
		// Collect all bib entries
		AssistIndex model = cache.get(bibfile.getLocation().toFile());

		int qlen = qualifier.length();
		List<AssistEntry> candidates = model.find(qualifier);

		ICompletionProposal[] proposals = new ICompletionProposal[candidates.size()];

		for (int i = 0; i < candidates.size(); i++) {
			AssistEntry entry = candidates.get(i);
			proposals[i] = new CompletionProposal(entry.getKey() + "}", offset - qlen, qlen,
					entry.getKey().length() + 1, null, entry.toString(), null, null);
		}
		return proposals;
	}

	/**
	 * Return available environment names as proposal
	 * 
	 * @param offset
	 * @return
	 */
	private ICompletionProposal[] envProposals(String qualifier, int offset) {
		int qlen = qualifier.length();
		List<AssistEntry> candidates = envs.find(qualifier);

		ICompletionProposal[] proposals = new ICompletionProposal[candidates.size()];

		for (int i = 0; i < candidates.size(); i++) {
			AssistEntry env = candidates.get(i);
			String key = env.getKey();
			String text = MessageFormat.format("{0}'}'\n\n\\end'{'{0}'}'\n", key);
			proposals[i] = new CompletionProposal(text, offset - qlen, qlen, key.length() + 2, null, key, null, null);
		}
		return proposals;
	}

	/**
	 * Find Proposal for a non-empty qualifier
	 * 
	 * @param qualifier
	 * @param documentOffset
	 * @return
	 */
	private ICompletionProposal[] findProposals(String qualifier, int documentOffset) {
		boolean includeBs = true;
		if (qualifier.startsWith("\\")) {
			includeBs = false;
			qualifier = qualifier.substring(1);
		}
		String lqualifier = qualifier.toLowerCase();
		int qlen = lqualifier.length();

		List<AssistEntry> entries = commands.find(lqualifier);
		ICompletionProposal[] props = new ICompletionProposal[entries.size()];
		for (int i = 0; i < entries.size(); ++i) {
			props[i] = fromCommand(entries.get(i), includeBs, documentOffset, qlen);
		}
		return props;
	}

	protected ICompletionProposal fromCommand(AssistEntry cmd, boolean includeBs, int documentOffset, int qlen) {
		String text = null;
		String display = null;

		if (includeBs) {
			text = "\\" + cmd.getKey();
			display = MessageFormat.format("\\{0} {1}", cmd.getKey(), cmd.getDescription());
		} else {
			text = cmd.getKey();
			display = MessageFormat.format("{0} {1}", cmd.getKey(), cmd.getDescription());
		}
		// Construct proposal
		CompletionProposal proposal = new CompletionProposal(text, documentOffset - qlen, qlen, text.length(), null,
				display, null, null);
		return proposal;
	}

	/**
	 * Find a proposal when nothing is provided
	 * 
	 * @param offset
	 * @return
	 */
	private ICompletionProposal[] noInputProposals(int offset) {
		return new ICompletionProposal[0];
	}

	/**
	 * Find proposals when text is selected
	 * 
	 * @param selection
	 * @param offset
	 * @param length
	 * @return
	 */
	private ICompletionProposal[] findSelectionProposals(String selection, int offset, int length) {
		String[] lines = selection.split("[\\r\\n]+");

		StringBuilder listBuilder = new StringBuilder();
		StringBuilder tableBuilder = new StringBuilder();
		int maxColumn = 0;
		for (String line : lines) {
			listBuilder.append(MessageFormat.format("\t\\item {0}\n", line));
			String[] cells = line.split("\\s+");
			maxColumn = Math.max(maxColumn, cells.length);
			for (String cell : cells) {
				tableBuilder.append(cell).append(" & ");
			}
			tableBuilder.delete(tableBuilder.length() - 2, tableBuilder.length()).append("\\\\\n");
			tableBuilder.append("\\hline\n");
		}

		StringBuilder colDef = new StringBuilder();
		colDef.append("{|");
		for (int i = 0; i < maxColumn; i++) {
			colDef.append("c|");
		}
		colDef.append("}");

		ICompletionProposal[] results = new ICompletionProposal[4];

		// itemize
		String data = MessageFormat.format("\\begin'{'itemize'}'\n{0}\\end'{'itemize'}'", listBuilder.toString());
		results[0] = new CompletionProposal(data, offset, length, data.length(), null,
				"\\begin{itemize}...\\end{itemize}", null, null);

		// enumerate
		data = MessageFormat.format("\\begin'{'enumerate'}'\n{0}\\end'{'enumerate'}'\n", listBuilder.toString());

		results[1] = new CompletionProposal(data, offset, length, data.length(), null,
				"\\begin{enumerate}...\\end{enumerate}", null, null);

		// description
		data = MessageFormat.format("\\begin'{'description'}'\n{0}\\end'{'description'}'", listBuilder.toString());
		results[2] = new CompletionProposal(data, offset, length, data.length(), null,
				"\\begin{description}...\\end{description}", null, null);

		// tabular
		data = MessageFormat.format("\\begin'{'tabular'}'{0}\n{1}\\end'{'tabular'}'", colDef.toString(),
				tableBuilder.toString());
		results[3] = new CompletionProposal(data, offset, length, data.length(), null,
				"\\begin{tabular}...\\end{tabular}", null, null);

		return results;
	}

	static final String[] dicts = { "dicts/dict_math", "dicts/dict_common" };

	static final String env_dict = "dicts/dict_env";

	AssistIndex commands = new AssistIndex();
	AssistIndex envs = new AssistIndex();

	public CommandAssistant() {
		loadCommandDicts();
		loadEnvDict();
	}

	private void loadCommandDicts() {
		for (String dict : dicts) {
			try {
				commands.loadFromFile(dict, (line) -> {
					String[] parts = line.split("\\s+");
					if (parts.length >= 2)
						return new AssistEntry(parts[0], parts[1]);
					else if (parts.length == 1) {
						return new AssistEntry(parts[0], "");
					}
					return null;
				});
			} catch (Exception e) {
				// Eat exception, just no completion
				e.printStackTrace();
			}
		}
	}

	private void loadEnvDict() {
		try {
			envs.loadFromFile(env_dict, (line) -> {
				return new AssistEntry(line.trim(), "");
			});

		} catch (Exception e) {
			// Eat exception, just no completion
			e.printStackTrace();
		}
	}

}
