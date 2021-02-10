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

package hao.texdojo.bibeditor.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import hao.texdojo.bibeditor.editors.BibEditor;
import hao.texdojo.bibeditor.filemodel.BibEntry;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class AddEntryHandler extends BibHandler {

	@Override
	public Object executeWithEditor(ExecutionEvent event, BibEditor editor) throws ExecutionException {
		BibEntry newEntry = new BibEntry();
		newEntry.setId("new_entry");
		editor.getUi().getModel().addEntry(newEntry);
		return null;
	}
}
