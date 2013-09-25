package statistics;

import java.util.Vector;

import model.Buch;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

public class MedienTop extends Composite {
	private int anzahl = 5;
	private Vector<StyledText> labels = new Vector<StyledText>();
	

	/**
	 * @return the anzahl
	 */
	public int getAnzahl() {
		return anzahl;
	}



	/**
	 * @param anzahl the anzahl to set
	 */
	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}



	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MedienTop(Composite parent, int style) {
		super(parent, style);
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.spacing = 5;
		setLayout(fillLayout);
				
		update();

	}
	
	@Override
	public void update() {
		for(StyledText l: labels)
			l.dispose();
		labels.clear();
		
		Vector<String[]> vec = Buch.getTop(5);
		
		
		for(int i=0;i<(int)Math.min(anzahl, vec.size());i++){
			String[] s = vec.get(i); 
			StyledText l = new StyledText(this, SWT.READ_ONLY | SWT.WRAP);
			
			l.setText(s[0]+" x  "+s[1]+" - "+s[2]+" ("+s[3]+")");
			
			int start;
			//#
			StyleRange range = new StyleRange();
			range.start = 0;
			range.length = s[0].length();
			range.fontStyle = SWT.BOLD;
			l.setStyleRange(range);
			
			//titel
			start = s[0].length() + s[1].length() + 7;
			range = new StyleRange();
			range.start = start;
			range.length = s[2].length();
			range.fontStyle = SWT.BOLD;
			l.setStyleRange(range);
			
			//isbn
			start = start + s[2].length() + 2;
			range = new StyleRange();
			range.start = start;
			range.length = s[3].length();
			range.fontStyle = SWT.ITALIC;
			range.foreground = SWTResourceManager.getColor(SWT.COLOR_GRAY);
			l.setStyleRange(range);
			
			labels.add(l);
		}
		
		layout(true, true);
	}
	
}
