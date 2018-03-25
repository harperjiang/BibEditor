package edu.uchicago.cs.hao.texdojo.latexeditor.editors.hover;

import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.MarkerAnnotation;

public class LaTeXTextHover extends DefaultTextHover implements ITextHoverExtension, ITextHoverExtension2 {

	public LaTeXTextHover(ISourceViewer sourceViewer) {
		super(sourceViewer);
	}

	@Override
	public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
		String information = getHoverInfo(textViewer, hoverRegion);
		IDocument doc = ((ISourceViewer) textViewer).getDocument();
		return new HoverContext(information, hoverRegion, doc);
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		return new IInformationControlCreator() {

			@Override
			public IInformationControl createInformationControl(Shell parent) {
				return new LaTeXInformationControl(parent);
			}
		};
	}

	@Override
	protected boolean isIncluded(Annotation annotation) {
		return annotation instanceof MarkerAnnotation;
	}

}
