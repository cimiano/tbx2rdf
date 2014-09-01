package tbx2rdf.types;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import java.util.ArrayList;
import java.util.List;
import tbx2rdf.DatatypePropertyMapping;
import tbx2rdf.ObjectPropertyMapping;
import tbx2rdf.vocab.TBX;

/**
 *
 * @author John McCrae
 */
public class DescripGrp extends NoteLinkInfo {
    public final Descrip descrip;
    public final List<DescripNote> descripNote = new ArrayList<DescripNote>();

    public DescripGrp(Descrip descrip) {
        super("eng", null);
        this.descrip = descrip;
    }

    private boolean isEmpty() {
        return descripNote.isEmpty() && AdminInfos.isEmpty() && References.isEmpty() &&
                Transactions.isEmpty() && Xreferences.isEmpty() && 
                (!(descrip.type instanceof ObjectPropertyMapping) || descrip.value.getLength() == 0) &&
                (!(descrip.type instanceof DatatypePropertyMapping) || descrip.datatype == null || descrip.datatype.equals(""));
    }

    @Override
    public void toRDF(Model model, Resource resource) {
        if(isEmpty()) {
            descrip.toRDF(model, resource);
        } else {
            final Resource descripRes = getRes(model);
            resource.addProperty(TBX.description, descripRes);
            descripRes.addProperty(RDF.type, TBX.Descrip);
            for(DescripNote note : descripNote) {
                note.toRDF(model, descripRes);
            }
            super.toRDF(model, descripRes);
        }
    }

    
}
