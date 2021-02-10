package hao.texdojo.latexeditor.editors.outline;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import hao.texdojo.latexeditor.editors.model.LaTeXDocModel;
import hao.texdojo.latexeditor.editors.model.LaTeXDocModelEvent;
import hao.texdojo.latexeditor.editors.model.LaTeXDocModelListener;

public class LaTeXEditorOutlinePage extends ContentOutlinePage implements LaTeXDocModelListener {

	private LaTeXDocModel model;

	private ILabelProvider outlineLabelProvider = new LaTeXOutlineLabelProvider();

	private IContentProvider outlineContentProvider = new LaTeXOutlineTreeContentProvider();

	private ILabelProvider figureLabelProvider = new LaTeXFigureLabelProvider();

	private IContentProvider figureContentProvider = new LaTeXFigureTreeContentProvider();

	private boolean figureMode = false;

	public LaTeXEditorOutlinePage(LaTeXDocModel model) {
		this.model = model;
		this.model.addModelListener(this);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		getTreeViewer().addSelectionChangedListener(this);
		getTreeViewer().setLabelProvider(outlineLabelProvider);
		getTreeViewer().setContentProvider(outlineContentProvider);
		getTreeViewer().setInput(model);
		getTreeViewer().expandAll();
	}

	@Override
	public void modelChanged(LaTeXDocModelEvent event) {
		if (getTreeViewer() != null) {
			getTreeViewer().refresh();
			getTreeViewer().expandAll();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		outlineLabelProvider.dispose();
		figureLabelProvider.dispose();
	}

	public void switchMode() {
		figureMode = !figureMode;
		if (figureMode) {
			getTreeViewer().setLabelProvider(figureLabelProvider);
			getTreeViewer().setContentProvider(figureContentProvider);
		} else {
			getTreeViewer().setLabelProvider(outlineLabelProvider);
			getTreeViewer().setContentProvider(outlineContentProvider);
		}
		getTreeViewer().expandAll();
	}
}
