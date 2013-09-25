package gui.overviews;

import gui.StringConstants;
import gui.selectors.BuecherSelector;
import gui.selectors.SchuelerSelector;
import gui.validators.IdBoolConverter;
import gui.validators.IntGreaterZeroValidator;
import gui.validators.NullValidator;
import model.Ausleihe;
import model.Buch;
import model.Schueler;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.dialog.TitleAreaDialogSupport;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import util.AnmerkungDialog;
import util.Java2SqlDateConverter;
import util.Sql2JavaDateConverter;
import util.TitleDialog;

public class AusleiheOverview extends TitleDialog {
	private DataBindingContext m_bindingContext;
	private Text dateTage;
	private DateTime dateVon;
	private Text txtAnmerkungen;
	
	private Ausleihe ausleihe = new Ausleihe();
	private Text txtMedium;
	private Text txtSchueler;
	private String messageText = "Übersicht über die gewählte Ausleihe";
	private Button btnVormerken;
	private ToolItem btnAusleiheBeginnen;
	private ToolItem btnZuruckgeben;
	private ToolItem btnDelete;
	private Text txtVorgemerktAn;
	private ToolItem btnReactivate;
	
	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public AusleiheOverview(Shell parentShell, Ausleihe ausleihe) {
		super(parentShell);
		setShellStyle(SWT.TITLE | SWT.APPLICATION_MODAL);
		setHelpAvailable(false);
		if(ausleihe != null) this.ausleihe = ausleihe;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceManager.getImage(AusleiheOverview.class, "/icons/Ausleihe.png"));
		setMessage(messageText);
		setTitle("Ausleihen\u00FCbersicht");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		ToolBar toolBar_2 = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
		
		btnDelete = new ToolItem(toolBar_2, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean ret = MessageDialog.openConfirm(getShell(), "Bestätigen", StringConstants.CONFIRM_DELETE_AUSLEIHE);
				if(!ret) return;
				ausleihe.entfernen();
				close();
			}
		});
		btnDelete.setImage(SWTResourceManager.getImage(AusleiheOverview.class, "/icons/delete.png"));
		btnDelete.setToolTipText("L\u00F6schen");
		
		btnZuruckgeben = new ToolItem(toolBar_2, SWT.NONE);
		btnZuruckgeben.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ausleihe.zuruckgeben();
				close();
			}
		});
		btnZuruckgeben.setImage(SWTResourceManager.getImage(AusleiheOverview.class, "/icons/zurueckgeben.png"));
		btnZuruckgeben.setToolTipText("zur\u00FCckgeben");
		
		btnReactivate = new ToolItem(toolBar_2, SWT.NONE);
		btnReactivate.setImage(SWTResourceManager.getImage(AusleiheOverview.class, "/icons/ausleihe-reactivate.png"));
		btnReactivate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ausleihe.reactivate();
				close();
			}
		});
		btnReactivate.setToolTipText("Ausleihe reaktivieren.");
		
		Group grpMedium = new Group(composite, SWT.NONE);
		grpMedium.setText("Medium");
		GridLayout gl_grpMedium = new GridLayout(3, false);
		gl_grpMedium.horizontalSpacing = 20;
		grpMedium.setLayout(gl_grpMedium);
		grpMedium.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpMedium.setBounds(0, 0, 70, 82);
		
		Label lblMedium = new Label(grpMedium, SWT.NONE);
		lblMedium.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblMedium.setText("Medium");
		
		txtMedium = new Text(grpMedium, SWT.BORDER | SWT.SINGLE);
		txtMedium.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				ausleihe.setB(Buch.getVerfuegbar(txtMedium.getText(), ausleihe.getVon(), ausleihe.getBis(), ausleihe));
			}
		});
		txtMedium.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		ToolBar toolBar = new ToolBar(grpMedium, SWT.FLAT | SWT.RIGHT);
		
		ToolItem toolEditMedium = new ToolItem(toolBar, SWT.NONE);
		toolEditMedium.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BuecherSelector s = new BuecherSelector(getParentShell(), ausleihe.getB());
				s.open();
				if(s.getReturnCode() == TitleAreaDialog.OK){
					ausleihe.setB(s.getBuch());
				}
			}
		});
		toolEditMedium.setToolTipText("Bearbeiten");
		toolEditMedium.setImage(SWTResourceManager.getImage(AusleiheOverview.class, "/icons/edit.png"));
		
		Group grpScheler = new Group(composite, SWT.NONE);
		grpScheler.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpScheler.setText("Sch\u00FCler");
		GridLayout gl_grpScheler = new GridLayout(3, false);
		gl_grpScheler.horizontalSpacing = 20;
		grpScheler.setLayout(gl_grpScheler);
		
		Label lblSschler = new Label(grpScheler, SWT.NONE);
		lblSschler.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSschler.setText("Sch\u00FCler");
		
		txtSchueler = new Text(grpScheler, SWT.BORDER | SWT.SINGLE);
		txtSchueler.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				try{
					ausleihe.setS(Schueler.fromId(new Integer(txtSchueler.getText())));
				}catch(NumberFormatException ex){}
			}
		});
		txtSchueler.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		ToolBar toolBar_1 = new ToolBar(grpScheler, SWT.FLAT | SWT.RIGHT);
		
		ToolItem toolEditSchueler = new ToolItem(toolBar_1, SWT.NONE);
		toolEditSchueler.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SchuelerSelector s = new SchuelerSelector(getParentShell(),ausleihe.getS());
				s.open();
				if(s.getReturnCode() == TitleAreaDialog.OK){
					ausleihe.setS(s.getSchueler());
				}
			}
		});
		toolEditSchueler.setImage(SWTResourceManager.getImage(AusleiheOverview.class, "/icons/edit.png"));
		toolEditSchueler.setToolTipText("Bearbeiten");
		
		Group grpZeitraum = new Group(composite, SWT.NONE);
		grpZeitraum.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		grpZeitraum.setText("Zeitraum");
		GridLayout gl_grpZeitraum = new GridLayout(8, false);
		gl_grpZeitraum.horizontalSpacing = 20;
		grpZeitraum.setLayout(gl_grpZeitraum);
		
		Label lblVon = new Label(grpZeitraum, SWT.NONE);
		lblVon.setText("Von");
		
		dateVon = new DateTime(grpZeitraum, SWT.BORDER | SWT.DROP_DOWN);
		new Label(grpZeitraum, SWT.NONE);
		
		Label lblBis = new Label(grpZeitraum, SWT.NONE);
		lblBis.setText("Dauer: ");
		
		dateTage = new Text(grpZeitraum, SWT.BORDER | SWT.DROP_DOWN);
		GridData gd_dateTage = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_dateTage.widthHint = 52;
		dateTage.setLayoutData(gd_dateTage);
		
		Label lblTage = new Label(grpZeitraum, SWT.NONE);
		lblTage.setText("Tage");
		new Label(grpZeitraum, SWT.NONE);
		
		ToolBar toolBar_4 = new ToolBar(grpZeitraum, SWT.FLAT | SWT.RIGHT);
		
		ToolItem btnNextFreeDate = new ToolItem(toolBar_4, SWT.NONE);
		btnNextFreeDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(ausleihe.getB() != null) ausleihe.setVon(ausleihe.getB().getNextFreeDate());
			}
		});
		btnNextFreeDate.setImage(SWTResourceManager.getImage(AusleiheOverview.class, "/icons/calendar_klein.png"));
		btnNextFreeDate.setToolTipText("N\u00E4chsten freien Termin ausw\u00E4hlen.");
		
		Group grpVormerken = new Group(composite, SWT.NONE);
		grpVormerken.setLayout(new GridLayout(3, false));
		grpVormerken.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		grpVormerken.setText("Vormerken");
		
		btnVormerken = new Button(grpVormerken, SWT.CHECK);
		btnVormerken.setText("Nur vormerken");
		new Label(grpVormerken, SWT.NONE);
		
		ToolBar toolBar_3 = new ToolBar(grpVormerken, SWT.FLAT | SWT.RIGHT);
		
		btnAusleiheBeginnen = new ToolItem(toolBar_3, SWT.NONE);
		btnAusleiheBeginnen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ausleihe.vorgemerktAusleihen();
				close();
			}
		});
		btnAusleiheBeginnen.setImage(SWTResourceManager.getImage(AusleiheOverview.class, "/icons/ausleihen.png"));
		btnAusleiheBeginnen.setToolTipText("Ausleihe beginnen");
		
		Group grpSonstiges = new Group(composite, SWT.NONE);
		grpSonstiges.setLayout(new GridLayout(1, false));
		grpSonstiges.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		grpSonstiges.setText("Anmerkungen");
		
		txtAnmerkungen = new Text(grpSonstiges, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		txtAnmerkungen.setToolTipText("Anmerkungen");
		txtAnmerkungen.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		Group grpVorgemerktAn = new Group(composite, SWT.NONE);
		grpVorgemerktAn.setLayout(new FillLayout(SWT.HORIZONTAL));
		grpVorgemerktAn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		grpVorgemerktAn.setText("Vorgemerkt an");
		
		txtVorgemerktAn = new Text(grpVorgemerktAn, SWT.BORDER | SWT.READ_ONLY);
		txtVorgemerktAn.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		txtVorgemerktAn.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		txtVorgemerktAn.setEditable(false);

		
		
		return area;
	}
	
	@Override
	protected void okPressed() {
		AnmerkungDialog.getDialog(getShell(), ausleihe.getB());
		ausleihe.eintragen();
		
		super.okPressed();
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				false);
		button.setText("Speichern");
		Button button_1 = createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		button_1.setText("Abbrechen");
	}
	
	@Override
	protected void initValues() {
		m_bindingContext = initDataBindings();
		setBindingValues(m_bindingContext, ausleihe);
		super.initValues();
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(657, 617);
	}

	/**
	 * @return the ausleihe
	 */
	public Ausleihe getAusleihe() {
		return ausleihe;
	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		
		
		
		//Schueler
		final IObservableValue sObservable = BeansObservables.observeValue(ausleihe, "s");
		IObservableValue target = SWTObservables.observeText(txtSchueler);
		UpdateValueStrategy str = new UpdateValueStrategy();
		str.setAfterGetValidator(new NullValidator());
		str.setConverter(new IConverter() {
			
			@Override
			public Object getToType() {
				return String.class;
			}
			
			@Override
			public Object getFromType() {
				return Schueler.class;
			}
			
			@Override
			public Object convert(Object fromObject) {
				Schueler s = ((Schueler)fromObject);
				if(s != null) return s.toNiceString();
				return "";
			}
		});
		Binding binding = bindingContext.bindValue(target, sObservable, null, str);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
		
		//Medium		
		final IObservableValue bObservable = BeansObservables.observeValue(ausleihe, "b");
		target = SWTObservables.observeText(txtMedium);
		str = new UpdateValueStrategy();
		str.setAfterGetValidator(new NullValidator());
		str.setConverter(new IConverter() {
			
			@Override
			public Object getToType() {
				return String.class;
			}
			
			@Override
			public Object getFromType() {
				return Buch.class;
			}
			
			@Override
			public Object convert(Object fromObject) {
				Buch b = ((Buch)fromObject);
				if(b != null){
					return b.toNiceString();
				}
				return "";
			}
		});
		binding = bindingContext.bindValue(target, bObservable, null, str);
		ControlDecorationSupport.create(binding, SWT.LEFT | SWT.TOP);
		
		//
		
		final IObservableValue vonObservable = BeansObservables.observeValue(ausleihe, "von");
		final IObservableValue bisObservable = BeansObservables.observeValue(ausleihe, "bis");
		target = SWTObservables.observeSelection(dateVon);
		str = new UpdateValueStrategy();
		str.setConverter(new Java2SqlDateConverter());
		UpdateValueStrategy strback = new UpdateValueStrategy();
		strback.setConverter(new Sql2JavaDateConverter());
		binding = bindingContext.bindValue(target, vonObservable, strback, str);
		
		//
		IObservableValue dauerObs = BeansObservables.observeValue(ausleihe, "dauer");
		target = SWTObservables.observeText(dateTage, SWT.Modify);
		str = new UpdateValueStrategy();
//		str.setConverter(new Anzahl2BisConverter(ausleihe));
		str.setAfterGetValidator(new IntGreaterZeroValidator());
		strback = new UpdateValueStrategy();
//		strback.setConverter(new Bis2AnzahlConverter(ausleihe));
		binding = bindingContext.bindValue(target, dauerObs, str, strback);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
		
		
		//Vorgemerkt
		IObservableValue model = BeansObservables.observeValue(ausleihe, "vorgemerkt");
		target = SWTObservables.observeSelection(btnVormerken);
		binding = bindingContext.bindValue(target, model);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);

		//button enabling
		model = BeansObservables.observeValue(ausleihe, "id");
		target = SWTObservables.observeEnabled(btnZuruckgeben);
		str = new UpdateValueStrategy();
		str.setConverter(new IdBoolConverter());
		binding = bindingContext.bindValue(target, model, null, str);
		
		target = SWTObservables.observeEnabled(btnDelete);
		binding = bindingContext.bindValue(target, model, null, str);
		
		target = SWTObservables.observeEnabled(btnAusleiheBeginnen);
		binding = bindingContext.bindValue(target, model, null, str);
		
		model = BeansObservables.observeValue(ausleihe, "done");
		target = SWTObservables.observeEnabled(btnReactivate);
		binding = bindingContext.bindValue(target, model);
		
		//anmerkungen
		target = SWTObservables.observeText(txtAnmerkungen, SWT.Modify);
		model = BeansObservables.observeValue(ausleihe, "anmerkungen");
		binding = bindingContext.bindValue(target, model);
		
		target = SWTObservables.observeText(txtVorgemerktAn);
		model = BeansObservables.observeValue(ausleihe, "vorgemerktAn");
		str = new UpdateValueStrategy();
		str.setConverter(new IConverter() {
			
			@Override
			public Object getToType() {
				return String.class;
			}
			
			@Override
			public Object getFromType() {
				return Schueler.class;
			}
			
			@Override
			public Object convert(Object fromObject) {
				if(fromObject == null) return "";
				return ((Schueler)fromObject).toNiceString();
			}
		});
		binding = bindingContext.bindValue(target, model, null, str);
		
		
		bindingContext.addValidationStatusProvider(new MultiValidator() {
			
			@Override
			protected IStatus validate() {
				if(bObservable.getValue() != null){
					if(ausleihe.getB().isVerliehen((java.sql.Date)vonObservable.getValue(), (java.sql.Date)bisObservable.getValue(), ausleihe)) return  ValidationStatus.error(StringConstants.VALIDATION_ERROR_ALREADYVERLIEHEN);
					else if(ausleihe.getB().isVorgemerkt((java.sql.Date)vonObservable.getValue(), (java.sql.Date)bisObservable.getValue(), ausleihe)) return  ValidationStatus.error(StringConstants.VALIDATION_ERROR_ALREADYVORGEMERKT);
				}
				return ValidationStatus.ok(); 
			}
		});
		
		
		//Support
		TitleAreaDialogSupport.create(this, bindingContext);
		
		return bindingContext;
	}
}
