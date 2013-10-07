package gui.tableviews;

import model.Schueler;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

public class SchuelerColumnViewerSorter extends ViewerComparator{
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public SchuelerColumnViewerSorter() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = 1-DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Schueler b1 = (Schueler)e1;
		Schueler b2 = (Schueler)e2;
		
		int rc = 0;
		switch (propertyIndex) {
		case 0:
			rc = (int) Math.signum(b2.getId()-b1.getId());
			break;
		case 1:
			rc = b1.getVorname().compareTo(b2.getVorname());
			break;
		case 2:
			rc = b1.getNachname().compareTo(b2.getNachname());
			break;
		case 3:
			rc = b1.getKlasse().compareTo(b2.getKlasse());
			break;
		default:
			rc = 0;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}

}
