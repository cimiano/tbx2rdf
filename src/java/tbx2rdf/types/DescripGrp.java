package tbx2rdf.types;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
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

        //@todo This has been done adhoc. There should be a general mechanism to solve this.
        if (descrip != null && descrip.type != null &&
				descrip.type.getURL().equals("http://tbx2rdf.lider-project.eu/tbx#reliabilityCode"))
        {
            descrip.datatype = "http://www.w3.org/2001/XMLSchema#integer";
        }
        this.descrip = descrip;
    }

    private boolean isEmpty() {
        return descripNote.isEmpty() && AdminInfos.isEmpty() && References.isEmpty() &&
                Transactions.isEmpty() && Xreferences.isEmpty();
    }

    @Override
    public void toRDF(Model model, Resource resource) {
    descrip.toRDF(model, resource);
        if(!isEmpty()) {
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
