package gui.tableviews;

import model.Kategorie;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class ComboKatLabelProvider extends LabelProvider implements ITableColorProvider {

	@Override
	public String getText(Object element) {
		Kategorie k = (Kategorie)element;
		return k.getName();
	}
	
	
	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		Kategorie k = (Kategorie)element;
		if(k.getColor().getRGB().getHSB()[2] < 0.5) return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		Kategorie k = (Kategorie)element;
		return k.getColor();
	}

}
