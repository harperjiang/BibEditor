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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.rules.MultiLineGreedyRule;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.rules.NonEmptyWordPatternRule;

/**
 * 
 * @author Hao Jiang
 *
 */
public class PartitionScanner extends RuleBasedPartitionScanner {

	public final static String LATEX_COMMAND = "__latex_command";

	public final static String LATEX_ARG = "__latex_arg";

	public final static String LATEX_OPTION = "__latex_option";

	public final static String LATEX_COMMENT = "__latex_comment";

	public final static String LATEX_MATHMODE = "__latex_mathmode";

	public final static String LATEX_ESCAPE = "__latex_escape";

	public static final String[] VALID_TYPE = new String[] { IDocument.DEFAULT_CONTENT_TYPE, LATEX_COMMAND, LATEX_ARG,
			LATEX_OPTION, LATEX_COMMENT, LATEX_MATHMODE };

	public PartitionScanner() {

		IToken command = new Token(LATEX_COMMAND);
		IToken arg = new Token(LATEX_ARG);
		IToken option = new Token(LATEX_OPTION);
		IToken comment = new Token(LATEX_COMMENT);
		IToken mathmode = new Token(LATEX_MATHMODE);
		IToken escapedText = new Token(LATEX_ESCAPE);
//		IToken text = new Token(IDocument.DEFAULT_CONTENT_TYPE);

		IPredicateRule[] rules = new IPredicateRule[7];

		rules[0] = new NonEmptyWordPatternRule(new WordDetector(), "\\", null, command);
		rules[1] = new MultiLineGreedyRule('[', ']', option);
		rules[2] = new MultiLineGreedyRule('{', '}', arg);
		rules[3] = new MultiLineRule("$", "$", mathmode);
		rules[4] = new MultiLineRule("\\[", "\\]", mathmode);
		rules[5] = new NonEmptyWordPatternRule(new EscapeDetector(), "\\", null, escapedText, 1);
		rules[6] = new EndOfLineRule("%", comment);
		setPredicateRules(rules);
		// Setting a default here will separate text to single character token
		// setDefaultReturnToken(text);

	}

	public static final boolean isCommand(String type) {
		return LATEX_COMMAND.equals(type);
	}

	public static final boolean isArgOption(String type) {
		return LATEX_ARG.equals(type) || LATEX_OPTION.equals(type);
	}

}
