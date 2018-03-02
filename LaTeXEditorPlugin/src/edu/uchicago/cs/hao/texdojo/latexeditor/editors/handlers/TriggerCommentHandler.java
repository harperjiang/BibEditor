package edu.uchicago.cs.hao.texdojo.latexeditor.editors.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.texteditor.IDocumentProvider;

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.LaTeXEditor;

public class TriggerCommentHandler extends TextHandler {

	@Override
	protected Object executeWithEditor(ExecutionEvent event, LaTeXEditor editor) throws ExecutionException {

		ISelection selection = editor.getSelectionProvider().getSelection();
		if (!selection.isEmpty()) {
			TextSelection ts = (TextSelection) selection;

			IDocumentProvider dp = editor.getDocumentProvider();
			IDocument doc = dp.getDocument(editor.getEditorInput());
			if (doc != null) {
				try {
					for (int i = ts.getStartLine(); i < ts.getEndLine(); i++) {
						IRegion line = doc.getLineInformation(i);
						String lineStart = doc.get(line.getOffset(), 1);
						if (!lineStart.equals("%")) {
							addComment(doc, ts);
							return null;
						}
					}
					removeComment(doc, ts);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	protected void addComment(IDocument doc, TextSelection ts) throws BadLocationException {
		for (int i = ts.getStartLine(); i < ts.getEndLine(); i++) {
			IRegion line = doc.getLineInformation(i);
			doc.replace(line.getOffset(), 0, "%");
		}
	}

	protected void removeComment(IDocument doc, TextSelection ts) throws BadLocationException {
		for (int i = ts.getStartLine(); i < ts.getEndLine(); i++) {
			IRegion line = doc.getLineInformation(i);
			doc.replace(line.getOffset(), 1, "");
		}
	}
}
