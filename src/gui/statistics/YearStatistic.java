package gui.statistics;

import java.util.ArrayList;

import model.Statistic;
import model.Statistic_SchuelerUndAnzahl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import stefan.piechart.PieChart;
import stefan.piechart.PieChartData;
import util.FontUtil;

public class YearStatistic extends Composite {
	private int year = 0;
	private Label lblAusleihen;
	private Label lblAusgelieheneMedien;
	private PieChart pieChartKat,pieChartKl;
	private Label lblPieTitleKat, lblPieTitleKl;
	private Tree treeKat, treeKl;
	
	private static final String ALLE_KLASSEN = "Alle Klassen", K1 = "1. Klassen", K2 = "2. Klassen", K3 = "3. Klassen", K4 = "4. Klassen", SONSTIGE = "Rest", GESAMT = "Gesamt", NACH_KLASSEN="Nach Klassen";
	private static final String male = "Jungen", female = "Mädchen";
	
	private static final Color COLOR_TOP = SWTResourceManager.getColor(240,255,240);
	private static final Color COLOR_FLOP = SWTResourceManager.getColor(255,240,240);
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public YearStatistic(Composite parent, int style, int year) {
		super(parent, SWT.NONE);
		this.year = year;
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		
		TabItem tbtmbersicht = new TabItem(tabFolder, SWT.NONE);
		tbtmbersicht.setText("\u00DCbersicht");
		
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmbersicht.setControl(composite);
		composite.setLayout(new GridLayout(1, false));
		
		Group grpJahresberblick = new Group(composite, SWT.NONE);
		GridLayout gl_grpJahresberblick = new GridLayout(2, false);
		gl_grpJahresberblick.verticalSpacing = 10;
		gl_grpJahresberblick.horizontalSpacing = 15;
		grpJahresberblick.setLayout(gl_grpJahresberblick);
		grpJahresberblick.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpJahresberblick.setText("Schuljahr-\u00DCberblick");
		
		Label lblImSchuljahrWurden = new Label(grpJahresberblick, SWT.NONE);
		lblImSchuljahrWurden.setText("Ausleihen:");
		
		lblAusgelieheneMedien = new Label(grpJahresberblick, SWT.NONE);
		
		Label lblAnzahlAnVerschiedenen = new Label(grpJahresberblick, SWT.NONE);
		lblAnzahlAnVerschiedenen.setText("Ausgeliehene Medien:");
		
		lblAusleihen = new Label(grpJahresberblick, SWT.NONE);
		
		
		TabItem tbtmKat = new TabItem(tabFolder, SWT.NONE);
		tbtmKat.setText("Kategorien-Statistik");
		
		Composite composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmKat.setControl(composite_1);
		composite_1.setLayout(new GridLayout(2, false));
		
		treeKat = calcKatTree(composite_1);
		treeKat.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2));
		
		lblPieTitleKat = new Label(composite_1, SWT.NONE);
		lblPieTitleKat.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		lblPieTitleKat.setFont(FontUtil.boldFontBigg);
		
		pieChartKat = new PieChart(composite_1, SWT.NONE, (PieChartData[]) null);
		pieChartKat.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmKl = new TabItem(tabFolder, SWT.NONE);
		tbtmKl.setText("Jahrgangs-Statistik");
		
		composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmKl.setControl(composite_1);
		composite_1.setLayout(new GridLayout(2, false));
		
		treeKl = calcKlassenTree(composite_1);
		treeKl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2));
		
		lblPieTitleKl = new Label(composite_1, SWT.NONE);
		lblPieTitleKl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		lblPieTitleKl.setFont(FontUtil.boldFontBigg);
		
		pieChartKl = new PieChart(composite_1, SWT.NONE, (PieChartData[]) null);
		pieChartKl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		
		TabItem tbtmTop = new TabItem(tabFolder, SWT.NONE);
		tbtmTop.setText("Top-Flop-Leser-Statistik");
		
		ScrolledComposite scrolled = new ScrolledComposite(tabFolder, SWT.V_SCROLL);
		scrolled.setAlwaysShowScrollBars(false);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);
		scrolled.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				ScrolledComposite scrollComposite = (ScrolledComposite)e.widget;
				Rectangle r = scrollComposite.getClientArea();
		        scrollComposite.setMinSize(scrollComposite.getContent().computeSize(r.width,
		            SWT.DEFAULT));
			}
		});
		
		composite_1 = createTopFlopComposite(scrolled);
		scrolled.setContent(composite_1);
		tbtmTop.setControl(scrolled);
		
		initValues();
	}
	
	private Composite createTopFlopComposite(Composite parent){
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1,false));
		
		Label l = FontUtil.getBoldBiggLabel(c, "Top Leser");
		l.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		Composite c2 = new Composite(c, SWT.NONE);
		RowLayout r = new RowLayout();
		r.spacing = 10;
		r.fill = true;
		c2.setLayout(r);
		c2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		//Top Leser
		ArrayList<String> klassen = Statistic.getKlassen(year);
		for(String s: klassen){
			Composite k = new Composite(c2, SWT.BORDER);
			k.setBackground(COLOR_TOP);
			k.setLayout(new GridLayout(1,false));
			//k.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
			l = FontUtil.getBoldBigLabel(k, s);
			l.setBackground(COLOR_TOP);
			//l.setLayoutData(new GridData(SWT.FILL));
			Composite inner = new Composite(k, SWT.NONE);
			inner.setBackground(COLOR_TOP);
			//inner.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,true));
			
			GridLayout g = new GridLayout(2, false);
			g.horizontalSpacing = 2;
			g.verticalSpacing = 5;
			g.marginTop = 5;
			inner.setLayout(g);
			
			ArrayList<Statistic_SchuelerUndAnzahl> stats = Statistic.getTop5(year,s);
			for(Statistic_SchuelerUndAnzahl anz: stats){
				l = new Label(inner, SWT.NONE);
				l.setBackground(COLOR_TOP);
				l.setText(anz.vorname+" "+anz.nachname);
				//l.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
				l = FontUtil.getBoldLabel(inner, "("+anz.count+")");
				l.setBackground(COLOR_TOP);
				//l.setLayoutData(new GridData(SWT.FILL));
			}
			
		}
		
		
		l = FontUtil.getBoldBiggLabel(c, "Flop Leser");
		l.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		c2 = new Composite(c, SWT.NONE);
		c2.setLayout(r);
		c2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		//Top Leser
		for(String s: klassen){
			Composite k = new Composite(c2, SWT.BORDER);
			k.setBackground(COLOR_FLOP);
			k.setLayout(new GridLayout(1,false));
			//k.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
			l = FontUtil.getBoldBigLabel(k, s);
			l.setBackground(COLOR_FLOP);
			//l.setLayoutData(new GridData(SWT.FILL));
			Composite inner = new Composite(k, SWT.NONE);
			inner.setBackground(COLOR_FLOP);
			//inner.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true,true));
			
			GridLayout g = new GridLayout(2, false);
			g.horizontalSpacing = 2;
			g.verticalSpacing = 5;
			g.marginTop = 5;
			inner.setLayout(g);
			
			ArrayList<Statistic_SchuelerUndAnzahl> stats = Statistic.getFlop5(year,s);
			for(Statistic_SchuelerUndAnzahl anz: stats){
				l = new Label(inner, SWT.NONE);
				l.setBackground(COLOR_FLOP);
				l.setText(anz.vorname+" "+anz.nachname);
				//l.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true));
				l = FontUtil.getBoldLabel(inner, "("+anz.count+")");
				l.setBackground(COLOR_FLOP);
				//l.setLayoutData(new GridData(SWT.FILL));
			}
			
		}
		return c;
	}
	
	private Tree calcKatTree(Composite parent){
		Tree tree = new Tree(parent, SWT.BORDER);
		
		TreeItem t = new TreeItem(tree, SWT.NONE);
		t.setText(ALLE_KLASSEN);
		t.setExpanded(true);
			TreeItem t1 = new TreeItem(t, SWT.NONE);
			t1.setText(female);
			t1.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(male);
			t1.setExpanded(true);
		
		t = new TreeItem(tree, SWT.NONE);
		t.setText(K1);
		t.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(female);
			t1.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(male);
			t1.setExpanded(true);
			
		t = new TreeItem(tree, SWT.NONE);
		t.setText(K2);
		t.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(female);
			t1.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(male);
			t1.setExpanded(true);
				
		t = new TreeItem(tree, SWT.NONE);
		t.setText(K3);
		t.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(female);
			t1.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(male);
			t1.setExpanded(true);
		
		t = new TreeItem(tree, SWT.NONE);
		t.setText(K4);
		t.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(female);
			t1.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(male);
			t1.setExpanded(true);
			
		t = new TreeItem(tree, SWT.NONE);
		t.setText(SONSTIGE);
		t.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(female);
			t1.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(male);
			t1.setExpanded(true);
		
			
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				treeKatSelected(e);
			}
		});
		return tree;
	}
	
	private Tree calcKlassenTree(Composite parent){
		Tree tree = new Tree(parent, SWT.BORDER);
		
		TreeItem t = new TreeItem(tree, SWT.NONE);
		t.setText(GESAMT);
		t.setExpanded(true);
			TreeItem t1 = new TreeItem(t, SWT.NONE);
			t1.setText(female);
			t1.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(male);
			t1.setExpanded(true);
			
		t = new TreeItem(tree, SWT.NONE);
		t.setText(NACH_KLASSEN);
		t.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(female);
			t1.setExpanded(true);
			t1 = new TreeItem(t, SWT.NONE);
			t1.setText(male);
			t1.setExpanded(true);
		
			
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				treeKlassenSelected(e);
			}
		});
		return tree;
	}
	
	private void treeKatSelected(SelectionEvent e){
		TreeItem t = (TreeItem)e.item;
		String text = t.getText();
		String gender = Statistic.GENDER_BOTH;
		String lblText = "Ausleihenübersicht nach Kategorien von: ";
		if(t.getParentItem() != null){
			if(text == female) gender = Statistic.GENDER_FEMALE;
			else gender = Statistic.GENDER_MALE;
			text = t.getParentItem().getText();
			lblText += t.getParentItem().getText()+" / "+t.getText();
		}
		else lblText += t.getText();
		lblPieTitleKat.setText(lblText);
		
		if(text == ALLE_KLASSEN) selectPieKat(gender, -1);
		else if(text == K1) selectPieKat(gender, 1);
		else if(text == K2) selectPieKat(gender, 2);
		else if(text == K3) selectPieKat(gender, 3);
		else if(text == K4) selectPieKat(gender, 4);
		else if(text == SONSTIGE) selectPieKat(gender, 0);
	}
	
	private void treeKlassenSelected(SelectionEvent e){
		TreeItem t = ((TreeItem)e.item);
		String txt = t.getText();
		String gender = Statistic.GENDER_BOTH;
		String lblTxt = "Ausleihenübersicht nach Jahrgängen von: ";
		if(t.getParentItem() != null){
			if(txt == male) gender = Statistic.GENDER_MALE;
			else if(txt == female) gender = Statistic.GENDER_FEMALE;
			txt = t.getParentItem().getText();
			lblTxt += txt+" / "+t.getText();
		}else lblTxt += txt;
		lblPieTitleKl.setText(lblTxt);
		selectPieKlassen(txt, gender);
	}
	
	private void expandAllTreeItems(Tree tree){
		TreeItem[] items = tree.getItems();
		if(items != null)
			for(TreeItem i: items)
				i.setExpanded(true);
	}
	
	private void initValues(){
		lblAusgelieheneMedien.setText(""+Statistic.getAusgelieheneMedien(year));
		lblAusleihen.setText(""+Statistic.getAusleihen(year));
		selectPieKat(Statistic.GENDER_BOTH, -1);
		selectPieKlassen(GESAMT,Statistic.GENDER_BOTH);
		expandAllTreeItems(treeKat);
		expandAllTreeItems(treeKl);
	}
	
	private void selectPieKat(String gender, int jahrgangsstufe){
		PieChartData[] data = Statistic.getPieData_Kategorien(year, gender, jahrgangsstufe);
		pieChartKat.setData(data);
	}
	
	private void selectPieKlassen(String nachKlassen,String gender){
		if(nachKlassen == NACH_KLASSEN)
			pieChartKl.setData(Statistic.getPieData_Klassen(year, gender));
		else
			pieChartKl.setData(Statistic.getPieData_Jahrgang(year, gender));
	}

	public int getYear() {
		return year;
	}
}
