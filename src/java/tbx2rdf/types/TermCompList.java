package tbx2rdf.types;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import java.util.ArrayList;
import java.util.List;
import tbx2rdf.Mapping;
import tbx2rdf.vocab.ONTOLEX;

/**
 *
 * @author John McCrae
 */
public class TermCompList extends Describable {
   public final List<TermCompGrp> termComp = new ArrayList<TermCompGrp>();
    public final Mapping type;
   
    public TermCompList(Mapping type) {
        super("eng", null);
        this.type = type;
    }

    @Override
    public void toRDF(Model model, Resource parent) {
        final Resource comp = getRes(model);
        comp.addProperty(ONTOLEX.identifies, parent);
        if (type != null)
            parent.addProperty(model.createProperty(type.getURL()), comp);
            for(TermCompGrp termCompGrp : termComp) {
                termCompGrp.toRDF(model, comp);
            }
        super.toRDF(model, comp);
    }
   
    
}
