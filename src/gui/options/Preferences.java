package gui.options;

import java.io.IOException;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;

public class Preferences {
	private static PreferenceStore prefs;
	
	public static PreferenceStore getPrefs(){
		if(prefs == null){
			prefs = new PreferenceStore("settings.txt");
		    try {
		    	prefs.load();
		    } catch (IOException e) {
		      // Ignore
		    }

		    prefs.setDefault("db.servername", "127.0.0.1");
		    prefs.setDefault("db.portnumber", "1521");
		    prefs.setDefault("db.sid", "xe");
		    prefs.setDefault("db.username", "schulbib");
		    prefs.setDefault("db.password", "oracle");
		    prefs.setDefault("log.file", "log.txt");
		    prefs.setDefault("log.errfile", "errlog.txt");
		    prefs.setDefault("inventur.file", "");
		}
		return prefs;
	}
	
	public void run(){
		// Create the preference manager
	    PreferenceManager mgr = new PreferenceManager();

	    // Create the nodes
	    mgr.addToRoot(new PreferenceNode("color", new ColorPrefPage()));
	    mgr.addToRoot(new PreferenceNode("db", new DbPrefPage()));
	    mgr.addToRoot(new PreferenceNode("log", new LogPrefPage()));
	    
	    // Create the preferences dialog
	    PreferenceDialog dlg = new PreferenceDialog(null, mgr);
	    dlg.setPreferenceStore(getPrefs());
	    
	 // Open the dialog
	    dlg.open();
	    
	    try{
	    	getPrefs().save();
	    }catch(IOException ex){}
	}

}
