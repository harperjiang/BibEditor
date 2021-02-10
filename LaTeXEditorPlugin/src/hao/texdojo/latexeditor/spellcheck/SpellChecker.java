package hao.texdojo.latexeditor.spellcheck;

import java.util.List;

public interface SpellChecker {

	public List<Suggestion> check(String line);
}
