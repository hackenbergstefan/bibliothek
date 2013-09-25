package gui.tableviews;

import model.Buch;
import model.Kategorie;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class BuchFilter extends ViewerFilter {

	private String isbn, autor, titel, jahr,stichworter;
	private int art, stufe,status;
	private Kategorie kategorie;

	public void setSearchText(String isbn, String autor, String titel, String jahr, int art, int stufe, String stichworter, Kategorie kategorie, int status) {
		this.isbn = isbn;
		this.autor = autor;
		this.titel = titel;
		this.jahr = jahr;
		this.art = art;
		this.stufe = stufe;
		this.stichworter = stichworter;
		this.kategorie = kategorie;
		this.status = status;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		return ((Buch)element).matches(isbn, autor, titel, jahr, art, stufe,stichworter,kategorie,status);
	}
}
