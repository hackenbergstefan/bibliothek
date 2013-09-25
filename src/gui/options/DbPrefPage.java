package gui.options;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.wb.swt.FieldLayoutPreferencePage;

public class DbPrefPage extends FieldLayoutPreferencePage {

	/**
	 * Create the preference page.
	 */
	public DbPrefPage() {
		setMessage("Datenbankeinstellungen");
		setDescription("");
		setTitle("Datenbank");
		noDefaultAndApplyButton();
	}

	@Override
	protected Control createPageContents(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1,false));
		
		// Create the field editors
		addField(new StringFieldEditor("db.servername", "Servername:", -1, c));
		addField(new StringFieldEditor("db.portnumber", "Portnummer:", -1, c));
		addField(new StringFieldEditor("db.sid", "SID:", -1, c));
		addField(new StringFieldEditor("db.username", "Username:", -1, c));
		addField(new StringFieldEditor("db.password", "Passwort:", -1, c));
		
		
		return c;
	}
	
	

}
