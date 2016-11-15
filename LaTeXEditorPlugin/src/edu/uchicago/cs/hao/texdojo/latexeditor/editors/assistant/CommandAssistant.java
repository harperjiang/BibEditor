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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Point;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
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

	/**
	 * Return available environment names as proposal
	 * 
	 * @param offset
	 * @return
	 */
	private ICompletionProposal[] envProposals(String qualifier, int offset) {
		int qlen = qualifier.length();
		List<String> candidates = new ArrayList<String>();
		for (String env : envs) {
			if (env.startsWith(qualifier))
				candidates.add(env);
		}

		ICompletionProposal[] proposals = new ICompletionProposal[candidates.size()];

		for (int i = 0; i < candidates.size(); i++) {
			String env = candidates.get(i);
			String text = MessageFormat.format("{0}'}'\n\n\\end'{'{0}'}'\n", env);
			proposals[i] = new CompletionProposal(text, offset - qlen, text.length(), env.length() + 2, null, env, null,
					null);
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

		List<ICompletionProposal> props = new ArrayList<ICompletionProposal>();
		int qlen = qualifier.length();

		// Search through all proposals
		int middleIndex = Collections.binarySearch(commands, new Command(qualifier, null), new Comparator<Command>() {
			@Override
			public int compare(Command o1, Command o2) {
				if (o1.key.startsWith(o2.key) || o2.key.startsWith(o1.key))
					return 0;
				return o1.key.compareTo(o2.key);
			}
		});
		if (middleIndex == -1) {
			return new ICompletionProposal[0];
		}
		// Rewind to the first
		int start = middleIndex;
		while (start >= 0) {
			Command cmd = commands.get(start);
			if (!cmd.key.startsWith(qualifier))
				break;
			else
				start--;
		}
		// Forward to the last
		int stop = middleIndex;
		while (stop < commands.size()) {
			Command cmd = commands.get(stop);
			if (!cmd.key.startsWith(qualifier))
				break;
			else
				stop++;
		}

		for (int i = start + 1; i < stop; i++) {
			Command cmd = commands.get(i);
			String text = null;
			String display = null;

			if (includeBs) {
				text = "\\" + cmd.key;
				display = MessageFormat.format("\\{0} {1}", cmd.key, cmd.display);
			} else {
				text = cmd.key;
				display = MessageFormat.format("{0} {1}", cmd.key, cmd.display);
			}
			// Construct proposal
			CompletionProposal proposal = new CompletionProposal(text, documentOffset - qlen, qlen, text.length(), null,
					display, null, null);

			// and add to result list
			props.add(proposal);

		}
		ICompletionProposal[] proparray = new ICompletionProposal[props.size()];
		props.toArray(proparray);
		return proparray;
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

	private static class Command {
		String key;
		String display;

		Command(String key, String display) {
			this.key = key;
			this.display = display;
		}

	}

	static final String[] dicts = { "dicts/dict_math", "dicts/dict_common" };

	static final String env_dict = "dicts/dict_env";

	List<Command> commands = new ArrayList<Command>();

	List<String> envs = new ArrayList<String>();

	public CommandAssistant() {
		loadCommandDicts();
		loadEnvDict();
	}

	private void loadCommandDicts() {
		BufferedReader br = null;
		for (String dict : dicts) {
			try {
				br = new BufferedReader(new InputStreamReader(
						Thread.currentThread().getContextClassLoader().getResourceAsStream(dict), "UTF-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					if (line.trim().length() != 0) {
						if (!line.startsWith("#")) {
							String[] parts = line.split("\\s+");
							if (parts.length >= 2)
								commands.add(new Command(parts[0], parts[1]));
							else if (parts.length == 1) {
								commands.add(new Command(parts[0], ""));
							}
						}
					}
				}
			} catch (Exception e) {
				// Eat exception, just no completion
			} finally {
				try {
					if (null != br)
						br.close();
				} catch (IOException e) {

				}
			}
		}
		commands.sort(new Comparator<Command>() {
			@Override
			public int compare(Command o1, Command o2) {
				return o1.key.compareTo(o2.key);
			}
		});
	}

	private void loadEnvDict() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(
					Thread.currentThread().getContextClassLoader().getResourceAsStream(env_dict), "UTF-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.trim().length() != 0) {
					if (!line.startsWith("#")) {
						envs.add(line.trim());
					}
				}
			}
		} catch (Exception e) {
			// Eat exception, just no completion
		} finally {
			try {
				if (null != br)
					br.close();
			} catch (IOException e) {

			}
		}
		envs.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
	}
}
