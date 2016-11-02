package edu.uchicago.cs.hao.bibeditor.filemodel;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.junit.Test;

import edu.uchicago.cs.hao.bibeditor.filemodel.BibParser.Token;
import edu.uchicago.cs.hao.bibeditor.filemodel.BibParser.TokenType;

public class BibParserTest {

	@Test
	public void testParse() throws Exception {
		InputStream testsrc1 = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("edu/uchicago/cs/hao/bibeditor/filemodel/testsrc");
		BibModel model = new BibParser().parse(testsrc1);

		assertEquals(2, model.getEntries().size());

		BibEntry entry = model.getEntries().get(0);
		assertEquals("inproceedings", entry.getType());
		assertEquals("dremel", entry.getId());

		assertEquals(6, entry.getProperties().size());

		assertEquals("title", entry.getProperties().get(0).getKey());
		assertEquals("{Dremel: Interactive Analysis of Web-Scale Datasets}", entry.getProperties().get(0).getValue());

		assertEquals("author", entry.getProperties().get(1).getKey());
		assertEquals(
				"Sergey Melnik and Andrey Gubarev and Jing Jing Long and Geoffrey Romer and Shiva Shivakumar and Matt Tolton and Theo Vassilakis",
				entry.getProperties().get(1).getValue());

		assertEquals("year", entry.getProperties().get(2).getKey());
		assertEquals("2010", entry.getProperties().get(2).getValue());

		assertEquals("url", entry.getProperties().get(3).getKey());
		assertEquals("http://www.vldb2010.org/accept.htm", entry.getProperties().get(3).getValue());

		assertEquals("booktitle", entry.getProperties().get(4).getKey());
		assertEquals("Proc. of the 36th Int'l Conf on Very Large Data Bases", entry.getProperties().get(4).getValue());

		assertEquals("pages", entry.getProperties().get(5).getKey());
		assertEquals("330-339", entry.getProperties().get(5).getValue());

		entry = model.getEntries().get(1);
		assertEquals("inproceedings", entry.getType());
		assertEquals("parallel_sm_gpu", entry.getId());

		assertEquals(8, entry.getProperties().size());

		assertEquals("author", entry.getProperties().get(0).getKey());
		assertEquals("G. Vasiliadis and M. Polychronakis and S. Ioannidis", entry.getProperties().get(0).getValue());

		assertEquals("booktitle", entry.getProperties().get(1).getKey());
		assertEquals("Workload Characterization (IISWC), 2011 IEEE International Symposium on",
				entry.getProperties().get(1).getValue());

		assertEquals("title", entry.getProperties().get(2).getKey());
		assertEquals("{Parallelization and characterization of pattern matching using GPUs}",
				entry.getProperties().get(2).getValue());

		assertEquals("year", entry.getProperties().get(3).getKey());
		assertEquals("2011", entry.getProperties().get(3).getValue());

		assertEquals("pages", entry.getProperties().get(4).getKey());
		assertEquals("216-225", entry.getProperties().get(4).getValue());

		assertEquals("keywords", entry.getProperties().get(5).getKey());
		assertEquals(
				"computer graphic equipment;coprocessors;parallel architectures;pattern matching;GPU-based pattern matching;NVIDIA CUDA architecture;compute unified device architecture;graphics processing unit;high-speed pattern matching;memory hierarchy;pattern matching characterization;pattern matching parallelization;regular expression matching;string searching;Arrays;Automata;Doped fiber amplifiers;Graphics processing unit;Instruction sets;Pattern matching;Throughput",
				entry.getProperties().get(5).getValue());
	}

	@Test
	public void testToken() throws IOException {
		InputStream testsrc1 = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("edu/uchicago/cs/hao/bibeditor/filemodel/testsrc");
		Iterable<Token> tokens = new BibParser().tokenize(testsrc1);

		Iterator<Token> i = tokens.iterator();
		Token token = i.next();
		assertEquals("inproceedings", token.content);
		assertEquals(TokenType.TYPE, token.type);
		assertEquals(1, token.from);

		token = i.next();
		assertEquals("dremel", token.content);
		assertEquals(TokenType.KEY, token.type);
		assertEquals(15, token.from);

		token = i.next();
		assertEquals("title", token.content);
		assertEquals(TokenType.PROP_KEY, token.type);
		assertEquals(24, token.from);

		token = i.next();
		assertEquals("{Dremel: Interactive Analysis of Web-Scale Datasets}", token.content);
		assertEquals(TokenType.PROP_VAL, token.type);
		assertEquals(33, token.from);

		token = i.next();
		assertEquals("author", token.content);
		token = i.next();
		assertEquals(
				"Sergey Melnik and Andrey Gubarev and Jing Jing Long and Geoffrey Romer and Shiva Shivakumar and Matt Tolton and Theo Vassilakis",
				token.content);
		token = i.next();
		assertEquals("year", token.content);
		token = i.next();
		assertEquals("2010", token.content);
	}

	@Test
	public void testParse2() throws Exception {
		String input = "@unknown{new_entry, name=Abc,user=Good boy, title={{Dremel}}}";
		BibModel model = new BibParser().parse(input);
	
		assertEquals(1,model.getEntries().size());
		assertEquals("new_entry",model.getEntries().get(0).getId());
		
	}
}
