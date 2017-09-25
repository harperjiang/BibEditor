package edu.uchicago.cs.hao.texdojo.latexeditor.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.uchicago.cs.hao.texdojo.latexeditor.model.ArgNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.BeginNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.CommandNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.CommentNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.EndNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.LaTeXNode;
import edu.uchicago.cs.hao.texdojo.latexeditor.model.TextNode;

public class LaTeXParserTest {

	@Test
	public void testScan() throws IOException {
		LaTeXParser parser = new LaTeXParser("\\begin{abc} \\end{abc}");

		LaTeXNode node = null;
		List<LaTeXNode> nodes = new ArrayList<LaTeXNode>();

		while ((node = parser.scan()) != null) {
			nodes.add(node);
		}

		assertEquals(5, nodes.size());
		assertTrue(nodes.get(0) instanceof BeginNode);
		assertTrue(nodes.get(1) instanceof ArgNode);
		assertEquals("abc", nodes.get(1).getContent());
		assertTrue(nodes.get(2) instanceof TextNode);
		assertTrue(nodes.get(3) instanceof EndNode);
		assertTrue(nodes.get(4) instanceof ArgNode);
		assertEquals("abc", nodes.get(4).getContent());

		parser = new LaTeXParser("\\begin{abc} %ttmpf \n" + "\\end{abc}");

		node = null;
		nodes = new ArrayList<LaTeXNode>();

		while ((node = parser.scan()) != null) {
			nodes.add(node);
		}

		assertEquals(6, nodes.size());
		assertTrue(nodes.get(0) instanceof BeginNode);
		assertTrue(nodes.get(1) instanceof ArgNode);
		assertTrue(nodes.get(2) instanceof TextNode);
		assertTrue(nodes.get(3) instanceof CommentNode);
		assertTrue(nodes.get(4) instanceof EndNode);
		assertTrue(nodes.get(5) instanceof ArgNode);
		
		parser = new LaTeXParser("\\begin{abc} \n" + "\\goodatm\\end{abc}");

		node = null;
		nodes = new ArrayList<LaTeXNode>();

		while ((node = parser.scan()) != null) {
			nodes.add(node);
		}

		assertEquals(6, nodes.size());
		assertTrue(nodes.get(0) instanceof BeginNode);
		assertTrue(nodes.get(1) instanceof ArgNode);
		assertTrue(nodes.get(2) instanceof TextNode);
		assertEquals(" \n",nodes.get(2).getContent());

		assertTrue(nodes.get(3) instanceof CommandNode);
		assertTrue(nodes.get(3).has("goodatm"));
		
		assertTrue(nodes.get(4) instanceof EndNode);
		assertTrue(nodes.get(5) instanceof ArgNode);
	}

}
