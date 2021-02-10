package hao.texdojo.latexeditor.editors.handlers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import hao.texdojo.latexeditor.editors.handlers.TextFormatterHandler;

public class TextFormatterHandlerTest {

	@Test
	public void testFormat() {

		String data = "\\documentclass{article}\n" + "\\begin{document}\n" + "This is a document\n"
				+ "This is a long long long long long doc\n" + "\\begin{another}\n" + "\\end{line}\n"
				+ "\\end{document}";

		assertEquals("\\documentclass{article}\n" + "\\begin{document}\n" + "This is a document\n"
				+ "This is a long long long long\nlong doc\n" + "\\begin{another}\n" + "\\end{line}\n"
				+ "\\end{document}", TextFormatterHandler.format(data, 30));

	}

	@Test
	public void testFormatLine() {
		String result = TextFormatterHandler
				.formatLine("This is a reallly really really really really really really really really long line", 30);
		assertEquals("This is a reallly really\n" + "really really really really\n" + "really really really long line", result);
	}
}
