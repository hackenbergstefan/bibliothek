package infos;

import gui.MainApplication;
import model.Info;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

import util.FontUtil;

public class InfoUeberfaellig extends InfoComposite {
	private Link linkZurueckgeben;
	private Link linkJumpToAusleihe;
	private Link linkJumpToSchueler;
	private Link linkVerlaengern;
	
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public InfoUeberfaellig(Composite parent, int style, final Info info) {
		super(parent, style,info);
		txtMessage.setTopMargin(5);
		txtMessage.setRightMargin(5);
		txtMessage.setLeftMargin(5);
		txtMessage.setBottomMargin(5);
		
		setBackground(INFO_UEBERFAELLIG_BACKGROUND);
		
//		txtTitle.setBottomMargin(5);
//		txtTitle.setRightMargin(5);
//		txtTitle.setTopMargin(5);
//		txtTitle.setLeftMargin(5);
//		
//		txtTitle.setText("Überfällig");
//		txtTitle.setForeground(INFO_UEBERFAELLIG);
		setText("Überfällig");
		setForeground(INFO_UEBERFAELLIG);
		
		linkZurueckgeben = new Link(compositeLinks, SWT.NONE);
		linkZurueckgeben.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				info.getAusleihe().zuruckgeben();
				changes.firePropertyChange("info", null, info);
			}
		});
		linkZurueckgeben.setText("<a>Medium zur\u00FCckgeben.</a>");
		
		linkVerlaengern = new Link(compositeLinks, SWT.NONE);
		linkVerlaengern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				info.getAusleihe().setDauer(info.getAusleihe().getDauer()+14);
				info.getAusleihe().eintragen();
				changes.firePropertyChange("info", null, info);
			}
		});
		linkVerlaengern.setText("<a>Ausleihe verlängern.</a>");
		
		linkJumpToAusleihe = new Link(compositeLinks, SWT.NONE);
		linkJumpToAusleihe.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getShell().dispose();
				MainApplication.MAIN.selectAusleihe(info.getAusleihe());
			}
		});
		
		linkJumpToAusleihe.setText("<a>Zur Ausleihe springen.</a>");
		
		linkJumpToSchueler = new Link(compositeLinks, SWT.NONE);
		linkJumpToSchueler.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getShell().dispose();
				MainApplication.MAIN.selectSchueler(info.getSchueler());
			}
		});
		
		linkJumpToSchueler.setText("<a>Zum Schüler springen.</a>");
		
		if(info != null){
		
			StyledString string = new StyledString();
			string.append(info.getSchueler().toStyledString());
			string.append("\nist mit der Ausleihe von\n");
			string.append(info.getBuch().toStyledString());
			string.append(" überfällig seit ");
			string.append(""+info.getAusleihe().getDaysTooLate(), new Styler() {
				
				@Override
				public void applyStyles(TextStyle textStyle) {
					textStyle.font = FontUtil.boldFont;
					textStyle.foreground = INFO_UEBERFAELLIG;
				}
			});
			string.append(" Tagen.\n");
			string.append(info.getAusleihe().getAnmerkungen(), new Styler() {
				
				@Override
				public void applyStyles(TextStyle textStyle) {
					textStyle.font = FontUtil.italicFont;
					textStyle.foreground = INFO_UEBERFAELLIG;
				}
			});
			
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
		
		tar = SWTObservables.observeBackground(linkJumpToAusleihe);
		bindingContext.bindValue(tar, getBackgroundRGBObserveValue);
		
		tar = SWTObservables.observeBackground(linkJumpToSchueler);
		bindingContext.bindValue(tar, getBackgroundRGBObserveValue);
		
		tar = SWTObservables.observeBackground(linkVerlaengern);
		bindingContext.bindValue(tar, getBackgroundRGBObserveValue);
		//
		return bindingContext;
	}

}
