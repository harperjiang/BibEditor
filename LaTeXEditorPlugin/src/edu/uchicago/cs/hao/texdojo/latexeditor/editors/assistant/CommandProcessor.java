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

public class CommandProcessor implements IContentAssistProcessor {

	List<Command> commands = new ArrayList<Command>();

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument doc = viewer.getDocument();
		// Retrieve qualifier
		String qualifier = getQualifier(doc, offset);
		ICompletionProposal[] proposals = findProposals(qualifier, offset);

		// Return the proposals
		return proposals;
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
				display = MessageFormat.format("\\{0}\t{1}", cmd.key, cmd.display);
			} else {
				text = cmd.key;
				display = MessageFormat.format("{0}\t{1}", cmd.key, cmd.display);
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

	private static class Command {
		String key;
		String display;

		Command(String key, String display) {
			this.key = key;
			this.display = display;
		}

	}

	public CommandProcessor() {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("edu/uchicago/cs/hao/texdojo/latexeditor/editors/assistant/dict")));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("#")) {
					String[] parts = line.split("\\s+");
					commands.add(new Command(parts[0], parts[1]));
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
		commands.sort(new Comparator<Command>() {
			@Override
			public int compare(Command o1, Command o2) {
				return o1.key.compareTo(o2.key);
			}
		});
	}

}
