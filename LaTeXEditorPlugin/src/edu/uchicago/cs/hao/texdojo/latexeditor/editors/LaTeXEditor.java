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

import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_BIBTEX_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COMPILE_DOC;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_LATEX_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_MAIN_TEX;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_TEMP_FILE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_BIB_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_COMPILE_DOCUMENT;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_LATEX_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_MAIN_TEX;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_TEMP_FILE;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.editors.text.TextEditor;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;
import edu.uchicago.cs.hao.texdojo.latexeditor.compile.LaTeXCompiler;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.LaTeXDocumentProvider;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.PartitionScanner;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXConstant;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXModel;

/**
 * 
 * @author Hao Jiang
 *
 */
public class LaTeXEditor extends TextEditor {

	private static final String CONSOLE_NAME = "TeXDojo";

	private LaTeXModel model = new LaTeXModel();

	public LaTeXEditor() {
		super();
		setSourceViewerConfiguration(new LaTeXConfiguration());
		setDocumentProvider(new LaTeXDocumentProvider());
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);

		IDocument doc = getDocumentProvider().getDocument(getEditorInput());

		// Init the model with document and start listening its change
		try {
			model.init(doc);
			doc.addDocumentListener(new ParserListener());
		} catch (BadLocationException e) {
			e.printStackTrace(new PrintStream(getConsole().newOutputStream()));
		}
	}

	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);
		
		// Invoke pdflatex to work on current latex document	
		compile();
	}

	/**
	 * Rescan the document and apply new color/font
	 */
	public void refresh() {
		// Create a new config to reload colors/fonts
		SourceViewerConfiguration config = new LaTeXConfiguration();
		setSourceViewerConfiguration(config);
		((SourceViewer) getSourceViewer()).configure(config);
		getSourceViewer().invalidateTextPresentation();
	}

	protected void compile() {
		// Check to see whether to compile current file
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		File inputFile = ((IPathEditorInput) getEditorInput()).getPath().toFile();

		String tempFiles = prefs.get(P_TEMP_FILE, DEFAULT_TEMP_FILE);

		boolean compileDoc = prefs.getBoolean(P_COMPILE_DOC, DEFAULT_COMPILE_DOCUMENT);
		String mainTex = prefs.get(P_MAIN_TEX, DEFAULT_MAIN_TEX);

		if (compileDoc) {
			if (!model.has("document"))
				return;
		} else if (!mainTex.equals(inputFile.getName())) {
			return;
		}

		String executable = prefs.get(P_LATEX_EXE, DEFAULT_LATEX_EXE);
		String bibexe = prefs.get(P_BIBTEX_EXE, DEFAULT_BIB_EXE);

		// Detect whether the file contains an bib command
		LaTeXCompiler.compile(executable, bibexe, inputFile, getConsole(), model.has(LaTeXConstant.EXTERNAL_BIB));

		// Remove temporary files
		removeTempFiles(inputFile.getParentFile(), tempFiles);
	}

	private void removeTempFiles(final File dir, String tempFiles) {
		final String[] tfs = tempFiles.split(",");
		File[] candidates = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File d, String name) {
				if (!dir.equals(d))
					return false;
				for (String tf : tfs) {
					if (name.matches(tf.replace(".", "\\.").replace("*", ".*")))
						return true;
				}
				return false;
			}
		});
		for (File can : candidates) {
			can.delete();
		}
	}

	protected static IOConsole getConsole() {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();

		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (CONSOLE_NAME.equals(existing[i].getName()))
				return (IOConsole) existing[i];
		// no console found, so create a new one
		IOConsole myConsole = new IOConsole(CONSOLE_NAME, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	private class ParserListener implements IDocumentListener {

		private int offset;

		@Override
		public void documentChanged(DocumentEvent event) {
			try {
				// Rescan from the beginning of region that may be impacted
				IDocument doc = event.getDocument();
				ITypedRegion[] tokens = doc.getDocumentPartitioner().computePartitioning(offset,
						doc.getLength() - offset);
				model.update(doc, tokens);
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

					offset = headRegion.getOffset();
					// Clear affected command

				} else {
					ITypedRegion in = doc.getPartition(event.getOffset());
					if (in.getOffset() == event.getOffset()) {
						// At the beginning of partition, check previous
						if (event.getOffset() != 0) {
							ITypedRegion prev = doc.getPartition(event.getOffset() - 1);
							if (!PartitionScanner.isArgOption(prev.getType())) {
								// This is an append to prev partition
								offset = prev.getOffset();
							} else { // This is an insert to next partition
								offset = in.getOffset();
							}
						} else {
							offset = in.getOffset();
						}
					} else {
						offset = in.getOffset();
					}
				}
				model.clear(offset, doc.getLength() - offset);
			} catch (BadLocationException e) {
				e.printStackTrace(new PrintWriter(getConsole().newOutputStream()));
			}
		}
	}
}
