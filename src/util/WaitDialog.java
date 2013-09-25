package util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class WaitDialog extends Dialog {
	private static WaitDialog wd;
	
	/**
	 * Shows WaitDialog and closes it after <code>closeAfter</code> has ended.
	 * @param parentShell
	 * @param closeAfter
	 */
	public static void show(Shell parentShell, Control caller, final Runnable closeAfter){
		if(wd == null) wd = new WaitDialog(parentShell);
		if(caller == null || caller.isVisible()) wd.open();
		Runnable run = new Runnable() {
			
			@Override
			public void run() {
				closeAfter.run();
				wd.close();
			}
		};
		BusyIndicator.showWhile(Display.getCurrent(), run);
//		Display.getCurrent().asyncExec(run);
	}
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	private WaitDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.BORDER);
		setBlockOnOpen(false);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setBackground(SWTResourceManager.getColor(255, 250, 250));
		Composite container = (Composite) super.createDialogArea(parent);
		container.setBackground(SWTResourceManager.getColor(255, 250, 250));
		container.setLayout(new GridLayout(1, false));
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblNewLabel.setBackground(SWTResourceManager.getColor(255, 250, 250));
		lblNewLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblNewLabel.setText("Einen Augenblick Geduld bitte...");

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		parent.setBackground(SWTResourceManager.getColor(255, 250, 250));
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(400, 100);
	}

}
