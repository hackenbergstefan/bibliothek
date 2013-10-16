package util;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import log.Logger;
import db.DBManager;

public class AsyncWritableListFiller {
	
	public static void fillListAsync(final WritableList2 data, final String sqlQuery, final Constructor construct, final int step){
		data.clear();
		new Thread(){
			public void run() {
				try{
					Statement state = DBManager.getIt().getConnection().createStatement();
					ResultSet set = state.executeQuery(sqlQuery);
					final ArrayList vec = new ArrayList();
					final MutableInteger curStart = new MutableInteger(0);
					while(set.next()){
						Object b = null;
						try {
							b = construct.newInstance(set);
						} catch (Exception e){
							e.printStackTrace();
						}
						vec.add(b);
						if(vec.size()%step == 0){
							AsyncWritableListFiller.addToList(data, vec, curStart, step);
						}
					}
					AsyncWritableListFiller.addToListAllRest(data, vec, curStart);
					set.close();
					state.close();
				}catch(SQLException ex){
					Logger.logError(ex.getMessage());
				}
			}
		}.start();
	}
	
	/**
	 * 
	 * @param data Writable List where data has to be added
	 * @param vec The entries which should be added to data
	 * @param curStart current starting position from where sublist of vec will be taken
	 * @param step number of entries to be added to data
	 */
	public static void addToList(final WritableList2 data, final ArrayList vec, final MutableInteger curStart, final int step){
		data.getRealm().asyncExec(new Runnable() {
			@Override
			public void run() {
				int curSize = vec.size();
				int cur = curStart.getValue();
				int curEnd = (int)Math.min(curSize, cur+step);
				curStart.setValue(curEnd);
				
				if(cur < curSize && curEnd <= curSize)
					data.addAll(vec.subList(cur, curEnd));
			}
		});
	}
	
	/**
	 * 
	 * @param data Writable List where data has to be added
	 * @param vec The entries which should be added to data
	 * @param curStart current starting position from where sublist of vec will be taken
	 * @param step number of entries to be added to data
	 */
	public static void addToListAllRest(final WritableList2 data, final ArrayList vec, final MutableInteger curStart){
		data.getRealm().exec(new Runnable() {
			@Override
			public void run() {
				int curSize = vec.size();
				int cur = curStart.getValue();
				int curEnd = curSize;
				curStart.setValue(curEnd);
				
				if(cur < curSize && curEnd <= curSize)
					data.addAll(vec.subList(cur, curEnd));
			}
		});
		if(data.getAfterChangeRun() != null)
			data.getRealm().exec(data.getAfterChangeRun());
	}

}
