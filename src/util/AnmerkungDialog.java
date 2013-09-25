package util;

import gui.overviews.BuecherOverview;
import gui.overviews.SchuelerOverview;
import model.Buch;
import model.Schueler;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

public class AnmerkungDialog extends MessageDialog {

	private AnmerkungDialog(Shell parentShell, String dialogTitle,
			Image dialogTitleImage, String dialogMessage, int dialogImageType,
			String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage,
				dialogImageType, dialogButtonLabels, defaultIndex);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public static void getDialog(Shell parentShell, Buch b) {
		if(b == null || b.getAnmerkungen() == null || b.getAnmerkungen().equals("")) return;
		MessageDialog d = new MessageDialog(parentShell, "Achtung", null, "Das von Ihnen gewählte Medium enthält die folgenden Anmerkungen:\n\n"+b.getAnmerkungen()+
				"\n\nKlicken Sie \"Ok\", falls diese auf dem aktuellen Stand sind. Klicken Sie \"Bearbeiten\", falls Sie aktualisiert werden müssen.", 
				MessageDialog.QUESTION, new String[]{"Ok", "Bearbeiten"}, 0);
		d.open();
		if(d.getReturnCode() == MessageDialog.CANCEL){
			BuecherOverview view = new BuecherOverview(parentShell, b);
			view.open();
		}
	}
	
	public static void getDialog(Shell parentShell, Schueler s) {
		if(s == null || s.getAnmerkungen() == null || s.getAnmerkungen().equals("")) return;
		MessageDialog d = new MessageDialog(parentShell, "Achtung", null, "Der von Ihnen gewählte Schüler enthält die folgenden Anmerkungen:\n\n"+s.getAnmerkungen()+
				"\n\nKlicken Sie \"Ok\", falls diese auf dem aktuellen Stand sind. Klicken Sie \"Bearbeiten\", falls Sie aktualisiert werden müssen.", 
				MessageDialog.QUESTION, new String[]{"Ok", "Bearbeiten"}, 0);
		d.open();
		if(d.getReturnCode() == MessageDialog.CANCEL){
			SchuelerOverview view = new SchuelerOverview(parentShell, s);
			view.open();
		}
	}

}
