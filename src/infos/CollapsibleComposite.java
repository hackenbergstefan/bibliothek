package infos;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import util.CollapseButtonListener;

public class CollapsibleComposite extends Composite {
	public Button btnCollapse;
	public Composite content;
	public StyledText txtTitle;
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CollapsibleComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		txtTitle = new StyledText(this, SWT.BORDER);
		final GridData gd_txtTitle = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		txtTitle.setLayoutData(gd_txtTitle);
		
		btnCollapse = new Button(this, SWT.ARROW | SWT.UP);
		btnCollapse.addListener(SWT.Selection, new CollapseButtonListener(this, gd_txtTitle));
		
		content = new Composite(this, SWT.NONE);
		content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	

}
