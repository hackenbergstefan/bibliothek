package util;

import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.observable.IObservablesListener;
import org.eclipse.core.databinding.observable.ObservableEvent;
import org.eclipse.core.databinding.observable.list.ListDiff;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.list.WritableList;


/**
 * Fires ListChangeEvent when finished majoyListChange, where only ListDiffEntry has position -1
 * @author Zero
 *
 */
public class WritableList2 extends WritableList{
	private Runnable afterChangeRun = null;

	public void finishMajorListChange(){
		
		fireListChange(new ListDiff() {
			
			@Override
			public ListDiffEntry[] getDifferences() {
				return new ListDiffEntry[]{new ListDiffEntry() {
					
					@Override
					public boolean isAddition() {
						return false;
					}
					
					@Override
					public int getPosition() {
						return size();
					}
					
					@Override
					public Object getElement() {
						return get(size()-1);
					}
				}};
			}
		});
	}
	
	public void addAfterChangeRun(final Runnable r){
		afterChangeRun = r;
	}
	
	public Runnable getAfterChangeRun(){
		return null;
	}
	
}
