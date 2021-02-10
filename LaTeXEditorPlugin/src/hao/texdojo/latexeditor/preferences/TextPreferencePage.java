package hao.texdojo.latexeditor.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import hao.texdojo.latexeditor.Activator;

public class TextPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public TextPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Text Editor Preference");
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_LINE_ALIGN, "Align new line with previous line",
				getFieldEditorParent()));

		IntegerFieldEditor autoWrapEditor = new IntegerFieldEditor(PreferenceConstants.P_LINE_WRAP,
				"Line auto-wrap size (-1 to disable the feature)", getFieldEditorParent());
		autoWrapEditor.setValidRange(-1, Integer.MAX_VALUE);
		addField(autoWrapEditor);

	}

}
