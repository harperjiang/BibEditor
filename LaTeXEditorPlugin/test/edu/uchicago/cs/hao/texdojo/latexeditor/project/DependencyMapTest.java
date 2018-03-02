package edu.uchicago.cs.hao.texdojo.latexeditor.project;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class DependencyMapTest {

	@Test
	public void testOverall() {
		DependencyMap dm = new DependencyMap();


		dm.include("a", "b");
		dm.include("c", "b");

		dm.mark("a");
		dm.mark("c");
		dm.mark("d");

		List<String> res = dm.roots();
		assertEquals(3, res.size());

		assertEquals(2, dm.roots("b").size());
	}

	@Test
	public void testNestedInclude() {
		DependencyMap dm = new DependencyMap();


		dm.include("a", "b");
		dm.include("b", "c");

		dm.mark("a");

		assertEquals(1, dm.roots("c").size());
	}

}
