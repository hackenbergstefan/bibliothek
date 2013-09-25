package model;

import java.util.Vector;

public class BuchConstants {
	public static final Vector<String> STUFEN = new Vector<String>();
	public static final Vector<String> ARTEN = new Vector<String>();
	
	static{
		STUFEN.add("beliebig");
		STUFEN.add("sehr leicht");
		STUFEN.add("leicht");
		STUFEN.add("mittel");
		STUFEN.add("schwer");
		STUFEN.add("sehr schwer");
		
		ARTEN.add("beliebig");
		ARTEN.add("Buch");
		ARTEN.add("CD");
		ARTEN.add("Zeitschrift");
		ARTEN.add("Kasette");
		ARTEN.add("Spiel");
	}
}
