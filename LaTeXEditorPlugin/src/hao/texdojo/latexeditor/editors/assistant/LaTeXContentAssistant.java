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

package hao.texdojo.latexeditor.editors.assistant;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;

import hao.texdojo.latexeditor.editors.text.PartitionScanner;

public class LaTeXContentAssistant extends ContentAssistant {

	public LaTeXContentAssistant() {
		super();

		enableAutoActivation(true);
		setAutoActivationDelay(500);

		// Configure content assistant
		setContentAssistProcessor(new CommandAssistant(), PartitionScanner.LATEX_COMMAND);
		setContentAssistProcessor(new CommandAssistant(), PartitionScanner.LATEX_ARG);
		setContentAssistProcessor(new CommandAssistant(), IDocument.DEFAULT_CONTENT_TYPE);
	}

}
