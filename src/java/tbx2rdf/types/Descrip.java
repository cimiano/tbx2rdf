package tbx2rdf.types;

import com.hp.hpl.jena.graph.NodeFactory;
import tbx2rdf.vocab.TBX;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import org.w3c.dom.NodeList;
import tbx2rdf.DatatypePropertyMapping;
import tbx2rdf.IndividualMapping;
import tbx2rdf.Mapping;
import tbx2rdf.Mappings;
import tbx2rdf.ObjectPropertyMapping;
import tbx2rdf.types.abs.impIDLangTypeTgtDtyp;

/**
 * This class provides a textual description
 * <descrip type="context">
 * 
 * www.isocat.org/datcat/DC-149
 * ISO30042: Context - A text which illustrates a concept or a term, by containing the concept designation itself. 
 * Contexts must be authentic, that is, they must be obtained from an existing source, and not created by the terminologist. Contexts are
 * documented very frequently in terminology collections. Contexts can provide information for determining term usage
 * and collocations. In TBX, the context as a term-related data-category. A context can be further categorized according
 * to context type.
 * @author Philipp Cimiano - Universität Bielefeld 
 * @author Victor Rodriguez - Universidad Politécnica de Madrid
 */
public class Descrip extends impIDLangTypeTgtDtyp {

    public final NodeList value;

    public Descrip(NodeList value, Mapping type, String lang, Mappings mappings) {
        super(type,lang,mappings);
        this.value = value;
    }

    /**
     * Returns the jena model
     * @todo complete
     */
    public void toRDF(Model model, Resource parent) {
        if(type == null) {
            System.err.println("Null type ignored!");            
        } else if(type instanceof IndividualMapping) {
            System.err.println("Using individual mapping as a property! <" + type.getURL() + ">");
            parent.addProperty(model.createProperty(type.getURL()), model.createResource(target));
        } else if(type instanceof ObjectPropertyMapping) {
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
            throw new RuntimeException("Unexpected mapping type");
        }
    }
}
