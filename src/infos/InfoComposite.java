package infos;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import model.Info;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.nebula.widgets.pgroup.FormGroupStrategy;
import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.nebula.widgets.pgroup.TwisteToggleRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

public class InfoComposite extends PGroup {
	protected DataBindingContext m_bindingContext;
	public static final Color INFO_UEBERFAELLIG = SWTResourceManager.getColor(SWT.COLOR_RED);
	public static final Color INFO_UEBERFAELLIG_BACKGROUND = new Color(Display.getCurrent(), new RGB(255,230,230));
	public static final Color INFO_VERLIEHEN = SWTResourceManager.getColor(SWT.COLOR_DARK_CYAN);
	public static final Color INFO_VERLIEHEN_BACKGROUND = new Color(Display.getCurrent(), new RGB(211,249,255));
	public static final Color INFO_VORGEMERKT_VERFUEGBAR = SWTResourceManager.getColor(SWT.COLOR_GREEN);
	public static final Color INFO_VORGEMERKT_VERFUEGBAR_BACKGROUND = new Color(Display.getCurrent(), new RGB(230,255,230));
	
	
	protected Composite compositeLinks;
	protected StyledText txtMessage;
	
	
	protected Info info;
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);

	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public InfoComposite(Composite parent, int style, Info info) {
		super(parent, SWT.NONE);
		setLinePosition(SWT.BOTTOM);
		setToggleRenderer(new TwisteToggleRenderer());
		
		this.info = info;
		
		setStrategy(new FormGroupStrategy());
		
		setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));

		setLayout(new GridLayout(1,false));
//		txtTitle = new StyledText(this, SWT.READ_ONLY | SWT.WRAP);
//		txtTitle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
//		txtTitle.setFont(SWTResourceManager.getFont("Segoe UI", 12, SWT.BOLD));
//		txtTitle.setEditable(false);
//		
		
		txtMessage = new StyledText(this, SWT.READ_ONLY | SWT.WRAP);
		txtMessage.setSize(0, 0);
		txtMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		txtMessage.setEditable(false);
		
		compositeLinks = new Composite(this, SWT.NONE);
		compositeLinks.setSize(0, 0);
		compositeLinks.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		compositeLinks.setLayout(new FillLayout(SWT.VERTICAL));
		setExpanded(false);
		
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	

	public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changes.removePropertyChangeListener(listener);
    }
}
