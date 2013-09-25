package statistics;

import java.util.Vector;

import model.Schueler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class SchuelerTop extends Composite {
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
	public SchuelerTop(Composite parent, int style) {
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
		
		Vector<String> vec = Schueler.getTop(5);
		
		
		for(int i=0;i<(int)Math.min(anzahl, vec.size());i++){
			String s = vec.get(i);
			StyledText l = new StyledText(this, SWT.READ_ONLY);
			l.setText(s);
			labels.add(l);
		}
		
		layout(true, true);
	}
	
}
