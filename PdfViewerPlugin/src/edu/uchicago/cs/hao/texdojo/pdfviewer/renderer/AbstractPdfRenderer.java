package edu.uchicago.cs.hao.texdojo.pdfviewer.renderer;

import java.io.File;

public abstract class AbstractPdfRenderer implements PdfRenderer {

	private File file;

	private int numPage;

	@Override
	public void load(File pdfFile) {
		this.file = pdfFile;
	}

	public File getFile() {
		return file;
	}

	public int getNumPage() {
		return numPage;
	}

	protected void setNumPage(int numPages) {
		this.numPage = numPages;
	}

}
