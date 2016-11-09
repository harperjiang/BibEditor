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

package edu.uchicago.cs.hao.texdojo.bibeditor.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uchicago.cs.hao.texdojo.bibeditor.dialogs.SearchDialog;
import edu.uchicago.cs.hao.texdojo.bibeditor.editors.BibEditor;

/**
 * @author Cathy
 *
 */
public class FindKeywordHandler extends BibHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.uchicago.cs.hao.texdojo.bibeditor.handlers.BibHandler#executeWithEditor(org.
	 * eclipse.core.commands.ExecutionEvent,
	 * edu.uchicago.cs.hao.texdojo.bibeditor.editors.BibEditor)
	 */
	@Override
	protected Object executeWithEditor(ExecutionEvent event, BibEditor editor) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		SearchDialog dialog = new SearchDialog(window.getShell());
		if (Dialog.OK == dialog.open()) {
			String text = dialog.getSearchString();
			editor.getUi().search(text);
		}

		return null;
	}

}
