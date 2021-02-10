package hao.texdojo.latexeditor.editors.assistant.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class AssistIndex {

	private List<AssistEntry> entries = new ArrayList<>();

	public void load(Supplier<AssistEntry> supplier) {
		AssistEntry entry = null;
		while ((entry = supplier.get()) != null) {
			entries.add(entry);
		}
		entries.sort(new Comparator<AssistEntry>() {
			@Override
			public int compare(AssistEntry o1, AssistEntry o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
	}

	public void loadFromFile(String file, Function<String, AssistEntry> parser) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				Thread.currentThread().getContextClassLoader().getResourceAsStream(file), "UTF-8"));
		load(() -> {
			String line = null;
			try {
				while ((line = br.readLine()) != null) {
					if (line.trim().length() != 0) {
						if (!line.startsWith("#")) {
							AssistEntry entry = parser.apply(line);
							if (null != entry) {
								return entry;
							}
						}
					}
				}
				return null;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		br.close();
	}

	public List<AssistEntry> find(String prefix) {
		List<AssistEntry> results = new ArrayList<>();
		// Search through all proposals
		int index = Collections.binarySearch(entries, new AssistEntry(prefix, null), new Comparator<AssistEntry>() {
			@Override
			public int compare(AssistEntry o1, AssistEntry o2) {
				return o1.getKey().toLowerCase().compareTo(o2.getKey().toLowerCase());
			}
		});
		if (index >= 0) {
			results.add(entries.get(index));
		} else {
			int ins = -index - 1;
			// Forward to the last
			while (ins < entries.size() && entries.get(ins).getKey().toLowerCase().startsWith(prefix)) {
				results.add(entries.get(ins));
				ins++;
			}
		}
		return results;
	}

}
