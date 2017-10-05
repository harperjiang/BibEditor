package edu.uchicago.cs.hao.texdojo.latexeditor.editors.assistant;

import static org.junit.Assert.*;

import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.junit.Test;

public class LineAlignStrategyTest {

	@Test
	public void testWrapText() {

		StringBuilder text = new StringBuilder();
		text.append(
				"This is a nice afternoon. Mr. Brancky wants to go out to buy some food for his dog Bleach. He went downstairs from his crowed apartment at 4th floor.");

		List<IRegion> result = LineAlignStrategy.wrapText(text, 100, 10, 25);

		assertEquals("This is a nice\n" + "afternoon. Mr. Brancky\n"
				+ "wants to go out to buy\nsome food for his dog\nBleach. He went downstairs from\nhis crowed apartment at\n4th floor.",
				text);
		
	}

}
