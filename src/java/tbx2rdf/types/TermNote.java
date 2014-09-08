package tbx2rdf.types;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import org.w3c.dom.NodeList;

import tbx2rdf.DatatypePropertyMapping;
import tbx2rdf.IndividualMapping;
import tbx2rdf.Mapping;
import tbx2rdf.Mappings;
import tbx2rdf.ObjectPropertyMapping;
import tbx2rdf.types.abs.impIDLangTypeTgtDtyp;

/**
 *
 * @author John McCrae
 */
public class TermNote extends impIDLangTypeTgtDtyp {
    public final NodeList value;

    public TermNote(NodeList value, Mapping type, String lang, Mappings mappings) {
        super(type, lang, mappings);
        this.value = value;
    }

    @Override
    public void toRDF(Model model, Resource parent) {
        if(type == null) {
            System.err.println("Null type ignored!");            
        } else if(type instanceof IndividualMapping) {
            System.err.println("Using individual mapping as a property! <" + type.getURL() + ">");
            parent.addProperty(model.createProperty(type.getURL()), model.createResource(target));
        } else if(type instanceof ObjectPropertyMapping) {
        	
        	System.out.print("Value: "+nodelistToString(value)+"\nTarget ="+target+"\n");
        	
        	if (((ObjectPropertyMapping) type).getTargetAtttribute() != null)
      	   	{
      		   parent.addProperty(model.createProperty(type.getURL()), model.createResource(target));
         	}
      	   
      	   else if (((ObjectPropertyMapping) type).getAllowedValues() != null)
      	   {
         		// check if set contains value, create an object property, turning the value into an individual
         		if (((ObjectPropertyMapping) type).getAllowedValues().contains(nodelistToString(value)))
         		{
         			 parent.addProperty(model.createProperty(type.getURL()), model.createResource("tbx:"+nodelistToString(value)));

         		}
         		else
         		{
         			throw new RuntimeException("Undefined value: "+target+" for type"+ "<type>" +" in element TermNote!\n");
         		}
      	   }
      	   
      	   else
      	   {
      		   Resource myRes = model.createResource("someNewResource");
         		
      		   parent.addProperty(model.createProperty(type.getURL()), myRes );
      		   model.add(myRes,RDFS.label, model.createLiteral(nodelistToString(value)));
      	   }
        	
            parent.addProperty(model.createProperty(type.getURL()), model.createResource(target));
            if(value.getLength() > 0) {
                parent.addProperty(RDF.value, nodelistToString(value), XMLLiteral);
            }
        } else if(type instanceof DatatypePropertyMapping) {
        	
        	System.out.print("It is a datatype prop!\n");
        	
            if(datatype != null) {
                parent.addProperty(model.createProperty(type.getURL()), nodelistToString(value), NodeFactory.getType(datatype));
            } else {
                parent.addProperty(model.createProperty(type.getURL()), nodelistToString(value), lang);
            }
            if(target != null && !target.equals("")) {
                parent.addProperty(DCTerms.source, model.createResource(target));
            }
        } else {
            throw new RuntimeException("Unexpected mapping type");
        }
    }

    
}
