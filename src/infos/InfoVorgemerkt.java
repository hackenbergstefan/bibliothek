package infos;

import gui.MainApplication;
import model.Info;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

import util.DateUtils;
import util.FontUtil;

public class InfoVorgemerkt extends InfoComposite {
	private Link linkZurueckgeben;
	private Link linkJumpToAusleihe;
	
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public InfoVorgemerkt(Composite parent, int style, final Info info) {
		super(parent, style,info);
		txtMessage.setTopMargin(5);
		txtMessage.setRightMargin(5);
		txtMessage.setLeftMargin(5);
		txtMessage.setBottomMargin(5);
		
		setBackground(INFO_VERLIEHEN_BACKGROUND);
		
//		txtTitle.setBottomMargin(5);
//		txtTitle.setRightMargin(5);
//		txtTitle.setTopMargin(5);
//		txtTitle.setLeftMargin(5);
//		
//		txtTitle.setText("Ausleihe");
//		txtTitle.setForeground(INFO_VERLIEHEN);
		
		setText("Vormerkung");
		setForeground(INFO_VERLIEHEN);
		
		linkZurueckgeben = new Link(compositeLinks, SWT.NONE);
		linkZurueckgeben.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				info.getAusleihe().setVorgemerkt(false);
				info.getAusleihe().eintragen();
				changes.firePropertyChange("info", null, info);
			}
		});
		linkZurueckgeben.setText("<a>Vormerkung in Ausleihe umwandeln.</a>");
		
		
		linkJumpToAusleihe = new Link(compositeLinks, SWT.NONE);
		linkJumpToAusleihe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getShell().dispose();
				MainApplication.MAIN.selectAusleihe(info.getAusleihe());
			}
		});
		
		linkJumpToAusleihe.setText("<a>Zur Ausleihe springen.</a>");

		
		if(info != null){
		
			StyledString string = new StyledString();
			string.append(info.getSchueler().toStyledString());
			string.append("\nhat\n");
			string.append(info.getBuch().toStyledString());
			string.append("\nvorgemerkt von ");
			string.append(DateUtils.date2string(info.getAusleihe().getVon()), FontUtil.boldStyler());
			
			string.append(" bis ");
			string.append(DateUtils.date2string(info.getAusleihe().getBis()), FontUtil.boldStyler());
			string.append(".");
			
			txtMessage.setText(string.getString());
			txtMessage.setStyleRanges(string.getStyleRanges());
		}		
		
		initDataBindings();
	}

	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue txtMessageObserveBackgroundObserveWidget = SWTObservables.observeBackground(txtMessage);
		IObservableValue getBackgroundRGBObserveValue = SWTObservables.observeBackground(this);
		bindingContext.bindValue(txtMessageObserveBackgroundObserveWidget, getBackgroundRGBObserveValue);
		//
		
		IObservableValue tar = SWTObservables.observeBackground(linkZurueckgeben);
		bindingContext.bindValue(tar, getBackgroundRGBObserveValue);
		//
		return bindingContext;
	}

}
