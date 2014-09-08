package tbx2rdf.types;

import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import tbx2rdf.DatatypePropertyMapping;
import tbx2rdf.Mapping;
import tbx2rdf.Mappings;
import tbx2rdf.ObjectPropertyMapping;
import tbx2rdf.types.abs.impIDLangTypeTgtDtyp;

/**
 * This class represents a Transaction as defined in the ISO-30042
 * A record that indicates the stage of the entry within the overall process of 
 * creation, approval, and use of a terminology entry. 
 * @author Philipp Cimiano - Universität Bielefeld 
 * @author Victor Rodriguez - Universidad Politécnica de Madrid
 */
public class Transaction extends impIDLangTypeTgtDtyp {
    public final String value;

//
//        Statement st;
//
//        Resource term = model.createResource(name);
//
//        Resource activity = model.createResource();
//
//        st = model.createStatement(term, PROVO.wasGeneratedBy, activity);
//
//        model.add(st);
//
//        st = model.createStatement(activity, RDF.type, PROVO.Activity);
//
//        model.add(st);
//
//        if (Date != null) {
//            st = model.createLiteralStatement(activity, PROVO.endedAtTime, Date);
//            model.add(st);
//        }
//
//        if (Agent != null) {
//
//            Resource agent = model.createResource();
//
//            st = model.createStatement(activity, PROVO.wasAssociatedWith, agent);
//            model.add(st);
//
//            st = model.createStatement(agent, RDF.type, PROVO.Agent);
//            model.add(st);
//
//            st = model.createStatement(agent, RDFS.label, Agent);
//            model.add(st);
//
//        }
//
//        if (Type != null) {
//            st = model.createStatement(activity, RDFS.label, Agent);
//            model.add(st);
//        }
//
//
//        return model;
//    }

    public Transaction(String value, Mapping type, String lang, Mappings mappings) {
        super(type, lang, mappings);
        this.value = value;
    }

    @Override
    public void toRDF(Model model, Resource parent) {
       if(type instanceof ObjectPropertyMapping) {
    	   
    	   
    	   if (((ObjectPropertyMapping) type).getTargetAtttribute() != null)
    	   {
    		   parent.addProperty(model.createProperty(type.getURL()), model.createResource(target));
       		}
    	   
    	   else if (((ObjectPropertyMapping) type).getAllowedValues() != null)
    	   {
    		   if (((ObjectPropertyMapping) type).getAllowedValues().contains(value))
        		{
          		   parent.addProperty(model.createProperty(type.getURL()), model.createResource("tbx:"+value));

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
       			model.add(myRes,RDFS.label, model.createLiteral(value));
       		}

        } else if(type instanceof DatatypePropertyMapping) {
            if(datatype != null) {
                parent.addProperty(model.createProperty(type.getURL()), value, NodeFactory.getType(datatype));
            } else {
                parent.addProperty(model.createProperty(type.getURL()), value, lang);
            }
            if(target != null && !target.equals("")) {
                parent.addProperty(DCTerms.source, model.createResource(target));
            }
        } else {
            throw new RuntimeException("Unexpected mapping type");
        }
    }

    
}
