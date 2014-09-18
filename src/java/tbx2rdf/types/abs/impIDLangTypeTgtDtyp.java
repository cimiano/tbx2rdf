package tbx2rdf.types.abs;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.net.URI;
import java.net.URISyntaxException;
import org.w3c.dom.NodeList;
import tbx2rdf.DatatypePropertyMapping;
import tbx2rdf.IndividualMapping;
import tbx2rdf.Mapping;
import tbx2rdf.Mappings;
import tbx2rdf.ObjectPropertyMapping;
import tbx2rdf.TBXFormatException;

/**
 *
 * @author John McCrae
 */
public abstract class impIDLangTypeTgtDtyp extends impIDLang {

	public final Mapping type;
	public String target;
	public String datatype;
	public NodeList value;

	public impIDLangTypeTgtDtyp(Mapping type, String lang, Mappings mappings, NodeList value) {
		super(lang, mappings);
		this.type = type;
		this.value = value;
	}

	@Override
	public void toRDF(Model model, Resource parent) {
		if (type instanceof ObjectPropertyMapping) {
			final ObjectPropertyMapping opm = (ObjectPropertyMapping) type;
			final String valueString = value == null ? null : nodelistToString(value);
			if (target != null && !opm.hasRange()) {
				try {
					final URI uri = new URI(target);
					parent.addProperty(model.createProperty(opm.getURL()), model.createResource(uri.toString()));
				} catch (URISyntaxException x) {
					throw new TBXFormatException("Bad URL " + target);
				}
			} else if (opm.hasRange()) {
				final IndividualMapping im = opm.getMapping(valueString);
				if (im == null) {
					throw new TBXFormatException("Mapping not in declared property range or value URI not declared: " + valueString + " " + opm.getURL());
				}
				parent.addProperty(model.createProperty(opm.getURL()),
					model.createResource(im.getURL()));
			} else {
				try {
					final URI uri = new URI(valueString);
				} catch (URISyntaxException x) {
					throw new TBXFormatException("Bad URI or could not otherwise understand object property value: " + valueString);
				}
			}
		} else if (type instanceof DatatypePropertyMapping) {
			final DatatypePropertyMapping dpm = (DatatypePropertyMapping)type;
			if (datatype != null) {
				parent.addProperty(model.createProperty(type.getURL()), nodelistToString(value), NodeFactory.getType(datatype));
			} else if(dpm.getDatatypeURL() != null) { 
				parent.addProperty(model.createProperty(type.getURL()), nodelistToString(value), NodeFactory.getType(dpm.getDatatypeURL()));
			} else if (value.getLength() <= 1) {
				parent.addProperty(model.createProperty(type.getURL()), nodelistToString(value), lang);
			} else {
				parent.addProperty(model.createProperty(type.getURL()), nodelistToString(value), XMLLiteral);
			}
		} else {
			throw new RuntimeException("Unexpected mapping type");
		}

	}
}
