package tbx2rdf.types;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import tbx2rdf.Mappings;
import tbx2rdf.types.abs.impIDLang;
import tbx2rdf.vocab.ONTOLEX;

/**
 *
 * @author John McCrae
 */
public class TermComp extends impIDLang {
    public final String value;

    public TermComp(String value, String lang, Mappings mappings) {
        super(lang, mappings);
        this.value = value;
    }

    @Override
    public void toRDF(Model model, Resource parent) {
        final Resource res = getRes(model);
        parent.addProperty(ONTOLEX.identifies, res);
        res.addProperty(RDF.type, ONTOLEX.LexicalEntry);
        res.addProperty(RDFS.label, value, lang);
    }
    
    
    
}
