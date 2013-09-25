package gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

public class VerliehenLabel extends Composite {
	private int status = 0;
	private Label label;

	public VerliehenLabel(Composite parent, int style) {
		super(parent, SWT.BORDER);
		setLayout(new GridLayout(1, false));
		
		label = new Label(this, SWT.CENTER);
		label.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		label.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		label.setAlignment(SWT.CENTER);
		label.setText("New Label");
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
		switch(status){
		case 0: setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
			label.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
			label.setText("Verfügbar"); break;
		case 1: setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));
			label.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));
			label.setText("Verliehen"); break;
		case 2: setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
			label.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
			label.setText("Vorgemerkt"); break;
		}
	}
}
