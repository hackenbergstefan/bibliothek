package gui;
import gui.inventur.InventurView;
import gui.options.Preferences;
import gui.overviews.AusleiheOverview;
import gui.overviews.BuecherOverview;
import gui.overviews.SchuelerOverview;
import gui.selectors.AusleihenSelector;
import gui.statistics.StatisticView;
import gui.tableviews.AusleihenTableView;
import gui.tableviews.BuecherTableView;
import gui.tableviews.SchuelerTableView;
import gui.views.MassiveAusleihe;

import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import log.Logger;
import model.Ausleihe;
import model.Buch;
import model.Schueler;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.nebula.widgets.xviewer.util.internal.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import actions.StartAusleiheOverview;
import actions.StartBuchOverview;
import actions.StartMassiveAusleihe;
import actions.StartSchuelerOverview;

import print.PrintableUeberfaellig;
import statistics.Statistics;
import util.DateUtils;


public class MainApplication {
	public static MainApplication MAIN;

	protected Shell shlBibliothek;
	public BuecherTableView buecherTableView;
	public SchuelerTableView schuelerTableView;
	public AusleihenTableView ausleihenTableView;
	private TabItem tbtmAusleihen;
	private TabItem tbtmSchler;
	private TabItem tbtmMedien;
	private TabFolder tabFolder;
	private TabItem tbtmInfo;
	private Statistics statistics;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();

		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				Display display = Display.getCurrent();
				
				MAIN = new MainApplication();
				
				Splash s = Splash.getSplash(display);
				
				s.open();
				
				while(!s.isDisposed())
					while(!display.readAndDispatch())
						display.sleep();
				
				MAIN.open();
				
				while(!MAIN.shlBibliothek.isDisposed())
					if(!display.readAndDispatch())
						display.sleep();
			}
		});
		
	}
	

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getCurrent();
		shlBibliothek.open();
		shlBibliothek.layout();
		while (!shlBibliothek.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	public void createContents() {
		shlBibliothek = new Shell();
		shlBibliothek.setImage(SWTResourceManager.getImage(MainApplication.class, "/icons/b\u00FCchereilogo_klein.png"));
		shlBibliothek.setBackgroundMode(SWT.INHERIT_DEFAULT);
		shlBibliothek.setSize(1237, 755);
		shlBibliothek.setText("Bibliothek");
		shlBibliothek.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite_1 = new Composite(shlBibliothek, SWT.NONE);
		composite_1.setLayout(new GridLayout(1, false));
		
		tabFolder = new TabFolder(composite_1, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(tabFolder.getSelectionIndex() == 0 && statistics != null){
					statistics.update();
				}
			}
		});
		
		tbtmInfo = new TabItem(tabFolder, SWT.NONE);
		tbtmInfo.setText("Info");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmInfo.setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		Composite composite_5 = new Composite(composite, SWT.NONE);
		composite_5.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		GridLayout grid_1 = new GridLayout(2, false);
		grid_1.horizontalSpacing = 0;
		composite_5.setLayout(grid_1);
				
		Label label_1 = new Label(composite_5, SWT.CENTER);
		label_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		label_1.setBackground(new Color(Display.getCurrent(), 197,11,12));
		label_1.setImage(SWTResourceManager.getImage(MainApplication.class, "/icons/schule3.png"));
		
		Label label_2 = new Label(composite_5, SWT.RIGHT);
		label_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		label_2.setBackground(new Color(Display.getCurrent(), 197,11,12));
		label_2.setImage(SWTResourceManager.getImage(MainApplication.class, "/icons/büchereilogo.png"));
		
		Group grpInfo = new Group(composite, SWT.NONE);
		grpInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		FillLayout fl_grpInfo = new FillLayout(SWT.HORIZONTAL);
		fl_grpInfo.marginHeight = 5;
		fl_grpInfo.marginWidth = 5;
		grpInfo.setLayout(fl_grpInfo);
		grpInfo.setText("Info");
		
		StyledText browser = new StyledText(grpInfo, SWT.NONE);
		browser.setText("Sie arbeiten mit dem Büchereiprogramm der Grundschule West Königsbrunn.\nViel Vergnügen!");
		
		statistics = new Statistics(composite, SWT.NONE);
		statistics.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Label label_3 = new Label(composite, SWT.NONE);
		label_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
		
		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		Group grpCopyright = new Group(composite, SWT.NONE);
		grpCopyright.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		FillLayout fl_grpCopyright = new FillLayout(SWT.HORIZONTAL);
		fl_grpCopyright.marginHeight = 5;
		fl_grpCopyright.marginWidth = 5;
		grpCopyright.setLayout(fl_grpCopyright);
		grpCopyright.setText("Copyright");
		
		StyledText browser_1 = new StyledText(grpCopyright, SWT.NONE);
		browser_1.setText("Erstellt von Stefan Hackenberg. \r\nDas Programm wurde frei erstellt und unterliegt keinerlei Lizenz-Restriktionen. Es wurde nicht f\u00FCr den kommerziellen Einsatz bestimmt.\r\n\r\nSpenden sind jedoch gern willkommen.");
		
		tbtmMedien = new TabItem(tabFolder, SWT.NONE);
		tbtmMedien.setImage(SWTResourceManager.getImage(MainApplication.class, "/icons/Buch_klein.png"));
		tbtmMedien.setText("Medien");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmMedien.setControl(composite_2);
		composite_2.setLayout(new GridLayout(1, false));
		
		ToolBar toolBar_2 = new ToolBar(composite_2, SWT.FLAT | SWT.RIGHT);
		toolBar_2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		ToolItem tltmNeuesMediumHinzufgen = new ToolItem(toolBar_2, SWT.NONE);
		tltmNeuesMediumHinzufgen.setText("Neues Medium hinzuf\u00FCgen");
		tltmNeuesMediumHinzufgen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new StartBuchOverview(null, shlBibliothek, buecherTableView).run();
			}
		});
		tltmNeuesMediumHinzufgen.setImage(SWTResourceManager.getImage(MainApplication.class, "/icons/new.png"));
		tltmNeuesMediumHinzufgen.setToolTipText("Neues Medium");
		
		buecherTableView = new BuecherTableView(composite_2, SWT.NONE);
		buecherTableView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		
		tbtmSchler = new TabItem(tabFolder, SWT.NONE);
		tbtmSchler.setImage(SWTResourceManager.getImage(MainApplication.class, "/icons/Sch\u00FCler_klein.png"));
		tbtmSchler.setText("Sch\u00FCler");
		
		Composite composite_3 = new Composite(tabFolder, SWT.NONE);
		tbtmSchler.setControl(composite_3);
		composite_3.setLayout(new GridLayout(1, false));
		
		ToolBar toolBar_3 = new ToolBar(composite_3, SWT.FLAT | SWT.RIGHT);
		toolBar_3.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		ToolItem tltmNeuenSchlerHinzufgen = new ToolItem(toolBar_3, SWT.NONE);
		tltmNeuenSchlerHinzufgen.setText("Neuen Sch\u00FCler hinzuf\u00FCgen");
		tltmNeuenSchlerHinzufgen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new StartSchuelerOverview(null, shlBibliothek, schuelerTableView).run();
			}
		});
		tltmNeuenSchlerHinzufgen.setImage(SWTResourceManager.getImage(MainApplication.class, "/icons/new.png"));
		tltmNeuenSchlerHinzufgen.setToolTipText("Neuen Sch\u00FCler hinzuf\u00FCgen");
		
		schuelerTableView = new SchuelerTableView(composite_3, SWT.NONE);
		schuelerTableView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		tbtmAusleihen = new TabItem(tabFolder, SWT.NONE);
		tbtmAusleihen.setImage(SWTResourceManager.getImage(MainApplication.class, "/icons/Ausleihe_klein.png"));
		tbtmAusleihen.setText("Ausleihen");
		
		Composite composite_4 = new Composite(tabFolder, SWT.NONE);
		tbtmAusleihen.setControl(composite_4);
		composite_4.setLayout(new GridLayout(1, false));
		
		ToolBar toolBar_4 = new ToolBar(composite_4, SWT.FLAT | SWT.RIGHT);
		toolBar_4.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		
		ToolItem tltmNeueAusleihe = new ToolItem(toolBar_4, SWT.NONE);
		tltmNeueAusleihe.setText("Neue Ausleihe");
		tltmNeueAusleihe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Ausleihe a = new Ausleihe();
				try{
					if(ausleihenTableView.txtSchuelerId.getText().length() != 0) a.setS(Schueler.fromId(new Integer(ausleihenTableView.txtSchuelerId.getText())));
				}catch(NumberFormatException ex){}
				
				new StartAusleiheOverview(a, shlBibliothek, ausleihenTableView).run();
			}
		});
		tltmNeueAusleihe.setImage(SWTResourceManager.getImage(MainApplication.class, "/icons/new.png"));
		tltmNeueAusleihe.setToolTipText("Neue Ausleihe hinzuf\u00FCgen.");
		
		ToolItem tltmMassiveAusleihe = new ToolItem(toolBar_4, SWT.NONE);
		tltmMassiveAusleihe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Ausleihe a = new Ausleihe();
				try{
					if(ausleihenTableView.txtSchuelerId.getText().length() != 0) a.setS(Schueler.fromId(new Integer(ausleihenTableView.txtSchuelerId.getText())));
				}catch(NumberFormatException ex){}
				new StartMassiveAusleihe(a, shlBibliothek, ausleihenTableView).run();
			}
		});
		tltmMassiveAusleihe.setImage(SWTResourceManager.getImage(MainApplication.class, "/icons/massiveausleihen.png"));
		tltmMassiveAusleihe.setText("Massenausleihe / -r\u00FCckgabe");
		
		ausleihenTableView = new AusleihenTableView(composite_4, SWT.NONE);
		ausleihenTableView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		shlBibliothek.setMaximized(true);
		
		Menu menu = new Menu(shlBibliothek, SWT.BAR);
		shlBibliothek.setMenuBar(menu);
		
		MenuItem mntmOptionen = new MenuItem(menu, SWT.CASCADE);
		mntmOptionen.setText("Optionen");
		
		Menu menu_1 = new Menu(mntmOptionen);
		mntmOptionen.setMenu(menu_1);
		
		MenuItem mntmOptionen_1 = new MenuItem(menu_1, SWT.NONE);
		mntmOptionen_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new Preferences().run();
			}
		});
		mntmOptionen_1.setText("Optionen");
		
		MenuItem mntmErweitert_1 = new MenuItem(menu, SWT.CASCADE);
		mntmErweitert_1.setText("Listen und Statistiken");
		
		Menu menu_2 = new Menu(mntmErweitert_1);
		mntmErweitert_1.setMenu(menu_2);
		
		MenuItem mntmVersumnislisteDrucken = new MenuItem(menu_2, SWT.NONE);
		mntmVersumnislisteDrucken.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AusleihenSelector sel = new AusleihenSelector(shlBibliothek);
				sel.open();
				Vector<Ausleihe> vec = sel.ausleihenTableView.getAusleihen();
				PrinterJob job = PrinterJob.getPrinterJob();
				job.setPrintable(new PrintableUeberfaellig(vec));
				if(job.printDialog()){
					try{
						job.print();
					}catch (Exception ex) {
						Logger.logError(ex.getMessage());
					}
				}
			}
		});
		mntmVersumnislisteDrucken.setText("Vers\u00E4umnisliste drucken");
		
		new MenuItem(menu_2, SWT.SEPARATOR);
		
		MenuItem mntmStatistiken = new MenuItem(menu_2, SWT.NONE);
		mntmStatistiken.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlBibliothek.setMaximized(true);
				new StatisticView(shlBibliothek).open();
			}
		});
		mntmStatistiken.setText("Statistiken");
		
		MenuItem mntmInventur = new MenuItem(menu, SWT.CASCADE);
		mntmInventur.setText("Inventur");
		
		Menu menu_3 = new Menu(mntmInventur);
		mntmInventur.setMenu(menu_3);
		
		final MenuItem mntmInventurFortsetzen = new MenuItem(menu_3, SWT.NONE);
		mntmInventurFortsetzen.setText("Inventur fortsetzen");
		if(!new File(Preferences.getPrefs().getString("inventur.file")).exists()) mntmInventurFortsetzen.setEnabled(false);
		mntmInventurFortsetzen.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				InventurView v = new InventurView(shlBibliothek);
				v.open();
			}
		});
		
		MenuItem mntmNewItem = new MenuItem(menu_3, SWT.SEPARATOR);
		
		MenuItem mntmNeueInventurBeginnen = new MenuItem(menu_3, SWT.NONE);
		mntmNeueInventurBeginnen.setText("Neue Inventur beginnen");
		mntmNeueInventurBeginnen.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(new File(Preferences.getPrefs().getString("inventur.file")).exists()){
					boolean ret = MessageDialog.openConfirm(shlBibliothek, "Neue Inventur?", "Möchten Sie wirklich eine neue Inventur beginnen.\n\n" +
							"Die Daten der bestehenden gehen dabei verloren.");
					if(!ret) return;
				}
					
				FileDialog fd = new FileDialog(shlBibliothek);
				fd.setText("Wählen Sie eine neue Datei aus, in der die Inventur gespeichert werden soll.");
				fd.setFileName("Inventur "+DateUtils.longFormat.format(new Date(System.currentTimeMillis()))+".xml");
				fd.setFilterExtensions(new String[]{"*.xml"});
				String file = fd.open();
				if(file == null) return;
				String filename = fd.getFilterPath()+"\\"+fd.getFileName();
				if(!filename.toLowerCase().endsWith(".xml")) filename += ".xml";
				File f = new File(filename);
				Preferences.getPrefs().setValue("inventur.file", f.getAbsolutePath());
				try {
					Preferences.getPrefs().save();
				} catch (IOException e1) {}
				
				mntmInventurFortsetzen.setEnabled(true);
				
				new InventurView(shlBibliothek).open();
			}
		});
		
	}
	
	public void updateAllTables(){
		buecherTableView.updateTable();
		schuelerTableView.updateTable();
		ausleihenTableView.updateTable();
	}
	
	public void selectMedium(Buch b){
		tabFolder.setSelection(tbtmMedien);
		buecherTableView.updateTable(b);
	}
	
	public void selectSchueler(Schueler s){
		tabFolder.setSelection(tbtmSchler);
		schuelerTableView.updateTable();
		schuelerTableView.selectSchueler(s);
	}
	
	public void selectAusleihe(Ausleihe a){
		System.out.println("MainApplication.selectAusleihe()");
		tabFolder.setSelection(tbtmAusleihen);
		if(a.isTooLate()) ausleihenTableView.btnVersumnisliste.setSelection(true);
		else ausleihenTableView.btnOffeneAusleihen.setSelection(true);
		ausleihenTableView.updateTable(a);
	}
	
	public void selectAusleihenTab(){
		tabFolder.setSelection(tbtmAusleihen);
	}
	
	public void selectSchuelerTab(){
		tabFolder.setSelection(tbtmSchler);
	}
	
	public void selectMediumTab(){
		tabFolder.setSelection(tbtmMedien);
	}
	
	public void selectStatisticsTab(){
		tabFolder.setSelection(tbtmInfo);
	}
}
