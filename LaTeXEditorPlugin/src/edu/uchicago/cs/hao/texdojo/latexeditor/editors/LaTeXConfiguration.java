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

package edu.uchicago.cs.hao.texdojo.latexeditor.editors;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.assistant.BeginEndStrategy;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.assistant.LaTeXContentAssistant;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.assistant.LineAlignStrategy;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.DoubleClickStrategy;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.LaTeXScanner;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.PartitionScanner;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.PartitionDamagerRepairer;
/**
 * 
 * @author Hao Jiang
 *
 */
public class LaTeXConfiguration extends SourceViewerConfiguration {

	private DoubleClickStrategy doubleClickStrategy = new DoubleClickStrategy();

	private LaTeXContentAssistant contentAssistant = new LaTeXContentAssistant();

	private IAutoEditStrategy[] strategies = new IAutoEditStrategy[] { new BeginEndStrategy(),
			new LineAlignStrategy() };

	public LaTeXConfiguration() {
		super();
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return PartitionScanner.VALID_TYPE;
	}

	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		return doubleClickStrategy;
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		return contentAssistant;
	}

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		return strategies;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new LaTeXScanner());
		reconciler.setDamager(dr, PartitionScanner.LATEX_COMMAND);
		reconciler.setRepairer(dr, PartitionScanner.LATEX_COMMAND);

		dr = new PartitionDamagerRepairer(new LaTeXScanner());
		reconciler.setDamager(dr, PartitionScanner.LATEX_ARG);
		reconciler.setRepairer(dr, PartitionScanner.LATEX_ARG);

		dr = new PartitionDamagerRepairer(new LaTeXScanner());
		reconciler.setDamager(dr, PartitionScanner.LATEX_OPTION);
		reconciler.setRepairer(dr, PartitionScanner.LATEX_OPTION);

		dr = new DefaultDamagerRepairer(new LaTeXScanner());
		reconciler.setDamager(dr, PartitionScanner.LATEX_COMMENT);
		reconciler.setRepairer(dr, PartitionScanner.LATEX_COMMENT);

		dr = new DefaultDamagerRepairer(new LaTeXScanner());
		reconciler.setDamager(dr, PartitionScanner.LATEX_MATHMODE);
		reconciler.setRepairer(dr, PartitionScanner.LATEX_MATHMODE);

		dr = new DefaultDamagerRepairer(new LaTeXScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}

}