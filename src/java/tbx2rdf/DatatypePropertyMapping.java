package tbx2rdf;

import java.util.Set;

public class DatatypePropertyMapping implements Mapping {

	String propertyURL;
	
	Set<String> allowedValues;
	
	public DatatypePropertyMapping(String url, Set<String> values)
	{
		propertyURL = url;
		allowedValues = values;
	}
	
	
	@Override
	public String getURL() {
		return propertyURL;
	}
	
	public Set<String> getAllowedValues()
	{
		return allowedValues;
	}
	
	public boolean allowed(String value)
	{
	
		if (allowedValues.isEmpty() || allowedValues.contains(value)) return true;
	
		return false;
	}
	
	public String toString()
	{
		return propertyURL + allowedValues;
	}
	
}
