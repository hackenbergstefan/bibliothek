package gui.validators;

import gui.StringConstants;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class IntGreaterZeroValidator implements IValidator{

	@Override
	public IStatus validate(Object value) {
		try{
			int i = new Integer((String)value);
			if(i > 0) return ValidationStatus.ok();
		}catch(NumberFormatException ex){
		}
		return ValidationStatus.error(StringConstants.VALIDATION_ERROR_NOTINTGREATERZERO);
	}

}
