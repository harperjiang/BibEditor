package edu.uchicago.cs.hao.texdojo.pdfviewer.renderer;

import java.awt.Frame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import org.jpedal.objects.PdfPageData;

public class JPedalPdfRenderer extends AbstractPdfRenderer {

	PdfDecoder decoder = new PdfDecoder();

	@Override
	public void init(Composite parent) {
		PdfDecoder.init(true);
		try {
			parent.setLayout(new FillLayout());
			Composite container = new Composite(parent, SWT.EMBEDDED);
			Frame frame = SWT_AWT.new_Frame(container);
			frame.add(decoder);
			decoder.openPdfFile(getFile().getAbsolutePath());
			setNumPage(decoder.getPageCount());
			showPage(1);
		} catch (PdfException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void showPage(int currentPage) {
		try {
			// pageCounter.setText("Page " + currentPage + " of " + pdf.getPageCount());

			float scale = 1f;

			final PdfPageData pageData = decoder.getPdfPageData();
			final int inset = 10;
			final int cw;
			final int ch;
			final int rotation = pageData.getRotation(currentPage);
			if (rotation == 90 || rotation == 270) {
				cw = pageData.getCropBoxHeight(currentPage);
				ch = pageData.getCropBoxWidth(currentPage);
			} else {
				cw = pageData.getCropBoxWidth(currentPage);
				ch = pageData.getCropBoxHeight(currentPage);
			}

			// define pdf view width and height
			final float width = (decoder.getWidth() - inset - inset);
			final float height = (decoder.getHeight() - inset - inset);

			if ((width > 0) && (height > 0)) {
				final float x_factor;
				final float y_factor;
				x_factor = width / cw;
				y_factor = height / ch;

				if (x_factor < y_factor) {
					scale = x_factor;
				} else {
					scale = y_factor;
				}
			}
			decoder.setPageParameters(scale, currentPage);
			decoder.decodePage(currentPage);

			// wait to ensure decoded
			decoder.waitForDecodingToFinish();

			decoder.invalidate();
			decoder.updateUI();
			decoder.validate();

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
