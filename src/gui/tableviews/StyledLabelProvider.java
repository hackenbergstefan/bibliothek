package gui.tableviews;

import model.IStringable;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

public class StyledLabelProvider extends StyledCellLabelProvider {

	@Override
	public void update(ViewerCell cell) {
		Object o = cell.getElement();
		if(o instanceof IStringable){
			StyledString s = ((IStringable)o).toStyledString();
			cell.setText(s.getString());
			cell.setStyleRanges(s.getStyleRanges());
		}
		super.update(cell);
	}
}
