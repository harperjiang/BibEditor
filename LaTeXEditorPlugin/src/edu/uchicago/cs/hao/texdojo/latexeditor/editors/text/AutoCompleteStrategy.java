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

package edu.uchicago.cs.hao.texdojo.latexeditor.editors.text;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;

/**
 * @author Cathy
 *
 */
public class AutoCompleteStrategy implements IAutoEditStrategy {

	static final Pattern begin = Pattern.compile("\\\\begin\\{([a-z]+)\\}");

	@Override
	public void customizeDocumentCommand(IDocument document, DocumentCommand command) {
		String text = command.text;
		Matcher matcher = begin.matcher(text);
		if (matcher.matches()) {
			command.text = text + MessageFormat.format("\\end'{'{0}'}'", matcher.group(1));
		}
	}

}
