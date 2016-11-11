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

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.ColorConstants;

/**
 * 
 * @author Hao Jiang
 *
 */
public class CommandArgScanner extends RuleBasedScanner {

	public CommandArgScanner() {
		IToken token = new Token(new TextAttribute(ColorConstants.get(ColorConstants.COMMAND_ARG)));

		IRule[] rules = new IRule[3];

		// Add rule for command args
		rules[0] = new MultiLineRule("{", "}", token);
		rules[1] = new MultiLineRule("[", "]", token);
		// Add generic whitespace rule.
		rules[2] = new WhitespaceRule(new WhitespaceDetector());

		setRules(rules);
		setDefaultReturnToken(token);
	}
}
