package edu.uchicago.cs.hao.texdojo.bibeditor.handlers;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import edu.uchicago.cs.hao.texdojo.bibeditor.editors.BibEditor;
import edu.uchicago.cs.hao.texdojo.bibeditor.external.arxiv.ArxivArticle;
import edu.uchicago.cs.hao.texdojo.bibeditor.external.arxiv.ArxivService;
import edu.uchicago.cs.hao.texdojo.bibeditor.external.arxiv.DefaultArxivService;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibEntry;
import edu.uchicago.cs.hao.texdojo.bibeditor.filemodel.BibProp;

/**
 * Generate entries from ArXiv Libraries
 * 
 * @author harper
 *
 */
public class ArxivEntryHandler extends BibHandler {

	static final Pattern ARTICLE_ID = Pattern.compile("\\d+\\.\\d+");

	@Override
	protected Object executeWithEditor(ExecutionEvent event, BibEditor editor) throws ExecutionException {

		InputDialog dlg = new InputDialog(Display.getCurrent().getActiveShell(), "", "ArXiv article id", "", null);
		if (dlg.open() == Window.OK) {
			String articleId = dlg.getValue();
			if (!ARTICLE_ID.matcher(articleId).matches()) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Invalid Article Id",
						"Not a valid article id. Please check your input.");
				return null;
			}
			ArxivService service = new DefaultArxivService();
			try {

				ArxivArticle article = service.getArticle(articleId);

				BibEntry newEntry = new BibEntry();
				newEntry.setId(article.getId());
				newEntry.setType("article");
				newEntry.setPreserveCase(true);
				newEntry.addProperty(new BibProp(BibProp.YEAR, article.getYear()));
				newEntry.addProperty(new BibProp(BibProp.TITLE, article.getTitle()));
				newEntry.addProperty(new BibProp(BibProp.AUTHOR, String.join(",", article.getAuthor())));
				if (!StringUtils.isEmpty(article.getLink())) {
					newEntry.addProperty(new BibProp("url", article.getLink()));
				}
				if (!StringUtils.isEmpty(article.getDoi())) {
					newEntry.addProperty(new BibProp("doi", article.getDoi()));
				}
				if (!StringUtils.isEmpty(article.getJournal())) {
					newEntry.addProperty(new BibProp("journaltitle", article.getJournal()));
				}
				if (!StringUtils.isEmpty(article.getSummary())) {
					newEntry.addProperty(new BibProp("abstract", article.getSummary()));
				}

				editor.getUi().getModel().addEntry(newEntry);
				MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Entry added",
						"BibTex entry has been added to list.");
			} catch (Exception e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Failed to fetch article",
						"Error occurred when trying to fetch article information. Please try again later.");
				LoggerFactory.getLogger(getClass()).error("ArxivService error", e);
				return null;
			}

		}

		return null;
	}

}
