/*******************************************************************************
 * Copyright (c) Oct 2016 Hao Jiang.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hao Jiang - initial API and implementation and/or initial documentation
 *******************************************************************************/

package edu.uchicago.cs.hao.texdojo.bibeditor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uchicago.cs.hao.texdojo.bibeditor.editors.BibEditor;

public abstract class BibHandler extends AbstractHandler {

	protected abstract Object executeWithEditor(ExecutionEvent event, BibEditor editor) throws ExecutionException;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		IEditorPart editor = window.getActivePage().getActiveEditor();

		if (editor instanceof BibEditor) {
			return executeWithEditor(event, (BibEditor) editor);
		} else {
			// MessageDialog.openWarning(window.getShell(), "No BibTex Editor
			// Found", "Please open a BibTex file");
			return null;
		}
	}

}
