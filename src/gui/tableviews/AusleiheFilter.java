package gui.tableviews;

import model.Ausleihe;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class AusleiheFilter extends ViewerFilter {

	private String s_id, b_isbn;
	boolean verliehenAn;

	public void setSearchText(String s_id, String b_isbn, boolean verliehenAn){
		this.s_id = s_id;
		this.b_isbn = b_isbn;
		this.verliehenAn = verliehenAn;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return ((Ausleihe)element).matches(s_id, b_isbn, verliehenAn);
	}
}
