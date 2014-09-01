package tbx2rdf;

import tbx2rdf.types.TBX_Terminology;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Main class for TBX2RDF Converter
 * Entry point of the functionality, it parses the parameters and invokes the conversion methods 
 * making them available from the command line.
 * @author John McCrae - Universität Bielefeld
 * @author Victor Rodriguez - Universidad Politécnica de Madrid 
 */
public class Main {
    // (JMC 20.06) Keep conversion logic separate from CLI logic!

    /**
     * Main method. 
     * 
     */
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
//        final HashMap<String, Lexicon> lexica = new HashMap<String, Lexicon>();
//        final Set<Term> terms = new HashSet<Term>();
        final Mappings mappings;

        //Uncomment the next line for a fast test
        // args = new String[1];
        // args[0] = "samples/test.xml";

        if (args.length == 0) {
            System.out.println("Usage: TBX2RDF_Converter <INPUT_FILE> (--output=<OUTPUT_FILE>)? (--mappings=<MAPPING_FILE>)?");
            System.out.println("If no OUTPUT_FILE is provided, then <INPUT FILE>s/.xml/.rdf/ will be assumed as output file.");
            System.out.println("If no MAPPING_FILE is provided, then mappings.default will be used.");
            return;
        }

        String input_file = args[0];                                           //First argument, input file
        String output_file = input_file.replaceAll("\\.xml", "\\.rdf");
        String mapping_file = "mappings2.default";
        String arg, key, value;
        for (int i = 1; i < args.length; i++) {
            arg = args[i];
            Pattern p = Pattern.compile("^--(output|mappings)=(.*?)$");
            Matcher matcher;
            matcher = p.matcher(arg);
            if (matcher.matches()) {
                key = matcher.group(1);
                value = matcher.group(2);
                if (key.equals("output")) {
                    output_file = value;
                    System.out.print("OUTPUT_FILE set to" + output_file + "\n");
                }
                if (key.equals("mappings")) {
                    mapping_file = value;
                    System.out.print("MAPPING_FILE set to" + mapping_file + "\n");
                }
            }
        }

        TBX2RDF_Converter converter = new TBX2RDF_Converter();
        
        //READ MAPPINGS
        System.out.print("Using mapping file: " + mapping_file + "\n");
        mappings = Mappings.readInMappings(mapping_file);

        //READ XML
        System.out.print("Opening file " + input_file + "\n");
        BufferedReader reader = new BufferedReader(new FileReader(input_file));
        
        // Document doc = Main.readXMLDocument(input_file);
        //  Document doc = db.parse(textstream);        //This is an alternative form, which @victor disregards as we won't always have a file (service etc.).

        //CONVERT
        System.out.print("Doing the conversion\n");
        TBX_Terminology terminology = converter.convert(reader, mappings);

        //WRITE. This one has been obtained from 
        System.out.print("Writting output to " + output_file + "\n");
        final Model model = terminology.getModel("file:" + output_file);
        RDFDataMgr.write(new FileOutputStream(output_file), model, Lang.TURTLE);

    }

    /**
     * Reads a XML document from a file, returning the XML Document
     * @param input_file Name of an input file, a XML file.
     * @return XML Document
     */
    public static Document readXMLDocument(String input_file) {
        try {
        	
        	TBX2RDF_Converter converter = new TBX2RDF_Converter();
        	
            BufferedReader reader = new BufferedReader(new FileReader(input_file));
            String line, textstream = "";
            while ((line = reader.readLine()) != null) {
                textstream += line + "\n";
            }
            reader.close();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(input_file));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
