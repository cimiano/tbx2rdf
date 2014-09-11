package tbx2rdf;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.FileReader;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import tbx2rdf.types.TBX_Terminology;
import tbx2rdf.vocab.ONTOLEX;

/**
 *
 * @author jmccrae
 */
public class Simple1Test {
    
    private Model model;
    
    public Simple1Test() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {      
        final Mappings mappings = Mappings.readInMappings("mappings2.default");
        final TBX_Terminology terminology = new TBX2RDF_Converter().convert(new FileReader("samples/simple1.xml"), mappings);
        model = terminology.getModel("http://www.example.com/example#");  
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test there is at least one triple in the model
     */
    @Test
    public void testNotEmpty() throws Exception {
        final List<Statement> stat = model.listStatements(null, null, (String)null).toList();
        assert(!stat.isEmpty());
    }
    
    /*
     * Test every lexical entry has a canonical form
    */
    @Test
    public void testCanonicalForm() throws Exception {
        final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();
        for(Statement stat : stats) {
            final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.canonicalForm, (RDFNode)null).toList();
            assert(!stats2.isEmpty());
        }
    }

}
