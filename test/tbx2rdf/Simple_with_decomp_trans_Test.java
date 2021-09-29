package tbx2rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.io.FileReader;
import java.util.List;
import org.apache.jena.riot.RDFDataMgr;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.jena.riot.Lang;
import static tbx2rdf.Main.output_file;

import tbx2rdf.types.TBX_Terminology;
import tbx2rdf.vocab.DC;
import tbx2rdf.vocab.ONTOLEX;
import tbx2rdf.vocab.PROVO;
import tbx2rdf.vocab.SKOS;
import tbx2rdf.vocab.TBX;

/**
 *
 * @author jmccrae
 */
public class Simple_with_decomp_trans_Test {
    
    private Model model;
    
    public Simple_with_decomp_trans_Test() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {      
        final Mappings mappings = Mappings.readInMappings("mappings.default");
        final TBX_Terminology terminology = new TBX2RDF_Converter().convert(new FileReader("samples/simple_with_decomp_trans.xml"), mappings);
        model = terminology.getModel("file:samples/simple_with_decomp_trans.rdf/");
//        RDFDataMgr.write(System.err,model, Lang.TURTLE);
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
            final List<Statement> stats3 = model.listStatements(stat.getSubject(), RDFS.label, (RDFNode)null).toList();
            assert(!stats2.isEmpty() || !stats3.isEmpty());
        }
    }
    
    /*
     * Test every lexical entry has exactly one language
    */
    @Test
    public void testLanguage() throws Exception {
        final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();
        for(Statement stat : stats) {
            final List<Statement> stats2 = model.listStatements(stat.getSubject(), DC.language, (RDFNode)null).toList();
            final List<Statement> stats3 = model.listStatements(stat.getSubject(), RDFS.label, (RDFNode)null).toList();
            assert(stats2.size() == 1 || !stats3.isEmpty());
        }
    }
    
  
  
    /*
     * Test that every Activity has an ending date
    */
    @Test
    public void testActivityHasDate() throws Exception {
        final List<Statement> stats = model.listStatements(null, RDF.type, PROVO.Activity).toList();
        for(Statement stat : stats) {
            final List<Statement> stats2 = model.listStatements(stat.getSubject(), PROVO.endedAtTime, (RDFNode)null).toList();
            assert(stats2.size() == 1);
        }
    }
    
    /*
     * Test that every Activity has an Agent that is associated with it
    */
    @Test
    public void testActivityHasAgent() throws Exception {
//        RDFDataMgr.write(System.err,model, Lang.TURTLE);
        final List<Statement> stats = model.listStatements(null, RDF.type, PROVO.Activity).toList();
        for(Statement stat : stats) {
            final List<Statement> stats2 = model.listStatements(stat.getSubject(), PROVO.wasAssociatedWith, (RDFNode)null).toList();
            assert(stats2.size() == 1);
            final List<Statement> stats3 = model.listStatements(stats2.get(0).getObject().asResource(), RDF.type, PROVO.Agent).toList();
            assert(stats3.size() == 1);
        }
    }
    
    /*
     * Test that every tbx:Transaction has a transactionType
    */
    @Test
    public void checkNumberTransactions() throws Exception {
        final List<Statement> stats = model.listStatements(null, RDF.type, TBX.Transaction).toList();
        for(Statement stat : stats) {
        	final List<Statement> stats2 = model.listStatements(stat.getSubject(), TBX.transactionType, (RDFNode)null).toList();
            assert(stats2.size() == 1);
        }
    }
    
    /*
     * Test that there are 3 tbx:Transactions
    */
    @Test
    public void testActivityDate() throws Exception {
        final List<Statement> stats = model.listStatements(null, RDF.type, TBX.Transaction).toList();
	    assert(stats.size() == 3);
   
    }
    
    
    
    /*
     * Tests that there is exactly one skos:Concept
    */
    @Test
    public void testSKOSConcept() throws Exception {
        final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.Concept).toList();
        assert(stats.size() == 1);
    }

	/*
	 * Checks that every skos:Concept has a subjectField
	 */
	@Test	
	public void testSubjectField() throws Exception {
	final List<Statement> stats = model.listStatements(null, RDF.type, SKOS.Concept).toList();
    
		for(Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), TBX.subjectField, (RDFNode)null).toList();
			assert(!stats2.isEmpty());
		}
	
    }
    
    
	// Removed tests as duplicates of Simple_with_decomposition_Test.java
}