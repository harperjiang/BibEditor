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
import org.eclipse.jface.text.rules.WordPatternRule;

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

	public static final String[] VALID_TYPE = new String[] { IDocument.DEFAULT_CONTENT_TYPE, LATEX_COMMAND, LATEX_ARG,
			LATEX_OPTION, LATEX_COMMENT };

	public PartitionScanner() {

		IToken command = new Token(LATEX_COMMAND);
		IToken arg = new Token(LATEX_ARG);
		IToken option = new Token(LATEX_OPTION);
		IToken comment = new Token(LATEX_COMMENT);
		IToken text = new Token(IDocument.DEFAULT_CONTENT_TYPE);

		IPredicateRule[] rules = new IPredicateRule[4];

		rules[0] = new MultiLineRule("{", "}", arg);
		rules[1] = new MultiLineRule("[", "]", option);
		rules[2] = new WordPatternRule(new WordDetector(), "\\", null, command);
		rules[3] = new EndOfLineRule("%", comment);
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
