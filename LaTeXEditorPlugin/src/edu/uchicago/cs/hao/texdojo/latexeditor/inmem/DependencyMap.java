package edu.uchicago.cs.hao.texdojo.latexeditor.inmem;

import java.io.FileNotFoundException;
import java.io.FileReader;
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DependencyMap {

	private Map<String, Set<String>> depend = new HashMap<>();

	private Map<String, Set<String>> inverse = new HashMap<>();

	private Set<String> roots = new HashSet<>();

	private Map<String, String> refs = new HashMap<>();

	public void include(String rname, String include) {
		depend.putIfAbsent(include, new HashSet<String>());
		depend.get(include).add(rname);

		inverse.putIfAbsent(rname, new HashSet<String>());
		inverse.get(rname).add(include);
	}

	public void ref(String rname, String bib) {
		refs.put(rname, bib);
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

	/// ======================================================
	/// Methods for lookup
	/// ======================================================

	public List<String> children(String root) {
		List<String> empty = Collections.<String>emptyList();
		Set<String> eset = Collections.<String>emptySet();
		if (roots.contains(root)) {
			List<String> visited = new ArrayList<>();
			visited.add(root);
			for (int i = 0; i < visited.size(); i++) {
				visited.addAll(inverse.getOrDefault(visited.get(i), eset));
			}
			return visited;
		} else {
			return empty;
		}
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

	public String ref(String file) {
		List<String> roots = roots(file);
		List<String> children = new ArrayList<>();
		for (String root : roots) {
			children.addAll(children(root));
		}
		for (String child : children) {
			if (refs.containsKey(child)) {
				return refs.get(child);
			}
		}
		return null;
	}

	private String ROOT = "root";

	private String DEP = "dep";

	private String REF = "ref";

	public void save(Writer writer) throws IOException {
		JsonObject obj = new JsonObject();
		JsonArray ro = new JsonArray();
		JsonObject dep = new JsonObject();
		JsonObject ref = new JsonObject();
		obj.add(ROOT, ro);
		obj.add(DEP, dep);
		obj.add(REF, ref);

		roots.forEach(s -> ro.add(s));

		depend.entrySet().forEach(entry -> {
			JsonArray deplist = new JsonArray();
			entry.getValue().forEach(s -> deplist.add(s));
			dep.add(entry.getKey(), deplist);
		});
		refs.entrySet().forEach(entry -> {
			ref.addProperty(entry.getKey(), entry.getValue());
		});

		writer.write(obj.toString());
	}

	public void load(Reader reader) {
		this.depend.clear();
		this.roots.clear();
		this.refs.clear();
		JsonObject configData = new JsonParser().parse(reader).getAsJsonObject();

		configData.get(ROOT).getAsJsonArray().forEach(e -> roots.add(e.getAsString()));

		JsonObject dep = configData.get(DEP).getAsJsonObject();
		dep.entrySet().forEach(entry -> {
			Set<String> items = new HashSet<>();
			entry.getValue().getAsJsonArray().forEach(item -> items.add(item.getAsString()));
			depend.put(entry.getKey(), items);
		});

		JsonObject ref = configData.get(REF).getAsJsonObject();
		ref.entrySet().forEach(entry -> {
			refs.put(entry.getKey(), entry.getValue().getAsString());
		});
	}

	public static DependencyMap load(IProject project) {
		IFile depfile = project.getFile(".texdojo");
		DependencyMap map = new DependencyMap();
		try {
			map.load(new FileReader(depfile.getLocation().toFile()));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return map;
	}

}
