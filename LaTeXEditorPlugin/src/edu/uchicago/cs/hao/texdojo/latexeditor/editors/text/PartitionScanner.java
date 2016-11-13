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

	public PartitionScanner() {

		IToken command = new Token(LATEX_COMMAND);
		IToken arg = new Token(LATEX_ARG);
		IToken option = new Token(LATEX_OPTION);
		IToken text = new Token(IDocument.DEFAULT_CONTENT_TYPE);

		IPredicateRule[] rules = new IPredicateRule[3];

		rules[0] = new MultiLineRule("{", "}", arg);
		rules[1] = new MultiLineRule("[", "]", option);
		rules[2] = new WordPatternRule(new WordDetector(), "\\", null, command);

		setPredicateRules(rules);
		setDefaultReturnToken(text);
	}
}
