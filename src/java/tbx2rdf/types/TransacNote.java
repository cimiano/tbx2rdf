package tbx2rdf.types;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
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

	@Override
	public void toRDF(Model model, Resource parent) {
		if(type.getURL().equalsIgnoreCase(PROVO.wasAssociatedWith.getURI())) {
			final Resource res = getRes(model);
			parent.addProperty(PROVO.wasAssociatedWith, res);
			res.addProperty(RDF.type, PROVO.Agent);
			res.addProperty(RDFS.label, nodelistToString(value));
		} else {
			super.toRDF(model, parent); 
		}
	}

	
}