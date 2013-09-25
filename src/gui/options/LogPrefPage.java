package gui.options;

import org.eclipse.jface.preference.FieldEditorPreferencePage;

import util.FileFieldEditor2;

public class LogPrefPage extends FieldEditorPreferencePage {

	/**
	 * Create the preference page.
	 */
	public LogPrefPage() {
		setMessage("Logging-Einstellungen");
		setDescription("");
		setTitle("Logging");
		noDefaultAndApplyButton();
	}
	
	@Override
	protected void createFieldEditors() {
		FileFieldEditor2 fileeditor = new FileFieldEditor2("log.file", "Logging-Datei", getFieldEditorParent());
		addField(fileeditor);
		fileeditor = new FileFieldEditor2("log.errfile", "Error-Logging-Datei", getFieldEditorParent());
		addField(fileeditor);
	}

	
	

}
