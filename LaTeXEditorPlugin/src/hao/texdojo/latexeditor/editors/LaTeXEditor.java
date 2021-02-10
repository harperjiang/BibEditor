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

package hao.texdojo.latexeditor.editors;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import hao.texdojo.latexeditor.editors.model.LaTeXDocModel;
import hao.texdojo.latexeditor.editors.outline.ILaTeXTreeNode;
import hao.texdojo.latexeditor.editors.outline.LaTeXEditorOutlinePage;
import hao.texdojo.latexeditor.editors.text.LaTeXDocumentProvider;
import hao.texdojo.latexeditor.editors.text.PartitionScanner;
import hao.texdojo.latexeditor.model.LaTeXNode;

/**
 * 
 * @author Hao Jiang
 *
 */
public class LaTeXEditor extends TextEditor implements ISelectionChangedListener {

	private LaTeXDocModel model = new LaTeXDocModel();

	private LaTeXEditorOutlinePage outlinePage = new LaTeXEditorOutlinePage(model);

	public LaTeXEditor() {
		super();
		setSourceViewerConfiguration(new LaTeXConfiguration());
		setDocumentProvider(new LaTeXDocumentProvider());

		outlinePage.addSelectionChangedListener(this);
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

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (event.getSource() == outlinePage && event.getSelection() instanceof TreeSelection) {
			TreeSelection treesel = (TreeSelection) event.getSelection();
			ILaTeXTreeNode selectedNode = (ILaTeXTreeNode) treesel.getFirstElement();
			if (null != selectedNode) {
				LaTeXNode node = selectedNode.getNode();
				selectAndReveal(node.getOffset(), node.getLength());
			}
		}
		return;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter.equals(IContentOutlinePage.class)) {
			return (T) outlinePage;
		}
		return super.getAdapter(adapter);
	}

	@Override
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
		super.configureSourceViewerDecorationSupport(support);
	}

	@Override
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { Constants.CONTEXT_ID, "org.eclipse.ui.textEditorScope" });
	}

	@Override
	public void dispose() {
		super.dispose();
		outlinePage.dispose();
	}

	public LaTeXEditorOutlinePage getOutlinePage() {
		return outlinePage;
	}
	
	public ISourceViewer getInnerSourceViewer() {
		return super.getSourceViewer();
	}

	public static IOConsole getConsole() {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();

		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (Constants.CONSOLE_NAME.equals(existing[i].getName()))
				return (IOConsole) existing[i];
		// no console found, so create a new one
		IOConsole myConsole = new IOConsole(Constants.CONSOLE_NAME, null);
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
