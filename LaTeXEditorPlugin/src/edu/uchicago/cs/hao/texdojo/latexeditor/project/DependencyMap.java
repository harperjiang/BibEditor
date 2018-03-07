package edu.uchicago.cs.hao.texdojo.latexeditor.project;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DependencyMap {

	private Map<String, Set<String>> depend = new HashMap<>();

	private Set<String> roots = new HashSet<>();

	public void include(String rname, String include) {
		depend.putIfAbsent(include, new HashSet<String>());
		depend.get(include).add(rname);
	}

	public void remove(String rname) {
		depend.remove(rname);
		roots.remove(rname);
	}

	// Mark as root
	public void mark(String rname) {
		roots.add(rname);
	}

	// Unmark as root
	public void unmark(String name) {
		roots.remove(name);
	}

	public List<String> roots() {
		return new ArrayList<String>(roots);
	}

	public List<String> roots(String rname) {
		Set<String> visited = new HashSet<String>();
		Set<String> found = new HashSet<String>();
		List<String> head = new ArrayList<String>();

		head.add(rname);

		while (!head.isEmpty()) {
			String h = head.remove(0);
			if (visited.contains(h)) {
				throw new RuntimeException("Circle Dependency Found");
			}
			visited.add(h);

			if (roots.contains(h)) {
				found.add(h);
			}
			head.addAll(depend.getOrDefault(h, Collections.<String>emptySet()));
		}
		return new ArrayList<String>(found);
	}

	private String ROOT = "root";

	private String DEP = "dep";

	public void save(Writer writer) throws IOException {
		JsonObject obj = new JsonObject();
		JsonArray ro = new JsonArray();
		JsonObject dep = new JsonObject();
		obj.add(ROOT, ro);
		obj.add(DEP, dep);

		roots.forEach(s -> ro.add(s));

		depend.entrySet().forEach(entry -> {
			JsonArray deplist = new JsonArray();
			entry.getValue().forEach(s -> deplist.add(s));
			dep.add(entry.getKey(), deplist);
		});

		writer.write(obj.toString());
	}

	public void load(Reader reader) {
		this.depend.clear();
		this.roots.clear();
		JsonObject configData = new JsonParser().parse(reader).getAsJsonObject();

		configData.get(ROOT).getAsJsonArray().forEach(e -> roots.add(e.getAsString()));

		JsonObject dep = configData.get(DEP).getAsJsonObject();
		dep.entrySet().forEach(entry -> {
			Set<String> items = new HashSet<>();
			entry.getValue().getAsJsonArray().forEach(item -> items.add(item.getAsString()));
			depend.put(entry.getKey(), items);
		});
	}
}
