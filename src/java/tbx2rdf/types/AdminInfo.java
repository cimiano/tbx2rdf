package tbx2rdf.types;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import org.w3c.dom.NodeList;
import tbx2rdf.DatatypePropertyMapping;
import tbx2rdf.Mapping;
import tbx2rdf.Mappings;
import tbx2rdf.ObjectPropertyMapping;
import tbx2rdf.types.abs.impIDLangTypeTgtDtyp;

public class AdminInfo extends impIDLangTypeTgtDtyp {
    public final NodeList value;

    public AdminInfo(NodeList value, Mapping type, String lang, Mappings mappings) {
        super(type, lang, mappings);
        this.value = value;
    }

    @Override
    public void toRDF(Model model, Resource parent) {
        if(type instanceof ObjectPropertyMapping) {
            parent.addProperty(model.createProperty(type.getURL()), model.createResource(target));
            if(value.getLength() > 0) {
                parent.addProperty(RDF.value, nodelistToString(value), XMLLiteral);
            }
        } else if(type instanceof DatatypePropertyMapping) {
            if(datatype != null) {
                parent.addProperty(model.createProperty(type.getURL()), nodelistToString(value), NodeFactory.getType(datatype));
            } else {
                parent.addProperty(model.createProperty(type.getURL()), nodelistToString(value), lang);
            }
            if(target != null && !target.equals("")) {
                parent.addProperty(DCTerms.source, model.createResource(target));
            }
        } else {
            throw new RuntimeException("Unexpected mapping type: " + type);
        }
    }
    

}
