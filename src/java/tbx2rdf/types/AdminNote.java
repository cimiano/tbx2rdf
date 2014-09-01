package tbx2rdf.types;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import tbx2rdf.DatatypePropertyMapping;
import tbx2rdf.Mapping;
import tbx2rdf.Mappings;
import tbx2rdf.ObjectPropertyMapping;
import tbx2rdf.types.abs.impIDLangTypeTgtDtyp;

/**
 *
 * @author John McCrae
 */
public class AdminNote extends impIDLangTypeTgtDtyp {
    private final String value;

    public AdminNote(String value, Mapping type, String lang, Mappings mappings) {
        super(type, lang, mappings);
        this.value = value;
    }

    @Override
    public void toRDF(Model model, Resource parent) {
        if(type instanceof ObjectPropertyMapping) {
            parent.addProperty(model.createProperty(type.getURL()), model.createResource(target));
            if(!value.equals("")) {
                parent.addProperty(RDF.value, value, lang);
            }
        } else if(type instanceof DatatypePropertyMapping) {
            if(datatype != null) {
                parent.addProperty(model.createProperty(type.getURL()), value, NodeFactory.getType(datatype));
            } else {
                parent.addProperty(model.createProperty(type.getURL()), value, lang);
            }
            if(target != null && !target.equals("")) {
                parent.addProperty(DCTerms.source, model.createResource(target));
            }
        } else {
            throw new RuntimeException("Unexpected mapping type");
        }
    }
    
}
