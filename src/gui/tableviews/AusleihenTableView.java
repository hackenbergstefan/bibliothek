package gui.tableviews;

import gui.overviews.AusleiheOverview;
import gui.selectors.BuecherSelector;
import gui.selectors.SchuelerSelector;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Date;
import java.util.Vector;

import model.Ausleihe;
import model.Schueler;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import util.DateUtils;
import util.WaitDialog;

public class AusleihenTableView extends Composite {
	private DataBindingContext m_bindingContext;
	private Table table;
	private TableColumn tblclmnMedium;
	private TableColumn tblclmnSchler;
	private TableColumn tblclmnVon;
	private TableColumn tblclmnBis;
	private TableColumn tblclmnStatus;
	private TableColumn tblclmnRueckdate;
	private TableViewerColumn tableViewerColumn_5;
	private TableViewerColumn tableViewerColumn_6;
	public TableViewer tableViewer;
	public Button btnOffeneAusleihen;
	public Button btnVersumnisliste;
	public Button btnVorgemerkte;
	public Button btnHistory;
	
	private AusleiheColumnViewerSorter comparator = new AusleiheColumnViewerSorter();
	private AusleiheFilter filter = new AusleiheFilter();
	
	private Ausleihe ausleihe = new Ausleihe();
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	/**
	 * Open overview or fire event only on table-doubleclick 
	 */
	public boolean openOverviewOnDoubleClick = true; 
	private TableColumn tblclmnVorgemerktAn;
	private Group grpSuchen;
	private Group grpSchler;
	private Label lblId;
	public Text txtSchuelerId;
	private Label lblIsbn;
	public Text txtMediumISBN;
	private Button radioSchuelerVerliehenAn;
	private Button radioSchuelerVorgemerktAn;
	private Composite composite;
	private ToolBar toolBar_2;
	private ToolItem tltmNewItem;
	private int widthStatus=170, widthVorgemerktAn=100, widthRueckdate=200;
	
	private Vector<Ausleihe> data = null;

	/**
	 * Create the composite.
	 * Set style == SWT.MULTI for multiple selection in table.
	 * @param parent
	 * @param style
	 */
	public AusleihenTableView(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		
		Composite container = new Composite(this, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		
		grpSuchen = new Group(container, SWT.NONE);
		grpSuchen.setLayout(new GridLayout(2, false));
		grpSuchen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpSuchen.setText("Filtern");
		
		grpSchler = new Group(grpSuchen, SWT.NONE);
		grpSchler.setLayout(new GridLayout(5, false));
		grpSchler.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpSchler.setText("Sch\u00FCler");
		
		lblId = new Label(grpSchler, SWT.RIGHT);
		GridData gd_lblId = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 2);
		gd_lblId.widthHint = 45;
		lblId.setLayoutData(gd_lblId);
		lblId.setText("ID:");
		
		txtSchuelerId = new Text(grpSchler, SWT.BORDER);
		GridData gd_txtSchuelerId = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2);
		gd_txtSchuelerId.widthHint = 234;
		txtSchuelerId.setLayoutData(gd_txtSchuelerId);
		txtSchuelerId.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateFilter();
			}
		});
		
		ToolBar toolBar = new ToolBar(grpSchler, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
		
		ToolItem btnEditSchueler = new ToolItem(toolBar, SWT.NONE);
		btnEditSchueler.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SchuelerSelector s = new SchuelerSelector(getShell(), null);
				s.open();
				if(s.getReturnCode() == TitleAreaDialog.OK){
					txtSchuelerId.setText(""+s.getSchueler().getId());
				}
			}
		});
		btnEditSchueler.setImage(SWTResourceManager.getImage(AusleihenTableView.class, "/icons/edit.png"));
		new Label(grpSchler, SWT.NONE);
		
		radioSchuelerVerliehenAn = new Button(grpSchler, SWT.RADIO);
		radioSchuelerVerliehenAn.setSelection(true);
		radioSchuelerVerliehenAn.setText("Verliehen an");
		new Label(grpSchler, SWT.NONE);
		
		radioSchuelerVorgemerktAn = new Button(grpSchler, SWT.RADIO);
		radioSchuelerVorgemerktAn.setText("Vorgemerkt an");
		
		Label label = new Label(grpSuchen, SWT.NONE);
		label.setAlignment(SWT.RIGHT);
		label.setImage(SWTResourceManager.getImage(AusleihenTableView.class, "/icons/Ausleihe_gro\u00DF.png"));
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 3));
		
		Group grpMedium = new Group(grpSuchen, SWT.NONE);
		grpMedium.setLayout(new GridLayout(3, false));
		grpMedium.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpMedium.setText("Medium");
		
		lblIsbn = new Label(grpMedium, SWT.RIGHT);
		GridData gd_lblIsbn = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblIsbn.widthHint = 45;
		lblIsbn.setLayoutData(gd_lblIsbn);
		lblIsbn.setText("ISBN:");
		
		txtMediumISBN = new Text(grpMedium, SWT.BORDER);
		txtMediumISBN.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateFilter();
			}
		});
		GridData gd_txtMediumISBN = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_txtMediumISBN.widthHint = 236;
		txtMediumISBN.setLayoutData(gd_txtMediumISBN);
		
		ToolBar toolBar_1 = new ToolBar(grpMedium, SWT.FLAT | SWT.RIGHT);
		
		ToolItem btnEditMedium = new ToolItem(toolBar_1, SWT.NONE);
		btnEditMedium.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BuecherSelector b = new BuecherSelector(getShell(), null);
				b.open();
				if(b.getReturnCode() == TitleAreaDialog.OK){
					txtMediumISBN.setText(b.getBuch().getIsbn());
					updateFilter();
				}
			}
		});
		btnEditMedium.setImage(SWTResourceManager.getImage(AusleihenTableView.class, "/icons/edit.png"));
		
		Button btnNeueSuche = new Button(grpSuchen, SWT.NONE);
		btnNeueSuche.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearValues();
			}
		});
		GridData gd_btnNeueSuche = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnNeueSuche.widthHint = 143;
		btnNeueSuche.setLayoutData(gd_btnNeueSuche);
		btnNeueSuche.setText("Neue Suche");
		
		Group grpEinstellungen = new Group(container, SWT.NONE);
		grpEinstellungen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpEinstellungen.setLayout(new GridLayout(4, false));
		grpEinstellungen.setText("\u00DCbersichten");
		
		btnOffeneAusleihen = new Button(grpEinstellungen, SWT.RADIO);
		btnOffeneAusleihen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateTable();
			}
		});
		btnOffeneAusleihen.setSelection(true);
		btnOffeneAusleihen.setText("Offene Ausleihen und Vormerkungen");
		
		btnVersumnisliste = new Button(grpEinstellungen, SWT.RADIO);
		btnVersumnisliste.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateTable();
			}
		});
		btnVersumnisliste.setText("Vers\u00E4umnisliste");
		
		btnVorgemerkte = new Button(grpEinstellungen, SWT.RADIO);
		btnVorgemerkte.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateTable();
			}
		});
		btnVorgemerkte.setText("Vormerkungen");
		
		btnHistory = new Button(grpEinstellungen, SWT.RADIO);
		btnHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateTable();
			}
		});
		btnHistory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		btnHistory.setText("History");
		
		composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		toolBar_2 = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		
		tltmNewItem = new ToolItem(toolBar_2, SWT.NONE);
		tltmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateTable();
			}
		});
		tltmNewItem.setToolTipText("Tabelle aktualisieren.");
		tltmNewItem.setImage(SWTResourceManager.getImage(AusleihenTableView.class, "/icons/refresh.png"));
		
		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | (style == SWT.MULTI?SWT.MULTI:SWT.NONE));
		tableViewer.addFilter(filter);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ausleiheSelected(e);
			}
		});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if(!openOverviewOnDoubleClick) return;
				AusleiheOverview view = new AusleiheOverview(getShell(), getAusleihe());
				view.open();
				updateTable();
				selectAusleihe(view.getAusleihe());
			}
		});
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new StyledCellLabelProvider() {
				@Override
				public void update(ViewerCell cell) {					
					StyledString s = ((Ausleihe)cell.getElement()).getB().toStyledString();
					
					cell.setStyleRanges(s.getStyleRanges());
					cell.setText(s.getString());
					
					super.update(cell);
			}
		});
		tblclmnMedium = tableViewerColumn.getColumn();
		tblclmnMedium.addSelectionListener(getSelectionAdapter(tblclmnMedium, 0));
		tblclmnMedium.setWidth(359);
		tblclmnMedium.setText("Medium");
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {					
				StyledString s = ((Ausleihe)cell.getElement()).getS().toStyledString();
				
				cell.setStyleRanges(s.getStyleRanges());
				cell.setText(s.getString());
				
				super.update(cell);
			}
		});
		tblclmnSchler = tableViewerColumn_1.getColumn();
		tblclmnSchler.addSelectionListener(getSelectionAdapter(tblclmnSchler, 1));
		tblclmnSchler.setWidth(193);
		tblclmnSchler.setText("Sch\u00FCler");
		
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return DateUtils.date2string(((Ausleihe)element).getVon());
			}
		});
		tblclmnVon = tableViewerColumn_2.getColumn();
		tblclmnVon.addSelectionListener(getSelectionAdapter(tblclmnVon, 2));
		tblclmnVon.setWidth(100);
		tblclmnVon.setText("Von");
		
		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				return DateUtils.date2string(((Ausleihe)element).getBis());
			}
		});
		tblclmnBis = tableViewerColumn_3.getColumn();
		tblclmnBis.addSelectionListener(getSelectionAdapter(tblclmnBis, 3));
		tblclmnBis.setWidth(100);
		tblclmnBis.setText("Bis");
		
		TableViewerColumn tableViewerColumn_4 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_4.setLabelProvider(new AusleiheLabelProvider_Progress(4));
		tblclmnStatus = tableViewerColumn_4.getColumn();
		tblclmnStatus.addSelectionListener(getSelectionAdapter(tblclmnStatus, 4));
		tblclmnStatus.setWidth(widthStatus);
		tblclmnStatus.setText("Status");
		
		tableViewerColumn_5 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_5.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				Ausleihe a = (Ausleihe)element;
				Schueler vorgemerkt = a.getVorgemerktAn();
				return vorgemerkt == null?"":vorgemerkt.toNiceString();
			}
		});
		tblclmnVorgemerktAn = tableViewerColumn_5.getColumn();
		tblclmnVorgemerktAn.addSelectionListener(getSelectionAdapter(tblclmnVorgemerktAn, 5));
		tblclmnVorgemerktAn.setWidth(widthVorgemerktAn);
		tblclmnVorgemerktAn.setText("Vorgemerkt an");
		
		tableViewerColumn_6 = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn_6.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				Date rueck = ((Ausleihe)element).getRueckdate();
				return rueck == null?"":DateUtils.longFormat.format(DateUtils.sql2java(rueck));
			}
		});
		tblclmnRueckdate = tableViewerColumn_6.getColumn();
		tblclmnRueckdate.addSelectionListener(getSelectionAdapter(tblclmnRueckdate, 6));
		tblclmnRueckdate.setWidth(widthRueckdate);
		tblclmnRueckdate.setText("R\u00FCckgabedatum");
		
		initComponents();
		m_bindingContext = initDataBindings();

	}
	
	public void clearValues(){
		txtSchuelerId.setText("");
		txtMediumISBN.setText("");
		
		updateFilter();
	}
	
	public void updateFilter(){
		filter.setSearchText(txtSchuelerId.getText(), txtMediumISBN.getText(), radioSchuelerVerliehenAn.getSelection());
		tableViewer.refresh();
	}

	
	private void initComponents(){
		tableViewer.setComparator(comparator);
		tableViewer.setContentProvider(new ArrayContentProvider());
		updateTable();
		
		//initial sorting
		comparator.setColumn(4);
		int dir = comparator.getDirection();
		tableViewer.getTable().setSortDirection(dir);
		tableViewer.refresh();
	}
	
	
	public void updateTable(){
		final int state;
		if(btnOffeneAusleihen.getSelection()){
			state = 0;
			collapseHistory();
		}
		else if(btnVorgemerkte.getSelection()){
			state = 1;
			collapseHistory();
		}
		else if(btnVersumnisliste.getSelection()){
			state = 2;
			collapseHistory();
		}
		else if(btnHistory.getSelection()){
			state = 3;
			expandHistory();
		}
		else state = -1;
		
		WaitDialog.show(getShell(), this,new Runnable() {
			
			@Override
			public void run() {
				switch(state){
				case 0: data = Ausleihe.getAllOpen(); break;
				case 1: data = Ausleihe.getVorgemerkte(); break;
				case 2: data = Ausleihe.getAllTooLate(); break;
				case 3: data = Ausleihe.getAllDone(); break;
				default: data = new Vector<Ausleihe>(); break;
				}
				tableViewer.setInput(data);
			}
		});
		
	}
	
	private void collapseHistory(){
		if(tblclmnRueckdate.getWidth() == 0 ) return;
		TableColumn col = tblclmnRueckdate;
		widthRueckdate = col.getWidth();
		col.setWidth(0);
		col.setResizable(false);
		
		col = tblclmnStatus;
		col.setWidth(widthStatus);
		col.setResizable(true);
		
		col = tblclmnVorgemerktAn;
		col.setWidth(widthVorgemerktAn);
		col.setResizable(true);
	}
	
	private void expandHistory(){
		if(tblclmnRueckdate.getWidth() > 0 ) return;
		TableColumn col = tblclmnStatus;
		widthStatus = col.getWidth();
		col.setWidth(0);
		col.setResizable(false);
		
		col = tblclmnVorgemerktAn;
		widthVorgemerktAn = col.getWidth();
		col.setWidth(0);
		col.setResizable(false);
		
		col = tblclmnRueckdate;
		col.setWidth(widthRueckdate);
		col.setResizable(true);
	}
	

	public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }
    
    private void ausleiheSelected(SelectionEvent e){
    	notifyListeners(SWT.Selection, new Event());
		if(table.getSelectionCount() == 0) changes.firePropertyChange("ausleihe", ausleihe, ausleihe = null);
		else{
			data.clear();
			for(TableItem i: table.getSelection()) data.add((Ausleihe) i.getData());
			changes.firePropertyChange("ausleihe", ausleihe, ausleihe = (Ausleihe)table.getSelection()[0].getData());
		}
    }
    
	
	/**
	 * @return the ausleihe
	 */
	public Ausleihe getAusleihe() {
		return ausleihe;
	}
	
	public Vector<Ausleihe> getAusleihen(){
		return (Vector<Ausleihe>) data.clone();
	}
	
	public void selectAusleihe(Ausleihe ausleihe){
		if(ausleihe != null && ausleihe.getId() != -1){
			clearValues();
			updateFilter();
			changes.firePropertyChange("ausleihe", this.ausleihe, this.ausleihe = ausleihe);
			tableViewer.setSelection(new StructuredSelection(AusleihenTableView.this.ausleihe),true);
			table.forceFocus();
		}
	}
	
	/**
	 * Sorts table and refreshes it.
	 * @param col
	 * @param asc true: ascending, false:descending
	 */
	public void sortBy(int col, boolean asc){
		comparator.setColumn(col);
		comparator.setDirection(asc);
		table.setSortColumn(table.getColumn(col));
		table.setSortDirection(comparator.getDirection());
		tableViewer.refresh();
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
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		
		//Schüler
		IObservableValue target = SWTObservables.observeText(txtSchuelerId);
		IObservableValue model = BeansObservables.observeValue(this, "ausleihe");
		UpdateValueStrategy str = new UpdateValueStrategy();
		str.setConverter(new IConverter() {
			
			@Override
			public Object getToType() {
				return String.class;
			}
			
			@Override
			public Object getFromType() {
				return Ausleihe.class;
			}
			
			@Override
			public Object convert(Object fromObject) {
				Ausleihe a = (Ausleihe) fromObject;
				if(radioSchuelerVerliehenAn.getSelection()) return ""+a.getS().getId();
				else return a.getVorgemerktAn() != null?""+a.getVorgemerktAn().getId():"";
			}
		});
		bindingContext.bindValue(target, model, null, str);
		
		//Medium
		target = SWTObservables.observeText(txtMediumISBN);
		model = BeansObservables.observeValue(this, "ausleihe.b.isbn");
		bindingContext.bindValue(target, model);
		
		//RueckDate Visibility
		target = PojoObservables.observeValue(tblclmnRueckdate, "width");
		model = SWTObservables.observeSelection(btnHistory);
		str = new UpdateValueStrategy();
		str.setConverter(new IConverter() {
			
			@Override
			public Object getToType() {
				return int.class;
			}
			
			@Override
			public Object getFromType() {
				return boolean.class;
			}
			
			@Override
			public Object convert(Object fromObject) {
				if((Boolean)fromObject) return 200;
				else return 0;
			}
		});
		bindingContext.bindValue(target, model, null, str);
		
		return bindingContext;
	}
}
