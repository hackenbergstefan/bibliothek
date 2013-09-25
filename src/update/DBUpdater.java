package update;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import db.DBManager;

public class DBUpdater {
	private static final String cmd = "cmd /C start /wait update.bat %s";
	private static final File update_dir = new File("dbUpdates");
	private static final FilenameFilter dmp_filter = new FilenameFilter() {
		
		@Override
		public boolean accept(File dir, String name) {
			if(name.toLowerCase().endsWith(".dmp")) return true;
			return false;
		}
	};
	
	private static final DBUpdater dbUpdater = new DBUpdater();
	
	private Shell currentShell;
	
	private DBUpdater(){	
	}
	
	public static void checkForDBUpdates(Shell currentShell){
		dbUpdater.currentShell = currentShell;

		if(!update_dir.exists()) return;
		File[] list_dmps = update_dir.listFiles(dmp_filter);
		
		if(list_dmps.length == 1){
			File upd = list_dmps[0];
			boolean d = MessageDialog.openConfirm(currentShell, "Update installieren?", "Möchten Sie das Datenbank-Update\n\n"+upd.getName()+"\n\ninstallieren?");
			if(d){
				DBManager.getIt().disconnect();
				dbUpdater.installUpdate(upd);
				DBManager.getIt().connect();
			}
		}
	}
	
	
	private void installUpdate(File upd){
		Runtime rt = Runtime.getRuntime();
		try {
			Process p = rt.exec(String.format(cmd, upd.getName()),null,update_dir);
			p.waitFor();
			MessageDialog.openInformation(currentShell, "Erfolg!", "Das Datenbank-Update\n\n"+upd.getName()+"\n\nkonnte erfolgreich installiert werden!");
			upd.renameTo(new File(upd.getAbsolutePath()+".bak"));
		} catch (Exception e) {
			MessageDialog.openError(currentShell, "Fehler!", "Leider konnte das Update nicht installiert werden! Folgender Fehler ist aufgetreten:\n"+e.getMessage());
		} 
	}
}
