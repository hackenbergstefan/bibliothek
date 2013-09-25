package gui.tableviews;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import model.Ausleihe;
import model.Buch;
import model.Schueler;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import util.DateUtils;
import util.TreeUtils;

public class TreeTestView extends Composite {
	private TreeViewer treeViewer;
	private Tree tree;
	private Table tableSchueler;
	private Table tableMedien;
	private int viewmode = 0;
	private Vector<?> data = new Vector();
	private TableViewer tableSchuelerViewer;
	private TableViewer tableMedienViewer;
	private AusleiheTreeContentProvider treeContentProvider;
	private TreeColumn trclmnSchler;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public TreeTestView(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(1, false));
		
		Composite composite_1 = new Composite(this, SWT.NONE);
		composite_1.setLayout(new GridLayout(2, false));
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite_1.heightHint = 180;
		composite_1.setLayoutData(gd_composite_1);
		
		Composite composite_2 = new Composite(composite_1, SWT.NONE);
		GridData gd_composite_2 = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_composite_2.widthHint = 250;
		gd_composite_2.heightHint = 168;
		composite_2.setLayoutData(gd_composite_2);
		TableColumnLayout tcl_composite_2 = new TableColumnLayout();
		composite_2.setLayout(tcl_composite_2);
		
		tableSchuelerViewer = new TableViewer(composite_2, SWT.BORDER | SWT.FULL_SELECTION);
		tableSchuelerViewer.setContentProvider(new ArrayContentProvider());
		tableSchueler = tableSchuelerViewer.getTable();
		
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableSchuelerViewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new StyledLabelProvider());
		TableColumn tblclmnSchler = tableViewerColumn.getColumn();
		tcl_composite_2.setColumnData(tblclmnSchler, new ColumnWeightData(90));
		tblclmnSchler.setText("Sch\u00FCler");
		
		Composite composite_3 = new Composite(composite_1, SWT.NONE);
		GridData gd_composite_3 = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_composite_3.widthHint = 381;
		composite_3.setLayoutData(gd_composite_3);
		TableColumnLayout tcl_composite_3 = new TableColumnLayout();
		composite_3.setLayout(tcl_composite_3);
		
		tableMedienViewer = new TableViewer(composite_3, SWT.BORDER | SWT.FULL_SELECTION);
		tableMedienViewer.setContentProvider(new ArrayContentProvider());
		tableMedien = tableMedienViewer.getTable();
		
		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(tableMedienViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new StyledLabelProvider());
		TableColumn tblclmnMedien = tableViewerColumn_1.getColumn();
		tcl_composite_3.setColumnData(tblclmnMedien, new ColumnWeightData(90));
		tblclmnMedien.setText("Medien");
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		TreeColumnLayout tcl_composite = new TreeColumnLayout();
		composite.setLayout(tcl_composite);
		
		treeViewer = new TreeViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		treeContentProvider = new AusleiheTreeContentProvider();
		treeViewer.setContentProvider(treeContentProvider);
		tree = treeViewer.getTree();
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object o = ((StructuredSelection)treeViewer.getSelection()).getFirstElement();
				if(o instanceof Schueler){
					tableSchuelerViewer.setSelection(new StructuredSelection(o), true);
					tableSchueler.showSelection();
					tableMedienViewer.setSelection(null);
					tableMedien.showSelection();
				}else if(o instanceof Ausleihe){
					tableSchuelerViewer.setSelection(new StructuredSelection(((Ausleihe)o).getS()), true);
					tableSchueler.showSelection();
					tableMedienViewer.setSelection(new StructuredSelection(((Ausleihe)o).getB()), true);
					tableMedien.showSelection();
				}
			}
		});
		tree.setHeaderVisible(true);
		tree.setLinesVisible(false);
		
		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(new StyledCellLabelProvider(){
			@Override
			public void update(ViewerCell cell) {
				Object obj = cell.getElement();
				if(obj instanceof Ausleihe){
					switch(viewmode){
					case 0:
						break;
					case 1:
						StyledString s = ((Ausleihe)obj).getS().toStyledString();
						cell.setText(s.getString());
						cell.setStyleRanges(s.getStyleRanges());
						break;
					}
				}else if(obj instanceof Schueler){
					StyledString s = ((Schueler)obj).toStyledString();
					cell.setText(s.getString());
					cell.setStyleRanges(s.getStyleRanges());
				}
				super.update(cell);
			}
		});
		trclmnSchler = treeViewerColumn.getColumn();
		trclmnSchler.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setViewmode(0);
			}

		});
		tcl_composite.setColumnData(trclmnSchler, new ColumnPixelData(150, true, true));
		trclmnSchler.setText("Sch\u00FCler");
		
		TreeViewerColumn treeViewerColumn_1 = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn_1.setLabelProvider(new StyledCellLabelProvider(){
			@Override
			public void update(ViewerCell cell) {
				Object obj = cell.getElement();
				if(obj instanceof Ausleihe){
					switch(viewmode){
					case 0:
						StyledString s = ((Ausleihe)obj).getB().toStyledString();
						cell.setText(s.getString());
						cell.setStyleRanges(s.getStyleRanges());
						break;
					case 1:
						break;
					}
				}else if(obj instanceof Buch){
					StyledString s = ((Buch)obj).toStyledString();
					cell.setText(s.getString());
					cell.setStyleRanges(s.getStyleRanges());
				}
				super.update(cell);
			}
		});
		TreeColumn trclmnMedium = treeViewerColumn_1.getColumn();
		trclmnMedium.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setViewmode(1);
			}
		});
		tcl_composite.setColumnData(trclmnMedium, new ColumnWeightData(100));
		trclmnMedium.setText("Medium");
		
		TreeViewerColumn treeViewerColumn_2 = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn_2.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				if(element instanceof Ausleihe){
					return DateUtils.shortFormat.format(((Ausleihe)element).getVon());
				}
				return "";
			}
		});
		
		TreeColumn trclmnVon = treeViewerColumn_2.getColumn();
		tcl_composite.setColumnData(trclmnVon, new ColumnWeightData(100));
		trclmnVon.setText("Von");
		
		TreeViewerColumn treeViewerColumn_3 = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn_3.setLabelProvider(new ColumnLabelProvider() {
			public Image getImage(Object element) {
				// TODO Auto-generated method stub
				return null;
			}
			public String getText(Object element) {
				if(element instanceof Ausleihe){
					return DateUtils.shortFormat.format(((Ausleihe)element).getBis());
				}
				return "";
			}
		});
		TreeColumn trclmnBis = treeViewerColumn_3.getColumn();
		tcl_composite.setColumnData(trclmnBis, new ColumnPixelData(150, true, true));
		trclmnBis.setText("Bis");
		
		TreeViewerColumn treeViewerColumn_4 = new TreeViewerColumn(treeViewer, SWT.NONE);
		treeViewerColumn_4.setLabelProvider(new AusleiheLabelProvider_Progress(3));
		TreeColumn trclmnStatus = treeViewerColumn_4.getColumn();
		trclmnStatus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setViewmode(2);
			}
		});
		tcl_composite.setColumnData(trclmnStatus, new ColumnPixelData(150, true, true));
		trclmnStatus.setText("Status");
		
		
		//
		setViewmode(0);

	}
	
	/**
	 * 
	 * @param viewmode 
	 * 		0: group by schueler
	 * 		1: group by medien
	 * 		2: group by status
	 * 		3: group by von
	 * 		4: group by bis
	 */
	public void setViewmode(int viewmode){
		this.viewmode = viewmode;
		treeViewer.setInput(new RootNode());
		treeViewer.expandAll();
		
		switch (viewmode) {
		case 0:
			tree.setColumnOrder(new int[]{0,1,2,3,4});
			break;

		case 1:
			tree.setColumnOrder(new int[]{1,0,2,3,4});
			break;
		case 2:
			tree.setColumnOrder(new int[]{2,0,1,3,4});
			break;
		}
		
		updateLists();
	}
	
	private void updateLists(){
		switch (viewmode) {
		case 0:
			//update schueler list
			List<Schueler> list = TreeUtils.getAllOfType(treeContentProvider, data, Schueler.class);
			Collections.sort(list);
			tableSchuelerViewer.setInput(list);
			
			//update medien
			List<Ausleihe> list2 = TreeUtils.getAllOfType(treeContentProvider, data, Ausleihe.class);
			List<Buch> list3 = TreeUtils.getSubList(list2, Buch.class, "getB");
			Collections.sort(list3);
			tableMedienViewer.setInput(list3);
			break;
		case 1:
			//update schueler list
			list2 = TreeUtils.getAllOfType(treeContentProvider, data, Ausleihe.class);
			list = TreeUtils.getSubList(list2, Schueler.class, "getS");
			Collections.sort(list);
			tableSchuelerViewer.setInput(list);
			
			//update medien
			list3 = TreeUtils.getAllOfType(treeContentProvider, data, Buch.class);
			Collections.sort(list3);
			tableMedienViewer.setInput(list3);
			break;
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	
	private class AusleiheTreeContentProvider implements ITreeContentProvider{
		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if(parentElement instanceof RootNode){
				return ((RootNode)parentElement).getChildren().toArray();
			}
			switch (viewmode) {
			case 0:if(parentElement instanceof Schueler){
					return ((Schueler)parentElement).getAllOpenAusleihen().toArray();
				}
				break;
			case 1:
				if(parentElement instanceof Buch){
					return ((Buch)parentElement).getAllOpenAusleihen().toArray();
				}
				break;
			}
			return new Object[0];
		}

		@Override
		public Object getParent(Object element) {
			switch (viewmode) {
			case 0:
				if(element instanceof Ausleihe){
					return ((Ausleihe)element).getS();
				}
				break;
			case 1:
				if(element instanceof Ausleihe){
					return ((Ausleihe)element).getB();
				}
				break;
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			switch(viewmode){
			case 0: 
				if(element instanceof Schueler) return true;
				else if(element instanceof Ausleihe) return false;
			case 1:
				if(element instanceof Buch) return true;
			}
			return false;
		}
		
	}
	
	private class RootNode{
		public Vector<?> getChildren(){
			switch (viewmode) {
			case 0:
				data = Schueler.getActive();
				break;
			case 1:
				data = Buch.getActive();
				break;
			}
			return data;
		}
	}
	
	private class TreeElementStatus{
		
	}
	
	
	public static void main(String[] args){
		Display display = Display.getDefault();
		Shell shell = new Shell(display);
		shell.setSize(500, 500);
		shell.setLayout(new FillLayout());
		new TreeTestView(shell, SWT.None);
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
