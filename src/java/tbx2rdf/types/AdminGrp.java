package tbx2rdf.types;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.ArrayList;
import java.util.List;
import tbx2rdf.DatatypePropertyMapping;
import tbx2rdf.ObjectPropertyMapping;
import tbx2rdf.types.abs.impID;
import tbx2rdf.vocab.TBX;

/**
 *
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
        return notes.isEmpty() && refs.isEmpty() && xrefs.isEmpty() && 
                (!(admin.type instanceof DatatypePropertyMapping) || admin.target == null || admin.target.equals("")) && 
                (!(admin.type instanceof ObjectPropertyMapping) || admin.value == null || admin.value.getLength() == 0);
    }
    
    @Override
    public void toRDF(Model model, Resource parent) {
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
            admin.toRDF(model, adminRes);
        } else {
            admin.toRDF(model, parent);
            
        }
    }
   
}
