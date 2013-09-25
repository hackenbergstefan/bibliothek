package gui.tableviews;


import gui.overviews.AusleiheOverview;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import model.Ausleihe;
import model.Buch;
import model.Schueler;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import util.DateUtils;
import de.ralfebert.rcputils.tables.ColumnBuilder;
import de.ralfebert.rcputils.tables.TableViewerBuilder;
import de.ralfebert.rcputils.tables.format.Formatter;



public class HistoryTableView extends Composite {
	private Schueler schueler;
	private Buch buch;
	private TableViewer tableViewer;
	private Table table;
	private Ausleihe ausleihe;

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public HistoryTableView(Composite parent, int style, Schueler schueler, Buch buch) {
		super(parent, style);
		
		this.schueler = schueler;
		this.buch = buch;
		
		
		TableViewerBuilder build = new TableViewerBuilder(this);
		ColumnBuilder c = build.createColumn("Rückgabedatum");
		c.bindToProperty("rueckdate");
		c.format(Formatter.forDate(DateUtils.longFormat));
		c.setPercentWidth(20);
		c.build();
		
		if(buch != null){
			c = build.createColumn("Schüler");
			c.bindToProperty("s");
			c.setPercentWidth(40);
			c.setCustomLabelProvider(new StyledCellLabelProvider() {
				@Override
				public void update(ViewerCell cell) {
					StyledString s = ((Ausleihe)cell.getElement()).getS().toStyledString();
					cell.setStyleRanges(s.getStyleRanges());
					cell.setText(s.getString());
					super.update(cell);
				}
			});
			c.makeEditable(new LinkCellEditor(build.getTable()));
			c.build();
		}
		
		if(schueler != null){
			c = build.createColumn("Medium");
			c.bindToProperty("b");
			c.setPercentWidth(60);
			c.setCustomLabelProvider(new StyledCellLabelProvider() {
				@Override
				public void update(ViewerCell cell) {
					StyledString s = ((Ausleihe)cell.getElement()).getB().toStyledString();
					cell.setStyleRanges(s.getStyleRanges());
					cell.setText(s.getString());
					super.update(cell);
				}
			});
			c.makeEditable(new LinkCellEditor(build.getTable()));
			c.build(); 
		}
		
		c = build.createColumn("von");
		c.bindToProperty("von");
		c.setPercentWidth(20);
		c.format(Formatter.forDate(DateUtils.shortFormat));
		c.build();
		
		c = build.createColumn("bis");
		c.bindToProperty("bis");
		c.setPercentWidth(20);
		c.format(Formatter.forDate(DateUtils.shortFormat));
		c.build();
		
		
		if(schueler != null){
			build.setInput(Ausleihe.getHistoryFor(schueler));
		}else if(buch != null){
			build.setInput(Ausleihe.getHistoryFor(buch));
		}
		
		table = build.getTable();
		tableViewer = build.getTableViewer();
		
		table.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				AusleiheOverview view = new AusleiheOverview(getShell(), ausleihe);
				view.open();
			}
		});
		
		initBindings();
	}
	
	private void initBindings() {
		DataBindingContext bdc = new DataBindingContext();
		bdc.bindValue(ViewersObservables.observeSingleSelection(tableViewer), BeansObservables.observeValue(this, "ausleihe"));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	/**
	 * @return the ausleihe
	 */
	public Ausleihe getAusleihe() {
		return ausleihe;
	}

	/**
	 * @param ausleihe the ausleihe to set
	 */
	public void setAusleihe(Ausleihe ausleihe) {
		changes.firePropertyChange("ausleihe", this.ausleihe, this.ausleihe = ausleihe);
	}
	

	public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }

}
