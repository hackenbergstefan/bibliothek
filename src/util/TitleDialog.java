package util;

import model.IDefault;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
        if (okButton != null) 
            okButton.setEnabled(newErrorMessage == null);
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
    	initValues();
    	return super.createButtonBar(parent);
    }
    
    protected void initValues(){
    	
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
