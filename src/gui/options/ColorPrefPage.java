package gui.options;

import model.Kategorie;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class ColorPrefPage extends PreferencePage {
	private Table table;
	private TableViewer tableViewer;
	private Text txtName;
	private Label lblColor;
	private ToolItem btnNew;
	private TableViewerColumn tblclmnColumn;
	
	/**
	 * Create the preference page.
	 */
	public ColorPrefPage() {
		setDescription("Hier k\u00F6nnen Sie die Medien-Kategorien bearbeiten.");
		setMessage("Medien-Kategorien");
		setTitle("Medien-Kategorien");
		noDefaultAndApplyButton();
	}

	/**
	 * Create contents of the preference page.
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(5, false));
		
		TableColumnLayout tblcollay = new TableColumnLayout();
		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 5, 1));
		composite.setLayout(tblcollay);
		
		tableViewer = new TableViewer(composite, SWT.BORDER | SWT.V_SCROLL);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Kategorie k = (Kategorie) table.getSelection()[0].getData();
				txtName.setText(k.getName());
				lblColor.setBackground(k.getColor());
			}
		});
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 5, 1));
		
		tblclmnColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tblclmnColumn.getColumn().setText("Column");
		tblcollay.setColumnData(tblclmnColumn.getColumn(), new ColumnWeightData(100));
		
		tblclmnColumn.setLabelProvider(new ColorLabelProvider(0));
		tableViewer.setContentProvider(new ArrayContentProvider());
		
		tableViewer.setInput(Kategorie.getKategorien());
		
		
		Label lblName = new Label(container, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name:");
		
		txtName = new Text(container, SWT.BORDER);
		GridData gd_txtName = new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1);
		gd_txtName.widthHint = 149;
		txtName.setLayoutData(gd_txtName);
		
		ToolBar toolBar = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
		toolBar.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2));
		
		btnNew = new ToolItem(toolBar, SWT.NONE);
		btnNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(txtName.getText().equals("")) return;
				if(table.getSelectionIndex() != -1){
					table.deselectAll();
					txtName.setText("");
					return;
				}
				Kategorie k = new Kategorie(txtName.getText(), lblColor.getBackground());
				k.eintragen();
				tableViewer.setInput(Kategorie.getKategorien());
			}
		});
		btnNew.setImage(SWTResourceManager.getImage(ColorPrefPage.class, "/icons/new.png"));
		btnNew.setToolTipText("Als neue Kategorie hinzuf\u00FCgen.");
		
		ToolItem tltmbernehmen = new ToolItem(toolBar, SWT.NONE);
		tltmbernehmen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(txtName.getText().equals("") || table.getSelectionIndex()==-1) return;
				((Kategorie)table.getSelection()[0].getData()).setName(txtName.getText());
				((Kategorie)table.getSelection()[0].getData()).setColor(lblColor.getBackground());
			}
		});
		tltmbernehmen.setText("\u00DCbernehmen");
		
		ToolBar toolBar_1 = new ToolBar(container, SWT.FLAT | SWT.RIGHT);
		toolBar_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 2));
		
		ToolItem btnDel = new ToolItem(toolBar_1, SWT.NONE);
		btnDel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(table.getSelectionIndex() == -1) return;
				Kategorie k = (Kategorie) ((StructuredSelection)tableViewer.getSelection()).getFirstElement();
				k.entfernen();
				tableViewer.setInput(Kategorie.getKategorien());
			}
		});
		btnDel.setToolTipText("Markierte Kategorie l\u00F6schen.");
		btnDel.setImage(SWTResourceManager.getImage(ColorPrefPage.class, "/icons/delete.png"));
		
		Label lblFarbe = new Label(container, SWT.NONE);
		lblFarbe.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFarbe.setText("Farbe:");
		
		lblColor = new Label(container, SWT.SHADOW_NONE);
		GridData gd_lblColor = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_lblColor.widthHint = 76;
		lblColor.setLayoutData(gd_lblColor);
		
		Button btnAuswhlen = new Button(container, SWT.NONE);
		btnAuswhlen.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnAuswhlen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog dlg = new ColorDialog(getShell());
				RGB rgb = dlg.open();
				if(rgb != null){
					lblColor.setBackground(new Color(Display.getDefault(),rgb));
				}
			}
		});
		btnAuswhlen.setText("Ausw\u00E4hlen");
		initBindings();
		
		return container;
	}

	
	private void initBindings(){
		DataBindingContext dbc = new DataBindingContext();
		
		//Name
		IObservableValue target = SWTObservables.observeEnabled(btnNew);
		IObservableValue model = SWTObservables.observeText(txtName, SWT.Modify);
		UpdateValueStrategy str = new UpdateValueStrategy();
		str.setConverter(new IConverter() {
			
			@Override
			public Object getToType() {
				return boolean.class;
			}
			
			@Override
			public Object getFromType() {
				return String.class;
			}
			
			@Override
			public Object convert(Object fromObject) {
				return !((String)fromObject).equals("");
			}
		});
		dbc.bindValue(target, model, null, str);
		
		
	}
}
