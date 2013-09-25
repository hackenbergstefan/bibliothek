package gui;

public class StringConstants {
	public static final String FEHLENDE_ANGABE_TITLE = "Fehlende Angabe!";
	public static final String FEHLENDE_ANGABE_MESSAGE = "Bitte erforderliche Felder ausfüllen!";
	
	public static final String NOTHING_SELECTED_TITLE = "Nichts ausgewählt!";
	public static final String NOTHING_SELECTED_MESSAGE = "Bitte erst etwas auswählen!";
	
	public static final String VALIDATION_ERROR_ISBN = "ISBN Codes müssen 10 oder 13 Zahlen enthalten!";
	public static final String VALIDATION_ERROR_NONEMPTY = "Feld darf nicht leer bleiben!";
	public static final String VALIDATION_ERROR_NONBELIEBIG = "Feld darf nicht beliebig bleiben!";
	public static final String VALIDATION_ERROR_NONAFTER = "Enddatum muss nach Anfangsdatum liegen!";
	public static final String VALIDATION_ERROR_ALREADYVORGEMERKT = "Für diesen Zeitraum bereits vorgemerkt!";
	public static final String VALIDATION_ERROR_ALREADYVERLIEHEN = "Für diesen Zeitraum bereits verliehen!";
	public static final String VALIDATION_ERROR_NOTINTGREATERZERO = "Keine ganze Zahl größer 0!";
	
	public static final String CONFIRM_DELETE_MEDIUM = "Möchten Sie dieses Medium wirklich löschen?";
	public static final String CONFIRM_DELETE_SCHUELER = "Möchten Sie diesen Schüler wirklich löschen?";
	public static final String CONFIRM_DELETE_AUSLEIHE = "Möchten Sie diese Ausleihe wirklich löschen?\nFalls Sie das Medium nur zurückgeben möchten, " +
			"klicken Sie auf \"Cancel\" und anschließend auf \"Zurückgeben\"";
	public static final String CONFIRM_DIFFERENT_VALUES = "Es wurden zu gleicher ISBN-Nummer verschiedene Werte gefunden!\n\nMöchten Sie alle Exemplare dieses Mediums angleichen?";
}
