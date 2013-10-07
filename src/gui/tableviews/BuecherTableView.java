package gui.tableviews;

import gui.overviews.BuecherOverview;
import gui.validators.PercentageConverter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.WriteAbortedException;
import java.util.Vector;

import model.Buch;
import model.BuchConstants;
import model.Kategorie;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.jface.tablecomboviewer.TableComboViewer;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
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
import org.eclipse.swt.widgets.Combo;
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

import actions.StartBuchOverview;

import util.FontUtil;
import util.WaitDialog;
import util.WritableList2;

public class BuecherTableView extends Composite {
	public Text txtISBN;
	public Text txtAutor;
	public Text txtTitel;
	public Text txtJahr;
	public Combo comboArt;
	public Combo comboStufe;
	public Table table;
	private TableColumn tblclmnVerliehen;
	private TableColumn tblclmnArt;
	private TableColumn tblclmnStufe;
	private TableColumn tblclmnJahr;
	private TableColumn tblclmnTitel;
	private TableColumn tblclmnAutor;
	private TableColumn tblclmnIsbn;
	private TableColumn tblclmnId;
	private TableViewer tableViewer;

	private BuchFilter filter = new BuchFilter();
	private Button btnNeueSuche;
	
	private BuchColumnViewerSorter comparator = new BuchColumnViewerSorter();
	private Label lblStichwrter;
	private TableColumn tblclmnStichwrter;
	private TableViewerColumn tableViewerColumn_8;
	private Text txtStichwort;
	
	private Buch buch;
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	
	/**
	 * Open overview or fire event only on table-doubleclick 
	 */
	public boolean openOverviewOnDoubleClick = true; 
	private TableColumn tblclmnKategorie;
	private TableViewerColumn tableViewerColumn_9;
	private Label lblKategorie;
	private TableCombo comboKat;
	private TableComboViewer comboKatViewer;
	private Label lblStatus;
	private Combo comboStatus;
	private Composite composite;
	private ToolBar toolBar;
	private ToolItem btnTableUpdate;
	
//	private TableColumnLayout colLay;
	private Label label;
	
	private final WritableList2 data = new WritableList2();
	
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public BuecherTableView(Composite parent, int style) {
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
		grpEigenschaften.setLayout(new GridLayout(11, false));
		
		Label lblIsbn = new Label(grpEigenschaften, SWT.NONE);
		lblIsbn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIsbn.setText("ISBN");
		
		txtISBN = new Text(grpEigenschaften, SWT.BORDER);
		txtISBN.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateFilter();
			}
		});
		txtISBN.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		txtISBN.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 9, 1));
		
		label = new Label(grpEigenschaften, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		label.setImage(SWTResourceManager.getImage(BuecherTableView.class, "/icons/Buch_gro\u00DF.png"));
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 8));
		
		Label lblAutor = new Label(grpEigenschaften, SWT.NONE);
		lblAutor.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAutor.setText("Autor");
		
		txtAutor = new Text(grpEigenschaften, SWT.BORDER);
		txtAutor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateFilter();
			}
		});
		txtAutor.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		txtAutor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 9, 1));
		
		Label lblTitel = new Label(grpEigenschaften, SWT.NONE);
		lblTitel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTitel.setText("Titel");
		
		txtTitel = new Text(grpEigenschaften, SWT.BORDER);
		txtTitel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateFilter();
			}
		});
		txtTitel.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		GridData gd_txtTitel = new GridData(SWT.FILL, SWT.CENTER, false, false, 9, 1);
		gd_txtTitel.widthHint = 143;
		txtTitel.setLayoutData(gd_txtTitel);
		
		lblStichwrter = new Label(grpEigenschaften, SWT.NONE);
		lblStichwrter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStichwrter.setText("Stichw\u00F6rter");
		
		txtStichwort = new Text(grpEigenschaften, SWT.BORDER);
		txtStichwort.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateFilter();
			}
		});
		txtStichwort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 9, 1));
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		
		Label lblJahr = new Label(grpEigenschaften, SWT.NONE);
		lblJahr.setAlignment(SWT.RIGHT);
		GridData gd_lblJahr = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblJahr.widthHint = 47;
		lblJahr.setLayoutData(gd_lblJahr);
		lblJahr.setText("Jahr");
		
		txtJahr = new Text(grpEigenschaften, SWT.BORDER);
		txtJahr.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateFilter();
			}
		});
		GridData gd_txtJahr = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtJahr.widthHint = 72;
		txtJahr.setLayoutData(gd_txtJahr);
		
		Label lblMedienart = new Label(grpEigenschaften, SWT.RIGHT);
		GridData gd_lblMedienart = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblMedienart.widthHint = 66;
		lblMedienart.setLayoutData(gd_lblMedienart);
		lblMedienart.setText("Medienart");
		
		comboArt = new Combo(grpEigenschaften, SWT.READ_ONLY);
		comboArt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFilter();
			}
		});
		comboArt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboArt.setItems(BuchConstants.ARTEN.toArray(new String[BuchConstants.ARTEN.size()]));
		comboArt.select(0);
		
		Label lblStufe = new Label(grpEigenschaften, SWT.NONE);
		GridData gd_lblStufe = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblStufe.widthHint = 43;
		lblStufe.setLayoutData(gd_lblStufe);
		lblStufe.setAlignment(SWT.RIGHT);
		lblStufe.setText("Stufe");
		
		comboStufe = new Combo(grpEigenschaften, SWT.READ_ONLY);
		comboStufe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFilter();
			}
		});
		comboStufe.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboStufe.setItems(BuchConstants.STUFEN.toArray(new String[BuchConstants.STUFEN.size()]));
		comboStufe.select(0);
		
		lblKategorie = new Label(grpEigenschaften, SWT.NONE);
		lblKategorie.setAlignment(SWT.RIGHT);
		GridData gd_lblKategorie = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblKategorie.widthHint = 75;
		lblKategorie.setLayoutData(gd_lblKategorie);
		lblKategorie.setText("Kategorie:");
		
		comboKatViewer = new TableComboViewer(grpEigenschaften, SWT.BORDER);
		comboKat = comboKatViewer.getTableCombo();
		comboKat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFilter();
			}
		});
		comboKat.setVisibleItemCount(24);
		comboKat.setShowTableHeader(false);
		comboKat.setShowTableLines(false);
		comboKat.setShowColorWithinSelection(false);
		comboKat.setShowFontWithinSelection(false);
		comboKat.setShowImageWithinSelection(false);
		comboKat.defineColumns(1);
		comboKat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboKatViewer.setLabelProvider(new ComboKatLabelProvider());
		comboKatViewer.setContentProvider(new ArrayContentProvider());
		
		lblStatus = new Label(grpEigenschaften, SWT.NONE);
		lblStatus.setAlignment(SWT.RIGHT);
		GridData gd_lblStatus = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblStatus.widthHint = 55;
		lblStatus.setLayoutData(gd_lblStatus);
		lblStatus.setText("Status:");
		
		comboStatus = new Combo(grpEigenschaften, SWT.READ_ONLY);
		comboStatus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFilter();
			}
		});
		comboStatus.setItems(new String[] {"beliebig", "verf\u00FCgbar", "verliehen", "vorgemerkt"});
		comboStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comboStatus.select(0);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
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
		btnNeueSuche.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		btnNeueSuche.setText("Neue Suche");
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		
		Group grpBuecher = new Group(this, SWT.NONE);
		grpBuecher.setLayout(new GridLayout(1, false));
		grpBuecher.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpBuecher.setText("Buecher");
		
		toolBar = new ToolBar(grpBuecher, SWT.FLAT | SWT.RIGHT);
		
		btnTableUpdate = new ToolItem(toolBar, SWT.NONE);
		btnTableUpdate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateTable();
			}
		});
		btnTableUpdate.setImage(SWTResourceManager.getImage(BuecherTableView.class, "/icons/refresh.png"));
		btnTableUpdate.setToolTipText("Tabelle aktualisieren.");
		
		composite = new Composite(grpBuecher, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new FillLayout());
//		colLay = new TableColumnLayout();
//		composite.setLayout(colLay);
		
		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if(!openOverviewOnDoubleClick) return;
				if(buch != null) new StartBuchOverview(buch, getShell(), BuecherTableView.this).run();
			}
		});
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				bookSelected(e);
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
				return ""+((Buch)element).getId();
			}
		});

		tblclmnId = tableViewerColumn.getColumn();
		tblclmnId.setWidth(40);
		tblclmnId.setText("ID");
		
		tableViewerColumn_9 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_9.setLabelProvider(new KategorieColorLabelProvider(1));
		tblclmnKategorie = tableViewerColumn_9.getColumn();
		tblclmnKategorie.setWidth(64);
		tblclmnKategorie.setText("Kategorie");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return ((Buch)element).getIsbn();
			}
		});
		tblclmnIsbn = tableViewerColumn_1.getColumn();
		tblclmnIsbn.setWidth(73);
		tblclmnIsbn.setText("ISBN");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return ((Buch)element).getAutor();
			}
		});
		tblclmnAutor = tableViewerColumn_2.getColumn();
		tblclmnAutor.setWidth(127);
		tblclmnAutor.setText("Autor");
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_3.setLabelProvider(new StyledCellLabelProvider() {
			Styler boldStyler = FontUtil.boldStyler();
			@Override
			public void update(ViewerCell cell) {
				Buch b = (Buch)cell.getElement();
				StyledString s = new StyledString();
				s.append(b.getTitel(), boldStyler);
				cell.setText(s.getString());
				cell.setStyleRanges(s.getStyleRanges());
				
			}
		});
		tblclmnTitel = tableViewerColumn_3.getColumn();
		tblclmnTitel.setWidth(132);
		tblclmnTitel.setText("Titel");
		
		tableViewerColumn_8 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_8.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return ""+((Buch)element).getStichworter();
			}
		});
		tblclmnStichwrter = tableViewerColumn_8.getColumn();
		tblclmnStichwrter.setWidth(125);
		tblclmnStichwrter.setText("Stichw\u00F6rter");
		
		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_4.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return ""+((Buch)element).getJahr();
			}
		});
		tblclmnJahr = tableViewerColumn_4.getColumn();
		tblclmnJahr.setWidth(52);
		tblclmnJahr.setText("Jahr");
		
		TableViewerColumn tableViewerColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return BuchConstants.STUFEN.get(((Buch)element).getStufe());
			}
		});
		tblclmnStufe = tableViewerColumn_5.getColumn();
		tblclmnStufe.setWidth(79);
		tblclmnStufe.setText("Stufe");
		
		TableViewerColumn tableViewerColumn_6 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_6.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return BuchConstants.ARTEN.get(((Buch)element).getMedienart());
			}
		});
		tblclmnArt = tableViewerColumn_6.getColumn();
		tblclmnArt.setWidth(60);
		tblclmnArt.setText("Art");
		
		TableViewerColumn tableViewerColumn_7 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_7.setLabelProvider(new BuchLabelProvider_Verliehen(9));
		tblclmnVerliehen = tableViewerColumn_7.getColumn();
		tblclmnVerliehen.setWidth(71);
		tblclmnVerliehen.setText("Verliehen");
		
		setProperties();
	}
	
	private void updateFilter(){
		filter.setSearchText(txtISBN.getText(), txtAutor.getText(), txtTitel.getText(), txtJahr.getText(), 
				comboArt.getSelectionIndex(), comboStufe.getSelectionIndex(),txtStichwort.getText(), 
				(Kategorie)((StructuredSelection)comboKatViewer.getSelection()).getFirstElement(),
				comboStatus.getSelectionIndex()-1);
		tableViewer.refresh();
	}
	
	public void updateTable(){
		Buch.getAllBuecher(data);
	}
	
	private void setProperties(){
		tableViewer.setContentProvider(new ObservableListContentProvider());
		tableViewer.setInput(data);
		tableViewer.addFilter(filter);
		tableViewer.setComparator(comparator);
		
		tblclmnId.addSelectionListener(getSelectionAdapter(tblclmnId, 0));
		tblclmnIsbn.addSelectionListener(getSelectionAdapter(tblclmnIsbn, 1));
		tblclmnAutor.addSelectionListener(getSelectionAdapter(tblclmnAutor, 2));
		tblclmnTitel.addSelectionListener(getSelectionAdapter(tblclmnTitel, 3));
		tblclmnJahr.addSelectionListener(getSelectionAdapter(tblclmnJahr, 4));
		tblclmnArt.addSelectionListener(getSelectionAdapter(tblclmnArt, 5));
		tblclmnStufe.addSelectionListener(getSelectionAdapter(tblclmnStufe, 6));
		tblclmnStichwrter.addSelectionListener(getSelectionAdapter(tblclmnStichwrter, 7));
		tblclmnKategorie.addSelectionListener(getSelectionAdapter(tblclmnKategorie, 8));
		tblclmnVerliehen.addSelectionListener(getSelectionAdapter(tblclmnVerliehen, 9));
		
		comboKatViewer.setInput(Kategorie.getKategorien());
		
		
		clearValues();
		
//		colLay.setColumnData(tblclmnId, new ColumnPixelData(30));
//		colLay.setColumnData(tblclmnKategorie, new ColumnWeightData(10));
//		colLay.setColumnData(tblclmnIsbn, new ColumnWeightData(10));
//		colLay.setColumnData(tblclmnAutor, new ColumnWeightData(10));     	
//		colLay.setColumnData(tblclmnTitel, new ColumnWeightData(30));		
//		colLay.setColumnData(tblclmnStichwrter, new ColumnWeightData(20));	
//		colLay.setColumnData(tblclmnJahr, new ColumnPixelData(60));
//		colLay.setColumnData(tblclmnStufe, new ColumnWeightData(5));
//		colLay.setColumnData(tblclmnArt, new ColumnWeightData(5));
//		colLay.setColumnData(tblclmnVerliehen, new ColumnWeightData(10));
		
				
		initBindings();
		
		updateTable();
		
		//initial sorting
		comparator.setColumn(8);
		int dir = comparator.getDirection();
		tableViewer.getTable().setSortDirection(dir);
		tableViewer.getTable().setSortColumn(tblclmnKategorie);
		tableViewer.refresh();
		
	}
	
	private void bookSelected(SelectionEvent e){
		enterValues((Buch)table.getSelection()[0].getData());
		
		if(table.getSelectionCount() == 0) changes.firePropertyChange("buch", buch, buch = null);
		else changes.firePropertyChange("buch", buch, buch = (Buch)table.getSelection()[0].getData());
	}
	
	public void enterValues(Buch b){
		txtISBN.setText(b.getIsbn());
		txtAutor.setText(b.getAutor());
		txtTitel.setText(b.getTitel());
		txtJahr.setText(""+b.getJahr());
		comboArt.select(b.getMedienart());
		comboStufe.select(b.getStufe());
		txtStichwort.setText(b.getStichworter());
		comboKatViewer.setSelection(new StructuredSelection(b.getKategorie()));
		comboStatus.select(b.getStatus()+1);
		
		notifyListeners(SWT.Selection, new Event());
	}
	
	public void clearValues(){
		txtISBN.setText("");
		txtAutor.setText("");
		txtTitel.setText("");
		txtStichwort.setText("");
		txtJahr.setText("");
		comboArt.select(0);
		comboStufe.select(0);
		comboKat.select(0);
		comboStatus.select(0);
		updateFilter();
	}
	
	public Buch getBuch(){
		return buch;
	}
	
	public void selectBuch(final Buch b){
		if(b != null && b.getId() != -1){
			if(data.contains(b)){
				selectBuchNow(b);
			}else data.addListChangeListener(new IListChangeListener() {
				
				@Override
				public void handleListChange(ListChangeEvent arg0) {
					if(data.contains(b)){
						selectBuchNow(b);
					}
					if(arg0.diff.getDifferences() != null && arg0.diff.getDifferences()[0] != null && 
							arg0.diff.getDifferences()[0].getPosition() == -1)
						data.removeListChangeListener(this);
				}
			});
			
		}
	}
	
	private void selectBuchNow(Buch b){
		if(buch!= null && buch.equals(b)){
			tableViewer.setSelection(new StructuredSelection(b),true);
		}else{
			clearValues();
			updateFilter();
			
			changes.firePropertyChange("buch", buch, buch = b);
			tableViewer.setSelection(new StructuredSelection(b),true);
			table.forceFocus();
			enterValues(b);
		}
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
	
	private void initBindings(){
		DataBindingContext dbc = new DataBindingContext();

		IObservableValue tar = SWTObservables.observeSize(table);
		TableColumn c = tblclmnKategorie;
		IObservableValue mod = PojoObservables.observeValue(c, "width");
		UpdateValueStrategy str = new UpdateValueStrategy();
		str.setConverter(new PercentageConverter(c, 0.1));
		dbc.bindValue(mod, tar, null, str);
		
		c = tblclmnIsbn;
		mod = PojoObservables.observeValue(c, "width");
		str = new UpdateValueStrategy();
		str.setConverter(new PercentageConverter(c, 0.05));
		dbc.bindValue(mod, tar, null, str);
		
		c = tblclmnAutor;
		mod = PojoObservables.observeValue(c, "width");
		str = new UpdateValueStrategy();
		str.setConverter(new PercentageConverter(c, 0.1));
		dbc.bindValue(mod, tar, null, str);
		
		c = tblclmnTitel;
		mod = PojoObservables.observeValue(c, "width");
		str = new UpdateValueStrategy();
		str.setConverter(new PercentageConverter(c, 0.3));
		dbc.bindValue(mod, tar, null, str);
		
		c = tblclmnStichwrter;
		mod = PojoObservables.observeValue(c, "width");
		str = new UpdateValueStrategy();
		str.setConverter(new PercentageConverter(c, 0.2));
		dbc.bindValue(mod, tar, null, str);
		
		c = tblclmnStufe;
		mod = PojoObservables.observeValue(c, "width");
		str = new UpdateValueStrategy();
		str.setConverter(new PercentageConverter(c, 0.05));
		dbc.bindValue(mod, tar, null, str);
		
		c = tblclmnArt;
		mod = PojoObservables.observeValue(c, "width");
		str = new UpdateValueStrategy();
		str.setConverter(new PercentageConverter(c, 0.05));
		dbc.bindValue(mod, tar, null, str);
		
		c = tblclmnVerliehen;
		mod = PojoObservables.observeValue(c, "width");
		str = new UpdateValueStrategy();
		str.setConverter(new PercentageConverter(c, 0.1));
		dbc.bindValue(mod, tar, null, str);
		
		c = tblclmnId;
		mod = PojoObservables.observeValue(c, "width");
		str = new UpdateValueStrategy();
		str.setConverter(new PercentageConverter(c, 0.025));
		dbc.bindValue(mod, tar, null, str);
		
		c = tblclmnJahr;
		mod = PojoObservables.observeValue(c, "width");
		str = new UpdateValueStrategy();
		str.setConverter(new PercentageConverter(c, 0.025));
		dbc.bindValue(mod, tar, null, str);
		
		
	}
}
