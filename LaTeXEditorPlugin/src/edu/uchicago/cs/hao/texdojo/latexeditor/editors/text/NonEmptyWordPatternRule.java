package edu.uchicago.cs.hao.texdojo.latexeditor.editors.text;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordPatternRule;

public class NonEmptyWordPatternRule extends WordPatternRule {

	private int lengthLimit = -1;

	public NonEmptyWordPatternRule(IWordDetector detector, String startSequence, String endSequence, IToken token,
			int length) {
		super(detector, startSequence, endSequence, token, (char) 0);
		this.lengthLimit = length;
	}

	public NonEmptyWordPatternRule(IWordDetector detector, String startSequence, String endSequence, IToken token) {
		this(detector, startSequence, endSequence, token, -1);
	}

	private StringBuilder fBuffer = new StringBuilder();

	@Override
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		fBuffer.setLength(0);
		int c = scanner.read();
		while ((lengthLimit == -1 || fBuffer.length() < lengthLimit) && fDetector.isWordPart((char) c)) {
			fBuffer.append((char) c);
			c = scanner.read();
		}
		scanner.unread();

		if (fBuffer.length() != 0 && fBuffer.length() >= fEndSequence.length) {
			for (int i = fEndSequence.length - 1, j = fBuffer.length() - 1; i >= 0; i--, j--) {
				if (fEndSequence[i] != fBuffer.charAt(j)) {
					unreadBuffer(scanner);
					return false;
				}
			}
			return true;
		}

		unreadBuffer(scanner);
		return false;
	}

	protected void unreadBuffer(ICharacterScanner scanner) {
		fBuffer.insert(0, fStartSequence);
		for (int i = fBuffer.length() - 1; i > 0; i--)
			scanner.unread();
	}
}
