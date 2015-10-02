package tbx2rdf;

import java.util.Set;

/**
 * 
 */
public class DatatypePropertyMapping implements Mapping {

	String propertyURL;
	String datatypeURL;
	public DatatypePropertyMapping(String url, String datatypeURL)
	{
		propertyURL = url;
		this.datatypeURL = datatypeURL;
	}
	@Override
	public String getURL() {
		return propertyURL;
	}
	public String getDatatypeURL() { 
		return datatypeURL;
	}
	public String toString()
	{
		return String.format("Datatype property: <%s> {%s}",propertyURL);
	}
}
