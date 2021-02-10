package hao.texdojo.latexeditor.spellcheck;

public class Suggestion {

	private int offset;
	private String origin;
	private String[] suggestions;

	public Suggestion(int offset, String origin, String... suggestions) {
		this.offset = offset;
		this.origin = origin;
		this.suggestions = suggestions;
	}
	
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String[] getSuggestions() {
		return suggestions;
	}

	public void setSuggestions(String[] suggestions) {
		this.suggestions = suggestions;
	}

}
