package edu.uchicago.cs.hao.texdojo.bibeditor.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import edu.uchicago.cs.hao.texdojo.bibeditor.editors.BibEditor;
import edu.uchicago.cs.hao.texdojo.bibeditor.editors.EditorUI;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibEntry;

public class CopyEntryHandler extends BibHandler {

	@Override
	protected Object executeWithEditor(ExecutionEvent event, BibEditor editor) throws ExecutionException {
		Clipboard cb = new Clipboard(Display.getCurrent());

		EditorUI ui = editor.getUi();
		// Determine Focus
		if (ui.getTable().getTable().isFocusControl()) {
			// Copy Entry
			BibEntry[] entries = ui.allSelected();

			StringBuilder sb = new StringBuilder();
			for (BibEntry entry : entries) {
				sb.append(entry.toString());
			}
			cb.setContents(new Object[] { sb.toString() }, new Transfer[] { TextTransfer.getInstance() });
		} else if (ui.getText().isFocusControl()) {
			// Copy Text
			ui.getText().copy();
		} else {
			// Do nothing
		}

		return null;
	}

}
