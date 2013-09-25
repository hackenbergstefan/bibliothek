package gui.views;

import gui.overviews.AusleiheOverview;
import gui.tableviews.AusleihenTableView;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class AusleihenView extends TitleAreaDialog {
	private AusleihenTableView view;
	private AusleihenTableView ausleihenTableView;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AusleihenView(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Hier erhalten Sie verschiedene \u00DCbersichten \u00FCber alle verliehenen B\u00FCcher.");
		setTitle("Ausleihen\u00FCbersicht");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AusleiheOverview a = new AusleiheOverview(getParentShell(), null);
				a.open();
				if(a.getReturnCode() == TitleAreaDialog.OK)
					ausleihenTableView.updateTable();
			}
		});
		tltmNewItem.setToolTipText("Neue Ausleihe");
		tltmNewItem.setImage(SWTResourceManager.getImage(AusleihenView.class, "/icons/new.png"));
		
		ausleihenTableView = new AusleihenTableView(container, SWT.NONE);
		ausleihenTableView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		

		return area;
	}
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Schlie\u00DFen",
				true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(795, 582);
	}

}
