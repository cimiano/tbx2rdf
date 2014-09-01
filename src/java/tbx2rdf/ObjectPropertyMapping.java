package tbx2rdf;

public class ObjectPropertyMapping implements Mapping{

	String propertyURL;
	
	String XML_Attribute;

	public ObjectPropertyMapping(String url, String attribute)
	{
		propertyURL = url;
		XML_Attribute = attribute;
	}
	
	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return propertyURL;
	}
	
	public String getXMLAttribute()
	{
		return XML_Attribute;
	}
	
   public String toString()
   {
	   return propertyURL + XML_Attribute;
   }
	
	
}
