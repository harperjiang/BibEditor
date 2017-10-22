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

package edu.uchicago.cs.hao.texdojo.latexeditor.editors.assistant;

import java.text.MessageFormat;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.PartitionScanner;

/**
 * This Strategy will automatically insert an "end" tag when a "begin" tag
 * followed by an "enter"
 * 
 * @author Hao Jiang
 *
 */
public class BeginEndStrategy implements IAutoEditStrategy {

	@Override
	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		if (command.text.equals("\n")) {
			// Only process begin tag followed by an enter

			// Look forward for an COMMAND
			int start = command.offset - 1;
			try {
				ITypedRegion before = document.getPartition(start);
				ITypedRegion onemore = document.getPartition(before.getOffset() - 1);

				if (PartitionScanner.LATEX_ARG.equals(before.getType())
						&& PartitionScanner.LATEX_COMMAND.equals(onemore.getType())) {
					String commandText = document.get(onemore.getOffset(), onemore.getLength());

					if ("\\begin".equals(commandText)) {
						String argText = document.get(before.getOffset() + 1, before.getLength() - 2);

						// Insert an end tag
						String text = MessageFormat.format("\n\n{0}'{'{1}'}'", "\\end", argText);

						command.text = text;
//						command.length = text.length();
						command.caretOffset = command.offset + 1;
						command.shiftsCaret = false;
					}
				}

			} catch (BadLocationException e) {
				// Do nothing
			}
		}
	}

}
