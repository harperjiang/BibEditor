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

import org.eclipse.ui.editors.text.TextEditor;

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.LaTeXConfiguration;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.LaTeXDocumentProvider;

/**
 * 
 * @author Hao Jiang
 *
 */
public class LaTeXEditor extends TextEditor {

	public LaTeXEditor() {
		super();
		setSourceViewerConfiguration(new LaTeXConfiguration());
		setDocumentProvider(new LaTeXDocumentProvider());
	}

	@Override
	public void dispose() {
		super.dispose();
	}

}
