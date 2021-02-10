package hao.texdojo.latexeditor.editors.assistant.model;

import java.text.MessageFormat;

public class AssistEntry {

	private String key;
	private String description;

	public AssistEntry(String k, String d) {
		key = k;
		description = d;
	}

	public String getKey() {
		return key;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0} {1}", key, description);
	}

}
