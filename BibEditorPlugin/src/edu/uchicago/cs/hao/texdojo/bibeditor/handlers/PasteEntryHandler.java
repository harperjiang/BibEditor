package edu.uchicago.cs.hao.texdojo.bibeditor.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;

import edu.uchicago.cs.hao.texdojo.bibeditor.editors.BibEditor;
import edu.uchicago.cs.hao.texdojo.bibeditor.editors.EditorUI;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibEntry;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibModel;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibParser;

public class PasteEntryHandler extends BibHandler {

	@Override
	protected Object executeWithEditor(ExecutionEvent event, BibEditor editor) throws ExecutionException {
		Clipboard cb = new Clipboard(Display.getCurrent());

		EditorUI ui = editor.getUi();
		// Determine Focus
		if (ui.getText().isFocusControl()) {
			// Paste Text
			ui.getText().paste();
		} else {
			// Paste Entry
			String data = (String) cb.getContents(TextTransfer.getInstance());

			try {
				BibModel newmodel = new BibParser().parse(data);
				for (BibEntry entry : newmodel.getEntries()) {
					ui.getModel().addEntry(entry);
				}
			} catch (Exception e) {
				// TODO Log the exception
			}
		}

		return null;
	}

}
