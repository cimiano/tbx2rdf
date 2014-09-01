package tbx2rdf;

/**
 * Mapping to an individual
 * @author John McCrae
 */
public class IndividualMapping implements Mapping {
    final String url;

    public IndividualMapping(String url) {
	this.url = url;
    }

    @Override
    public String getURL() {
	return url;
    }
    
    @Override
    public String toString() {
        return String.format("Individiual <%s>", url);
    }
}
