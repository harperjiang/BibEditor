package edu.uchicago.cs.hao.texdojo.latexeditor.spellcheck.aspell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import edu.uchicago.cs.hao.texdojo.latexeditor.spellcheck.SpellChecker;
import edu.uchicago.cs.hao.texdojo.latexeditor.spellcheck.Suggestion;

public class ASpellChecker implements SpellChecker {

	private String path = "aspell";

	private String option = null;

	public ASpellChecker(String p, String opt) {
		if (!StringUtils.isEmpty(p))
			this.path = p;
		this.option = opt;
	}

	@Override
	public List<Suggestion> check(String line) {
		List<Suggestion> sugs = new ArrayList<Suggestion>();

		ProcessBuilder pb = new ProcessBuilder();
		pb.command("/bin/sh", "-c", MessageFormat.format("echo \"{0}\"| {1} -t -a {2}", escape(line), path, option));

		try {
			Process p = pb.start();
			p.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String respLine = null;
			while ((respLine = br.readLine()) != null) {
				if (respLine.startsWith("*")) {
					// Ok word
				}
				if (respLine.startsWith("&")) {
					sugs.add(buildSuggestion(respLine));
				}
				if (respLine.startsWith("#")) {
					sugs.add(buildFailed(respLine));
				}
			}

			br.close();
		} catch (IOException | InterruptedException e) {

		}

		return sugs;
	}

	protected String escape(String input) {
		return input.replace("\\", "\\\\\\\\");
	}
	
	protected Suggestion buildSuggestion(String input) {
		String[] pieces = input.split(":");
		String[] meta = pieces[0].split("\\s+");
		String[] sugg = pieces[1].split("[\\s,]+");

		return new Suggestion(Integer.valueOf(meta[3]), meta[1], sugg);
	}

	protected Suggestion buildFailed(String input) {
		String[] meta = input.split("\\s+");
		return new Suggestion(Integer.valueOf(meta[2]), meta[1]);
	}
}
