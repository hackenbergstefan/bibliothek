package gui.tableviews;


import model.Ausleihe;

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

import util.DateUtils;

public class AusleiheLabelProvider_Progress extends OwnerDrawLabelProvider {
	private int colnum;
	
	public AusleiheLabelProvider_Progress(int colnum){
		this.colnum = colnum;
	}
	

	@Override
	protected void measure(Event event, Object element) {
		//System.out.println(DateUtils.getDifferenceToToday(a.getVon())+" / "+ DateUtils.getDifferenceInDays(a.getVon(), a.getBis()));
	}

	@Override
	protected void paint(Event event, Object element) {
		if(!(element instanceof Ausleihe)) return;
		Ausleihe a = (Ausleihe)element;
		GC gc = event.gc;
		Color foreground = gc.getForeground();
        Color background = gc.getBackground();
                
        Rectangle bounds = null;
		if(event.item instanceof TableItem)
			bounds = ((TableItem)event.item).getBounds(colnum);
		else if(event.item instanceof TreeItem)
			bounds = ((TreeItem)event.item).getBounds(colnum);
		
		if(a.isVorgemerkt()){
			gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_YELLOW));
	        gc.fillRectangle(bounds);
	        
	        String text = "Vorgemerkt";
	        Point size = event.gc.textExtent(text);
	        int offset = Math.max(0, (event.height - size.y) / 2);
	        
	        gc.drawText(text, event.x + 2, event.y + offset, true);
		}else if(DateUtils.getDifferenceToToday(a.getVon()) < 0){
			String text = "Beginnt in "+-DateUtils.getDifferenceToToday(a.getVon())+" Tagen.";
	        Point size = event.gc.textExtent(text);
	        int offset = Math.max(0, (event.height - size.y) / 2);
	        gc.drawText(text, event.x + 2, event.y + offset, true);
	        
		}else{
			double percentage = DateUtils.getDifferenceToToday(a.getVon()) / (double)DateUtils.getDifferenceInDays(a.getVon(), a.getBis());
	        
	        int newForeground = 0;
	        int newBackground = 0;
	        
	        if(percentage < 0.7){
	        	newForeground = SWT.COLOR_DARK_GREEN;
	        	newBackground = SWT.COLOR_GREEN;
	        }else if(percentage <= 1){
	        	newForeground = SWT.COLOR_YELLOW;
	        	newBackground = SWT.COLOR_DARK_YELLOW;
	        }else if(percentage > 1){
	        	newForeground = SWT.COLOR_RED;
	        	newBackground = SWT.COLOR_DARK_RED;
	        }
	        
	        gc.setForeground(Display.getDefault().getSystemColor(newForeground));
	    	gc.setBackground(Display.getDefault().getSystemColor(newBackground));
	        
	        int width = (int)Math.round((bounds.width - 1) * percentage);
	        
	        gc.fillGradientRectangle(event.x, event.y, width, event.height, false);
	//        Rectangle rect2 = new Rectangle(event.x, event.y, width - 1, event.height - 1);
	//        gc.drawRectangle(rect2);
	        
	        
	        if(percentage <= 1){
		        gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
	        	String text = -DateUtils.getDifferenceToToday(a.getBis())+" Tag(e) übrig.";
		        Point size = event.gc.textExtent(text);
		        int offset = Math.max(0, (event.height - size.y) / 2);
		        gc.drawText(text, event.x + 2, event.y + offset, true);
	        }else{
		        gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		        String text = a.getDaysTooLate()+" Tag(e) überfällig";
		        Point size = event.gc.textExtent(text);
		        int offset = Math.max(0, (event.height - size.y) / 2);
		        gc.drawText(text, event.x + 2, event.y + offset, true);
	        }
	        
	        gc.setForeground(background);
	        gc.setBackground(foreground);
		}
	}
	

}
