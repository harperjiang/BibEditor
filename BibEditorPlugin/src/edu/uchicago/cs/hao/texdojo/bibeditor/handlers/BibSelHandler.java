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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uchicago.cs.hao.texdojo.bibeditor.editors.BibEditor;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibEntry;

public abstract class BibSelHandler extends BibHandler {

	protected abstract Object executeWithSelection(ExecutionEvent event, BibEditor editor, BibEntry[] selection)
			throws ExecutionException;

	@Override
	public Object executeWithEditor(ExecutionEvent event, BibEditor editor) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		BibEntry[] selected = editor.getUi().allSelected();
		if (null != selected) {
			return executeWithSelection(event, editor, selected);
		} else {
			MessageDialog.openWarning(window.getShell(), "No entry selected", "Please select an entry to operate on");
			return null;
		}
	}

}
