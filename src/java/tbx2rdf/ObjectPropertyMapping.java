package tbx2rdf;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ObjectPropertyMapping implements Mapping {

	final Map<String, IndividualMapping> indivMappingRef;
	String propertyURL;

	Set<String> allowedValues;

	public ObjectPropertyMapping(String url, Set<String> values, Map<String, IndividualMapping> indivMappingRef) {
		propertyURL = url;
		allowedValues = values;
		this.indivMappingRef = Collections.unmodifiableMap(indivMappingRef);
	}

	public ObjectPropertyMapping(String url, Map<String, IndividualMapping> indivMappingRef) {
		propertyURL = url;
		allowedValues = null;
		this.indivMappingRef = Collections.unmodifiableMap(indivMappingRef);
	}

	@Override
	public String getURL() {
		return propertyURL;
	}
    
	public boolean allowed(String value) {

		if (allowedValues.isEmpty() || allowedValues.contains(value)) {
			return true;
		}

		return false;
	}

    public IndividualMapping getMapping(String value) {
        return indivMappingRef.get(value);
    }

    public boolean hasRange() {
	    return allowedValues != null;
    }

    @Override
	public String toString() {
		return String.format("ObjectProperty <%s>", propertyURL);
	}

}
