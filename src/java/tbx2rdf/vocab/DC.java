package tbx2rdf.vocab;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

/**
 * Common Jena Resources for Dublincore 
 * @author Victor - Ontology Engineering Group - UPM
 */
public class DC {
     private static Model defaultModel = ModelFactory.createDefaultModel(); 
     
     public static Property rights = defaultModel.createProperty("http://purl.org/dc/terms/rights");
     public static Property source = defaultModel.createProperty("http://purl.org/dc/terms/source");
     public static Property attribution= defaultModel.createProperty("http://creativecommons.org/ns#attributionName");
     
     public static Property language = defaultModel.createProperty("http://purl.org/dc/terms/language");

}
