package tbx2rdf.types;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.w3c.dom.NodeList;
import tbx2rdf.Mapping;
import tbx2rdf.Mappings;
import tbx2rdf.types.abs.impIDLangTypeTgtDtyp;
import tbx2rdf.vocab.PROVO;

/**
 *
 * @author jmccrae
 */
public class TransacNote extends impIDLangTypeTgtDtyp {
    public TransacNote(NodeList value, Mapping type, String lang, Mappings mappings) {
        super(type, lang, mappings, value);
    }
    
        //Static map to match at last the first 10000 agents to see if they are repeated. After this number, 
        //performance may be very slow and it does not worth. Also do mind that this is a static member. Handle with care!
        public static Map<String, Resource> mapAgents = new HashMap();

	@Override
	public void toRDF(Model model, Resource parent) {
		if(type.getURL().equalsIgnoreCase(PROVO.wasAssociatedWith.getURI())) {
                        String svalue = nodelistToString(value);
                        Resource res = mapAgents.get(svalue);
                        if (res==null)
                        {
                            res = model.createResource(model.expandPrefix(":" +"Agent-" + UUID.randomUUID().toString()));
                            res.addProperty(RDF.type, PROVO.Agent);
                            res.addProperty(RDFS.label, svalue);
//                            model.add(res, RDF.type, PROVO.Agent);
                            mapAgents.put(svalue,res);
                            if (mapAgents.size()>10000)
                                mapAgents.clear();
                        }
 			parent.addProperty(PROVO.wasAssociatedWith, res);
		} else {
			super.toRDF(model, parent); 
		}
	}
        
     

	
}