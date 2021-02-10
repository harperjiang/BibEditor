package hao.texdojo.latexeditor.editors.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class TriggerCommentHandler extends TextHandler {

	@Override
	protected Object executeWithEditor(ExecutionEvent event) throws ExecutionException {
		IDocumentProvider dp = editor.getDocumentProvider();
		IDocument doc = dp.getDocument(editor.getEditorInput());
		if (doc != null) {
			ISelection selection = editor.getSelectionProvider().getSelection();
			if (!selection.isEmpty()) {
				ITextSelection ts = (ITextSelection) selection;
				try {
					for (int i = ts.getStartLine(); i <= ts.getEndLine(); i++) {
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
			} else {

			}
		}

		return null;
	}

	protected void addComment(IDocument doc, ITextSelection ts) throws BadLocationException {
		for (int i = ts.getStartLine(); i <= ts.getEndLine(); i++) {
			IRegion line = doc.getLineInformation(i);
			doc.replace(line.getOffset(), 0, "%");
		}
		// Preserve selection
		ITextSelection newsel = build(ts, true);
		editor.getSelectionProvider().setSelection(newsel);

	}

	protected void removeComment(IDocument doc, ITextSelection ts) throws BadLocationException {
		for (int i = ts.getStartLine(); i <= ts.getEndLine(); i++) {
			IRegion line = doc.getLineInformation(i);
			doc.replace(line.getOffset(), 1, "");
		}
		// Preserve selection
		ITextSelection newsel = build(ts, false);
		editor.getSelectionProvider().setSelection(newsel);
	}

	protected ITextSelection build(ITextSelection existing, boolean inc) {
		// Select from line to line
		int linediff = existing.getEndLine() - existing.getStartLine();
		int direction = (inc ? 1 : -1);
		return new TextSelection(existing.getOffset() + direction, existing.getLength() + linediff * direction);
	}
}
