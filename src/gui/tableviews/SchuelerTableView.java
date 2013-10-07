package gui.tableviews;

import gui.overviews.SchuelerOverview;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;

import model.Schueler;

import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import actions.StartSchuelerOverview;

import util.FontUtil;
import util.WaitDialog;

public class SchuelerTableView extends Composite {
	public Text txtVorname;
	public Text txtNachname;
	public Text txtKlasse;
	public Table table;
	public TableViewer tableViewer;

	private SchuelerFilter filter = new SchuelerFilter();
	private Button btnNeueSuche;
	
	private SchuelerColumnViewerSorter comparator = new SchuelerColumnViewerSorter();

	private TableColumn tblclmnId;
	private TableColumn tblclmnVorname;
	private TableViewerColumn tableViewerColumn_1;
	private TableColumn tblclmnNachname;
	private TableViewerColumn tableViewerColumn_2;
	private TableColumn tblclmnKlasse;
	private TableViewerColumn tableViewerColumn_3;
	private Label lblVorname;
	private Label lblNachname;
	private Label lblKlasse;
	
	private Schueler schueler;
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	/**
	 * Open overview or fire event only on table-doubleclick 
	 */
	public boolean openOverviewOnDoubleClick = true; 
	private Label lblId;
	private Text txtId;
	private TableColumn tblclmnInfo;
	private TableViewerColumn tableViewerColumn_4;
	private Composite composite;
	private ToolBar toolBar;
	private ToolItem toolItem;
	private Label label;
	
	private TableColumn tblclmnAnmerkungen;
	private TableViewerColumn tableViewerColumn_5;
	
	private final WritableList data = new WritableList();
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SchuelerTableView(Composite parent, int style) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		
		Group grpEigenschaften = new Group(this, SWT.NONE);
		GridData gd_grpEigenschaften = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_grpEigenschaften.widthHint = 610;
		grpEigenschaften.setLayoutData(gd_grpEigenschaften);
		grpEigenschaften.setText("Eigenschaften");
		grpEigenschaften.setLayout(new GridLayout(4, false));
		
		lblId = new Label(grpEigenschaften, SWT.NONE);
		lblId.setAlignment(SWT.RIGHT);
		lblId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblId.setText("ID:");
		
		txtId = new Text(grpEigenschaften, SWT.BORDER);
		txtId.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if(table.getItemCount() == 1){
					updateFilter();
					selectSchueler((Schueler) table.getItem(0).getData());
					openSchueler();
				}
			}
		});
		txtId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateFilter();
			}
		});
		GridData gd_txtId = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtId.widthHint = 126;
		txtId.setLayoutData(gd_txtId);
		
		
		new Label(grpEigenschaften, SWT.NONE);
		
		label = new Label(grpEigenschaften, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		label.setImage(SWTResourceManager.getImage(SchuelerTableView.class, "/icons/Sch\u00FCler_gro\u00DF.png"));
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 7));
		
		lblVorname = new Label(grpEigenschaften, SWT.NONE);
		lblVorname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblVorname.setText("Vorname:");
		
		txtVorname = new Text(grpEigenschaften, SWT.BORDER);
		txtVorname.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateFilter();
			}
		});
		txtVorname.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		GridData gd_txtVorname = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_txtVorname.widthHint = 376;
		txtVorname.setLayoutData(gd_txtVorname);
		
		lblNachname = new Label(grpEigenschaften, SWT.NONE);
		lblNachname.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNachname.setText("Nachname:");
		
		txtNachname = new Text(grpEigenschaften, SWT.BORDER);
		txtNachname.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateFilter();
			}
		});
		txtNachname.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		txtNachname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
		lblKlasse = new Label(grpEigenschaften, SWT.NONE);
		lblKlasse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblKlasse.setText("Klasse:");
		
		txtKlasse = new Text(grpEigenschaften, SWT.BORDER);
		txtKlasse.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateFilter();
			}
		});
		txtKlasse.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		GridData gd_txtKlasse = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtKlasse.widthHint = 112;
		txtKlasse.setLayoutData(gd_txtKlasse);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		
		btnNeueSuche = new Button(grpEigenschaften, SWT.NONE);
		btnNeueSuche.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearValues();
			}
		});
		btnNeueSuche.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		btnNeueSuche.setText("Neue Suche");
		
		Group grpSchueler = new Group(this, SWT.NONE);
		grpSchueler.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpSchueler.setText("Sch\u00FCler");
		grpSchueler.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		composite = new Composite(grpSchueler, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		
		toolItem = new ToolItem(toolBar, SWT.NONE);
		toolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateTable();
			}
		});
		toolItem.setImage(SWTResourceManager.getImage(SchuelerTableView.class, "/icons/refresh.png"));
		toolItem.setToolTipText("Tabelle aktualisieren.");
		
		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				openSchueler();
			}
		});
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				schuelerSelected(e);
			}
		});
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return ((Schueler)element).getId()+"";
			}
		});
		tblclmnId = tableViewerColumn.getColumn();
		tblclmnId.setWidth(25);
		tblclmnId.setText("ID");
		
		tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return ((Schueler)element).getVorname();
			}
		});
		tblclmnVorname = tableViewerColumn_1.getColumn();
		tblclmnVorname.setWidth(100);
		tblclmnVorname.setText("Vorname");
		
		tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_2.setLabelProvider(new StyledCellLabelProvider() {
			public void update(ViewerCell cell) {
				StyledString s = new StyledString();
				s.append(((Schueler)cell.getElement()).getNachname(), FontUtil.boldStyler());
				cell.setText(s.getString());
				cell.setStyleRanges(s.getStyleRanges());
				
				super.update(cell);
			};
		});
		tblclmnNachname = tableViewerColumn_2.getColumn();
		tblclmnNachname.setWidth(100);
		tblclmnNachname.setText("Nachname");
		
		tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return ((Schueler)element).getKlasse();
			}
		});
		tblclmnKlasse = tableViewerColumn_3.getColumn();
		tblclmnKlasse.setWidth(100);
		tblclmnKlasse.setText("Klasse");
		
		tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_4.setLabelProvider(new CellLabelProvider() {
			
			@Override
			public void update(ViewerCell cell) {
				Schueler s = (Schueler) cell.getElement();
				if(s.getInfos().size() > 0) cell.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
			}
		});
		tblclmnInfo = tableViewerColumn_4.getColumn();
		tblclmnInfo.setToolTipText("Stehen f\u00FCr diesen Sch\u00FCler Infos zur Verf\u00FCgung?");
		tblclmnInfo.setWidth(100);
		tblclmnInfo.setText("Info");
		
		tableViewerColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return ((Schueler)element).getAnmerkungen();
			}
		});
		tblclmnAnmerkungen = tableViewerColumn_5.getColumn();
		tblclmnAnmerkungen.setWidth(100);
		tblclmnAnmerkungen.setText("Anmerkungen");
		
		setProperties();
	}
	
	private void updateFilter(){
		filter.setSearchText(txtId.getText(), txtVorname.getText(), txtNachname.getText(), txtKlasse.getText());
		tableViewer.refresh();
		if(table.getItemCount() == 1){
			//setComponentsEnabled(false);
			//enterValues((Buch)tableViewer.getElementAt(0));
		}
	}
	
	public void updateTable(){
		Schueler.getAllSchueler(data);
	}
	
	private void setProperties(){
		tableViewer.setContentProvider(new ObservableListContentProvider());
		tableViewer.setInput(data);
		tableViewer.addFilter(filter);
		tableViewer.setComparator(comparator);
		
		tblclmnId.addSelectionListener(getSelectionAdapter(tblclmnId, 0));
		tblclmnVorname.addSelectionListener(getSelectionAdapter(tblclmnVorname, 1));
		tblclmnNachname.addSelectionListener(getSelectionAdapter(tblclmnNachname, 2));
		tblclmnKlasse.addSelectionListener(getSelectionAdapter(tblclmnKlasse, 3));
		
		updateTable();
		
		//initial sorting
		comparator.setColumn(3);
		tableViewer.getTable().setSortDirection(comparator.getDirection());
		tableViewer.refresh();
	}
	
	private void openSchueler(){
		if(!openOverviewOnDoubleClick) return;
		if(schueler!= null) new StartSchuelerOverview(schueler, getShell(), SchuelerTableView.this).run();
	}
	
	private void schuelerSelected(SelectionEvent e){
		enterValues((Schueler)table.getSelection()[0].getData());
		
		if(table.getSelectionCount() == 0) changes.firePropertyChange("schueler", schueler, schueler = null);
		else changes.firePropertyChange("schueler", schueler, schueler = (Schueler)table.getSelection()[0].getData());
	}
	
	
	public void enterValues(Schueler b){
		txtVorname.setText(b.getVorname());
		txtNachname.setText(b.getNachname());
		txtKlasse.setText(b.getKlasse());
		txtId.setText(""+b.getId());
		
		notifyListeners(SWT.Selection, new Event());
	}
	
	public void clearValues(){
		txtVorname.setText("");
		txtNachname.setText("");
		txtKlasse.setText("");
		txtId.setText("");
		updateFilter();
	}
	
	public Schueler getSchueler(){
		return schueler;
	}
	
	public void selectSchueler(final Schueler b){
		if(b != null && b.getId() != -1){
			if(data.contains(b)){
				selectSchueler(b);
			}else data.addListChangeListener(new IListChangeListener() {
				
				@Override
				public void handleListChange(ListChangeEvent ev) {
					if(data.contains(b)){
						selectSchuelerNow(b);
						data.removeListChangeListener(this);
					}
				}
			});
		}
	}
	
	private void selectSchuelerNow(Schueler b){
		changes.firePropertyChange("schueler", schueler, schueler = b);
		tableViewer.setSelection(new StructuredSelection(b),true);
		table.forceFocus();
		enterValues(b);
	}
	
	public String getVorname(){
		return txtVorname.getText();
	}
	
	public String getNachname(){
		return txtNachname.getText();
	}
	
	public String getKlasse(){
		return txtKlasse.getText();
	}
	

	public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }
	
	
	private SelectionAdapter getSelectionAdapter(final TableColumn column,
			final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				table.setSortColumn(column);
				tableViewer.getTable().setSortDirection(dir);
				tableViewer.refresh();
			}
		};
		return selectionAdapter;
	}
}
