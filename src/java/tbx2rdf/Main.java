package tbx2rdf;

import tbx2rdf.types.TBX_Terminology;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.xml.sax.SAXException;

/**
 * Main class for TBX2RDF Converter.
 * WRONG DESIGN: IT IS CURRENTLY MIXING TBX2RDF AND IATE AFFAIRS
 * 
 * Entry point of the functionality, it parses the parameters and invokes the conversion methods 
 * making them available from the command line.
 * Example of params for the command line: samples/iate.xml --output samples/iate.nt --big=true
 * Another example: samples/CounterSample.xml --output=samples/CounterSample.nt
 *  --output samples/iatefullmini.nt
 * 
 * It is advice to set a parameter in the Java Virtual Machine: -Dfile.encoding=UTF-8 in order to have good character encoding.
 * 
 * @author John McCrae - Universität Bielefeld
 * @author Victor Rodriguez - Universidad Politécnica de Madrid 
 */
public class Main {

    //Determines whether it will be a stream-parsing (if big=true) or a block conversion (big=false)
    static boolean big = false;
    // Establishes the file with the mappings
    static String mapping_file = "mappings.default";
    // Input file name to be read from;
    static String input_file = "";
    // Output file name to be written to
    static String output_file = "";
    // If the output is to be shown in console
    static boolean bOutputInConsole = true;
    //Determines if the parsing is going to be lenient or strict
    public static boolean lenient = false;
    // The mappings to be used
    public static Mappings mappings;
    // The base namespace of the dataset
    public static String DATA_NAMESPACE = "http://tbx2rdf.lider-project.eu/data/iate/";
    //the system id used for relative URI
    //public static String systemIdForRelativeURI;
    

    /**
     * Main method. 
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");

        try {
			readConfigurationFile(args[0]);
			mappings = Mappings.readInMappings(mapping_file);

	        if (big) {
	            convertBigFile();
	        } else {
	            convertSmallFile();
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        /*boolean ok = parseParams(args);
        if (!ok) {
            System.exit(-1);
        }*/
        
        //READ MAPPINGS
        //System.err.println("Using mapping file: " + mapping_file + "\n");
        /*mappings = Mappings.readInMappings(mapping_file);

        if (big) {
            convertBigFile();
        } else {
            convertSmallFile();
        }*/

    }

    private static void readConfigurationFile(String propFile) throws Exception {
    	File fileProp = new File(propFile);
		Properties prop = new Properties();
		prop.load(new FileInputStream(fileProp));
		
		String bigFile = prop.getProperty("bigfile", "false");
		big = Boolean.parseBoolean(bigFile);
		
		String tbxfile = prop.getProperty("tbxfile");
		input_file = tbxfile;
		File file = new File(input_file);
        if (!file.exists()) {
            System.err.println("The file " + input_file + " does not exist");
            throw new FileNotFoundException("The file " + input_file + " does not exist");
        }
		
        String output = prop.getProperty("output", "").trim();
		if(output.length()==0) {
			output_file = input_file.replaceAll("\\.(xml|tbx)", "\\.rdf");
	        if (!output_file.endsWith(".rdf")) {
	            output_file += ".rdf";
	        }
		}
        
        String namespace = prop.getProperty("namespace", "");
        if(namespace.length()==0) {
        	throw new Exception("namespace value is missing in the property file");
        }
		DATA_NAMESPACE = namespace;
		
		String mappings = prop.getProperty("mappings", "mappings.default");
		mapping_file = mappings;
		
		//recap al values that will be used
		System.out.println("The following values will be used:");
		System.out.println("\tbig = "+big);
		System.out.println("\tinput_file = "+input_file);
		System.out.println("\toutput_file = "+output_file);
		System.out.println("\tnamespace = "+DATA_NAMESPACE);
		System.out.println("\tmapping_file = "+mapping_file);
		
	}

	/**
     * Parses the command line parameters
     */
    /*public static boolean parseParams(String[] args) {
        String ejecutando = "";
        for (String ejecutandox : args) {
            ejecutando += " " + ejecutandox;
        }
        if (args.length == 0) {
            System.err.println("Usage: TBX2RDF_Converter <INPUT_FILE> (--output=<OUTPUT_FILE>)? (--mappings=<MAPPING_FILE>)? (--big=true)? (--datanamespace=<DATA_NAMESPACE>)?");
            System.err.println("If no OUTPUT_FILE is provided, then <OUTPUT FILE>s/.xml/.rdf/ will be assumed as output file.");
            System.err.println("If no MAPPING_FILE is provided, then mappings.default will be used.");
            return false;
        }
        input_file = args[0];                                           //First argument, input file
        File file = new File(input_file);
        if (!file.exists())
        {
            System.err.println("The file " + input_file + " does not exist");
            return false;
        }
        
        output_file = input_file.replaceAll("\\.(xml|tbx)", "\\.rdf");
        if (!output_file.endsWith(".rdf")) {
            output_file += ".rdf";
        }
        String arg, key, value;
        for (int i = 1; i < args.length; i++) {
            arg = args[i];
            Pattern p = Pattern.compile("^--(output|mappings|datanamespace|big|lenient)=(.*?)$");
            Matcher matcher;
            matcher = p.matcher(arg);
            if (matcher.matches()) {
                key = matcher.group(1);
                value = matcher.group(2);
                if (key.equals("output")) {
                    output_file = value;
                    bOutputInConsole = false;
                    System.err.println("OUTPUT_FILE set to" + output_file + "\n");
                }
                if (key.equals("mappings")) {
                    mapping_file = value;
                    System.err.println("MAPPING_FILE set to" + mapping_file + "\n");
                }
                if (key.equals("datanamespace")) {
                    DATA_NAMESPACE = value;
                    System.err.println("DATA_NAMESPACE set to" + DATA_NAMESPACE + "\n");
                }
                if (key.equals("big")) {
                    if (value.equals("true")) {
                        big = true;
                    }
                    System.err.println("Processing large file");
                }
                if (key.equals("lenient")) {
                    if (value.equals("true")) {
                        lenient = true;
                    }
                    System.err.println("Processing in lenient mode");
                }
            }
        }
        return true;
    }*/

    /**
     * This is the conversion to be invoked for large files, that will be processed in a stream
     * The output is serialized as the conversion is being done     
     */
    public static boolean convertBigFile() {
        try {
            bOutputInConsole = false;
            System.err.println("Doing the conversion of a big file\n");
            TBX2RDF_Converter converter = new TBX2RDF_Converter();
            PrintStream fos;
            if (output_file.isEmpty() || bOutputInConsole) {
                fos = System.out;
            } else {
                fos = new PrintStream(output_file, "UTF-8");
            }
            if (fos == null) {
                System.err.println("output file could not be open");
                return false;
            }
            converter.convertAndSerializeLargeFile(input_file, fos, mappings, Main.DATA_NAMESPACE);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Standard conversion
     * This is the conversion invoked from the web service. 
     * Input file is read as a whole and kept in memory.
     */
    public static boolean convertSmallFile() {
        try {
            System.out.println("Doing the standard conversion (not a big file)\n");
            //READ TBX XML
            System.out.println("Opening file " + input_file + "\n");
            BufferedReader reader = new BufferedReader(new FileReader(input_file));
            TBX2RDF_Converter converter = new TBX2RDF_Converter();
            TBX_Terminology terminology = converter.convert(reader, mappings);
            //WRITE. This one has been obtained from 
            System.out.println("Writting output to " + output_file + "\n");
//            final Model model = terminology.getModel("file:" + output_file);
            final Model model = terminology.getModel(Main.DATA_NAMESPACE);           
            RDFDataMgr.write(new FileOutputStream(output_file), model, Lang.TURTLE);
            reader.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;

    }
}
