package gui.statistics;

import model.Statistic;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import util.TitleDialog;

public class StatisticView extends TitleDialog {
	private TabFolder tabFolder;


	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public StatisticView(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.SHELL_TRIM);
		setHelpAvailable(false);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Statistiken");
		setMessage("Hier sehen Sie eine statistische Auswertung.");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		tabFolder = new TabFolder(container, SWT.NONE);		
		addYearStatistics();

		return area;
	}
	
	private void addYearStatistics(){
		for(int y=Statistic.getCurrentSchuljahr(); y >= Statistic.YEAR_OF_START; y--){
			TabItem t = new TabItem(tabFolder, SWT.NONE);
			t.setText("Statistik "+y);
			YearStatistic s = new YearStatistic(tabFolder, SWT.NONE, y);
			t.setControl(s);
		}
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(792, 578);
	}
	
	@Override
	protected void initValues() {
		
		super.initValues();
	}
	

	public static void main(String[] args){
		final Display d = new Display();
		final Shell s = new Shell(d);
		final StatisticView v = new StatisticView(s);
		v.open();
		
		while(!s.isDisposed())
			if(!d.readAndDispatch())
				d.sleep();
	}
	
}
