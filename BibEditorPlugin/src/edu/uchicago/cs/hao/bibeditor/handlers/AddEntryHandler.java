package edu.uchicago.cs.hao.bibeditor.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import edu.uchicago.cs.hao.bibeditor.editors.BibEditor;
import edu.uchicago.cs.hao.bibeditor.filemodel.BibEntry;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class AddEntryHandler extends BibHandler {

	@Override
	public Object executeWithEditor(ExecutionEvent event, BibEditor editor) throws ExecutionException {
		BibEntry newEntry = new BibEntry();
		newEntry.setId("new_entry");
		editor.getUi().getModel().addEntry(newEntry);
		return null;
	}
}
