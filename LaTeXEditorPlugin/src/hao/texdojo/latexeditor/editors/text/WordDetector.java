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

package hao.texdojo.latexeditor.editors.text;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * @author Hao Jiang
 *
 */
public class WordDetector implements IWordDetector {

	@Override
	public boolean isWordPart(char c) {
		return Character.isAlphabetic(c) || Character.isDigit(c);
	}

	@Override
	public boolean isWordStart(char c) {
		return Character.isAlphabetic(c) || Character.isDigit(c);
	}
}