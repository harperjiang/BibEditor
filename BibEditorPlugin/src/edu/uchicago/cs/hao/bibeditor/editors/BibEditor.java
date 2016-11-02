package edu.uchicago.cs.hao.bibeditor.editors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import edu.uchicago.cs.hao.bibeditor.filemodel.BibModel;
import edu.uchicago.cs.hao.bibeditor.filemodel.BibParseException;
import edu.uchicago.cs.hao.bibeditor.filemodel.BibParser;

public class BibEditor extends EditorPart {

	private BibModel model;

	private EditorUI ui;

	public BibEditor() {
		super();
		ui = new EditorUI();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
		setContentDescription(input.getToolTipText());
		setTitleToolTip(input.getToolTipText());

		IPath path = ((IPathEditorInput) input).getPath();
		try {
			model = new BibParser().parse(new FileInputStream(path.toFile()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (BibParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void createPartControl(Composite parent) {
		ui.setModel(model);
		ui.createUI(parent);
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void doSave(IProgressMonitor arg0) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void dispose() {
		super.dispose();
		ui.dispose();
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

}
