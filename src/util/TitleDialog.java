package util;

import model.IDefault;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.internal.databinding.swt.WidgetBooleanValueProperty;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class TitleDialog extends TitleAreaDialog {

    private String message = null;
    private int messageType;

    public TitleDialog(Shell parentShell) {
        super(parentShell);
    }
   
            
    @Override
    public void setErrorMessage(String newErrorMessage) {
        Button okButton = getButton(IDialogConstants.OK_ID);
        /*if (okButton != null) 
            okButton.setEnabled(newErrorMessage == null);*/
        super.setErrorMessage(newErrorMessage);
    }
    
    @Override
    public void setMessage(String newMessage, int type){
        if (this.message == null){
            this.message = newMessage;/// Done just once
            this.messageType = type;
        }
        if (newMessage == null && this.message != null){
            newMessage = this.message;
            type = this.messageType;
        }
        super.setMessage(newMessage, type);
    }
    
    @Override
    protected Control createButtonBar(Composite parent) {
    	Control c = super.createButtonBar(parent);
    	getButton(OK).setEnabled(false);
    	initValues();
    	return c;
    }

    
    protected void initValues(){
    	
    }
    
    protected void addValidation(DataBindingContext dbc){
    	// aggregate the status information of all bindings in one object
    	// (AggregateValidationStatus.MAX_SEVERITY means, that we get the most
    	//  severe status when more than one is not OK.)
    	AggregateValidationStatus avs = new AggregateValidationStatus(dbc,
	      AggregateValidationStatus.MAX_SEVERITY);
    	IObservableValue okObs = WidgetProperties.enabled().observe(getButton(IDialogConstants.OK_ID));
    	UpdateValueStrategy str = new UpdateValueStrategy();
    	str.setConverter(new IConverter() {
			
			@Override
			public Object getToType() {
				return boolean.class;
			}
			
			@Override
			public Object getFromType() {
				return IStatus.class;
			}
			
			@Override
			public Object convert(Object arg0) {
				IStatus s = (IStatus)arg0;
				return s.isOK();
			}
		});
    	dbc.bindValue(okObs, avs, null, str);
    	
    }
    
    /**
     * Updates Targets if object has not default values and validates target to model connection.
     * @param dbc
     * @param object
     */
    protected void setBindingValues(DataBindingContext dbc, IDefault object){
    	for(Object o: dbc.getBindings()){
			Binding b = (Binding) o;
			b.validateTargetToModel();
		}
    }
}
