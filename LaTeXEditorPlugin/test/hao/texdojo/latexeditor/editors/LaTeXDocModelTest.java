/*******************************************************************************
 * Copyright (c) 2016 Hao Jiang.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hao Jiang - initial API and implementation and/or initial documentation
 *******************************************************************************/

package hao.texdojo.latexeditor.editors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.junit.Before;
import org.junit.Test;

import hao.texdojo.latexeditor.editors.model.LaTeXDocModel;
import hao.texdojo.latexeditor.editors.text.PartitionScanner;
import hao.texdojo.latexeditor.model.CommentNode;
import hao.texdojo.latexeditor.model.GroupNode;
import hao.texdojo.latexeditor.model.InvokeNode;
import hao.texdojo.latexeditor.model.TextNode;

/**
 * @author Hao Jiang
 *
 */
public class LaTeXDocModelTest {

	IDocument doc;

	@Before
	public void init() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("edu/uchicago/cs/hao/texdojo/latexeditor/model/doc")));

		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null)
			sb.append(line).append('\n');

		doc = new Document(sb.toString());
		IDocumentPartitioner partitioner = new FastPartitioner(new PartitionScanner(),
				new String[] { IDocument.DEFAULT_CONTENT_TYPE, PartitionScanner.LATEX_COMMAND,
						PartitionScanner.LATEX_ARG, PartitionScanner.LATEX_OPTION, PartitionScanner.LATEX_COMMENT });
		partitioner.connect(doc);
		doc.setDocumentPartitioner(partitioner);
		br.close();
	}

	@Test
	public void testInit() throws Exception {

		LaTeXDocModel model = new LaTeXDocModel();

		model.init(doc);

		assertEquals(4, model.nodes().size());
		assertTrue(model.nodes().get(0) instanceof CommentNode);
		assertTrue(model.nodes().get(1) instanceof InvokeNode);
		assertTrue(model.nodes().get(2) instanceof TextNode);
		assertTrue(model.nodes().get(3) instanceof GroupNode);
	}

	@Test
	public void testClear() throws Exception {
		LaTeXDocModel model = new LaTeXDocModel();
		model.init(doc);

		model.clear(50, 20);

		assertEquals(14, model.nodes().size());

	}

	@Test
	public void testUpdate() {

	}
}
