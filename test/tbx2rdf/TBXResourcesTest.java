package tbx2rdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.junit.Test;
import tbx2rdf.types.TBX_Terminology;

/**
 *
 * @author John McCrae
 */
public class TBXResourcesTest {

	@Test
	public void testAllResources() throws Exception {
		final File dir = new File("samples/TBX-resources");
		for (File f : dir.listFiles()) {
			if (f.getName().endsWith(".tbx") && !f.getName().contains("coreInvalid")
					&& !f.getName().contains("notWellformed")) {
				System.err.println(f.getName());
				TBX2RDF_Converter converter = new TBX2RDF_Converter();

				//READ XML
				BufferedReader reader = new BufferedReader(new FileReader(f));

				final Mappings mappings = Mappings.readInMappings("mappings.default");

				TBX_Terminology terminology = converter.convert(reader, mappings);
			}
		}
	}

}
