package util;

import java.util.Vector;
import java.util.regex.Pattern;

public class ArrayUtils {
	
	public static String vec2CommaSeparatedString(Vector vec){
		String s = "";
		if(vec.size() > 0){
			int i=0;
			for(i=0;i<vec.size()-1;i++){
				s += vec.get(i)+", ";
			}
			s += vec.get(i);
		}
		return s;
	}
	
	public static Vector<String> commaSeparatedString2vec(String string){
		Vector<String> vec = new Vector<String>();
		if(string == null) return vec;
		for(String s: string.split("[,;]")){
			vec.add(s.trim());
		}
		return vec;
	}
	
	
	public static boolean matchesVec2Vec(Vector<String> what, Vector<String> where){
		if(what == null || what.size() == 0) return true;
		
		for(String w: what){
			boolean match = false;
			for(String s: where){
				if(s.matches("(?i).*"+Pattern.quote(w)+".*")){
					match = true;
					break;
				}
			}
			if(!match) return false;
		}
		return true;
	}
	
	public static boolean matchesVec2String(Vector<String> what, String where){
		if(what == null || what.size() == 0) return true;
		
		for(String w: what){
			if(!where.matches("(?i).*"+Pattern.quote(w)+".*")){
				return false;
			}
		}
		return true;
	}
}
