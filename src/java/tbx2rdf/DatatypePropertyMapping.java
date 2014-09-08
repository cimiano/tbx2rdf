package tbx2rdf;

import java.util.Set;

public class DatatypePropertyMapping implements Mapping {

	String propertyURL;

	
	public DatatypePropertyMapping(String url)
	{
		propertyURL = url;
	}
	
	
	@Override
	public String getURL() {
		return propertyURL;
	}
	

	
	public String toString()
	{
		return String.format("Datatype property: <%s> {%s}",propertyURL);
	}
	
}
