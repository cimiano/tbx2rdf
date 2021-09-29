package tbx2rdf.types.abs;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDFS;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;
import tbx2rdf.DatatypePropertyMapping;
import tbx2rdf.ExceptionMapping;
import tbx2rdf.IndividualMapping;
import tbx2rdf.Main;
import tbx2rdf.Mapping;
import tbx2rdf.Mappings;
import tbx2rdf.ObjectPropertyMapping;
import tbx2rdf.TBXFormatException;
import tbx2rdf.datasets.iate.SubjectField;
import tbx2rdf.datasets.iate.SubjectFields;
import tbx2rdf.vocab.TBX;

/**
 * 
 * @author John McCrae
 */
public abstract class impIDLangTypeTgtDtyp extends impIDLang {

    private final static Logger logger = Logger.getLogger(impIDLangTypeTgtDtyp.class);
    
    
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
		} else if (type instanceof ExceptionMapping){
                    final ExceptionMapping em = (ExceptionMapping)type;
                    try {
                        Class<?> c = Class.forName("tbx2rdf.ExceptionMethods");
                        Object o = c.newInstance();
                        Class[] paramTypes = new Class[1];
                        paramTypes[0]=String.class;
                        Method m = c.getDeclaredMethod(em.getURL(), paramTypes);
                        String res=(String) m.invoke(o, nodelistToString(value));
                        Resource r=model.createResource(res);
                        parent.addProperty(TBX.subjectField,r);
                        
                        //Special procedure for IATE subject fields. THIS SHOULD NOT BE HERE because it is breaking the converter neutrality
                        if(r.toString().contains("http://tbx2rdf.lider-project.eu/data/iate/subjectField/"))
                        {
                            String urisf = r.getURI().toString();
                            urisf = urisf.substring(urisf.lastIndexOf("/")+1, urisf.length());
                            SubjectField sf = SubjectFields.mapauris.get(urisf);
                            if (sf!=null)
                                model.add(r,RDFS.label, sf.getTopicString());
                        }
                        //End of the special procedure for IATE subject fields
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                else {
                    logger.warn("Unexpected mapping type when processing " + parent.getURI()  );
                    if (Main.lenient==false)
                        throw new RuntimeException("Unexpected mapping type. You may want to visit https://github.com/cimiano/tbx2rdf/blob/master/MAPPINGS.md");
		}
	}
        
        /**
         * 
         */
    public static String getFirstLabel(Model model, String uri) {
        Resource res = model.getResource(uri);
        if (res == null) {
            return "";
        }
        StmtIterator it = res.listProperties(model.createProperty("http://www.w3.org/2000/01/rdf-schema#label"));
        while (it.hasNext()) {
            Statement stmt2 = it.nextStatement();
            RDFNode nodo = stmt2.getObject();
            return nodo.toString();
        }
        return "";
    }

        
        
}
