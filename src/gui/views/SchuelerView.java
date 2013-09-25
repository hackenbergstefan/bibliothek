package gui.views;

import gui.tableviews.SchuelerTableView;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class SchuelerView extends TitleAreaDialog {
	public static final String BTNCLOSE_LABEL_NORMAL = "Schließen";
	public static final String BTNCLOSE_LABEL_EDIT = "Abbrechen";
	public static final String BTNEDIT_LABEL_NORMAL = "Schüler bearbeiten";
	public static final String BTNEDIT_LABEL_EDIT = "Speichern";
	
	private SchuelerTableView schuelerTableView;
	
	private boolean editMode = false;
	private Composite composite_1;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SchuelerView(Shell parentShell) {
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
		setTitleImage(SWTResourceManager.getImage(SchuelerView.class, "/gui/schueler/schueler.gif"));
		setTitle("Sch\u00FCler\u00FCbersicht");
		setMessage("\u00DCbersicht \u00FCber alle Sch\u00FCler.");
		Composite area = (Composite) super.createDialogArea(parent);
		
		composite_1 = new Composite(area, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		Composite container = new Composite(composite_1, SWT.NONE);
		GridLayout gl_container = new GridLayout(1, false);
		container.setLayout(gl_container);
		
		schuelerTableView = new SchuelerTableView(container, SWT.NONE);
		schuelerTableView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		return area;
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CLOSE_ID, "Schließen",true);
	}
	
	/*
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(719, 615);
	}
}
