package hao.texdojo.latexeditor.model;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import hao.texdojo.latexeditor.model.BeginNode;
import hao.texdojo.latexeditor.model.EndNode;
import hao.texdojo.latexeditor.model.GroupNode;
import hao.texdojo.latexeditor.model.LaTeXNode;

public class GroupNodeTest {

	@Test
	public void testHas() {
		GroupNode gn = new GroupNode(new BeginNode("document", 0, 0, 5), new EndNode("document", 0, 0, 7),
				new ArrayList<LaTeXNode>());
		assertTrue(gn.has("document"));
	}

}
