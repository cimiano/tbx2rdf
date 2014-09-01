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
public class TransacGrp extends NoteLinkInfo {

    public final Transaction transaction;

    public final List<TransacNote> transacNotes = new ArrayList<TransacNote>();

    public String date;
    
    public TransacGrp(Transaction transaction) {
        super("en", null);
        this.transaction = transaction;
    }
    private boolean isEmpty() {
        return transacNotes.isEmpty() && AdminInfos.isEmpty() && References.isEmpty() &&
                Transactions.isEmpty() && Xreferences.isEmpty() && 
                (!(transaction.type instanceof ObjectPropertyMapping) || transaction.value == null || transaction.value.equals("")) &&
                (!(transaction.type instanceof DatatypePropertyMapping) || transaction.datatype == null || transaction.datatype.equals(""));
    }

    @Override
    public void toRDF(Model model, Resource resource) {
        if(isEmpty()) {
            transaction.toRDF(model, resource);
        } else {
            final Resource descripRes = getRes(model);
            resource.addProperty(TBX.transaction, descripRes);
            descripRes.addProperty(RDF.type, TBX.Transaction);
            super.toRDF(model, descripRes);
        }
    }
}
