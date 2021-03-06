package hao.texdojo.latexeditor.editors.text.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class MultiLineGreedyRule implements IPredicateRule {

	private char start;
	private char end;
	private IToken successToken;

	public MultiLineGreedyRule(char start, char end, IToken successToken) {
		this.start = start;
		this.end = end;
		this.successToken = successToken;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	@Override
	public IToken getSuccessToken() {
		return successToken;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		// Dunno how to support resume
		if (resume)
			return Token.UNDEFINED;
		boolean escape = false;
		int c = scanner.read();

		if (start == c) {
			// Start match, look for end
			int readCount = 1;
			int stackCount = 0;

			while ((c = scanner.read()) != ICharacterScanner.EOF) {
				readCount++;
				if (c == start && !escape) {
					stackCount++;
				}
				if (c == end && !escape) {
					if (stackCount == 0) {
						return successToken;
					} else {
						stackCount--;
					}
				}
				if (c == '\\') { // Escape
					escape = !escape;
				} else {
					if (escape)
						escape = false;
				}
			}
			for (int i = 0; i < readCount; i++) {
				scanner.unread();
			}
		} else {
			scanner.unread();
		}

		return Token.UNDEFINED;
	}

}
