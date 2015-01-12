package tbx2rdf.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Common Jena Resources for IATE 
 * @author Victor - Ontology Engineering Group - UPM
 */

public class IATE {
	private static Model defaultModel = ModelFactory.createDefaultModel(); 

        public static Resource rights = defaultModel.createResource("http://iate.europa.eu/copyright.html");
        public static Resource iate = defaultModel.createResource("http://iate.europa.eu/");
        public static Resource Lexicon = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/data/iate#subjectField");
        
}
