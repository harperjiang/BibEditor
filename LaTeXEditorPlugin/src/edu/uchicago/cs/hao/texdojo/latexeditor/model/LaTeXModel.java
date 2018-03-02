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

package edu.uchicago.cs.hao.texdojo.latexeditor.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import edu.uchicago.cs.hao.texdojo.latexeditor.parser.LaTeXParser;

/**
 * This model manage document as collections of <code>LaTeXNode</code>
 * 
 * @author Hao Jiang
 *
 */
public class LaTeXModel {

	private List<LaTeXNode> nodes = new ArrayList<LaTeXNode>();

	public List<LaTeXNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<LaTeXNode> nodes) {
		this.nodes = nodes;
	}

	public boolean has(String command) {
		return !find(n -> {
			return !StringUtils.isEmpty(n.getContent()) && n.getContent().equals(command);
		}).isEmpty();
	}

	public List<LaTeXNode> find(Predicate<LaTeXNode> p) {
		return nodes.stream().flatMap(n -> {
			return n.find(p).stream();
		}).collect(Collectors.toList());
	}

	public void organize() {
		nodes = new LaTeXOrganizer().parse(nodes);
	}

	public static LaTeXModel parseFromFile(InputStream input) {
		try {
			LaTeXParser parser = new LaTeXParser(input);
			List<LaTeXNode> nodes = new ArrayList<LaTeXNode>();
			LaTeXNode node = null;
			while ((node = parser.scan()) != null) {
				nodes.add(node);
			}
			LaTeXModel model = new LaTeXModel();
			model.setNodes(nodes);
			model.organize();
			return model;
		} catch (Exception e) {
			return new LaTeXModel();
		}
	}

}
