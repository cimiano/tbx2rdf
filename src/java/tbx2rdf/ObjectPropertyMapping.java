package tbx2rdf;

import java.util.Set;

public class ObjectPropertyMapping implements Mapping{

	String propertyURL;
	
	String targetAttribute;
	
	Set<String>	allowedValues;
	

	public ObjectPropertyMapping(String url, String attribute)
	{
		propertyURL = url;
		targetAttribute = attribute;
		allowedValues = null;
	}
	
	public ObjectPropertyMapping(String url, Set<String> values)
	{
		propertyURL = url;
		targetAttribute = null;
		allowedValues = values;
	}
	
	public ObjectPropertyMapping(String url)
	{
		propertyURL = url;
		targetAttribute = null;
		allowedValues = null;
	}
	
	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return propertyURL;
	}
	
	public String getTargetAtttribute()
	{
		return targetAttribute;
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
	   return String.format("ObjectProperty <%s>^^<%s>",propertyURL, targetAttribute);
   }
	
	
}
