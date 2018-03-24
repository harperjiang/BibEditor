package edu.uchicago.cs.hao.texdojo.latexeditor.editors.handlers;

import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_SPELLCHECKER;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_SPELLCHECKER_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceConstants.P_SPELLCHECKER_OPTION;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_SPELLCHECKER;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_SPELLCHECKER_EXE;
import static edu.uchicago.cs.hao.texdojo.latexeditor.preferences.PreferenceInitializer.DEFAULT_SPELLCHECKER_OPT;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;

import edu.uchicago.cs.hao.texdojo.latexeditor.Activator;
import edu.uchicago.cs.hao.texdojo.latexeditor.editors.text.ColorManager;
import edu.uchicago.cs.hao.texdojo.latexeditor.spellcheck.SpellChecker;
import edu.uchicago.cs.hao.texdojo.latexeditor.spellcheck.Suggestion;
import edu.uchicago.cs.hao.texdojo.latexeditor.spellcheck.aspell.ASpellChecker;

public class SpellCheckHandler extends TextHandler {

	@Override
	protected Object executeWithEditor(ExecutionEvent event) throws ExecutionException {
		Event trigger = (Event) event.getTrigger();
		ToolItem item = (ToolItem) trigger.widget;
		
		SourceViewer sourceViewer = (SourceViewer) editor.getInnerSourceViewer();
		
		if (item.getSelection()) {

			IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);

			String spellChecker = prefs.get(P_SPELLCHECKER, DEFAULT_SPELLCHECKER);
			String spellCheckerExe = prefs.get(P_SPELLCHECKER_EXE, DEFAULT_SPELLCHECKER_EXE);
			String spellCheckerOption = prefs.get(P_SPELLCHECKER_OPTION, DEFAULT_SPELLCHECKER_OPT);

			SpellChecker checker = null;
			switch (spellChecker) {
			case "aspell":
				checker = new ASpellChecker(spellCheckerExe, spellCheckerOption);
				break;
			default:
				break;
			}
			if (checker == null) {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "No spell checker is chosen",
						"Please select a spell checker in Preference");
			} else {
				IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
				// Check all lines

				int numLines = doc.getNumberOfLines();
					for (int i = 1; i <= numLines; i++) {
					try {
						IRegion lineInfo = doc.getLineInformation(i);
						String line = doc.get(lineInfo.getOffset(), lineInfo.getLength());
						List<Suggestion> suggestions = checker.check(line);

						TextPresentation region = new TextPresentation();

						for (Suggestion sug : suggestions) {
							// Create a Style indicating error words
							StyleRange style = new StyleRange(sug.getOffset() + lineInfo.getOffset(),
									sug.getOrigin().length(), ColorManager.get(ColorManager.BLACK),
									ColorManager.get(ColorManager.ORANGE));
							style.underline = true;
							style.underlineColor = ColorManager.get(ColorManager.RED);
							style.underlineStyle = SWT.UNDERLINE_ERROR;
							region.addStyleRange(style);
						}
						sourceViewer.changeTextPresentation(region, false);

					} catch (BadLocationException e) {
						// Silently fails
					}
				}
				sourceViewer.setRedraw(true);
			}
		} else {
			sourceViewer.refresh();
		}

		return null;
	}

}
