package gui.selectors;

import gui.tableviews.BuecherTableView;
import model.Buch;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import util.TitleDialog;

public class BuecherSelector extends TitleDialog {	
	private BuecherTableView buchTableView;
	private Composite composite_1;
	
	private DataBindingContext dbc;
	
	private Buch buch;
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public BuecherSelector(Shell parentShell, Buch buch) {
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(SWT.MAX | SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		this.buch = buch;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceManager.getImage(BuecherSelector.class, "/icons/Buch.png"));
		setTitle("Medienauswahl");
		setMessage("Medium ausw\u00E4hlen.");
		Composite area = (Composite) super.createDialogArea(parent);
		
		composite_1 = new Composite(area, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		Composite container = new Composite(composite_1, SWT.NONE);
		GridLayout gl_container = new GridLayout(1, false);
		container.setLayout(gl_container);
		
		buchTableView = new BuecherTableView(container, SWT.NONE);
		buchTableView.table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				okPressed();
			}
		});
		buchTableView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		buchTableView.openOverviewOnDoubleClick = false;

		return area;
	}
	
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button btnAuswhlen = createButton(parent, IDialogConstants.OK_ID, "New button", true);
		btnAuswhlen.setText("Ausw\u00E4hlen");
		Button button = createButton(parent, IDialogConstants.CANCEL_ID, "Schlieﬂen",false);
		button.setText("Abbrechen");
		
		dbc = getdbc();
		
		buchTableView.selectBuch(buch);
	}
	
	/*
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(800, 800);
	}
	
	private DataBindingContext getdbc(){
		DataBindingContext dbc = new DataBindingContext();
		
		IObservableValue model = BeansObservables.observeValue(buchTableView, "buch");
		IObservableValue target = SWTObservables.observeEnabled(getButton(IDialogConstants.OK_ID));
		UpdateValueStrategy str = new UpdateValueStrategy();
		str.setConverter(new IConverter() {
			
			@Override
			public Object getToType() {
				return boolean.class;
			}
			
			@Override
			public Object getFromType() {
				return Buch.class;
			}
			
			@Override
			public Object convert(Object fromObject) {
				return fromObject != null;
			}
		});
		
		dbc.bindValue(target,model, null, str);
		
		
		model = BeansObservables.observeValue(buchTableView, "buch");
		target = PojoObservables.observeValue(this, "buch");
		dbc.bindValue(target, model);
		
		return dbc;
	}

	public Buch getBuch() {
		return buch;
	}
	
	public void setBuch(Buch buch){
		this.buch = buch;
	}
}
