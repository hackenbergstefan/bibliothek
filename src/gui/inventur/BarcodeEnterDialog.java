package gui.inventur;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class BarcodeEnterDialog extends Dialog {
	private Text txtcode;
	private String txt = "";
	
	public static String getDialog(Shell parentShell){
		BarcodeEnterDialog d = new BarcodeEnterDialog(parentShell);
		d.open();
		if(d.getReturnCode() == CANCEL) return "";
		return d.txt;
	}

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	private BarcodeEnterDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
		setBlockOnOpen(true);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;
		
		CLabel lblGebenSieHier = new CLabel(container, SWT.NONE);
		lblGebenSieHier.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblGebenSieHier.setText("Scannen Sie hier den Barcode des n\u00E4chsten Mediums.");
		
		Label lblBarcode = new Label(container, SWT.NONE);
		lblBarcode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBarcode.setText("Barcode:");
		
		txtcode = new Text(container, SWT.BORDER);
		txtcode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				txt = txtcode.getText();
				close();
			}
		});
		txtcode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		txtcode.forceFocus();
		container.setTabList(new Control[]{txtcode, lblGebenSieHier});

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.CANCEL_ID, "New button", false);
		button.setText("Zur\u00FCck");
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(400, 200);
	}

}
