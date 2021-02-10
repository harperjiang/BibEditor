package hao.texdojo.latexeditor.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import hao.texdojo.latexeditor.model.ArgNode;
import hao.texdojo.latexeditor.model.BeginNode;
import hao.texdojo.latexeditor.model.CommandNode;
import hao.texdojo.latexeditor.model.EndNode;
import hao.texdojo.latexeditor.model.GroupNode;
import hao.texdojo.latexeditor.model.InvokeNode;
import hao.texdojo.latexeditor.model.LaTeXNode;
import hao.texdojo.latexeditor.model.LaTeXOrganizer;
import hao.texdojo.latexeditor.model.OptionNode;
import hao.texdojo.latexeditor.model.TextNode;

public class LaTeXOrganizerTest {

	@Test
	public void testParse() {
		LaTeXOrganizer parser = new LaTeXOrganizer();

		List<LaTeXNode> tokens = new ArrayList<LaTeXNode>();

		tokens.add(new BeginNode(null, 0, 5, 5));
		tokens.add(new ArgNode("document", 6, 10, 5));

		tokens.add(new TextNode("abc", 17, 10, 5));

		tokens.add(new CommandNode("ttt", 10, 20, 5));
		tokens.add(new ArgNode("ddd", 20, 30, 5));
		tokens.add(new ArgNode("mmm", 30, 50, 5));
		tokens.add(new OptionNode("adadf", 40, 20, 5));

		tokens.add(new EndNode(null, 30, 10, 5));
		tokens.add(new ArgNode("document", 45, 10, 5));

		tokens.add(new CommandNode("aka", 55, 10, 5));
		tokens.add(new ArgNode("ttt", 65, 10, 5));

		List<LaTeXNode> newnodes = parser.parse(tokens);

		assertEquals(2, newnodes.size());
		assertTrue(newnodes.get(0) instanceof GroupNode);
		assertTrue(newnodes.get(0).getContent().equals("document"));

		GroupNode gn = (GroupNode) newnodes.get(0);
		assertEquals(2, gn.getChildren().size());

		assertTrue(gn.getChildren().get(0) instanceof TextNode);
		assertTrue(gn.getChildren().get(1) instanceof InvokeNode);

		assertTrue(newnodes.get(1) instanceof InvokeNode);
	}

}
