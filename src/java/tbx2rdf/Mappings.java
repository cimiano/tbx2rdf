package tbx2rdf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class containing the mappings
 *
 * @author jmccrae
 */
public class Mappings {

    // omg.
    HashMap<String, HashMap<String, HashMap<String, Mapping>>> mappings;
    
    //
    HashMap<String, IndividualMapping> individualMappings = new HashMap<>();
    
    //
    public String defaultLanguage = "en";

    
    public Mappings() {
        mappings = new HashMap<String, HashMap<String, HashMap<String, Mapping>>>();
    }
    
    /**
     * Reads the mappings in a mapping file
     *
     * @param mapping_file Name of the file to be read
     * @return Nothing, but the global mappings HashMap is updated
     */
    public static Mappings readInMappings(String mapping_file) throws IOException {
        return readInMappings(new FileReader(mapping_file));
    }

    /**
     * Creates a Mappings object from a reader
     */
    public static Mappings readInMappings(Reader fstream) throws IOException {
        final Mappings mappings = new Mappings();
        final BufferedReader br = new BufferedReader(fstream);
        final Pattern mapping1 = Pattern.compile("^(\\S*?)\\s+<(\\S*?)>$");
        final Pattern mapping2 = Pattern.compile("^(\\S*?)\\s+(\\S*?)\\s+(\\S*?)\\s+<(\\S*)>\\s+OP(\\s*\\{(.*?)\\})?$");
        final Pattern mapping3 = Pattern.compile("^(\\S*?)\\s+(\\S*?)\\s+(\\S*?)\\s+<(\\S*)>\\s+DP(\\s*<(.*?)>)?$");
        final Pattern mapping4 = Pattern.compile("^(\\S*?)\\s+(\\S*?)\\s+(\\S*?)\\s+<(\\S*)>\\s+EX(\\s*\\{(.*?)\\})?$");

        String strLine;
        Matcher matcher;
        while ((strLine = br.readLine()) != null) {
            strLine = strLine.trim();

            if ((matcher = mapping1.matcher(strLine)).find()) {
                mappings.addMapping(matcher.group(1), new IndividualMapping(matcher.group(2)));
            } else if ((matcher = mapping3.matcher(strLine)).find()) {
                mappings.addMapping(matcher.group(1), matcher.group(2), matcher.group(3), new DatatypePropertyMapping(matcher.group(4), matcher.group(6)));
            } else if ((matcher = mapping2.matcher(strLine)).find()) {
                final Set<String> set = new HashSet<String>();
                if (matcher.group(6) != null) {
                    final String[] values = matcher.group(6).split(",");
                    for (int i = 0; i < values.length; i++) {
                        set.add(values[i]);
                    }
                    final ObjectPropertyMapping objectPropertyMapping = new ObjectPropertyMapping(matcher.group(4), set, mappings.individualMappings);
                    mappings.addMapping(matcher.group(1), matcher.group(2), matcher.group(3), objectPropertyMapping);
                } else {
                    final ObjectPropertyMapping objectPropertyMapping = new ObjectPropertyMapping(matcher.group(4), mappings.individualMappings);
                    mappings.addMapping(matcher.group(1), matcher.group(2), matcher.group(3), objectPropertyMapping);

                }
            } else if ((matcher = mapping4.matcher(strLine)).find()) {
                final ExceptionMapping em = new ExceptionMapping(matcher.group(4), "");
                mappings.addMapping(matcher.group(1), matcher.group(2), matcher.group(3), em);
                //                  System.out.println("XXXXXXXXXXXXX " + matcher.group(1) + " "+matcher.group(2)+" "+matcher.group(3)+" "+matcher.group(4) );

            } else {
                throw new RuntimeException("Bad line in mapping file: " + strLine);
            }
        }
        br.close();
        return mappings;
    }


    public void addMapping(String name, IndividualMapping target) {
        individualMappings.put(name, target);
    }

    public void addMapping(String element, String attribute, String value, Mapping mapping) {

        //System.out.print("Adding mapping for: "+element+" "+attribute+" "+value+" "+mapping);
        HashMap<String, HashMap<String, Mapping>> element2attr;

        HashMap<String, Mapping> attr2mappings;

        if (mappings.containsKey(element)) {
            element2attr = mappings.get(element);
        } else {
            element2attr = new HashMap<String, HashMap<String, Mapping>>();
            mappings.put(element, element2attr);
        }

        if (element2attr.containsKey(attribute)) {
            attr2mappings = element2attr.get(attribute);
        } else {
            attr2mappings = new HashMap<String, Mapping>();
            element2attr.put(attribute, attr2mappings);
        }

        attr2mappings.put(value, mapping);

    }

    /**
     * Gets the mapping for an element, attribute and value
     * @param element XML element, for example "descrip"
     * @param attribute XML attribute, for example, "subjectField"
     * @param value String literal with the value
     */
    public Mapping getMapping(String element, String attribute, String value) {
        HashMap<String, HashMap<String, Mapping>> element2attr;

        HashMap<String, Mapping> attr2mappings;

        if (mappings.containsKey(element)) {
            element2attr = mappings.get(element);

            if (element2attr.containsKey(attribute)) {
                attr2mappings = element2attr.get(attribute);

                if (attr2mappings.containsKey(value)) {
                    return attr2mappings.get(value);
                } else {
                    return null;
                }

            } else {
                return null;
            }

        } else {
            return null;
        }

    }
}
