package util;

import org.eclipse.core.databinding.conversion.IConverter;

public class Sql2JavaDateConverter implements IConverter{

	@Override
	public Object getFromType() {
		return java.util.Date.class;
	}

	@Override
	public Object getToType() {
		return java.sql.Date.class;
	}

	@Override
	public Object convert(Object fromObject) {
		return DateUtils.java2sql((java.util.Date)fromObject);
	}

}
