package gui.validators;

import gui.StringConstants;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class NonBeliebigValidator implements IValidator{

	@Override
	public IStatus validate(Object value) {
		if (value instanceof Integer) {
			int s = (Integer) value;
			// We check if the string is longer then 2 signs
			if (s != 0) {
				return Status.OK_STATUS;
			} else {
				return ValidationStatus
						.error(StringConstants.VALIDATION_ERROR_NONBELIEBIG);
			}
		} else {
			throw new RuntimeException(
					"Not supposed to be called for non-strings.");
		}
	}

}
