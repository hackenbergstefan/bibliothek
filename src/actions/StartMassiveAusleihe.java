package actions;

import gui.MainApplication;
import gui.overviews.AusleiheOverview;
import gui.tableviews.AusleihenTableView;
import gui.views.MassiveAusleihe;
import model.Ausleihe;
import model.Schueler;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;

public class StartMassiveAusleihe extends Action{
	private Ausleihe a;
	private Shell shell;
	private AusleihenTableView tableViewer;
	
	public StartMassiveAusleihe(Ausleihe a, Shell shell, AusleihenTableView tableViewer){
		this.shell=shell;
		this.a=a;
		this.tableViewer = tableViewer;
	}
	
	
	public void run(){
		MassiveAusleihe view = new MassiveAusleihe(shell, a);
		view.open();
		
		if(view.getReturnCode() == AusleiheOverview.CANCEL) return;
		
		MainApplication.MAIN.selectAusleihe(view.getAusleihe());
	}
}
