package tbx2rdf.vocab;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.DCTerms;

/**
 * Class with some common static terms 
 * @author vroddon
 */
public class TBX {
    private static Model defaultModel = ModelFactory.createDefaultModel();
     public static Resource SkosConcept = defaultModel.createResource("http://www.w3.org/2004/02/skos/core#Concept");
     public static Resource Context = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#Context");
     public static Resource Descrip = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#Descrip");
     public static Resource MartifHeader = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#MartifHeader");
     public static Resource Admin = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#Admin");
     public static Resource TermNote = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#TermNote");
     public static Resource Transaction = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#transaction");
     public static Resource SubjectField = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#SubjectField");
     
     
     public static Property subjectField = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#subjectField");
     public static Property description = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#description");
     public static Property target = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#target");
     public static Property type = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#type");
     public static Property context = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#context");
     public static Property value = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#value");
     public static Property status = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#status");
     public static Property definition = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#definition");
     public static Property source = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#source");
     public static Property xref = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#xreference");
     public static Property datatype = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#datatype");
     public static Property language = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#language");
     public static Property note = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#note");
     public static Property encodingDesc = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#encodingDesc");
     public static Property revisionDesc = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#revisionDesc");
     public static Property publicationStmt = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#publicationStmt");
     public static Property sourceDesc = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#sourceDesc");
     public static Property admin = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#admin");
     public static Property termNote = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#termNote");
     public static Property transaction = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#transaction");
     public static Property reliabilityCode = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#reliabilityCode");
     public static Property termType = defaultModel.createProperty("http://www.lexinfo.net/ontology/2.0/lexinfo#termType");//http://tbx2rdf.lider-project.eu/tbx#termType
     public static Property partOfSpeech = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#partOfSpeech");
     public static Property grammaticalNumber = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#grammaticalNumber");
     public static Property transactionType = defaultModel.createProperty("http://tbx2rdf.lider-project.eu/tbx#transactionType");
     
     //These are not being used!
     public static Resource reliabilityCode1 = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#reliabilityCode1");
     public static Resource reliabilityCode2 = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#reliabilityCode2");
     public static Resource reliabilityCode3 = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#reliabilityCode3");
     public static Resource reliabilityCode4 = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#reliabilityCode4");
     public static Resource reliabilityCode5 = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#reliabilityCode5");
     public static Resource reliabilityCode6 = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#reliabilityCode6");
     public static Resource reliabilityCode7 = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#reliabilityCode7");
     public static Resource reliabilityCode8 = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#reliabilityCode8");
     public static Resource reliabilityCode9 = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#reliabilityCode9");
     public static Resource reliabilityCode10 = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#reliabilityCode10");
     public static Resource noun = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#noun");
     public static Resource properNoun = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#properNoun");
     public static Resource adverb = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#adverb");
     public static Resource adjective = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#adjective");
     public static Resource verb = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#verb");
     public static Resource other = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#other");
     public static Resource singular = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#singular");
     public static Resource plural = defaultModel.createResource("http://tbx2rdf.lider-project.eu/tbx#plural");
     
     
     
     
    /**
     * Adds the most common prefixes to the generated model
     * 
     */
    public static void addPrefixesToModel(Model model)
    {
        model.setNsPrefix("tbx", "http://tbx2rdf.lider-project.eu/tbx#");
        model.setNsPrefix("ontolex", "http://www.w3.org/ns/lemon/ontolex#");
        model.setNsPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
        model.setNsPrefix("odrl", "http://www.w3.org/ns/odrl/2/"); //http://w3.org/ns/odrl/2/
        model.setNsPrefix("dct", "http://purl.org/dc/terms/");
        model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        model.setNsPrefix("cc", "http://creativecommons.org/ns#");
        model.setNsPrefix("ldr", "http://purl.oclc.org/NET/ldr/ns#");
        model.setNsPrefix("void", "http://rdfs.org/ns/void#");
        model.setNsPrefix("dcat", "http://www.w3.org/ns/dcat#");
        model.setNsPrefix("prov", "http://www.w3.org/ns/prov#");
        model.setNsPrefix("decomp", "http://www.w3.org/ns/lemon/decomp#");
        
    }    
     
}
