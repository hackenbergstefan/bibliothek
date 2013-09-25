package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Info {
	public static final String INFO_UEBERFAELLIG = "überfällig";
	public static final String INFO_VERLIEHEN = "verliehen";
	public static final String INFO_VORGEMERKT = "vorgemerkt";
	public static final String INFO_VORGEMERKT_VERFUEGBAR = "vorgemerkt verfügbar";
	
	private Schueler schueler;
	private Ausleihe ausleihe;
	private Buch buch;
	private String type;

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	
	
	
	public Info(String type) {
		super();
		this.type = type;
	}
	
	public Info(Schueler schueler, Ausleihe ausleihe, Buch buch, String type) {
		super();
		this.schueler = schueler;
		this.ausleihe = ausleihe;
		this.buch = buch;
		this.type = type;
	}
	
	/**
	 * @return the schueler
	 */
	public Schueler getSchueler() {
		return schueler;
	}
	/**
	 * @param schueler the schueler to set
	 */
	public void setSchueler(Schueler schueler) {
		changes.firePropertyChange("schueler", this.schueler, this.schueler = schueler);
	}
	/**
	 * @return the ausleihe
	 */
	public Ausleihe getAusleihe() {
		return ausleihe;
	}
	/**
	 * @param ausleihe the ausleihe to set
	 */
	public void setAusleihe(Ausleihe ausleihe) {
		changes.firePropertyChange("ausleihe", this.ausleihe, this.ausleihe = ausleihe);
	}
	/**
	 * @return the buch
	 */
	public Buch getBuch() {
		return buch;
	}
	/**
	 * @param buch the buch to set
	 */
	public void setBuch(Buch buch) {
		changes.firePropertyChange("buch", this.buch, this.buch = buch);
	}
		
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }
	

}
