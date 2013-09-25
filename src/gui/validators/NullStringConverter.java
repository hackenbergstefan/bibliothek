package gui.validators;

import org.eclipse.core.databinding.conversion.IConverter;

public class NullStringConverter implements IConverter{
	@Override
	public Object getToType() {
		return String.class;
	}
	
	@Override
	public Object getFromType() {
		return String.class;
	}
	
	@Override
	public Object convert(Object fromObject) {
		return fromObject==null?"":fromObject;
	}
}
