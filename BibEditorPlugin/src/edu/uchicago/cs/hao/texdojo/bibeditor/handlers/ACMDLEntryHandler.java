package edu.uchicago.cs.hao.texdojo.bibeditor.handlers;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import edu.uchicago.cs.hao.texdojo.bibeditor.dialogs.SelectEntryDialog;
import edu.uchicago.cs.hao.texdojo.bibeditor.editors.BibEditor;
import edu.uchicago.cs.hao.texdojo.bibeditor.external.acmdl.ACMDLService;
import edu.uchicago.cs.hao.texdojo.bibeditor.external.acmdl.DefaultACMDLService;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibEntry;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibParseException;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibParser;

/**
 * Generate entries from ArXiv Libraries
 * 
 * @author harper
 *
 */
public class ACMDLEntryHandler extends BibHandler {

	static final Pattern ARTICLE_ID = Pattern.compile("\\d+");

	@Override
	protected Object executeWithEditor(ExecutionEvent event, BibEditor editor) throws ExecutionException {

		InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), "", "ACM Digital Library Article id",
				"", null);
		if (dlg.open() == Window.OK) {
			String articleId = dlg.getValue();
			if (!ARTICLE_ID.matcher(articleId).matches()) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Invalid Article Id",
						"Not a valid article id. Please check your input.");
				return null;
			}
			ACMDLService service = new DefaultACMDLService();
			BibParser parser = new BibParser();
			try {

				List<String> bibtexs = service.getBib(articleId);
				List<BibEntry> entries = bibtexs.stream().flatMap((input) -> {
					try {
						return parser.parse(input).getEntries().stream();
					} catch (IOException e1) {
						return Collections.<BibEntry>emptyList().stream();
					} catch (BibParseException e2) {
						return Collections.<BibEntry>emptyList().stream();
					}
				}).collect(Collectors.toList());
				switch (entries.size()) {
				case 0:
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Failed to fetch article",
							"No valid entry found.");
					return null;
				case 1:
					editor.getUi().getModel().addEntry(entries.get(0));
					MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Entry added",
							"BibTex entry has been added to list.");
					return null;
				default:
					// Multiple Entries found, choose one
					SelectEntryDialog dialog = new SelectEntryDialog(Display.getCurrent().getActiveShell());
					dialog.setEntries(entries);
					if (dialog.open() == Window.OK) {
						Object[] choice = dialog.getChoice();
						for (Object c : choice) {
							editor.getUi().getModel().addEntry((BibEntry) c);
						}
					}
					return null;
				}
			} catch (Exception e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Failed to fetch article",
						"Error occurred when trying to fetch article information. Please try again later.");
				LoggerFactory.getLogger(getClass()).error("ACMDLService error", e);
				return null;
			}

		}

		return null;
	}

}
