package hao.texdojo.latexeditor.editors.text;

import org.eclipse.jface.text.rules.IWordDetector;

public class EscapeDetector implements IWordDetector {

	@Override
	public boolean isWordStart(char c) {
		return !Character.isWhitespace(c);
	}

	@Override
	public boolean isWordPart(char c) {
		return !Character.isWhitespace(c);
	}

}
