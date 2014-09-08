package tbx2rdf;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

    /**
     * Reads the mappings in a mapping file
     *
     * @param mapping_file Name of the file to be read
     * @return Nothing, but the global mappings HashMap is updated
     */
    public static Mappings readInMappings(String mapping_file) throws IOException {
        Mappings mappings = new Mappings();
        FileInputStream fstream = new FileInputStream(mapping_file);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        Pattern mapping1 = Pattern.compile("^(\\S*?)\\s*(\\S*?)\\s*(\\S*?)\\s*<(\\S*)>\\s*OP\\s*(\\S*?)$");
        Pattern mapping2 = Pattern.compile("^(\\S*?)\\s*(\\S*?)\\s*(\\S*?)\\s*<(\\S*)>\\s*OP(\\s*\\{(.*?)\\})?$");
        Pattern mapping3 = Pattern.compile("^(\\S*?)\\s*(\\S*?)\\s*(\\S*?)\\s*<(\\S*)>\\s*DP$");
        Pattern other = Pattern.compile("^(\\S*?)\\s*(\\S*?)\\s*(\\S*?)\\s*<(\\S*)>$");
        Set<String> set;
        String[] values;
        Matcher matcher;
        while ((strLine = br.readLine()) != null) {
            if((matcher = mapping1.matcher(strLine)).find()) {
                mappings.addMapping(matcher.group(1), matcher.group(2), matcher.group(3), new ObjectPropertyMapping(matcher.group(4), matcher.group(5)));
            } 
            
            else if((matcher= mapping3.matcher(strLine)).find())
            {
            	mappings.addMapping(matcher.group(1), matcher.group(2), matcher.group(3), new DatatypePropertyMapping(matcher.group(4)));
            }
            
            else if((matcher = mapping2.matcher(strLine)).find()) {
                set = new HashSet<String>();
                if (matcher.group(6) != null) {
                    values = matcher.group(6).split(",");
                    for (int i = 0; i < values.length; i++) {
                        set.add(values[i]);
                    }
                }
                mappings.addMapping(matcher.group(1), matcher.group(2), matcher.group(3), new ObjectPropertyMapping(matcher.group(4), set));
            } else if((matcher = mapping3.matcher(strLine)).find()) {
		mappings.addMapping(matcher.group(1), matcher.group(2), matcher.group(3), new IndividualMapping(matcher.group(4)));
	    } else {
		throw new RuntimeException("Bad line in mapping file: " + strLine);
	    }
        }
        in.close();
        return mappings;
    }
 
	HashMap<String,HashMap<String,HashMap<String,Mapping>>> mappings;
	public String defaultLanguage = "en";

	public Mappings()
	{
		mappings = new HashMap<String,HashMap<String,HashMap<String,Mapping>>>();
		
	}
	
	public void addMapping(String element, String attribute, String value, Mapping mapping)
	{
		
		//System.out.print("Adding mapping for: "+element+" "+attribute+" "+value+" "+mapping);
		
		HashMap<String,HashMap<String,Mapping>> element2attr;
		
		HashMap<String,Mapping> attr2mappings;
		
		if (mappings.containsKey(element))
		{
			element2attr = mappings.get(element);
		}
		else
		{
			element2attr = new HashMap<String,HashMap<String,Mapping>>();
			mappings.put(element,element2attr);
		}
		
		
		if (element2attr.containsKey(attribute))
		{
			attr2mappings = element2attr.get(attribute);
		}
		else
		{
			attr2mappings = new HashMap<String,Mapping>();
			element2attr.put(attribute,attr2mappings);
		}
		
		attr2mappings.put(value, mapping);
		
	}
	
	public Mapping getMapping(String element, String attribute, String value)
	{
		HashMap<String,HashMap<String,Mapping>> element2attr;
		
		HashMap<String,Mapping> attr2mappings;
		
		if (mappings.containsKey(element))
		{
			element2attr = mappings.get(element);
			
			if (element2attr.containsKey(attribute))
			{
				attr2mappings = element2attr.get(attribute);
				
				if (attr2mappings.containsKey(value))
				{
					return attr2mappings.get(value);
				}
				else
				{
					return null;
				}
				
			}
			else
			{
				return null;
			}
			
		}
		else
		{
			return null;
		}
		
	}
}
