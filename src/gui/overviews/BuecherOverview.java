package gui.overviews;

import gui.MainApplication;
import gui.StringConstants;
import gui.VerliehenLabel;
import gui.tableviews.ComboKatLabelProvider;
import gui.tableviews.HistoryTableView;
import gui.validators.IdBoolConverter;
import gui.validators.NonBeliebigValidator;
import gui.validators.NonEmptyValidator;
import infos.InfoComposite;
import infos.InfoUeberfaellig;
import infos.InfoVerliehen;
import infos.InfoVorgemerkt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import model.Ausleihe;
import model.Buch;
import model.BuchConstants;
import model.Info;
import model.Kategorie;
import model.Schueler;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.dialog.TitleAreaDialogSupport;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.nebula.jface.tablecomboviewer.TableComboViewer;
import org.eclipse.nebula.widgets.tablecombo.TableCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import util.ISBNfromHTML;
import util.TitleDialog;
import actions.StartAusleiheOverview;

public class BuecherOverview extends TitleDialog {
	private DataBindingContext m_bindingContext;
	private Text txtISBN;
	private Text txtAutor;
	private Text txtTitel;
	private Text txtJahr;
	private Combo comboArt;
	private Combo comboStufe;
	private Buch buch = new Buch();
	private VerliehenLabel verliehenLabel;
	private Text textStichwort;
	private Text txtAnmerkungen;
	private ToolItem btnAusleihen;
	private ToolItem btnVormerken;
	private Label lblAnzahl;
	private TableCombo comboKat;
	private TableComboViewer comboKatViewer;
	private ToolItem btnNewExemplar;
	private ToolItem btnDelete;
	private ScrolledComposite scrolledComposite;
	private Composite compositeInfos;
	
	private boolean withDB = true;
	
	private PropertyChangeListener infoChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateInfos();
		}
	};
	
	
	public static Buch getOverview(Shell parentShell, Buch b, boolean withDB){
		BuecherOverview o = new BuecherOverview(parentShell, b, withDB);
		o.open();
		if(o.getReturnCode() == CANCEL) return null;
		return o.getBuch();
	}
	
	public static Buch getOverview(Shell parentShell, String isbn, boolean withDB){
		BuecherOverview o = new BuecherOverview(parentShell, null, withDB);
		o.buch.setIsbn(isbn);
		o.open();
		if(o.getReturnCode() == CANCEL) return null;
		return o.getBuch();
	}

	/**
	 * Create the dialog.
	 * @param parentShell
	 * @wbp.parser.constructor
	 */
	public BuecherOverview(Shell parentShell, Buch b) {
		super(parentShell);
		if(b != null) this.buch = b;
		setHelpAvailable(false);
		setShellStyle(SWT.TITLE | SWT.APPLICATION_MODAL);
	}
	
	/**
	 * if withDB is false then no changes in database are made.
	 * @param parentShell
	 */
	public BuecherOverview(Shell parentShell, Buch b, boolean withDB) {
		super(parentShell);
		if(b != null) this.buch = b;
		this.withDB = withDB;
		setHelpAvailable(false);
		setShellStyle(SWT.TITLE | SWT.APPLICATION_MODAL);
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceManager.getImage(BuecherOverview.class, "/icons/Buch.png"));
		setMessage("Hier erhalten Sie \u00DCbersicht \u00FCber die Eigenschaften des gew\u00E4hlten Mediums.");
		setTitle("Mediums\u00FCbersicht");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		GridLayout gl_container = new GridLayout(2, false);
		gl_container.verticalSpacing = 10;
		gl_container.horizontalSpacing = 0;
		container.setLayout(gl_container);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite_2 = new Composite(container, SWT.NONE);
		GridLayout gl_composite_2 = new GridLayout(2, false);
		gl_composite_2.horizontalSpacing = 20;
		composite_2.setLayout(gl_composite_2);
		composite_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		
		ToolBar toolBar_1 = new ToolBar(composite_2, SWT.FLAT | SWT.RIGHT);
		
		btnDelete = new ToolItem(toolBar_1, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean ret = MessageDialog.openConfirm(getShell(), "Bestätigen", StringConstants.CONFIRM_DELETE_MEDIUM);
				if(!ret) return;
				buch.entfernen();
				close();
			}
		});
		btnDelete.setImage(SWTResourceManager.getImage(BuecherOverview.class, "/icons/delete.png"));
		btnDelete.setToolTipText("Medium l\u00F6schen");
		if(!withDB) btnDelete.setEnabled(false);
		
		ToolBar toolBar_2 = new ToolBar(composite_2, SWT.NONE);
		
		btnAusleihen = new ToolItem(toolBar_2, SWT.NONE);
		btnAusleihen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
				setReturnCode(CANCEL);
				Ausleihe a = new Ausleihe();
				a.setB(buch);
				new StartAusleiheOverview(a,getShell(),MainApplication.MAIN.ausleihenTableView).run();
			}
		});
		btnAusleihen.setImage(SWTResourceManager.getImage(BuecherOverview.class, "/icons/ausleihen.png"));
		btnAusleihen.setToolTipText("ausleihen");
		if(!withDB) btnAusleihen.setEnabled(false);
		
		btnVormerken = new ToolItem(toolBar_2, SWT.NONE);
		btnVormerken.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
				setReturnCode(CANCEL);
				Ausleihe a = new Ausleihe();
				a.setB(buch);
				a.setVorgemerkt(true);
				new StartAusleiheOverview(a,getShell(),MainApplication.MAIN.ausleihenTableView).run();
			}
		});
		btnVormerken.setImage(SWTResourceManager.getImage(BuecherOverview.class, "/icons/vormerken.png"));
		btnVormerken.setToolTipText("vormerken");
		if(!withDB) btnVormerken.setEnabled(false);
		
		Group composite_3 = new Group(container, SWT.NONE);
		composite_3.setText("Infos");
		composite_3.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		gd_composite_2.widthHint = 265;
		composite_3.setLayoutData(gd_composite_2);
		
		scrolledComposite = new ScrolledComposite(composite_3, SWT.V_SCROLL);
		scrolledComposite.setAlwaysShowScrollBars(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		
		compositeInfos = new Composite(scrolledComposite, SWT.NONE);
		compositeInfos.setLayout(new GridLayout(1, false));
		scrolledComposite.setContent(compositeInfos);
		scrolledComposite.addControlListener(new ControlAdapter() {
		      public void controlResized(ControlEvent e) {
		        updateScrollRectSize();
		      }
		    });
		
		TabFolder tabFolder = new TabFolder(container, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmbersicht = new TabItem(tabFolder, SWT.NONE);
		tbtmbersicht.setText("\u00DCbersicht");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmbersicht.setControl(composite);

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginBottom = 10;
		gridLayout.marginRight = 10;
		gridLayout.marginTop = 10;
		gridLayout.marginLeft = 10;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		composite.setLayout(gridLayout);
		
		
		Composite grpEigenschaften = new Composite(composite, SWT.NONE);
		GridData gd_grpEigenschaften = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_grpEigenschaften.widthHint = 610;
		grpEigenschaften.setLayoutData(gd_grpEigenschaften);
		GridLayout gl_grpEigenschaften = new GridLayout(6, false);
		gl_grpEigenschaften.verticalSpacing = 10;
		gl_grpEigenschaften.horizontalSpacing = 10;
		grpEigenschaften.setLayout(gl_grpEigenschaften);
		
		Label lblIsbn = new Label(grpEigenschaften, SWT.NONE);
		lblIsbn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIsbn.setText("ISBN");
		
		txtISBN = new Text(grpEigenschaften, SWT.BORDER);
		txtISBN.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				Vector<Buch> fromIsbn = Buch.getFromIsbn(txtISBN.getText());
				if(fromIsbn.size() > 0 && buch.getId() == -1){
					buch.setValues(fromIsbn.get(0));
				}else
					searchInternet();
			}
		});
		txtISBN.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		GridData gd_txtISBN = new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1);
		gd_txtISBN.widthHint = 210;
		txtISBN.setLayoutData(gd_txtISBN);
		
		Label lblAutor = new Label(grpEigenschaften, SWT.NONE);
		lblAutor.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAutor.setText("Autor");
		
		txtAutor = new Text(grpEigenschaften, SWT.BORDER);
		txtAutor.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		GridData gd_txtAutor = new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1);
		gd_txtAutor.widthHint = 265;
		txtAutor.setLayoutData(gd_txtAutor);
		
		Label lblTitel = new Label(grpEigenschaften, SWT.NONE);
		lblTitel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTitel.setText("Titel");
		
		txtTitel = new Text(grpEigenschaften, SWT.BORDER);
		txtTitel.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		GridData gd_txtTitel = new GridData(SWT.FILL, SWT.CENTER, false, false, 5, 1);
		gd_txtTitel.widthHint = 257;
		txtTitel.setLayoutData(gd_txtTitel);
		
		Label lblStichwrter = new Label(grpEigenschaften, SWT.NONE);
		lblStichwrter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1));
		lblStichwrter.setText("Stichw\u00F6rter");
		
		textStichwort = new Text(grpEigenschaften, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_textStichwort = new GridData(SWT.FILL, SWT.FILL, false, true, 5, 1);
		gd_textStichwort.widthHint = 308;
		gd_textStichwort.heightHint = 40;
		textStichwort.setLayoutData(gd_textStichwort);
		
		Label lblJahr = new Label(grpEigenschaften, SWT.NONE);
		lblJahr.setAlignment(SWT.RIGHT);
		GridData gd_lblJahr = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblJahr.widthHint = 47;
		lblJahr.setLayoutData(gd_lblJahr);
		lblJahr.setText("Jahr");
		
		txtJahr = new Text(grpEigenschaften, SWT.BORDER);
		GridData gd_txtJahr = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_txtJahr.widthHint = 72;
		txtJahr.setLayoutData(gd_txtJahr);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		
		Label lblMedienart = new Label(grpEigenschaften, SWT.RIGHT);
		GridData gd_lblMedienart = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblMedienart.widthHint = 66;
		lblMedienart.setLayoutData(gd_lblMedienart);
		lblMedienart.setText("Medienart");
		
		comboArt = new Combo(grpEigenschaften, SWT.READ_ONLY);
		GridData gd_comboArt = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_comboArt.widthHint = 94;
		comboArt.setLayoutData(gd_comboArt);
		comboArt.setItems(BuchConstants.ARTEN.toArray(new String[BuchConstants.ARTEN.size()]));
		comboArt.select(0);
		
		Label lblNewLabel = new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		
		verliehenLabel = new VerliehenLabel(grpEigenschaften, SWT.NONE);
		GridData gd_verliehenLabel = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 3);
		gd_verliehenLabel.widthHint = 254;
		verliehenLabel.setLayoutData(gd_verliehenLabel);
		
		Label lblKategorie = new Label(grpEigenschaften, SWT.NONE);
		lblKategorie.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblKategorie.setText("Kategorie:");
		
		comboKatViewer = new TableComboViewer(grpEigenschaften, SWT.BORDER);
		comboKat = comboKatViewer.getTableCombo();
		comboKat.defineColumns(1);
		comboKat.setEditable(false);
		comboKat.setShowTableLines(false);
		comboKat.setShowTableHeader(false);
		comboKat.setShowImageWithinSelection(false);
		comboKat.setShowFontWithinSelection(false);
		comboKat.setShowColorWithinSelection(false);
		comboKat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		comboKatViewer.setContentProvider(new ArrayContentProvider());
		comboKatViewer.setLabelProvider(new ComboKatLabelProvider());
		comboKatViewer.setInput(Kategorie.getKategorien());
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		
		Label lblStufe = new Label(grpEigenschaften, SWT.NONE);
		GridData gd_lblStufe = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblStufe.widthHint = 43;
		lblStufe.setLayoutData(gd_lblStufe);
		lblStufe.setAlignment(SWT.RIGHT);
		lblStufe.setText("Stufe");
		
		comboStufe = new Combo(grpEigenschaften, SWT.READ_ONLY);
		comboStufe.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		comboStufe.setItems(BuchConstants.STUFEN.toArray(new String[BuchConstants.STUFEN.size()]));
		comboStufe.select(0);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		
		Label lblAnzahlAnExemplaren = new Label(grpEigenschaften, SWT.NONE);
		lblAnzahlAnExemplaren.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAnzahlAnExemplaren.setText("Anzahl an Exemplaren:");
		
		lblAnzahl = new Label(grpEigenschaften, SWT.NONE);
		lblAnzahl.setText("New Label");
		
		ToolBar toolBar_3 = new ToolBar(grpEigenschaften, SWT.FLAT | SWT.RIGHT);
		toolBar_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		btnNewExemplar = new ToolItem(toolBar_3, SWT.NONE);
		btnNewExemplar.setToolTipText("Duplikat hinzuf\u00FCgen.");
		btnNewExemplar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buch.addExemplar();
				close();
			}
		});
		btnNewExemplar.setImage(SWTResourceManager.getImage(BuecherOverview.class, "/icons/new.png"));
		if(!withDB) btnNewExemplar.setEnabled(false);
		
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		new Label(grpEigenschaften, SWT.NONE);
		grpEigenschaften.setTabList(new Control[]{txtISBN, txtAutor, txtTitel, textStichwort, txtJahr, comboArt, comboKat, comboStufe, verliehenLabel, toolBar_3});
		
		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Details");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(composite_1);
		composite_1.setLayout(new GridLayout(1, false));
		
		Group grpAnmerkungen = new Group(composite_1, SWT.NONE);
		grpAnmerkungen.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_grpAnmerkungen = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_grpAnmerkungen.heightHint = 117;
		grpAnmerkungen.setLayoutData(gd_grpAnmerkungen);
		grpAnmerkungen.setText("Anmerkungen (Zubeh\u00F6r, Besch\u00E4digungen, ...)");
		
		txtAnmerkungen = new Text(grpAnmerkungen, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		
		TabItem tbtmHistory = new TabItem(tabFolder, SWT.NONE);
		tbtmHistory.setText("History");
		
		Composite composite_4 = new Composite(tabFolder, SWT.NONE);
		tbtmHistory.setControl(composite_4);
		composite_4.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		new HistoryTableView(composite_4, SWT.NONE, (Schueler) null, (Buch) buch);
		
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Speichern",
				false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				"Abbrechen", false);
		
	}
	
	@Override
	protected void initValues() {
		m_bindingContext = initDataBindings();
		setBindingValues(m_bindingContext, buch);
		updateInfos();
		
		txtISBN.forceFocus();
		super.initValues();
	}
	
	@Override
	protected void okPressed() {
		if(withDB) buch.eintragen();
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(801, 664);
	}
	
	private void searchInternet(){
		String[] res = ISBNfromHTML.fromHTML(txtISBN.getText());
		if(res != null){
			buch.setTitel(res[0]);
			buch.setAutor(res[1]);
			buch.setJahr(res[2]);
			buch.setMedienart(1);
		}
	}
	
	
	/**
	 * @return the buch
	 */
	public Buch getBuch() {
		return buch;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue txtISBNObserveTextObserveWidget = SWTObservables.observeText(txtISBN, SWT.Modify);
		IObservableValue bISBNObserveValue = BeansObservables.observeValue(buch, "isbn");
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new NonEmptyValidator());
		Binding bindValue = bindingContext.bindValue(txtISBNObserveTextObserveWidget, bISBNObserveValue, strategy, null);
		ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT);
		//
		IObservableValue txtAutorObserveTextObserveWidget = SWTObservables.observeText(txtAutor, SWT.Modify);
		IObservableValue bAutorObserveValue = BeansObservables.observeValue(buch, "autor");
		strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new NonEmptyValidator());
		bindValue = bindingContext.bindValue(txtAutorObserveTextObserveWidget, bAutorObserveValue, strategy, null);
		ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT);
		//
		IObservableValue txtTitelObserveTextObserveWidget = SWTObservables.observeText(txtTitel, SWT.Modify);
		IObservableValue bTitelObserveValue = BeansObservables.observeValue(buch, "titel");
		strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new NonEmptyValidator());
		bindValue = bindingContext.bindValue(txtTitelObserveTextObserveWidget, bTitelObserveValue, strategy, null);
		ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT);
		//
		IObservableValue txtJahrObserveTextObserveWidget = SWTObservables.observeText(txtJahr, SWT.Modify);
		IObservableValue bJahrObserveValue = BeansObservables.observeValue(buch, "jahr");
		bindValue = bindingContext.bindValue(txtJahrObserveTextObserveWidget, bJahrObserveValue, null, null);
		ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT);
		//
		IObservableValue comboArtObserveSingleSelectionIndexObserveWidget = SWTObservables.observeSingleSelectionIndex(comboArt);
		IObservableValue bMedienartObserveValue = BeansObservables.observeValue(buch, "medienart");
		strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new NonBeliebigValidator());
		bindValue = bindingContext.bindValue(comboArtObserveSingleSelectionIndexObserveWidget, bMedienartObserveValue, strategy, null);
		ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT);
		//
		IObservableValue comboStufeObserveSingleSelectionIndexObserveWidget = SWTObservables.observeSingleSelectionIndex(comboStufe);
		IObservableValue bStufeObserveValue = BeansObservables.observeValue(buch, "stufe");
		strategy = new UpdateValueStrategy();
		strategy.setBeforeSetValidator(new NonBeliebigValidator());
		bindValue = bindingContext.bindValue(comboStufeObserveSingleSelectionIndexObserveWidget, bStufeObserveValue, strategy, null);
		ControlDecorationSupport.create(bindValue, SWT.TOP | SWT.LEFT);
		//
		IObservableValue obsValue = PojoObservables.observeValue(verliehenLabel, "status");
		IObservableValue modelObsValue = BeansObservables.observeValue(buch, "status");
		bindingContext.bindValue(obsValue, modelObsValue, null, null);
		//
		IObservableValue textStichwortObserveTextObserveWidget = SWTObservables.observeText(textStichwort, SWT.Modify);
		IObservableValue buchStichworterObserveValue = BeansObservables.observeValue(buch, "stichworter");
		bindingContext.bindValue(textStichwortObserveTextObserveWidget, buchStichworterObserveValue, null, null);
		//
		IObservableValue textAnmerungenObserveTextObserveWidget = SWTObservables.observeText(txtAnmerkungen, SWT.Modify);
		IObservableValue buchAnmerkungenObserveValue = BeansObservables.observeValue(buch, "anmerkungen");
		bindingContext.bindValue(textAnmerungenObserveTextObserveWidget, buchAnmerkungenObserveValue, null, null);
		//
		IObservableValue obs = SWTObservables.observeEnabled(btnAusleihen);
		IObservableValue beanobs = BeansObservables.observeValue(buch, "verfuegbar");
		strategy = new UpdateValueStrategy();
		bindingContext.bindValue(obs, beanobs, null, strategy);
		//
		obs = SWTObservables.observeText(lblAnzahl);
		beanobs = BeansObservables.observeValue(buch, "anzahl");
		bindingContext.bindValue(obs, beanobs, null, null);
		
		//comboKat
		obs = ViewersObservables.observeSingleSelection(comboKatViewer);
		beanobs = BeansObservables.observeValue(buch, "kategorie");
		bindingContext.bindValue(obs,beanobs);
		
		//btns enabled or disabled for id != or == -1
		obs = SWTObservables.observeEnabled(btnNewExemplar);
		beanobs = BeansObservables.observeValue(buch, "id");
		strategy = new UpdateValueStrategy();
		strategy.setConverter(new IdBoolConverter());
		bindingContext.bindValue(obs, beanobs, null, strategy);
		
		obs = SWTObservables.observeEnabled(btnDelete);
		bindingContext.bindValue(obs, beanobs, null, strategy);
		
		obs = SWTObservables.observeEnabled(btnVormerken);
		bindingContext.bindValue(obs, beanobs, null, strategy);
		
		obs = SWTObservables.observeEnabled(btnAusleihen);
		bindingContext.bindValue(obs, beanobs, null, strategy);
		
		TitleAreaDialogSupport.create(this, bindingContext);
		
		addValidation(bindingContext);
		
		return bindingContext;
	}
	
	public void updateInfos(){
		if(buch == null) return;
		
		for(Control c: compositeInfos.getChildren())
			c.dispose();
		
		Vector<Info> infos = buch.getInfos();
		for(Info i: infos){
			InfoComposite info = null;
			if(i.getType() == Info.INFO_UEBERFAELLIG) info = new InfoUeberfaellig(compositeInfos, SWT.NONE, i);
			if(i.getType() == Info.INFO_VERLIEHEN) info = new InfoVerliehen(compositeInfos, SWT.NONE, i);
			if(i.getType() == Info.INFO_VORGEMERKT) info = new InfoVorgemerkt(compositeInfos, SWT.NONE, i);
			
			info.addPropertyChangeListener(infoChangeListener);
			info.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					updateScrollRectSize();
				}
			});
			
			info.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		}
		
		scrolledComposite.layout(true, true);
		updateScrollRectSize();
	}
	

	private void updateScrollRectSize(){
		Rectangle r = scrolledComposite.getClientArea();
		scrolledComposite.setMinSize(compositeInfos.computeSize(r.width,
				            SWT.DEFAULT));
	}
	
	
}
