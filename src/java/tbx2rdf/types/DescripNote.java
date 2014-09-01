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

/**
 *
 * @author John McCrae
 */
public class DescripNote extends impIDLangTypeTgtDtyp {
    public final NodeList noteText;

    public DescripNote(NodeList noteText, Mapping type, String lang, Mappings mappings) {
        super(type, lang, mappings);
        this.noteText = noteText;
    }

    @Override
    public void toRDF(Model model, Resource parent) {
        if(type instanceof ObjectPropertyMapping) {
            parent.addProperty(model.createProperty(type.getURL()), model.createResource(target));
            if(noteText.getLength() > 0) {
                parent.addProperty(RDF.value, nodelistToString(noteText), XMLLiteral);
            }
        } else if(type instanceof DatatypePropertyMapping) {
            if(datatype != null) {
                parent.addProperty(model.createProperty(type.getURL()), nodelistToString(noteText), NodeFactory.getType(datatype));
            } else {
                parent.addProperty(model.createProperty(type.getURL()), nodelistToString(noteText), lang);
            }
            if(target != null && !target.equals("")) {
                parent.addProperty(DCTerms.source, model.createResource(target));
            }
        } else {
            throw new RuntimeException("Unexpected mapping type");
        }
    }

    
    
}
