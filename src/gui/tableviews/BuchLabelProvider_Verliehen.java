package gui.tableviews;



import model.Buch;

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TableItem;

public class BuchLabelProvider_Verliehen extends OwnerDrawLabelProvider{
	private int index;
	
	public BuchLabelProvider_Verliehen(int index){
		this.index = index;
	}
	
	@Override
	protected void measure(Event event, Object element) {
	}

	@Override
	protected void paint(Event event, Object element) {
		Buch b = (Buch)element;
		GC gc = event.gc;
		Color foreground = gc.getForeground();
        Color background = gc.getBackground();
        
        String text = "";
		Color newBackground;
		switch(b.getStatus()){
		case 0: text = "Verfügbar"; newBackground =  Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN); break;
		case 1: text = "Verliehen"; newBackground =  Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED); break;
		case 2: text = "Vorgemerkt"; newBackground =  Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW); break;
		default: newBackground = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		}
		
		Rectangle bounds = ((TableItem)event.item).getBounds(index);
		
		gc.setBackground(newBackground);
        gc.fillRectangle(bounds);
        
        gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        Point size = event.gc.textExtent(text);
        int offset = Math.max(0, (event.height - size.y) / 2);
        gc.drawText(text, event.x + 2, event.y + offset, true);
        

        gc.setForeground(background);
        gc.setBackground(foreground);
	}


}
