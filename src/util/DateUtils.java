package util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DateUtils {
	public static SimpleDateFormat shortFormat = new SimpleDateFormat("dd. MMMM");
	public static SimpleDateFormat longFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

	
//	/**
//	 * computes d2 - d1 
//	 * @param d1
//	 * @param d2
//	 * @return
//	 */
//	public static int getDifferenceInDays(Date d1, Date d2){
//		Calendar c = Calendar.getInstance();
//		int sign = 1;
//		if(d1.after(d2)){
//			c.setTimeInMillis(d1.getTime()-d2.getTime());
//			sign = -1;
//		}else{
//			c.setTimeInMillis(d2.getTime()-d1.getTime());
//			sign = 1;
//		}
////		System.out.println("DateUtils.getDifferenceInDays() "+c.get(Calendar.DAY_OF_YEAR)+" "+c.getTimeInMillis());
//		//Falls die Differenz kleiner als ein Tag ist, gib 0 zurück, sonst true
//		if(c.getTimeInMillis() < 86400000)
//			return 0;
//		else return sign*(c.get(Calendar.DATE)-1); 
//	}
	

	/**
	 * computes d2 - d1 and gives result in days.
	 * 
	 * Highly inefficient!
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int getDifferenceInDays(final Date d1, final Date d2) {
		Calendar sDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		int sign;
		if(d2.after(d1)){
			sign = 1;
			sDate.setTimeInMillis(d1.getTime());
			endDate.setTimeInMillis(d2.getTime());
		}else{
			sign = -1;
			sDate.setTimeInMillis(d2.getTime());
			endDate.setTimeInMillis(d1.getTime());
		}
		
		int daysBetween = 0;
		while(sDate.get(Calendar.YEAR) < endDate.get(Calendar.YEAR)) {
			sDate.add(Calendar.DAY_OF_MONTH, 1);  
		    daysBetween++;
		}
		while(sDate.get(Calendar.DAY_OF_YEAR) < endDate.get(Calendar.DAY_OF_YEAR)){
				sDate.add(Calendar.DAY_OF_MONTH, 1);  
			    daysBetween++;
	    }  
		return sign*daysBetween;  
	}


	
	/**
	 * computes today - d
	 * @param d
	 * @return
	 */
	public static int getDifferenceToToday(Date d){
		Date now = new Date(System.currentTimeMillis());
		return getDifferenceInDays(d, now);
	}
	
	/**
	 * 
	 * @return date mit wert des datums heute + 2 wochen
	 */
	public static Date getTwoWeeks(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 14);
		return new Date(c.getTimeInMillis());
	}
	
	
	public static java.util.Date sql2java(Date d){
		return new java.util.Date(d.getTime());
	}
	
	public static Date java2sql(java.util.Date d){
		return new Date(d.getTime());
	}
	
	public static String date2string(Date d){
		return shortFormat.format(d);
	}
	
	public static Date getDaysAdded(Date d, int daysToAdd){
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(d.getTime());
		c.add(Calendar.DATE, daysToAdd);
		return new Date(c.getTimeInMillis());
	}
}
