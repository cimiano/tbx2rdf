package tbx2rdf.types;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import tbx2rdf.Mapping;
import tbx2rdf.Mappings;
import tbx2rdf.types.abs.impIDLangTypeTgtDtyp;

public class Reference extends impIDLangTypeTgtDtyp {

    public Reference(Mapping type, String language, Mappings mappings) {
        super(type, language, mappings);
    }

    @Override
    public void toRDF(Model model, Resource parent) {
        parent.addProperty(model.createProperty(type.getURL()), model.createResource(target));
        // TODO: What if a reference has a language or a datatype ??
    }
   
}
