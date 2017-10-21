package edu.uchicago.cs.hao.texdojo.latexeditor.editors.model;

import java.util.EventListener;

public interface LaTeXDocModelListener extends EventListener {

	public void modelChanged(LaTeXDocModelEvent event);
}
