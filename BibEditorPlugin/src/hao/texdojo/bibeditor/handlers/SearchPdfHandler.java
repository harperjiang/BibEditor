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

package hao.texdojo.bibeditor.handlers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import hao.texdojo.bibeditor.editors.BibEditor;
import hao.texdojo.bibeditor.filemodel.BibEntry;

/**
 * @author Cathy
 *
 */
public class SearchPdfHandler extends BibSelHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * hao.texdojo.bibeditor.handlers.BibSelHandler#executeWithSelection
	 * (org.eclipse.core.commands.ExecutionEvent,
	 * hao.texdojo.bibeditor.editors.BibEditor,
	 * hao.texdojo.bibeditor.filemodel.BibEntry)
	 */
	@Override
	protected Object executeWithSelection(ExecutionEvent event, BibEditor editor, BibEntry[] selection)
			throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		String title = selection[0].getProperty("title");
		if (title.isEmpty()) {
			MessageDialog.openWarning(window.getShell(), "Paper title not found", "Please check the entry data format");
			return null;
		}

		try {
			String encodedfilename = URLEncoder.encode(title, "utf-8");
			String url = MessageFormat.format("https://www.google.com/search?q={0}", encodedfilename);
			Program.launch(url);
		} catch (UnsupportedEncodingException e) {
			MessageDialog.openWarning(window.getShell(), "Error while searching for the file", e.getMessage());
			return null;
		}

		return null;
	}

}
