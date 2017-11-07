package edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.Token;

public class WhitespaceRule extends org.eclipse.jface.text.rules.WhitespaceRule implements IPredicateRule {

	public WhitespaceRule(IWhitespaceDetector detector) {
		super(detector);
	}

	@Override
	public IToken getSuccessToken() {
		return Token.WHITESPACE;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		return evaluate(scanner);
	}

}
