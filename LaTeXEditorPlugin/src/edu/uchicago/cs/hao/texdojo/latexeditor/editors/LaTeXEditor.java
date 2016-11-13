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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.editors.text.TextEditor;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.compile.LaTeXCompiler;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.model.LaTeXConstant;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.model.LaTeXModel;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.LaTeXDocumentProvider;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.PartitionScanner;
import edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants;

/**
 * 
 * @author Hao Jiang
 *
 */
public class LaTeXEditor extends TextEditor {

	private static final String CONSOLE_NAME = "TeXDojo";

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

		// Detect whether the file contains an bib command
		LaTeXCompiler.compile(executable, bibexe, inputFile, getConsole(), model.has(LaTeXConstant.EXTERNAL_BIB));
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

		private IRegion scanRegion;

		@Override
		public void documentChanged(DocumentEvent event) {
			try {
				// Rescan from the beginning of region that may be impacted
				IDocument doc = event.getDocument();
				ITypedRegion[] tokens = doc.getDocumentPartitioner().computePartitioning(scanRegion.getOffset(),
						scanRegion.getLength());
				for (ITypedRegion token : tokens) {
					if (PartitionScanner.LATEX_COMMAND.equals(token.getType())
							|| PartitionScanner.LATEX_ARG.equals(token.getType())) {
						model.add(doc.get(token.getOffset(), token.getLength()), token.getOffset());
					}
				}
			} catch (BadLocationException e) {
				e.printStackTrace(new PrintWriter(getConsole().newOutputStream()));
			}
		}

		@Override
		public void documentAboutToBeChanged(DocumentEvent event) {
			try {
				IDocument doc = event.getDocument();
				if (event.getLength() != 0) {
					// Replace text, determine affected region

					ITypedRegion headRegion = doc.getPartition(event.getOffset());
					ITypedRegion tailRegion = doc.getPartition(event.getOffset() + event.getLength());

					scanRegion = new Region(headRegion.getOffset(), tailRegion.getOffset() + tailRegion.getLength()
							- headRegion.getOffset() - event.getLength() + event.getText().length());

					// Clear affected token
					model.remove(headRegion.getOffset(),
							tailRegion.getOffset() + tailRegion.getLength() - headRegion.getOffset());
				} else {
					scanRegion = doc.getPartition(event.getOffset());
				}
			} catch (BadLocationException e) {
				e.printStackTrace(new PrintWriter(getConsole().newOutputStream()));
			}
		}
	}
}
