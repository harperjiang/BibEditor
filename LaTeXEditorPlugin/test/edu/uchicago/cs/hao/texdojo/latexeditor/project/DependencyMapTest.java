package edu.uchicago.cs.hao.texdojo.latexeditor.project;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.junit.Test;

public class DependencyMapTest {

	@Test
	public void testOverall() {
		DependencyMap dm = new DependencyMap();

		dm.add("a", new DemoResource());
		dm.add("b", new DemoResource());
		dm.add("c", new DemoResource());
		dm.add("d", new DemoResource());

		dm.include("a", "b");
		dm.include("c", "b");

		dm.mark("a");
		dm.mark("c");
		dm.mark("d");

		List<IResource> res = dm.roots();
		assertEquals(3, res.size());
		
		assertEquals(2,dm.roots("b").size());
	}

}
