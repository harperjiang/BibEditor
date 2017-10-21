package edu.uchicago.cs.hao.texdojo.latexeditor.editors.model;

import java.util.EventObject;

public class LaTeXDocModelEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -198050477891262052L;

	public LaTeXDocModelEvent(LaTeXDocModel source) {
		super(source);
	}
}
