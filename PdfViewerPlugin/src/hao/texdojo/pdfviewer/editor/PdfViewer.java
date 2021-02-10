package hao.texdojo.pdfviewer.editor;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import hao.texdojo.pdfviewer.renderer.IcePdfRenderer;
import hao.texdojo.pdfviewer.renderer.PdfRenderer;

public class PdfViewer extends EditorPart {

	// TODO Select renderer by preference
	private PdfRenderer renderer = new IcePdfRenderer();

	@Override
	public void doSave(IProgressMonitor monitor) {
		// No save function
	}

	@Override
	public void doSaveAs() {
		// No save function
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);

		setPartName(input.getName());
		setContentDescription(input.getToolTipText());
		setTitleToolTip(input.getToolTipText());

		// Load PDF file
		IPath path = ((IPathEditorInput) getEditorInput()).getPath();
		try {
			File pdfFile = path.toFile();
			renderer.load(pdfFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		renderer.init(parent);
	}

	@Override
	public void setFocus() {

	}

}
