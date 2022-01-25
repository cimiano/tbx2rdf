package tbx2rdf.vocab;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * Class with some common static terms 
 * @author vroddon
 */
public class SKOS {
    private static Model defaultModel = ModelFactory.createDefaultModel(); 
     public static Resource Concept = defaultModel.createResource("http://www.w3.org/2004/02/skos/core#Concept");
     
}
