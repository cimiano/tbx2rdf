package tbx2rdf.types;

import tbx2rdf.vocab.TBX;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import tbx2rdf.types.abs.impIDType;

/**
 * This class represents a XReference - A cross-reference that points to an external object using a URI (ISO 30042)
 * 
 * @author Philipp Cimiano - Universität Bielefeld 
 * @author Victor Rodriguez - Universidad Politécnica de Madrid
 */
public class XReference extends impIDType {

    public final String Target, Value;

    public XReference(String Target, String Value) {
        this.Target = Target;
        this.Value = Value;
    }

    /**
     * Gets the RDF model in JENA, with a target, type and value.
     * @return Jena Model
     */
    public void toRDF(Model model, Resource parent) {
        if(type != null) {
            parent.addProperty(model.createProperty(type.getURL()), model.createResource(Target));
        } else {
            parent.addProperty(TBX.xref, model.createResource(Target));
        }
        if(!Value.equals("")) {
            final Resource res = getRes(model);
            res.addProperty(TBX.target, model.createResource(Target));
            res.addProperty(TBX.value, Value);
            parent.addProperty(TBX.xref, res);
        }
        
    }
}
