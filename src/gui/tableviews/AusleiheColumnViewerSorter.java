package gui.tableviews;

import model.Ausleihe;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

public class AusleiheColumnViewerSorter extends ViewerComparator{
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public AusleiheColumnViewerSorter() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}
	
	public void setDirection(boolean asc){
		direction = asc==true?0:DESCENDING;
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
		Ausleihe b1 = (Ausleihe)e1;
		Ausleihe b2 = (Ausleihe)e2;
		
		int rc = 0;
		switch (propertyIndex) {
		case 0:
			rc = b1.getB().compareTo(b2.getB()); break;
		case 1:
			rc = b1.getS().compareTo(b2.getS()); break;
		case 2:
			rc = b1.getVon().compareTo(b2.getVon()); break;
		case 3:
			rc = b1.getBis().compareTo(b2.getBis()); break;
		case 4:
			if(b1.isVorgemerkt() && b2.isVorgemerkt()) 
				rc = b1.getVon().compareTo(b2.getVon());
			else if(b1.isVorgemerkt()) rc = -1;
			else if(b2.isVorgemerkt()) rc = 1;
			else rc = b1.getDaysTooLate() - b2.getDaysTooLate();
			break;
		case 5:
			if(b1.getVorgemerktAn() != null && b2.getVorgemerktAn() != null)
				rc = b1.getVorgemerktAn().compareTo(b2.getVorgemerktAn());
			else if(b2.getVorgemerktAn() == null)
				rc = 1;
			else if(b1.getVorgemerktAn() == null)
				rc = -1;
			else rc = 0;
			break;
		case 6:
			if(b1.getRueckdate() != null && b2.getRueckdate() != null)
				rc = b1.getRueckdate().compareTo(b2.getRueckdate());
			else if(b2.getRueckdate() == null)
				rc = 1;
			else if(b1.getRueckdate() == null)
				rc = -1;
			else rc = 0;
			break;
		}
			
		
		
		// If descending order, flip the direction
		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}

}
