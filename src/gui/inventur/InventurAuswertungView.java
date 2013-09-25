package gui.inventur;

import gui.MainApplication;
import gui.tableviews.StyledTableView;
import model.Inventur;
import model.Inventur.UpdateObject;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import util.TitleDialog;
import util.WaitDialog;

public class InventurAuswertungView extends TitleDialog {
	private Text txtFile;
	private StyledTableView tableNotInDB;
	
	private Inventur inventur = Inventur.getCurrent();
	private StyledTableView tableNotInInventur;
	private Label lblToAdd;
	private Label lblToRemove;
	private Label lblToUpdate;
	private StyledTableView tableToUpdate;
	private TabFolder tabFolder;
	private Composite area;
	private Composite composite;
	private InventurView parent;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public InventurAuswertungView(Shell parentShell, InventurView parent) {
		super(parentShell);
		setShellStyle(SWT.SHELL_TRIM | SWT.APPLICATION_MODAL);
		setHelpAvailable(false);
		setBlockOnOpen(false);
		this.parent = parent;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Hier sehen Sie die Auswertung der aktuellen Inventur.");
		setTitle("Inventurauswertung");
		area = (Composite) super.createDialogArea(parent);
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
		
		tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmHinzufgenlschen = new TabItem(tabFolder, SWT.NONE);
		tbtmHinzufgenlschen.setText("Hinzuf\u00FCgen/L\u00F6schen");
		
		composite = new Composite(tabFolder, SWT.NONE);
		tbtmHinzufgenlschen.setControl(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Group grpInventur = new Group(composite, SWT.NONE);
		grpInventur.setToolTipText("Diese Medien befinden sich nicht in dem aktuellen Datenbank-Bestand und m\u00FCssen hinzugef\u00FCgt werden.");
		grpInventur.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		grpInventur.setLayout(new GridLayout(1, false));
		grpInventur.setText("Hinzuzuf\u00FCgende Medien");
		
		tableNotInDB = new StyledTableView(grpInventur, SWT.YES, (Object[]) null);
		tableNotInDB.table.setBackground(SWTResourceManager.getColor(240, 255, 240));
		tableNotInDB.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		lblToAdd = new Label(grpInventur, SWT.NONE);
		lblToAdd.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblToAdd.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
		lblToAdd.setText("New Label");
		

		tableNotInDB.tableViewer.setInput(inventur.getAusgewertetAdd());
		
		lblToAdd.setText("Es werden "+inventur.getAusgewertetAdd().size()+" Medien hinzugefügt.");
		
		Group grpInDB = new Group(composite, SWT.NONE);
		grpInDB.setToolTipText("Diese Medien befinden sich noch im Datenbank-Bestand, tauchen aber nicht in der aktuellen Inventur auf.");
		grpInDB.setText("Zu l\u00F6schende Medien");
		grpInDB.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		grpInDB.setLayout(new GridLayout(1, false));
		
		tableNotInInventur = new StyledTableView(grpInDB, SWT.YES, (Object[]) null);
		tableNotInInventur.table.setBackground(SWTResourceManager.getColor(255, 228, 225));
		tableNotInInventur.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		lblToRemove = new Label(grpInDB, SWT.NONE);
		lblToRemove.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblToRemove.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
		lblToRemove.setText("New Label");
		tableNotInInventur.tableViewer.setInput(inventur.getAusgewertetRemove());
		lblToRemove.setText("Es werden "+inventur.getAusgewertetRemove().size()+" Medien gelöscht.");
		
		TabItem tbtmUpdates = new TabItem(tabFolder, SWT.NONE);
		tbtmUpdates.setText("Updates");
		
		composite = new Composite(tabFolder, SWT.NONE);
		tbtmUpdates.setControl(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Group grpZuAktualisierendeMedien = new Group(composite, SWT.NONE);
		grpZuAktualisierendeMedien.setToolTipText("Die Medien der Spalte \"Alte Daten\" werden durch die Werte in der Spalte \"Neue Daten\" ersetzt.");
		grpZuAktualisierendeMedien.setText("Zu aktualisierende Medien");
		grpZuAktualisierendeMedien.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		grpZuAktualisierendeMedien.setLayout(new GridLayout(1, false));
		
		tableToUpdate = new UpdatesTableView(grpZuAktualisierendeMedien, SWT.YES);
		tableToUpdate.table.setBackground(SWTResourceManager.getColor(240, 248, 255));
		tableToUpdate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		lblToUpdate = new Label(grpZuAktualisierendeMedien, SWT.NONE);
		lblToUpdate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblToUpdate.setText("New Label");
		lblToUpdate.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_BLUE));
		lblToUpdate.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		
		tableToUpdate.tableViewer.setInput(inventur.getUpdates());
		lblToUpdate.setText("Es werden "+inventur.getUpdates().size()+" Medien aktualisiert.");
		
		Group grpnderungenSpeichern = new Group(container, SWT.NONE);
		grpnderungenSpeichern.setLayout(new GridLayout(1, false));
		grpnderungenSpeichern.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpnderungenSpeichern.setText("\u00C4nderungen speichern");
		
		Label lblKlickenSieHier = new Label(grpnderungenSpeichern, SWT.NONE);
		lblKlickenSieHier.setText("Klicken Sie hier, um die oben gezeigten \u00C4nderungen zu speichern.");
		
		Label lblAchtungDiesLsst = new Label(grpnderungenSpeichern, SWT.NONE);
		lblAchtungDiesLsst.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		lblAchtungDiesLsst.setText("Achtung: Dies l\u00E4sst sich nicht wieder r\u00FCckg\u00E4ngig machen!");
		
		Button btnnderungenbernehmen = new Button(grpnderungenSpeichern, SWT.NONE);
		btnnderungenbernehmen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean ret = MessageDialog.openConfirm(getShell(), "Bestätigen", "Möchten Sie wirklich alle angezeigten Änderungen übernehmen?\nDies kann nicht wieder rückgängig gemacht werden!");
				if(!ret) return;
				WaitDialog.show(getShell(), null, new Runnable() {
					
					@Override
					public void run() {
						close();
						InventurAuswertungView.this.parent.close();
						inventur.commit();
						MainApplication.MAIN.updateAllTables();
						MainApplication.MAIN.selectMediumTab();
					}
				});
			}
		});
		btnnderungenbernehmen.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		btnnderungenbernehmen.setText("\u00C4nderungen \u00FCbernehmen.");

		return area;
	}
		
	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, "Zur\u00FCck", false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(882, 668);
	}
	
	private class UpdatesTableView extends StyledTableView{
		private StyledCellLabelProvider labelProvider = new StyledCellLabelProvider(){
			public void update(org.eclipse.jface.viewers.ViewerCell cell) {
				if(cell.getElement() instanceof UpdateObject){
					StyledString s = null;
					if(cell.getColumnIndex() == 0){
						s = ((UpdateObject)cell.getElement()).to.toStyledString();
					}else{
						s = ((UpdateObject)cell.getElement()).from.toStyledString();
					}
					
					cell.setText(s.getString());
					cell.setStyleRanges(s.getStyleRanges());
					
				}
				
				super.update(cell);
			};
		}; 
		
		public UpdatesTableView(Composite parent, int style) {
			super(parent,style);
			colLay = new TableColumnLayout();
			setLayout(colLay);
			tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
			table = tableViewer.getTable();
			
			
			TableViewerColumn dataColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			TableColumn tblclmnDataColumn = dataColumn.getColumn();
			colLay.setColumnData(tblclmnDataColumn, new ColumnWeightData(50));
			tblclmnDataColumn.setText("Neue Werte");
			
			dataColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			tblclmnDataColumn = dataColumn.getColumn();
			colLay.setColumnData(tblclmnDataColumn, new ColumnWeightData(50));
			tblclmnDataColumn.setText("Alte Werte");
			
			tableViewer.setLabelProvider(labelProvider);

			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			
			tableViewer.setContentProvider(new ArrayContentProvider());
			tableViewer.setInput((Object[])null);
		}
	}
}
