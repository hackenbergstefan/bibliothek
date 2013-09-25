package actions;

import gui.MainApplication;
import gui.overviews.AusleiheOverview;
import gui.overviews.BuecherOverview;
import gui.tableviews.AusleihenTableView;
import gui.tableviews.BuecherTableView;
import model.Ausleihe;
import model.Buch;
import model.Schueler;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;

public class StartBuchOverview extends Action{
	private Buch b;
	private Shell shell;
	private BuecherTableView tableViewer;
	
	public StartBuchOverview(Buch b, Shell shell, BuecherTableView tableViewer){
		this.shell=shell;
		this.b=b;
		this.tableViewer = tableViewer;
	}
	
	
	public void run(){
		BuecherOverview over = new BuecherOverview(shell, b);
		over.open();
		if(over.getReturnCode() == BuecherOverview.CANCEL) return;
		
		MainApplication.MAIN.selectMedium(over.getBuch());
	}
}
