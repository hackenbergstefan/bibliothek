package model;

public enum Gender {
	MALE("Junge"),
	FEMALE("Mädchen");
	
	public final String name;
	
	Gender(String name){
		this.name = name;
	}
	
	public static Gender fromString(String s){
		if(s.equals(FEMALE.name)) return FEMALE;
		else return MALE;
	}
}
