package gui.validators;

import gui.StringConstants;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class NullValidator implements IValidator{
	@Override
	public IStatus validate(Object value) {
		if(value == null || value.equals(""))
			return ValidationStatus.error(StringConstants.VALIDATION_ERROR_NONEMPTY);
		else return ValidationStatus.ok();
	}
}
