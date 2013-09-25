package gui.tableviews;

import model.IStringable;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Creates a Composite with a TableView with primary one column. This column displays a StyledString returned
 * by an IStringable element. If the element is not a IStringable, nothing will be displayed.
 * 
 * If style has SWT.YES there will be numbers in a second column to the left of the primary one. 
 * 
 * @author Zero
 *
 */
public class StyledTableView extends Composite {
	public Table table;
	protected TableColumnLayout colLay;
	public TableViewer tableViewer;
	
	private StyledCellLabelProvider labelProvider = new StyledCellLabelProvider(){
		public void update(org.eclipse.jface.viewers.ViewerCell cell) {
			if(cell.getElement() instanceof IStringable){
				StyledString s = ((IStringable)cell.getElement()).toStyledString();
				cell.setText(s.getString());
				cell.setStyleRanges(s.getStyleRanges());
			}
			
			super.update(cell);
		};
	};

	/**
	 * Create the composite.
	 * 
	 * If style has SWT.YES there will be numbers in a second column to the left of the primary one.
	 * 
	 * @param parent
	 * @param style
	 */
	public StyledTableView(Composite parent, int style, Object[] input) {
		super(parent, style);
		
		colLay = new TableColumnLayout();
		setLayout(colLay);
		tableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		
		//if numbers are requested
		if((style & SWT.YES) != 0){
			TableViewerColumn numCol = new TableViewerColumn(tableViewer, SWT.NONE);
			numCol.setLabelProvider(new RowNumberLabelProvider());
			TableColumn tblclmnNumCol = numCol.getColumn();
			colLay.setColumnData(tblclmnNumCol, new ColumnPixelData(40));
			tblclmnNumCol.setText("Num");
		}
		
		TableViewerColumn dataColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		dataColumn.setLabelProvider(labelProvider);
		TableColumn tblclmnDataColumn = dataColumn.getColumn();
		colLay.setColumnData(tblclmnDataColumn, new ColumnWeightData(100));
		tblclmnDataColumn.setText("New Column");
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(input);
	}

	public StyledTableView(Composite parent, int style) {
		super(parent,style);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
