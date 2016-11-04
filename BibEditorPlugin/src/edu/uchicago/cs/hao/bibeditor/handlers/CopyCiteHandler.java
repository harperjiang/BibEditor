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

package edu.uchicago.cs.hao.bibeditor.handlers;

import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import edu.uchicago.cs.hao.bibeditor.editors.BibEditor;
import edu.uchicago.cs.hao.bibeditor.filemodel.BibEntry;

/**
 * @author Hao Jiang
 *
 */
public class CopyCiteHandler extends BibSelHandler {

	@Override
	protected Object executeWithSelection(ExecutionEvent event, BibEditor editor, BibEntry[] selection)
			throws ExecutionException {
		Clipboard cb = new Clipboard(Display.getCurrent());
		StringBuilder sb = new StringBuilder();
		for (BibEntry sel : selection) {
			sb.append(sel.getId()).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		cb.setContents(new Object[] { MessageFormat.format("\\cite'{'{0}'}'", sb.toString()) },
				new Transfer[] { TextTransfer.getInstance() });
		return null;
	}
}
