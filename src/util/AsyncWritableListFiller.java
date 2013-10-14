package util;

import java.util.ArrayList;

import org.eclipse.core.databinding.observable.list.WritableList;

public class AsyncWritableListFiller {
	
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
