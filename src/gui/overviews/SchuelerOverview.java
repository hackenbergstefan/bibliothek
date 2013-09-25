package gui.overviews;

import gui.StringConstants;
import gui.tableviews.HistoryTableView;
import gui.validators.IdBoolConverter;
import gui.validators.NonEmptyValidator;
import infos.InfoComposite;
import infos.InfoUeberfaellig;
import infos.InfoVerliehen;
import infos.InfoVorgemerkt;

import java.awt.BorderLayout;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Vector;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterResolution;

import log.Logger;
import model.Buch;
import model.Gender;
import model.Info;
import model.Schueler;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.dialog.TitleAreaDialogSupport;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import util.TitleDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;

import ausweis.Ausweis;
import ausweis.Ausweis_Printable;

public class SchuelerOverview extends TitleDialog {
	private Text txtVorname;
	private Text txtKlasse;
	private Text txtAnmerkungen;
	private Text txtNachname;
	
	private Schueler schueler = new Schueler();
	private Text txtID;
	private Composite compositeInfos;
	private ScrolledComposite scrolledComposite;
	private ToolItem btnDelete;
	private DataBindingContext dbc;
	
	private PropertyChangeListener infoChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateInfos();
		}
	};
	private Combo comboGeschlecht;

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public SchuelerOverview(Shell parentShell, Schueler schueler) {
		super(parentShell);
		setHelpAvailable(false);
		setShellStyle(SWT.RESIZE | SWT.TITLE | SWT.APPLICATION_MODAL);
		
		if(schueler != null){
			this.schueler = schueler;
			schueler.addPropertyChangeListener(infoChangeListener);
		}
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceManager.getImage(SchuelerOverview.class, "/icons/Sch\u00FCler.png"));
		
		setMessage("Hier erhalten Sie \u00DCbersicht \u00FCber den gew\u00E4hlten Sch\u00FCler.");
		setTitle("Sch\u00FCler\u00FCbersicht");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
		
		btnDelete = new ToolItem(toolBar, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean ret = MessageDialog.openConfirm(getShell(), "Bestätigen", StringConstants.CONFIRM_DELETE_SCHUELER);
				if(!ret) return;
				schueler.entfernen();
				close();
			}
		});
		btnDelete.setImage(SWTResourceManager.getImage(SchuelerOverview.class, "/icons/delete.png"));
		btnDelete.setToolTipText("Sch\u00FCler l\u00F6schen.");
		
		Group composite_2 = new Group(container, SWT.NONE);
		composite_2.setText("Infos");
		composite_2.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		gd_composite_2.widthHint = 168;
		composite_2.setLayoutData(gd_composite_2);
		
		scrolledComposite = new ScrolledComposite(composite_2, SWT.V_SCROLL);
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
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		TabItem tbtmbersicht = new TabItem(tabFolder, SWT.NONE);
		tbtmbersicht.setText("\u00DCbersicht");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmbersicht.setControl(composite);
		composite.setLayout(new GridLayout(4, false));
		
		Label lblId = new Label(composite, SWT.NONE);
		lblId.setAlignment(SWT.RIGHT);
		lblId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblId.setText("ID:");
		
		txtID = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		GridData gd_txtID = new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1);
		gd_txtID.widthHint = 137;
		txtID.setLayoutData(gd_txtID);
		
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label.setText("Vorname");
		label.setBounds(0, 0, 48, 15);
		
		txtVorname = new Text(composite, SWT.BORDER);
		GridData gd_txtVorname = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gd_txtVorname.widthHint = 360;
		txtVorname.setLayoutData(gd_txtVorname);
		txtVorname.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		txtVorname.setBounds(0, 0, 582, 31);
		
		Label label_1 = new Label(composite, SWT.NONE);
		label_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_1.setText("Nachname");
		label_1.setBounds(0, 0, 58, 15);
		
		txtNachname = new Text(composite, SWT.BORDER);
		GridData gd_txtNachname = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gd_txtNachname.widthHint = 360;
		txtNachname.setLayoutData(gd_txtNachname);
		txtNachname.setFont(SWTResourceManager.getFont("Segoe UI", 14, SWT.BOLD));
		txtNachname.setBounds(0, 0, 582, 31);
		
		Label label_2 = new Label(composite, SWT.NONE);
		label_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		label_2.setText("Klasse");
		label_2.setBounds(0, 0, 32, 15);
		
		txtKlasse = new Text(composite, SWT.BORDER);
		GridData gd_txtKlasse = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		gd_txtKlasse.widthHint = 73;
		txtKlasse.setLayoutData(gd_txtKlasse);
		txtKlasse.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		txtKlasse.setBounds(0, 0, 203, 21);
		
		Label lblGeschlecht = new Label(composite, SWT.NONE);
		lblGeschlecht.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		lblGeschlecht.setText("Geschlecht:");
		
		comboGeschlecht = new Combo(composite, SWT.NONE);
		comboGeschlecht.setItems(new String[] {"Junge", "M\u00E4dchen"});
		GridData gd_comboGeschlecht = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_comboGeschlecht.widthHint = 126;
		comboGeschlecht.setLayoutData(gd_comboGeschlecht);
		
		Group grpAnmerkungen = new Group(composite, SWT.NONE);
		FillLayout fl_grpAnmerkungen = new FillLayout(SWT.HORIZONTAL);
		fl_grpAnmerkungen.marginWidth = 5;
		fl_grpAnmerkungen.marginHeight = 5;
		grpAnmerkungen.setLayout(fl_grpAnmerkungen);
		grpAnmerkungen.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 4, 1));
		grpAnmerkungen.setText("Anmerkungen");
		
		txtAnmerkungen = new Text(grpAnmerkungen, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		
		TabItem tbtmHistory = new TabItem(tabFolder, SWT.NONE);
		tbtmHistory.setText("History");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmHistory.setControl(composite_1);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		new HistoryTableView(composite_1, SWT.NONE, (Schueler) schueler, (Buch) null);
		
		TabItem tbtmAusweis = new TabItem(tabFolder, SWT.NONE);
		tbtmAusweis.setText("Ausweis");
		
		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tbtmAusweis.setControl(composite_3);
		composite_3.setLayout(new GridLayout(1, false));
		
		Canvas canvas = new Canvas(composite_3, SWT.NONE);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				GC g = e.gc;
				Image img = new Image(g.getDevice(), Ausweis.getAusweisSWT(schueler));
				
				int cropedWidth = 0, cropedHeight = 0;
				if(e.width < e.height){
					cropedWidth = e.width;
					cropedHeight = (int)( e.height/e.width * cropedWidth);
				}else{
					cropedHeight = e.height;
					cropedWidth = (int)( e.width/e.height * cropedHeight);
				}
				g.drawImage(img, 0,0,img.getImageData().width,img.getImageData().height,
						(int)(0.5*(e.width - cropedWidth)), (int)(0.5*(e.height - cropedHeight)),
						cropedWidth, cropedHeight);
			}
		});
		
		Button btnAusweisDrucken = new Button(composite_3, SWT.NONE);
		btnAusweisDrucken.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PrinterJob job = PrinterJob.getPrinterJob();
				job.setJobName("Ausweis "+schueler.getVorname()+" "+schueler.getNachname());
				job.setPrintable(new Ausweis_Printable(schueler));
				if(job.printDialog()){
					try {
						job.print();
					} catch (PrinterException e1) {
						Logger.logError(e1.getMessage());
					}
				}
//				
//				/* Construct the print request specification.
//                * The print data is a Printable object.
//                * the request additonally specifies a job name, 2 copies, and
//                * landscape orientation of the media.
//                */
//                DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
//                PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
//                aset.add(PrintQuality.HIGH);
//                aset.add(new JobName("My job", null));
//
//                /* locate a print service that can handle the request */
//                PrintService[] printServices =
//                        PrintServiceLookup.lookupPrintServices(flavor, aset);
//                PrintService service = ServiceUI.printDialog(null, 200, 200, printServices, null, flavor, aset);
//
//                if (service != null) {
//                        System.out.println("selected printer " + service.getName());
//
//                        /* create a print job for the chosen service */
//                        DocPrintJob pj = service.createPrintJob();
//
//                        try {
//                                /* 
//                                * Create a Doc object to hold the print data.
//                                */
//                                Doc doc = new SimpleDoc(new Ausweis_Printable(schueler,72), flavor, null);
//
//                                /* print the doc as specified */
//                                pj.print(doc, aset);
//
//                        } catch (PrintException e1) { 
//                                System.err.println(e1);
//                        }
//                }
			}
		});
		btnAusweisDrucken.setText("Ausweis drucken");

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
		initBindings();
		setBindingValues(dbc, schueler);
		updateInfos();
		
		super.initValues();
	}
	
	@Override
	protected void okPressed() {
		schueler.eintragen();
		super.okPressed();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(737, 515);
	}
	
	
	
	private void initBindings(){
		dbc = new DataBindingContext();
		
		//Vorname
		IObservableValue tarVorname = SWTObservables.observeText(txtVorname, SWT.Modify);
		IObservableValue obsVorname = BeansObservables.observeValue(schueler, "vorname");
		UpdateValueStrategy str = new UpdateValueStrategy();
		str.setBeforeSetValidator(new NonEmptyValidator());
		Binding binding = dbc.bindValue(tarVorname, obsVorname, str, null);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
		
		//Nachname
		IObservableValue tarNachname = SWTObservables.observeText(txtNachname, SWT.Modify);
		IObservableValue obsNachname = BeansObservables.observeValue(schueler, "nachname");
		str = new UpdateValueStrategy();
		str.setBeforeSetValidator(new NonEmptyValidator());
		binding = dbc.bindValue(tarNachname, obsNachname, str, null);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
		
		//Klasse
		IObservableValue tarKlasse = SWTObservables.observeText(txtKlasse, SWT.Modify);
		IObservableValue obsKlasse = BeansObservables.observeValue(schueler, "klasse");
		str = new UpdateValueStrategy();
		str.setBeforeSetValidator(new NonEmptyValidator());
		binding = dbc.bindValue(tarKlasse, obsKlasse, str, null);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
		
		//Geschlecht
		IObservableValue tarGeschl = SWTObservables.observeSingleSelectionIndex(comboGeschlecht);
		IObservableValue obsGeschl = BeansObservables.observeValue(schueler, "gender");
		str = new UpdateValueStrategy();
		str.setConverter(new IConverter() {
			
			@Override
			public Object getToType() {
				return Gender.class;
			}
			
			@Override
			public Object getFromType() {
				return int.class;
			}
			
			@Override
			public Object convert(Object fromObject) {
				int index = (Integer)fromObject;
				switch(index){
				case 0: return Gender.FEMALE;
				case 1: return Gender.MALE;
				default: return null;
				}
			}
		});
		UpdateValueStrategy backstr = new UpdateValueStrategy();
		backstr.setConverter(new IConverter() {
			
			@Override
			public Object getToType() {
				return int.class;
			}
			
			@Override
			public Object getFromType() {
				return Gender.class;
			}
			
			@Override
			public Object convert(Object fromObject) {
				Gender g = (Gender)fromObject;
				if(g == Gender.MALE)
					return 1;
				else return 0;
			}
		});
		binding = dbc.bindValue(tarGeschl, obsGeschl, str, backstr);
		
		//Anmerkungen
		IObservableValue tarAnmerkungen = SWTObservables.observeText(txtAnmerkungen, SWT.Modify);
		IObservableValue obsAnmerkungen = BeansObservables.observeValue(schueler, "anmerkungen");
		binding = dbc.bindValue(tarAnmerkungen, obsAnmerkungen);
		
		
		//ID
		IObservableValue tarID = SWTObservables.observeText(txtID);
		IObservableValue obsID = BeansObservables.observeValue(schueler, "id");
		binding = dbc.bindValue(tarID, obsID);
		
		//btns enabled or disabled for id != or == -1
		IObservableValue obs = SWTObservables.observeEnabled(btnDelete);
		IObservableValue beanobs = BeansObservables.observeValue(schueler, "id");
		UpdateValueStrategy strategy = new UpdateValueStrategy();
		strategy.setConverter(new IdBoolConverter());
		dbc.bindValue(obs, beanobs, null, strategy);
		
		
		TitleAreaDialogSupport.create(this, dbc);
	}
	
	public Schueler getSchueler(){
		return schueler;
	}
	
	
	public void updateInfos(){
		if(schueler == null) return;
		
		for(Control c: compositeInfos.getChildren())
			c.dispose();
		
		Vector<Info> infos = schueler.getInfos();
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
