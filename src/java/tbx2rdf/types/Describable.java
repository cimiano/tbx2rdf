package tbx2rdf.types;

//JENA
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
//Java
import java.util.ArrayList;
import java.util.List;
//tbx2rdf
import tbx2rdf.Mappings;

/**
 * 
 */
public class Describable extends NoteLinkInfo {

	// This interface corresponds to an XML Element in the tbx spec that can have auxInfo
	// where auxInfo is defined as follows
	
//	<!ENTITY % auxInfo '(descrip | descripGrp | admin | adminGrp | transacGrp | note | ref
//	| xref)*' >

    final public List<DescripGrp> Descriptions = new ArrayList<DescripGrp>();

    public Describable(String language, Mappings mappings) {
        super(language, mappings);
    }

    @Override
    public void toRDF(Model model, Resource parent) {
        super.toRDF(model, parent);
        for(DescripGrp descrip : Descriptions) {
            descrip.toRDF(model, parent);
        }
    }
    
}
