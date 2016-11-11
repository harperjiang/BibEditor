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

package edu.uchicago.cs.hao.texdojo.latexeditor.editors.text;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

/**
 * 
 * @author Hao Jiang
 *
 */
public class LaTeXConfiguration extends SourceViewerConfiguration {

	private DoubleClickStrategy doubleClickStrategy = new DoubleClickStrategy();

	private IAutoEditStrategy[] editStrategy = new IAutoEditStrategy[] { new AutoCompleteStrategy() };

	public LaTeXConfiguration() {

	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, PartitionScanner.LATEX_COMMAND,
				PartitionScanner.LATEX_ARG };
	}

	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		return doubleClickStrategy;
	}

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		return editStrategy;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(new CommandScanner());
		reconciler.setDamager(dr, PartitionScanner.LATEX_COMMAND);
		reconciler.setRepairer(dr, PartitionScanner.LATEX_COMMAND);

		dr = new DefaultDamagerRepairer(new CommandArgScanner());
		reconciler.setDamager(dr, PartitionScanner.LATEX_ARG);
		reconciler.setRepairer(dr, PartitionScanner.LATEX_ARG);

		dr = new DefaultDamagerRepairer(new TextScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		// NonRuleBasedDamagerRepairer ndr = new NonRuleBasedDamagerRepairer(
		// new
		// TextAttribute(colorManager.getColor(ColorConstants.XML_COMMENT)));
		// reconciler.setDamager(ndr, PartitionScanner.XML_COMMENT);
		// reconciler.setRepairer(ndr, PartitionScanner.XML_COMMENT);

		return reconciler;
	}

}