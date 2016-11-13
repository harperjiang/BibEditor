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

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordPatternRule;
import org.eclipse.swt.SWT;

/**
 * 
 * @author Hao Jiang
 *
 */
public class LaTeXScanner extends RuleBasedScanner {

	public LaTeXScanner() {
		IToken text = new Token(new TextAttribute(ColorConstants.get(ColorConstants.DEFAULT)));
		IToken arg = new Token(new TextAttribute(ColorConstants.get(ColorConstants.COMMAND_ARG)));
		IToken command = new Token(new TextAttribute(ColorConstants.get(ColorConstants.COMMAND), null, SWT.BOLD));
		IToken option = new Token(new TextAttribute(ColorConstants.get(ColorConstants.OPTION)));

		IRule[] rules = new IRule[4];

		// Add rule for keyword
		rules[0] = new WordPatternRule(new WordDetector(), "\\", null, command);
		// Add rule for command args
		rules[1] = new MultiLineRule("{", "}", arg);
		rules[2] = new MultiLineRule("[", "]", option);
		// Add generic whitespace rule.
		rules[3] = new WhitespaceRule(new WhitespaceDetector());

		setRules(rules);
		setDefaultReturnToken(text);
	}

}
