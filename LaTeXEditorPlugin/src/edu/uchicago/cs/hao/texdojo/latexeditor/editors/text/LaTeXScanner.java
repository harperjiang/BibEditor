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

import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COLOR_ARG;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COLOR_COMMAND;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COLOR_COMMENT;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COLOR_MATHMODE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COLOR_OPTION;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_COLOR_TEXT;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.swt.SWT;

/**
 * 
 * @author Hao Jiang
 *
 */
public class LaTeXScanner extends RuleBasedScanner {

	public LaTeXScanner() {
		IToken text = new Token(new TextAttribute(ColorManager.get(P_COLOR_TEXT)));
		IToken arg = new Token(new TextAttribute(ColorManager.get(P_COLOR_ARG)));
		IToken command = new Token(new TextAttribute(ColorManager.get(P_COLOR_COMMAND), null, SWT.BOLD));
		IToken option = new Token(new TextAttribute(ColorManager.get(P_COLOR_OPTION)));
		IToken comment = new Token(new TextAttribute(ColorManager.get(P_COLOR_COMMENT), null, SWT.ITALIC));
		IToken mathmode = new Token(new TextAttribute(ColorManager.get(P_COLOR_MATHMODE)));
		
		IRule[] rules = new IRule[8];

		// Add rule for keyword
		rules[0] = new NonEmptyWordPatternRule(new WordDetector(), "\\", null, command);
		// Add rule for command args
		rules[1] = new MultiLineGreedyRule('{', '}', arg);
		rules[2] = new MultiLineGreedyRule('[', ']', option);

		// Math mode
		rules[3] = new MultiLineRule("$", "$", mathmode);
		rules[4] = new MultiLineRule("\\[", "\\]", mathmode);

		// Add rule for comment
		rules[5] = new NonEmptyWordPatternRule(new EscapeDetector(), "\\", null, text, 1);
		rules[6] = new EndOfLineRule("%", comment);

		// Add generic whitespace rule.
		rules[7] = new WhitespaceRule(new WhitespaceDetector());

		setRules(rules);
		setDefaultReturnToken(text);
	}

}
