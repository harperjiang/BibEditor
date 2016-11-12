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

import java.io.File;
import java.io.PrintWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.editors.text.TextEditor;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.compile.LaTeXCompiler;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.model.LaTeXModel;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.model.LaTeXModel.Token;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.LaTeXDocumentProvider;
import edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants;

/**
 * 
 * @author Hao Jiang
 *
 */
public class LaTeXEditor extends TextEditor {

	private static final String CONSOLE_NAME = "TeX Dojo";

	private static final String DEFAULT_LATEX_EXE = "pdflatex";

	private static final String DEFAULT_BIB_EXE = "bibtex";

	private LaTeXModel model = new LaTeXModel();

	public LaTeXEditor() {
		super();
		setSourceViewerConfiguration(new LaTeXConfiguration());
		setDocumentProvider(new LaTeXDocumentProvider());
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		getDocumentProvider().getDocument(getEditorInput()).addDocumentListener(new ParserListener());
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);
		// Invoke pdflatex to work on current latex document
		compile();
	}

	protected void compile() {
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		String executable = prefs.get(PreferenceConstants.P_LATEX_EXE, DEFAULT_LATEX_EXE);
		String bibexe = prefs.get(PreferenceConstants.P_BIBTEX_EXE, DEFAULT_BIB_EXE);

		File inputFile = ((IPathEditorInput) getEditorInput()).getPath().toFile();

		// TODO Detect whether the file contains an bib command
		LaTeXCompiler.compile(executable, bibexe, inputFile, getConsole(), true);
	}

	protected static MessageConsole getConsole() {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (CONSOLE_NAME.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(CONSOLE_NAME, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	private class ParserListener implements IDocumentListener {
		@Override
		public void documentChanged(DocumentEvent event) {
			// When doing insert, the replace text length is 0,
			try {
				if (0 == event.getLength()) {
					// Insert
					String contentType = event.getDocument().getContentType(event.getOffset());
					Token token = getToken(event.getDocument(), contentType, event.getOffset());
					model.add(token);
				} else {
					// Remove something
					model.remove(event.getOffset(), event.getLength());
				}
			} catch (BadLocationException e) {
				e.printStackTrace(new PrintWriter(getConsole().newOutputStream()));
			}
		}

		@Override
		public void documentAboutToBeChanged(DocumentEvent event) {

		}

		private Token getToken(IDocument doc, String type, int offset) throws BadLocationException {
			int start = offset;
			int end = offset;
			while (start >= 0 && type.equals(doc.getContentType(start))) {
				start--;
			}
			while (end < doc.getLength() && type.equals(doc.getContentType(end))) {
				end++;
			}
			return new Token(doc.get(start + 1, end - start - 1), start + 1);
		}
	}
}
