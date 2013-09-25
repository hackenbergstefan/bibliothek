package gui.views;

import gui.overviews.BuecherOverview;
import gui.tableviews.BuecherTableView;

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

public class BuecherView extends TitleAreaDialog {
	public static final String BTNCLOSE_LABEL_NORMAL = "Schlieﬂen";
	public static final String BTNCLOSE_LABEL_EDIT = "Abbrechen";
	public static final String BTNEDIT_LABEL_NORMAL = "Medium bearbeiten";
	public static final String BTNEDIT_LABEL_EDIT = "Speichern";
	
	public BuecherTableView buecherTableView;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public BuecherView(Shell parentShell) {
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceManager.getImage(BuecherView.class, "/gui/buecher/book.png"));
		setTitle("Medien\u00FCbersicht");
		setMessage("\u00DCbersicht \u00FCber alle Medien.");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		GridLayout gl_container = new GridLayout(1, false);
		container.setLayout(gl_container);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
		
		ToolItem toolItem = new ToolItem(toolBar, SWT.NONE);
		toolItem.setImage(SWTResourceManager.getImage(BuecherView.class, "/icons/new.png"));
		toolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BuecherOverview over = new BuecherOverview(getParentShell(), null);
				over.open();
				System.out.println(over.getReturnCode()+"\t"+SWT.OK);
				if(over.getReturnCode() == TitleAreaDialog.OK){
					buecherTableView.updateTable();
					buecherTableView.selectBuch(over.getBuch());
				}
			}
		});
		
		buecherTableView = new BuecherTableView(container, SWT.NONE);
		buecherTableView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		return area;
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CLOSE_ID, "Schlieﬂen",true);
	}
	
	/*
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(859, 757);
	}
}
