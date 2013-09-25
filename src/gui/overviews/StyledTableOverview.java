package gui.overviews;

import gui.tableviews.StyledTableView;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import util.TitleDialog;

public class StyledTableOverview extends TitleDialog {
	private StyledTableView view;
	private Object[] input;
	private String message;
	
	public static void getDialog(Shell parentShell, Object[] input, String message){
		StyledTableOverview view = new StyledTableOverview(parentShell, input, message);
		view.open();
	}
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	private StyledTableOverview(Shell parentShell, Object[] input, String message) {
		super(parentShell);
		this.input = input;
		this.message = message;
		setShellStyle(SWT.TITLE | SWT.APPLICATION_MODAL);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("\u00DCbersicht");
		setMessage(message);
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		view = new StyledTableView(container, SWT.NONE, input);

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
	}

}
