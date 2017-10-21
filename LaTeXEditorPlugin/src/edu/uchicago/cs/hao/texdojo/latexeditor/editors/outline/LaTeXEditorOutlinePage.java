package edu.uchicago.cs.hao.texdojo.latexeditor.editors.outline;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import edu.uchicago.cs.hao.texdojo.latexeditor.editors.model.LaTeXDocModel;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.model.LaTeXDocModelEvent;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.model.LaTeXDocModelListener;

public class LaTeXEditorOutlinePage extends ContentOutlinePage implements LaTeXDocModelListener {

	private LaTeXDocModel model;

	private ILabelProvider labelProvider = new LaTeXOutlineLabelProvider();

	private IContentProvider contentProvider = new LaTeXOutlineTreeContentProvider();

	public LaTeXEditorOutlinePage(LaTeXDocModel model) {
		this.model = model;
		this.model.addModelListener(this);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		getTreeViewer().addSelectionChangedListener(this);
		getTreeViewer().setLabelProvider(labelProvider);
		getTreeViewer().setContentProvider(contentProvider);
		getTreeViewer().setInput(model);
		getTreeViewer().expandAll();
	}

	@Override
	public void modelChanged(LaTeXDocModelEvent event) {
		getTreeViewer().refresh();
		getTreeViewer().expandAll();
	}

}
