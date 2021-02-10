package hao.texdojo.latexeditor.editors.hover;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

public class HoverContext {

	public String information;
	
	public IRegion hoverRegion;
	
	public IDocument document;
	
	public HoverContext(String i,IRegion hr,IDocument doc) {
		this.information = i;
		this.hoverRegion = hr;
		this.document = doc;
	}
}
