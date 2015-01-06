package tbx2rdf.vocab;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Specific IATE resources
 * @author Victor
 */
public class IATE {
	private static Model defaultModel = ModelFactory.createDefaultModel(); 


        public static Resource rights = defaultModel.createResource("http://iate.europa.eu/copyright.html");
        public static Resource iate = defaultModel.createResource("http://iate.europa.eu/");
        
        
        public static Resource Lexicon = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/data/iate#subjectField");
        
}
