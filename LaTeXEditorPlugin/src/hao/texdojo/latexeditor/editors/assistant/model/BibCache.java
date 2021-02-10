package hao.texdojo.latexeditor.editors.assistant.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import hao.texdojo.bibeditor.filemodel.BibEntry;
import hao.texdojo.bibeditor.filemodel.BibModel;
import hao.texdojo.bibeditor.filemodel.BibParser;

public class BibCache {

	static class Entry {
		FileTime timestamp;
		AssistIndex model;
	}

	private Map<String, Entry> cache = new HashMap<>();

	public AssistIndex get(File file) {
		try {
			FileTime lastModified = Files.getLastModifiedTime(file.toPath());

			String key = file.getAbsolutePath();
			Entry entry = cache.get(key);
			if (null == entry || entry.timestamp.compareTo(lastModified) < 0) {
				load(key);
			}

			return cache.get(key).model;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	class EntrySupplier implements Supplier<AssistEntry> {
		int index = 0;
		BibModel model;

		EntrySupplier(BibModel model) {
			this.model = model;
		}

		@Override
		public AssistEntry get() {
			if (index >= model.getEntries().size())
				return null;
			BibEntry entry = model.getEntries().get(index++);
			return new AssistEntry(entry.getId(), entry.getProperty("title"));
		}
	}

	protected void load(String key) throws IOException {
		AssistIndex index = new AssistIndex();
		BibModel model = new BibParser().parse(new FileInputStream(key));

		index.load(new EntrySupplier(model));

		Entry entry = new Entry();
		entry.model = index;
		entry.timestamp = Files.getLastModifiedTime(Paths.get(key));
		cache.put(key, entry);
	}
}
