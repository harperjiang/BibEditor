package edu.uchicago.cs.hao.texdojo.latexeditor.project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IResource;

public class DependencyMap {

	private Map<String, IResource> resMap = new HashMap<>();

	private Map<String, List<String>> depend = new HashMap<>();

	private Set<String> roots = new HashSet<>();

	public void add(String rname, IResource resource) {
		resMap.put(rname, resource);
	}

	public void include(String rname, String include) {
		depend.putIfAbsent(include, new ArrayList<String>());
		depend.get(include).add(rname);
	}

	public void remove(String rname) {
		resMap.remove(rname);
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

	public List<IResource> roots() {
		return roots.stream().map(resMap::get).collect(Collectors.toList());
	}

	public List<IResource> roots(String rname) {
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
			head.addAll(depend.getOrDefault(h, Collections.<String>emptyList()));
		}
		return found.stream().map(resMap::get).collect(Collectors.toList());
	}
}
