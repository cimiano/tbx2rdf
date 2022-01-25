package tbx2rdf;

import java.io.FileReader;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.Lang;
import org.xml.sax.SAXException;
import tbx2rdf.types.TBX_Terminology;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.List;

import tbx2rdf.types.TBX_Terminology;
import tbx2rdf.vocab.ONTOLEX;
import tbx2rdf.vocab.SKOS;
import tbx2rdf.vocab.TBX;
/**
 * Some static methods for a fast introduction to TBX2RDF
 * @author vroddon
 */
public class TBX2RDF_Tutorial {
    
    public static void main(String[] args) throws Exception{
     
        Example1();
    }
    
    public static void Example1() throws Exception
    {
        final Mappings mappings = Mappings.readInMappings("mappings.default");
        final TBX_Terminology terminology = new TBX2RDF_Converter().convert(new FileReader("samples/test4.tbx"), mappings);
        Model model = terminology.getModel("file:samples/simple_with_decomposition.rdf");
        RDFDataMgr.write(System.err,model, Lang.TURTLE);
        RDFDataMgr.write(new FileOutputStream("samples/test4.rdf"), model, org.apache.jena.riot.Lang.TURTLE);
    }
}
