package util;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

public class StyledStringCellLabelProvider extends StyledCellLabelProvider {
	@Override
	public void update(ViewerCell cell) {
		IStyledClass el = (IStyledClass) cell.getElement();
		StyledString s = el.toStyledString();
		cell.setStyleRanges(s.getStyleRanges());
		cell.setText(s.getString());
		super.update(cell);
	}
}
