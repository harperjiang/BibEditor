package edu.uchicago.cs.hao.texdojo.latexeditor.editors.outline;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.LaTeXEditor;

public class ViewFiguresHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		IEditorPart editor = window.getActivePage().getActiveEditor();
		if (editor instanceof LaTeXEditor) {
			LaTeXEditor latexEditor = (LaTeXEditor) editor;
			latexEditor.getOutlinePage().switchMode();
		}

		return null;
	}

}
