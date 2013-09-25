package statistics;

import gui.MainApplication;
import gui.overviews.StyledTableOverview;
import model.Buch;
import model.Schueler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;

public class Statistics extends Group {
	private StyledText txtMedienAnzahl;
	private StyledText txtMedienVerliehen;
	private StyledText txtMedienNeverVerliehen;
	private MedienTop medienTop;
	private KategorienTop kategorienTop;
	private StyledText txtSchuelerAnzahl;
	private StyledText txtSchuelerAktuell;
	private StyledText txtSchuelerPassiv;
	private SchuelerTop schuelerTop;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Statistics(Composite parent, int style) {
		super(parent, style);
		setText("Statistiken");
		FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
		fillLayout.spacing = 10;
		fillLayout.marginHeight = 10;
		fillLayout.marginWidth = 10;
		setLayout(fillLayout);
		
		Group grpMedien = new Group(this, SWT.NONE);
		grpMedien.setText("Medien");
		GridLayout gl_grpMedien = new GridLayout(2, false);
		gl_grpMedien.marginHeight = 10;
		gl_grpMedien.marginWidth = 10;
		gl_grpMedien.verticalSpacing = 15;
		gl_grpMedien.horizontalSpacing = 15;
		grpMedien.setLayout(gl_grpMedien);
		
		Link lblAnzahlAnVerzeichneten = new Link(grpMedien, SWT.NONE);
		lblAnzahlAnVerzeichneten.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MainApplication.MAIN.selectMediumTab();
			}
		});
		lblAnzahlAnVerzeichneten.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAnzahlAnVerzeichneten.setText("<a>Registrierte Medien:</a>");
		
		txtMedienAnzahl = new StyledText(grpMedien, SWT.NONE);
		txtMedienAnzahl.setEditable(false);
		txtMedienAnzahl.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		Link lblAktuellVerlieheneMedien = new Link(grpMedien, SWT.NONE);
		lblAktuellVerlieheneMedien.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MainApplication.MAIN.selectAusleihenTab();
			}
		});
		lblAktuellVerlieheneMedien.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAktuellVerlieheneMedien.setText("<a>Aktuell verliehene Medien:</a>");
		
		txtMedienVerliehen = new StyledText(grpMedien, SWT.NONE);
		txtMedienVerliehen.setEditable(false);
		txtMedienVerliehen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Link lblNochNieVerliehene = new Link(grpMedien, SWT.NONE);
		lblNochNieVerliehene.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StyledTableOverview.getDialog(getShell(), Buch.getNochNieVerliehen().toArray(), "Hier sehen Sie noch nie verliehene Medien.");
			}
		});
		lblNochNieVerliehene.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNochNieVerliehene.setText("<a>Noch nie verliehene Medien:</a>");
		
		txtMedienNeverVerliehen = new StyledText(grpMedien, SWT.NONE);
		txtMedienNeverVerliehen.setEditable(false);
		txtMedienNeverVerliehen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Group grpTop = new Group(grpMedien, SWT.NONE);
		grpTop.setText("Top 5 Medien");
		FillLayout fl_grpTop = new FillLayout(SWT.HORIZONTAL);
		fl_grpTop.marginHeight = 5;
		fl_grpTop.marginWidth = 5;
		grpTop.setLayout(fl_grpTop);
		grpTop.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		
		medienTop = new MedienTop(grpTop, SWT.NONE);
		
		Group grpTopKategorien = new Group(grpMedien, SWT.NONE);
		FillLayout fl_grpTopKategorien = new FillLayout(SWT.HORIZONTAL);
		fl_grpTopKategorien.marginHeight = 5;
		fl_grpTopKategorien.marginWidth = 5;
		grpTopKategorien.setLayout(fl_grpTopKategorien);
		grpTopKategorien.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		grpTopKategorien.setText("Top 5 Kategorien");
		
		kategorienTop = new KategorienTop(grpTopKategorien, SWT.NONE);
		
		Group grpSchler = new Group(this, SWT.NONE);
		grpSchler.setText("Sch\u00FCler");
		GridLayout gl_grpSchler = new GridLayout(2, false);
		gl_grpSchler.verticalSpacing = 15;
		gl_grpSchler.horizontalSpacing = 15;
		gl_grpSchler.marginHeight = 10;
		gl_grpSchler.marginWidth = 10;
		grpSchler.setLayout(gl_grpSchler);
		
		Link link = new Link(grpSchler, SWT.NONE);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MainApplication.MAIN.selectSchuelerTab();
			}
		});
		link.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		link.setText("<a>Registrierte Sch\u00FCler:</a>");
		
		txtSchuelerAnzahl = new StyledText(grpSchler, SWT.NONE);
		txtSchuelerAnzahl.setEditable(false);
		txtSchuelerAnzahl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Link link_1 = new Link(grpSchler, SWT.NONE);
		link_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MainApplication.MAIN.selectAusleihenTab();
			}
		});
		link_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		link_1.setText("<a>Aktuell lesende Sch\u00FCler:</a>");
		
		txtSchuelerAktuell = new StyledText(grpSchler, SWT.NONE);
		txtSchuelerAktuell.setEditable(false);
		txtSchuelerAktuell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Link link_2 = new Link(grpSchler, SWT.NONE);
		link_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StyledTableOverview.getDialog(getShell(), Schueler.getPassiv().toArray(), "Hier sehen Sie diejenigen Schüler, die noch nie etwas ausgeliehen haben.");
			}
		});
		link_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		link_2.setText("<a>Passive Sch\u00FCler:</a>");
		
		txtSchuelerPassiv = new StyledText(grpSchler, SWT.NONE);
		txtSchuelerPassiv.setEditable(false);
		txtSchuelerPassiv.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Group grpTopSchler = new Group(grpSchler, SWT.NONE);
		grpTopSchler.setText("Top 5 Sch\u00FCler");
		FillLayout fl_grpTopSchler = new FillLayout(SWT.HORIZONTAL);
		fl_grpTopSchler.marginHeight = 5;
		fl_grpTopSchler.marginWidth = 5;
		grpTopSchler.setLayout(fl_grpTopSchler);
		grpTopSchler.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		
		schuelerTop = new SchuelerTop(grpTopSchler, SWT.NONE);

		update();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	@Override
	public void update() {
		txtMedienAnzahl.setText(""+Buch.getCount());
		txtMedienVerliehen.setText(""+Buch.getCountAktuellVerliehen());
		txtMedienNeverVerliehen.setText(""+Buch.getCountNochNieVerliehen());
		medienTop.update();
		kategorienTop.update();
		
		txtSchuelerAnzahl.setText(""+Schueler.getCount());
		txtSchuelerAktuell.setText(""+Schueler.getCountAktuellVerliehen());
		txtSchuelerPassiv.setText(""+Schueler.getCountPassiv());
		schuelerTop.update();
		
		super.update();
	}
}
