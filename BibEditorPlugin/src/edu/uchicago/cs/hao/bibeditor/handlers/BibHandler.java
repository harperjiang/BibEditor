package edu.uchicago.cs.hao.bibeditor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uchicago.cs.hao.bibeditor.editors.BibEditor;

public abstract class BibHandler extends AbstractHandler {

	protected abstract Object executeWithEditor(ExecutionEvent event, BibEditor editor) throws ExecutionException;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		IEditorPart editor = window.getActivePage().getActiveEditor();

		if (editor instanceof BibEditor) {
			return executeWithEditor(event, (BibEditor) editor);
		} else {
			MessageDialog.openWarning(window.getShell(), "No BibTex Editor Found", "Please open a BibTex file");
			return null;
		}
	}

}
