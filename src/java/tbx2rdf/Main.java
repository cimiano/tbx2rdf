package tbx2rdf;

import tbx2rdf.types.TBX_Terminology;
import com.hp.hpl.jena.rdf.model.Model;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Main class for TBX2RDF Converter
 * Entry point of the functionality, it parses the parameters and invokes the conversion methods 
 * making them available from the command line.
 * Example of params for the command line: samples/iate.xml --output samples/iate.nt --big=true
 * @author John McCrae - Universität Bielefeld
 * @author Victor Rodriguez - Universidad Politécnica de Madrid 
 */
public class Main {

    public static String DATA_NAMESPACE = "http://tbx2rdf.lider-project.eu/data/iate/";
    private final static Logger logger = Logger.getLogger(Main.class);        
    
    /**
     * Main method. 
     * 
     */
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        final Mappings mappings;
        
        PropertyConfigurator.configure("log4j.properties");

        if (args.length == 0) {
            System.out.println("Usage: TBX2RDF_Converter <INPUT_FILE> (--output=<OUTPUT_FILE>)? (--mappings=<MAPPING_FILE>)? (--big=true)? (--datanamespace=<DATA_NAMESPACE>)?");
            System.out.println("If no OUTPUT_FILE is provided, then <INPUT FILE>s/.xml/.rdf/ will be assumed as output file.");
            System.out.println("If no MAPPING_FILE is provided, then mappings.default will be used.");
            return;
        }
        boolean big = false;
        String input_file = args[0];                                           //First argument, input file
        String output_file = input_file.replaceAll("\\.xml", "\\.rdf");
        String mapping_file = "mappings.default";
        String arg, key, value;
        for (int i = 1; i < args.length; i++) {
            arg = args[i];
            Pattern p = Pattern.compile("^--(output|mappings|big)=(.*?)$");
            Matcher matcher;
            matcher = p.matcher(arg);
            if (matcher.matches()) {
                key = matcher.group(1);
                value = matcher.group(2);
                if (key.equals("output")) {
                    output_file = value;
                    logger.info("OUTPUT_FILE set to" + output_file + "\n");
                }
                if (key.equals("mappings")) {
                    mapping_file = value;
                    logger.info("MAPPING_FILE set to" + mapping_file + "\n");
                }
                if (key.equals("datanamespace")) {
                    DATA_NAMESPACE = value;
                    logger.info("DATA_NAMESPACE set to" + DATA_NAMESPACE + "\n");
                }
                if (key.equals("big")) {
                    if (value.equals("true"))
                        big = true;
                    logger.info("Processing large file");
                }
            }
        }

        TBX2RDF_Converter converter = new TBX2RDF_Converter();

        //READ MAPPINGS
        logger.info("Using mapping file: " + mapping_file + "\n");
        mappings = Mappings.readInMappings(mapping_file);

        //READ XML
        logger.info("Opening file " + input_file + "\n");
        BufferedReader reader = new BufferedReader(new FileReader(input_file));

        // Document doc = Main.readXMLDocument(input_file);
        //  Document doc = db.parse(textstream);        //This is an alternative form, which @victor disregards as we won't always have a file (service etc.).

        /// UNCOMMENT THE FOLLOWING LINE TO USE SAX PARSING
        //      TBX_Terminology terminology2 = converter.convertLargeFile(input_file, mappings);
        if (big) {
            logger.info("Doing the conversion of a big file\n");
            TBX_Terminology terminology3 = converter.convertAndSerializeLargeFile(input_file, mappings);
            //Note: The output is serialized as the conversion is being done
            
        } else { //standard conversion
            logger.info("Doing the conversion\n");
            TBX_Terminology terminology = converter.convert(reader, mappings);
            //WRITE. This one has been obtained from 
            logger.info("Writting output to " + output_file + "\n");
            final Model model = terminology.getModel("file:" + output_file);
            RDFDataMgr.write(new FileOutputStream(output_file), model, Lang.TURTLE);
        }

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
