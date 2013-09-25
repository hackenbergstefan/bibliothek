package gui.inventur;

import gui.overviews.BuecherOverview;
import gui.tableviews.StyledTableView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import model.Buch;
import model.Inventur;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import util.FontUtil;
import util.TitleDialog;
import util.WaitDialog;

public class InventurView extends TitleDialog {
	private Text txtFile;
	private StyledTableView styledTableView;
	
	private Inventur inventur = Inventur.getCurrent();
	
	private PropertyChangeListener listener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateTable();
		}
	};

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public InventurView(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.SHELL_TRIM | SWT.APPLICATION_MODAL);
		setHelpAvailable(false);
		setBlockOnOpen(false);
		
		inventur.addPropertyChangeListener(listener);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Inventur\u00FCbersicht");
		setTitle("Inventur");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group grpAktuelleInventur = new Group(container, SWT.NONE);
		grpAktuelleInventur.setLayout(new GridLayout(2, false));
		grpAktuelleInventur.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpAktuelleInventur.setText("Aktuelle Inventur");
		
		Label lblDatei = new Label(grpAktuelleInventur, SWT.NONE);
		GridData gd_lblDatei = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblDatei.widthHint = 41;
		lblDatei.setLayoutData(gd_lblDatei);
		lblDatei.setText("Datei:");
		
		txtFile = new Text(grpAktuelleInventur, SWT.BORDER);
		txtFile.setEditable(false);
		txtFile.setText(inventur.getPath().getAbsolutePath());
		GridData gd_txtFile = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_txtFile.widthHint = 320;
		txtFile.setLayoutData(gd_txtFile);
		
		Group grpInventur = new Group(container, SWT.NONE);
		grpInventur.setLayout(new GridLayout(1, false));
		grpInventur.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpInventur.setText("Inventur");
		grpInventur.setBounds(0, 0, 70, 82);
		
		styledTableView = new StyledTableView(grpInventur, SWT.YES, (Object[]) null);
		styledTableView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
				
		Composite composite_2 = new Composite(grpInventur, SWT.NONE);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		composite_2.setLayout(new GridLayout(3, false));
		composite_2.setBounds(0, 0, 64, 64);
		
		ToolBar toolBar = new ToolBar(composite_2, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		ToolItem tltmNeuesMediumHinzufgen = new ToolItem(toolBar, SWT.NONE);
		tltmNeuesMediumHinzufgen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String isbn = "";
				do{
					isbn = BarcodeEnterDialog.getDialog(getShell());
					if(isbn.length() == 0) return;
					
					//look if the book is already stocked 
					Buch b = inventur.searchInStocked(isbn);
					
					if(b != null) inventur.addBuch(b);
					else{
						//if not look in the db
						Vector<Buch> vec = Buch.getFromIsbn(isbn);
						if(vec.size() == 0){
							inventur.addBuch(BuecherOverview.getOverview(getShell(), isbn, false));
						}else{
							inventur.addBuch(vec.get(0));
						}
					}
				}while(isbn.length()!=0);
				
			}
		});
		tltmNeuesMediumHinzufgen.setImage(SWTResourceManager.getImage(InventurView.class, "/icons/new.png"));
		tltmNeuesMediumHinzufgen.setText("Neues Medium aufnehmen");
		new Label(composite_2, SWT.NONE);
		
		ToolBar toolBar_1 = new ToolBar(composite_2, SWT.FLAT | SWT.RIGHT);
		toolBar_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		
		ToolItem tltmAktuellesMediumVon = new ToolItem(toolBar_1, SWT.NONE);
		tltmAktuellesMediumVon.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean ret = MessageDialog.openConfirm(getShell(), "Bestätigen", "Möchten Sie das ausgewählte Medium wirklich von der Liste entfernen?");
				if(!ret) return;
				inventur.removeIndex(styledTableView.table.getSelectionIndex());
			}
		});
		tltmAktuellesMediumVon.setImage(SWTResourceManager.getImage(InventurView.class, "/icons/minus.png"));
		tltmAktuellesMediumVon.setText("Markiertes Medium von der Inventur streichen");
		
		Composite composite = new Composite(grpInventur, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Button btnInventurAuswerten = new Button(composite, SWT.NONE);
		btnInventurAuswerten.setFont(FontUtil.boldFont);
		btnInventurAuswerten.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				WaitDialog.show(getShell(), null, new Runnable() {
					
					@Override
					public void run() {
						inventur.auswerten();
						System.out
								.println("InventurView.createDialogArea(...).new SelectionAdapter() {...}.widgetSelected(...).new Runnable() {...}.run()");
						new InventurAuswertungView(getShell(),InventurView.this).open();
						System.out.println("InventurView.updateTable()");
					}
				});
			}
		});
		btnInventurAuswerten.setText("Inventur auswerten");

		updateTable();
		
		
		return area;
	}	
	
	private void updateTable(){
		Vector<Buch> vec = inventur.getCurrentlyStocked();
		styledTableView.tableViewer.setInput(vec);
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
	}
	
	@Override
	public boolean close() {
		inventur.removePropertyChangeListener(listener);
		return super.close();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(882, 668);
	}
}
