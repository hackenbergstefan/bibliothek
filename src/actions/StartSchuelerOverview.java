package actions;

import gui.MainApplication;
import gui.overviews.AusleiheOverview;
import gui.overviews.BuecherOverview;
import gui.overviews.SchuelerOverview;
import gui.tableviews.AusleihenTableView;
import gui.tableviews.BuecherTableView;
import gui.tableviews.SchuelerTableView;
import model.Ausleihe;
import model.Buch;
import model.Schueler;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;

public class StartSchuelerOverview extends Action{
	private Schueler s;
	private Shell shell;
	private SchuelerTableView tableViewer;
	
	public StartSchuelerOverview(Schueler s, Shell shell, SchuelerTableView tableViewer){
		this.shell=shell;
		this.s=s;
		this.tableViewer = tableViewer;
	}
	
	
	public void run(){
		SchuelerOverview over = new SchuelerOverview(shell, s);
		over.open();
		if(over.getReturnCode() == SchuelerOverview.CANCEL) return;
		
		MainApplication.MAIN.selectSchueler(over.getSchueler());
	}
}
