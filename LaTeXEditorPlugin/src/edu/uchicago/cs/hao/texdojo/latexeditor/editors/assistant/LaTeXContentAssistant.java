package edu.uchicago.cs.hao.texdojo.latexeditor.editors.assistant;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.PartitionScanner;

public class LaTeXContentAssistant extends ContentAssistant {

	public LaTeXContentAssistant() {
		super();

		enableAutoActivation(true);
		setAutoActivationDelay(500);

		// Configure content assistant
		setContentAssistProcessor(new CommandProcessor(), PartitionScanner.LATEX_COMMAND);
		setContentAssistProcessor(new CommandProcessor(), PartitionScanner.LATEX_ARG);
		setContentAssistProcessor(new CommandProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
	}

}
