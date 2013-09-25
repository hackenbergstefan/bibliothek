package actions;

import gui.MainApplication;
import gui.overviews.AusleiheOverview;
import model.Ausleihe;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;

public class NewAusleiheAction extends Action{
	private Ausleihe a;
	private Shell shell;
	
	public NewAusleiheAction(Ausleihe a, Shell shell){
		this.shell=shell;
		this.a=a;
	}
	
	
	public void run(){
		AusleiheOverview view = new AusleiheOverview(shell, a);
		view.open();
		
		if(view.getReturnCode() == TitleAreaDialog.OK)
			MainApplication.MAIN.selectAusleihe(view.getAusleihe());
	}
}
