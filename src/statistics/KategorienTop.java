package statistics;

import java.util.Vector;

import model.Kategorie;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

public class KategorienTop extends Composite {
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
	public KategorienTop(Composite parent, int style) {
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
		
		Vector<Object[]> vec = Kategorie.getTop(anzahl);
		
		
		for(int i=0;i<(int)Math.min(anzahl, vec.size());i++){
			Object[] s = vec.get(i);
			Kategorie k = (Kategorie)s[1];
			StyledText l = new StyledText(this, SWT.READ_ONLY);
			l.setText("  "+s[0]+" x  "+k.getName());
			l.setBackground(k.getColor());
			if(k.getColor().getRGB().getHSB()[2] < 0.7) l.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			
			labels.add(l);
		}
		
		layout(true, true);
	}
	
}
