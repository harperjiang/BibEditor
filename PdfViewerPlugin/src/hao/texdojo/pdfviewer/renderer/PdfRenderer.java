package hao.texdojo.pdfviewer.renderer;

import java.io.File;

import org.eclipse.swt.widgets.Composite;

public interface PdfRenderer {

	void init(Composite container);

	void load(File pdfFile);
	
	int getNumPage();
	
	void showPage(int pageNum);
}
