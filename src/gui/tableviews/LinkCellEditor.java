package gui.tableviews;

import gui.MainApplication;
import model.Buch;
import model.IStringable;
import model.Schueler;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;

public class LinkCellEditor extends CellEditor{
		private Link l;
		private Object value;
		private SelectionListener listener;
		private Composite parent;
		
		public LinkCellEditor(Composite parent) {
			super(parent);
			this.parent = parent;
		}
		
		@Override
		protected void doSetValue(Object value) {
			this.value = value; 
			l.setText("<a>"+((IStringable)value).toNiceString()+"</a>");
		}
		
		@Override
		protected void doSetFocus() {
			
		}
		
		@Override
		protected Object doGetValue() {
			return value;
		}
		
		@Override
		protected Control createControl(Composite parent) {
			l = new Link(parent, SWT.None);
			l.addSelectionListener(getListener());
			return l;
		}
		
		private SelectionListener getListener(){
			if(listener == null){
				listener = new SelectionAdapter() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						parent.getShell().dispose();
						if(value instanceof Buch){
							MainApplication.MAIN.selectMedium((Buch)value);
						}else if(value instanceof Schueler){
							MainApplication.MAIN.selectSchueler((Schueler)value);
						}
					}
				};
			}
			return listener;
		}
    }
