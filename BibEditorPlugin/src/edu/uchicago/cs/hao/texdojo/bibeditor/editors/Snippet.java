/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package edu.uchicago.cs.hao.texdojo.bibeditor.editors;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibParser;

public class Snippet {

	public static void main(String[] args) throws Exception {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		EditorUI ui = new EditorUI();
		ui.setModel(new BibParser().parse(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("edu/uchicago/cs/hao/bibeditor/filemodel/testsrc")));
		ui.createUI(shell);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.dispose();
		ui.dispose();
	}
}