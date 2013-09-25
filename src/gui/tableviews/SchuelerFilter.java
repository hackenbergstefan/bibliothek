package gui.tableviews;

import model.Schueler;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class SchuelerFilter extends ViewerFilter {

	private String id,vorname,nachname,klasse;

	public void setSearchText(String id, String vorname, String nachname, String klasse){
		this.id = id;
		this.vorname = vorname;
		this.nachname = nachname;
		this.klasse = klasse;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		int id = -1;
		try{
			id = new Integer(this.id);
		}catch(NumberFormatException ex){}
		return ((Schueler)element).matches(id, vorname,nachname,klasse);
	}
}
