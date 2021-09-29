package tbx2rdf.types;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import java.util.ArrayList;
import java.util.List;
import tbx2rdf.DatatypePropertyMapping;
import tbx2rdf.ObjectPropertyMapping;
import tbx2rdf.types.abs.impID;
import tbx2rdf.vocab.TBX;

/**
 * A meta data-category is a core-structure module data-category that takes a type attribute, such as <descrip>, <admin>, and <termNote>.
 * In particular, <admin> Contains information of an administrative nature for the node in question, such as the source of information, or the
 * project or client for which it applies. The type of administrative information is indicated by the value of the type
 * attribute. It can appear alone, or, if additional information needs to be provided, such as a note or a reference, it can
 * be nested in an <adminGrp> elemen
 * @author jmccrae
 */
public class AdminGrp extends impID {
    public final AdminInfo admin;
    public final List<AdminNote> notes = new ArrayList<AdminNote>();
    public final List<Reference> refs = new ArrayList<Reference>();
    public final List<XReference> xrefs = new ArrayList<XReference>();

    public AdminGrp(AdminInfo admin) {
        this.admin = admin;
    }

    private boolean isEmpty() {
        return notes.isEmpty() && refs.isEmpty() && xrefs.isEmpty();
    }
    
    /**
     * We enter here when the <admin> element is being entered.
     */
    @Override
    public void toRDF(Model model, Resource parent) {
	admin.toRDF(model, parent);
        if(!isEmpty()) {
            final Resource adminRes = getRes(model);
            adminRes.addProperty(RDF.type, TBX.Admin);
            parent.addProperty(TBX.admin, adminRes);
            for(AdminNote note : notes) {
                note.toRDF(model, adminRes);
            }
            for(Reference ref : refs) {
                ref.toRDF(model, adminRes);
            }
            for(XReference ref : xrefs) {
                ref.toRDF(model, adminRes);
            }
        }
    }
   
}
