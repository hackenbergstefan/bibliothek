package gui.views;

import gui.selectors.BuecherSelector;
import gui.selectors.SchuelerSelector;
import gui.validators.IntGreaterZeroValidator;
import gui.validators.NullBoolConverter;
import gui.validators.NullValidator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;

import model.Ausleihe;
import model.Buch;
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
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

import util.AnmerkungDialog;
import util.Java2SqlDateConverter;
import util.Sql2JavaDateConverter;
import util.TitleDialog;

public class MassiveAusleihe extends TitleDialog {
	private class TableLabelProvider extends StyledCellLabelProvider{
		@Override
		public void update(ViewerCell cell) {
			Buch b = null;
			if(cell.getElement() instanceof Buch)
				b = (Buch)cell.getElement();
			else if(cell.getElement() instanceof Ausleihe)
				b = ((Ausleihe)cell.getElement()).getB();
			if(b != null){
				StyledString string = b.toStyledString();
				cell.setText(string.getString());
				cell.setStyleRanges(string.getStyleRanges());
			}
			super.update(cell);
		}
	}
	
	private Text txtSchuelerID;
	private Table tableRueck, tableAus;
	private Text txtMediumIsbnRueck, txtMediumIsbnAus;
	
	private Schueler schueler;
	private Vector<Ausleihe> buecherRueck = new Vector<Ausleihe>();
	private Vector<Buch> buecherAus = new Vector<Buch>();
	private Ausleihe ausleihe = new Ausleihe();

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	private TableViewer tableViewerRueck, tableViewerAus;
	private ControlDecoration txtMediumDecorationRueck,txtMediumDecorationAus;
	private DateTime dateTimeAusVon;
	private Text dateTimeAusBis;
	private ToolItem btnSelectRueck;
	private ToolItem btnSelectAus;
	
	

	/**
	 * Create the dialog.
	 * @param parentShell
	 */
	public MassiveAusleihe(Shell parentShell, Ausleihe a) {
		super(parentShell);
		setHelpAvailable(false);
		if(a != null) ausleihe = a;
	}

	/**
	 * Create contents of the dialog.
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		setMessage("Hier k\u00F6nnen Sie auf einen Schlag mehere B\u00FCcher an gew\u00E4hlten Sch\u00FCler verliehen oder verliehene B\u00FCcher zur\u00FCcknehmen.");
		setTitle("Massenausleihe/-r\u00FCckgabe");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		GridLayout gl_container = new GridLayout(1, false);
		gl_container.verticalSpacing = 10;
		gl_container.horizontalSpacing = 10;
		container.setLayout(gl_container);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group grpSchler = new Group(container, SWT.NONE);
		grpSchler.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		GridLayout gl_grpSchler = new GridLayout(3, false);
		gl_grpSchler.verticalSpacing = 10;
		gl_grpSchler.horizontalSpacing = 10;
		grpSchler.setLayout(gl_grpSchler);
		grpSchler.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpSchler.setText("Sch\u00FCler");
		
		Label lblId = new Label(grpSchler, SWT.NONE);
		lblId.setAlignment(SWT.RIGHT);
		GridData gd_lblId = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblId.widthHint = 34;
		lblId.setLayoutData(gd_lblId);
		lblId.setText("ID:");
		
		txtSchuelerID = new Text(grpSchler, SWT.BORDER);
		txtSchuelerID.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				try{
					setSchueler(Schueler.fromId(new Integer(txtSchuelerID.getText())));
				}catch(NumberFormatException ex){}
			}
		});
		txtSchuelerID.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				try{
					ausleihe.setS(Schueler.fromId(new Integer(txtSchuelerID.getText())));
				}catch(NumberFormatException ex){}
			}
			
		});
		GridData gd_txtSchuelerID = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_txtSchuelerID.widthHint = 223;
		txtSchuelerID.setLayoutData(gd_txtSchuelerID);
		
		
		ToolBar toolBar = new ToolBar(grpSchler, SWT.FLAT | SWT.RIGHT);
		
		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SchuelerSelector view = new SchuelerSelector(getShell(), schueler);
				view.open();
				if(view.getReturnCode() == TitleAreaDialog.OK)
					setSchueler(view.getSchueler());
			}
		});
		tltmNewItem.setImage(SWTResourceManager.getImage(MassiveAusleihe.class, "/icons/edit.png"));
		tltmNewItem.setToolTipText("Sch\u00FCler manuell ausw\u00E4hlen.");
		
		Group grpRckgaben = new Group(container, SWT.NONE);
		grpRckgaben.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		GridLayout gl_grpRckgaben = new GridLayout(4, false);
		gl_grpRckgaben.verticalSpacing = 10;
		gl_grpRckgaben.horizontalSpacing = 10;
		grpRckgaben.setLayout(gl_grpRckgaben);
		grpRckgaben.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpRckgaben.setText("R\u00FCckgaben");
		
		Composite composite = new Composite(grpRckgaben, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1));
		TableColumnLayout tblColLay = new TableColumnLayout();
		composite.setLayout(tblColLay);
		
		tableViewerRueck = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		tableRueck = tableViewerRueck.getTable();
		tableRueck.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1));
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewerRueck, SWT.NONE);
		TableColumn tblclmnNewColumn = tableViewerColumn.getColumn();
		tblColLay.setColumnData(tblclmnNewColumn, new ColumnWeightData(100));
		tblclmnNewColumn.setResizable(false);
		tableViewerRueck.setContentProvider(new ArrayContentProvider());
		tableViewerRueck.setLabelProvider(new TableLabelProvider());
		tableViewerRueck.setInput(buecherRueck);
		
		Label lblIsbn = new Label(grpRckgaben, SWT.NONE);
		lblIsbn.setAlignment(SWT.RIGHT);
		GridData gd_lblIsbn = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblIsbn.widthHint = 42;
		lblIsbn.setLayoutData(gd_lblIsbn);
		lblIsbn.setText("ISBN:");
		
		txtMediumIsbnRueck = new Text(grpRckgaben, SWT.BORDER);
		txtMediumIsbnRueck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				addMediumRueck();
			}
		});
		GridData gd_txtMediumIsbn = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtMediumIsbn.widthHint = 212;
		txtMediumIsbnRueck.setLayoutData(gd_txtMediumIsbn);
		
		txtMediumDecorationRueck = new ControlDecoration(txtMediumIsbnRueck, SWT.LEFT | SWT.TOP);
		txtMediumDecorationRueck.setImage(SWTResourceManager.getImage(MassiveAusleihe.class, "/org/eclipse/jface/fieldassist/images/errorqf_ovr.gif"));
		txtMediumDecorationRueck.setDescriptionText("Sch\u00FCler hat dieses Buch nicht ausgeliehen!");
		txtMediumDecorationRueck.hide();
		
		ToolBar toolBar_1 = new ToolBar(grpRckgaben, SWT.FLAT | SWT.RIGHT);
		btnSelectRueck = new ToolItem(toolBar_1, SWT.NONE);
		btnSelectRueck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BuecherSelector view = new BuecherSelector(getShell(), null);
				view.open();
				if(view.getReturnCode() == TitleAreaDialog.OK){
					txtMediumIsbnRueck.setText(view.getBuch().getIsbn());
					addMediumRueck();
				}
			}
		});
		btnSelectRueck.setImage(SWTResourceManager.getImage(MassiveAusleihe.class, "/icons/edit.png"));
		btnSelectRueck.setToolTipText("Medium manuell ausw\u00E4hlen.");
		
		ToolBar toolBar_2 = new ToolBar(grpRckgaben, SWT.FLAT | SWT.RIGHT);
		toolBar_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		
		ToolItem toolItem_1 = new ToolItem(toolBar_2, SWT.NONE);
		toolItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(tableRueck.getSelectionCount() != 1) return;
				buecherRueck.remove(tableRueck.getSelection()[0].getData());
				updateTableRueck();
			}
		});
		toolItem_1.setImage(SWTResourceManager.getImage(MassiveAusleihe.class, "/icons/minus.png"));
		toolItem_1.setToolTipText("Gew\u00E4hlten Eintrag entfernen.");
		
		Group grpAusleihen = new Group(container, SWT.NONE);
		grpAusleihen.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
		GridLayout lay = new GridLayout(4, false);
		lay.horizontalSpacing = 10;
		lay.verticalSpacing = 10;
		grpAusleihen.setLayout(lay);
		grpAusleihen.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpAusleihen.setText("Ausleihen");
		
		Group grpZeitraum = new Group(grpAusleihen, SWT.NONE);
		GridLayout gl_grpZeitraum = new GridLayout(6, false);
		gl_grpZeitraum.verticalSpacing = 10;
		gl_grpZeitraum.horizontalSpacing = 10;
		grpZeitraum.setLayout(gl_grpZeitraum);
		grpZeitraum.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1));
		grpZeitraum.setText("Zeitraum");
		
		Label lblVon = new Label(grpZeitraum, SWT.NONE);
		lblVon.setAlignment(SWT.RIGHT);
		GridData gd_lblVon = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblVon.widthHint = 36;
		lblVon.setLayoutData(gd_lblVon);
		lblVon.setText("Von:");
		
		dateTimeAusVon = new DateTime(grpZeitraum, SWT.BORDER | SWT.DROP_DOWN);
		new Label(grpZeitraum, SWT.NONE);
		
		Label lblBis = new Label(grpZeitraum, SWT.NONE);
		lblBis.setAlignment(SWT.RIGHT);
		GridData gd_lblBis = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblBis.widthHint = 46;
		lblBis.setLayoutData(gd_lblBis);
		lblBis.setText("Dauer:");
		
		dateTimeAusBis = new Text(grpZeitraum, SWT.BORDER | SWT.DROP_DOWN);
		
		Label lblTage = new Label(grpZeitraum, SWT.NONE);
		lblTage.setText("Tage");
		
		composite = new Composite(grpAusleihen, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
		TableColumnLayout tblCol2Lay = new TableColumnLayout();
		composite.setLayout(tblCol2Lay);
		
		tableViewerAus = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		tableAus = tableViewerAus.getTable();
		tableAus.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 4, 1));
		
		tableViewerColumn = new TableViewerColumn(tableViewerAus, SWT.NONE);
		tblclmnNewColumn = tableViewerColumn.getColumn();
		tblColLay.setColumnData(tblclmnNewColumn, new ColumnWeightData(100));
		tblclmnNewColumn.setResizable(false);
		tableViewerAus.setContentProvider(new ArrayContentProvider());
		tableViewerAus.setLabelProvider(new TableLabelProvider());
		tableViewerAus.setInput(buecherAus);
		
		Label lblIsbn2 = new Label(grpAusleihen, SWT.NONE);
		lblIsbn2.setAlignment(SWT.RIGHT);
		GridData gd_lblIsbn2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_lblIsbn2.widthHint = 42;
		lblIsbn2.setLayoutData(gd_lblIsbn2);
		lblIsbn2.setText("ISBN:");
		
		txtMediumIsbnAus = new Text(grpAusleihen, SWT.BORDER);
		txtMediumIsbnAus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				addMediumAus();
			}
		});
		gd_txtMediumIsbn = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_txtMediumIsbn.widthHint = 212;
		txtMediumIsbnAus.setLayoutData(gd_txtMediumIsbn);
		
		txtMediumDecorationAus = new ControlDecoration(txtMediumIsbnAus, SWT.LEFT | SWT.TOP);
		txtMediumDecorationAus.setImage(SWTResourceManager.getImage(MassiveAusleihe.class, "/org/eclipse/jface/fieldassist/images/errorqf_ovr.gif"));
		txtMediumDecorationAus.setDescriptionText("Kein Exemplar f\u00FCr diesen Zeitraum verf\u00FCgbar!");
		txtMediumDecorationAus.hide();
		
		toolBar_1 = new ToolBar(grpAusleihen, SWT.FLAT | SWT.RIGHT);
		
		btnSelectAus = new ToolItem(toolBar_1, SWT.NONE);
		btnSelectAus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BuecherSelector view = new BuecherSelector(getShell(), null);
				view.open();
				if(view.getReturnCode() == TitleAreaDialog.OK){
					txtMediumIsbnAus.setText(view.getBuch().getIsbn());
					addMediumAus();
				}
			}
		});
		btnSelectAus.setImage(SWTResourceManager.getImage(MassiveAusleihe.class, "/icons/edit.png"));
		btnSelectAus.setToolTipText("Medium manuell ausw\u00E4hlen.");
		
		toolBar_2 = new ToolBar(grpAusleihen, SWT.FLAT | SWT.RIGHT);
		toolBar_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		
		toolItem_1 = new ToolItem(toolBar_2, SWT.NONE);
		toolItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(tableAus.getSelectionCount() != 1) return;
				buecherAus.remove(tableAus.getSelection()[0].getData());
				updateTableAus();
			}
		});
		toolItem_1.setImage(SWTResourceManager.getImage(MassiveAusleihe.class, "/icons/minus.png"));
		toolItem_1.setToolTipText("Gew\u00E4hlten Eintrag entfernen.");

		return area;
	}
	
	private void addMediumRueck(){
		if(schueler != null){
			Ausleihe a = schueler.getAusleihe(txtMediumIsbnRueck.getText());
			if(a == null){
				txtMediumDecorationRueck.show();
			}else{
				AnmerkungDialog.getDialog(getShell(), a.getB());
				txtMediumDecorationRueck.hide();
				if(!buecherRueck.contains(a)) buecherRueck.add(a);
				updateTableRueck();
				txtMediumIsbnRueck.setText("");
			}
		}
	}
	
	private void addMediumAus(){
		if(schueler != null){
			Buch b = Buch.getVerfuegbar(txtMediumIsbnAus.getText(), ausleihe.getVon(), ausleihe.getBis(), null);
			if(b == null){
				txtMediumDecorationAus.show();
			}else{
				AnmerkungDialog.getDialog(getShell(), b);
				txtMediumDecorationAus.hide();
				if(!buecherAus.contains(b)) buecherAus.add(b);
				updateTableAus();
				txtMediumIsbnAus.setText("");
			}
		}
	}

	/**
	 * Create contents of the button bar.
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button button = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		
		initBindings();
	}
	
	@Override
	protected void okPressed() {
		for(Ausleihe a: buecherRueck){
			a.zuruckgeben();
		}
		for(Buch b: buecherAus){
			Ausleihe a = new Ausleihe();
			a.setVon(ausleihe.getVon());
			a.setDauer(ausleihe.getDauer());
			a.setS(schueler);
			a.setB(b);
			a.eintragen();
			if(buecherAus.lastElement().equals(b)) ausleihe = a;
		}
		super.okPressed();
	}
	
	private void initBindings(){
		DataBindingContext dbc = new DataBindingContext();
		
		IObservableValue tar = SWTObservables.observeText(txtSchuelerID);
		IObservableValue obs = BeansObservables.observeValue(this, "schueler");
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
				if(fromObject == null) return "";
				else return ((Schueler)fromObject).toNiceString();
			}
		});
		UpdateValueStrategy strback = new UpdateValueStrategy();
		strback.setAfterGetValidator(new NullValidator());
		Binding binding = dbc.bindValue(tar, obs, strback, str);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
		
		
		
		//von bis
		IObservableValue vonObservable = BeansObservables.observeValue(ausleihe, "von");
		IObservableValue target = SWTObservables.observeSelection(dateTimeAusVon);
		str = new UpdateValueStrategy();
		str.setConverter(new Java2SqlDateConverter());
		strback = new UpdateValueStrategy();
		strback.setConverter(new Sql2JavaDateConverter());
		binding = dbc.bindValue(target, vonObservable, strback, str);
		
		//
		IObservableValue bisObservable = BeansObservables.observeValue(ausleihe, "dauer");
		target = SWTObservables.observeText(dateTimeAusBis, SWT.Modify);
		str = new UpdateValueStrategy();
		str.setAfterGetValidator(new IntGreaterZeroValidator());
		binding = dbc.bindValue(target, bisObservable, str, null);
		
		
		//Enabled
		IObservableValue model = BeansObservables.observeValue(this, "schueler");
		str = new UpdateValueStrategy();
		str.setConverter(new NullBoolConverter());
		dbc.bindValue(SWTObservables.observeEnabled(txtMediumIsbnAus), model, null, str);
		dbc.bindValue(SWTObservables.observeEnabled(txtMediumIsbnRueck), model, null, str);
		dbc.bindValue(SWTObservables.observeEnabled(btnSelectRueck), model, null, str);
		dbc.bindValue(SWTObservables.observeEnabled(btnSelectAus), model, null, str);
		
		
				
		
		TitleAreaDialogSupport.create(this, dbc);
		dbc.updateTargets();
		
	}
	
	private void updateTableRueck(){
		tableViewerRueck.refresh();
	}
	private void updateTableAus(){
		tableViewerAus.refresh();
	}
	
	public Ausleihe getAusleihe(){
		return ausleihe;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }
    
    
    
	/**
	 * @return the schueler
	 */
	public Schueler getSchueler() {
		return schueler;
	}

	/**
	 * @param schueler the schueler to set
	 */
	public void setSchueler(Schueler schueler) {
		if(schueler != null){
			AnmerkungDialog.getDialog(getShell(), schueler);
		}
		changes.firePropertyChange("schueler", this.schueler, this.schueler = schueler);
	}
	
	

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(694, 636);
	}

}
