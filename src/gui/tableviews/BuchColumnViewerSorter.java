package gui.tableviews;

import model.Buch;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

public class BuchColumnViewerSorter extends ViewerComparator{
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public BuchColumnViewerSorter() {
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
			direction = DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Buch b1 = (Buch)e1;
		Buch b2 = (Buch)e2;
		
		int rc = 0;
		switch (propertyIndex) {
		case 0:
			rc = (int) Math.signum(b2.getId()-b1.getId());
			break;
		case 1:
			rc = b1.getIsbn().compareTo(b2.getIsbn());
			break;
		case 2:
			rc = b1.getAutor().compareTo(b2.getAutor());
			break;
		case 3:
			rc = b1.getTitel().compareTo(b2.getTitel());
			break;
		case 4:
			rc = b1.getJahr().compareTo(b2.getJahr());
			break;
		case 5:
			rc = (int) Math.signum(b2.getMedienart()-b1.getMedienart());
			break;
		case 6:
			rc = (int) Math.signum(b2.getStufe()-b1.getStufe());
			break;
		case 7:
			rc = 0;
			break;
		case 8:
			rc = (int)Math.signum(b2.getKategorie().getId() - b1.getKategorie().getId());
			break;
			
		case 9:
			rc = b1.isVerliehen()?1:0;
			rc -= b2.isVerliehen()?1:0;
			if(rc == 0){ 
				rc = b1.isVorgemerkt()?1:0;
				rc -= b2.isVorgemerkt()?1:0;
			}
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
