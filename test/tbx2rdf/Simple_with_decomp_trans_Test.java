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
        final Mappings mappings = Mappings.readInMappings("mappings2.default");
        final TBX_Terminology terminology = new TBX2RDF_Converter().convert(new FileReader("samples/simple_with_decomp_trans.xml"), mappings);
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
    
    /*
     * Test every lexical entry has exactly one language
    */
    @Test
    public void testLanguage() throws Exception {
        final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();
        for(Statement stat : stats) {
            final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.language, (RDFNode)null).toList();
            assert(stats2.size() == 1);
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
        final List<Statement> stats = model.listStatements(null, RDF.type, PROVO.Activity).toList();
        for(Statement stat : stats) {
            final List<Statement> stats2 = model.listStatements(stat.getSubject(), PROVO.wasAssociatedWith, (RDFNode)null).toList();
            assert(stats2.size() == 1);
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
        final List<Statement> stats = model.listStatements(null, RDF.type, SKOS.Concept).toList();
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
    
    
    
    /* check that there are exactly three lexical entries
     * 
     */
    @Test
    public void checkNumberofEntries() throws Exception {
        final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();
        
        assert(stats.size() == 3);

    }
    
    /* check that there are exactly six lexical entries
     * 
     */
    @Test
    public void checkNumberofLexicons() throws Exception {
        final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.Lexicon).toList();
        
        assert(stats.size() == 6);

    }
    

    
    /* checks that there there are three lexical entries with the appropriate canonical form
     */
    @Test
    public void checkDecomposition() throws Exception {
    	
    	
    	final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();
        
        Boolean found;
        
        found = false;
        
        for(Statement stat : stats) {
            final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.canonicalForm, (RDFNode)null).toList();
           
            assert (stats2.size() == 1);
            
            for (Statement stmt: stats2)
            {
            	if (stmt.getObject().asLiteral().getString().equals("competence of the Member States")) 
            	{
            		found = true;
            		
            		List<Statement> stats3 = model.listStatements(stmt.getSubject(), TBX.termType, (RDFNode)null).toList();
                    
            		assert (stats3.size() == 1);
            		
            		stats3 = model.listStatements(stmt.getSubject(), TBX.reliabilityCode, TBX.reliabilityCode3).toList();
                    
            		assert (stats3.size() == 1);
            	}
            	
            }
        }
        
        assert(found);

            
        found = false;
        
        for(Statement stat : stats) {
            final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.canonicalForm, (RDFNode)null).toList();
           
            assert (stats2.size() == 1);
            
            for (Statement stmt: stats2)
            {
            	if (stmt.getObject().asLiteral().getString().equals("competence"))
            	{	
            		found = true;
            		
            		List<Statement> stats3 = model.listStatements(stmt.getSubject(), TBX.partOfSpeech, TBX.noun).toList();
                    
            		assert (stats3.size() == 1);
            		
            		stats3 = model.listStatements(stmt.getSubject(), TBX.grammaticalNumber, TBX.singular).toList();
                    
            		assert (stats3.size() == 1);
            		
            	}
            }
        }
        
        assert(found);
                
        found = false;
        
        for(Statement stat : stats) {
            final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.canonicalForm, (RDFNode)null).toList();
           
            assert (stats2.size() == 1);
            
            for (Statement stmt: stats2)
            {
            	if (stmt.getObject().asLiteral().getString().equals("of")) found = true;
            }
        }
        
        assert(found);
        
        found = false;
        
        for(Statement stat : stats) {
            final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.canonicalForm, (RDFNode)null).toList();
           
            assert (stats2.size() == 1);
            
            for (Statement stmt: stats2)
            {
            	if (stmt.getObject().asLiteral().getString().equals("the"))
            	{
            		found = true;
            		
            		List<Statement> stats3 = model.listStatements(stmt.getSubject(), TBX.partOfSpeech, TBX.other).toList();
                    
            		assert (stats3.size() == 1);

            	}
            }
        }
        
        assert(found);
    
        found = false;
        
        for(Statement stat : stats) {
            final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.canonicalForm, (RDFNode)null).toList();
           
            assert (stats2.size() == 1);
            
            for (Statement stmt: stats2)
            {
            	if (stmt.getObject().asLiteral().getString().equals("Member"))
            	{
            		found = true;
            		
            		List<Statement> stats3 = model.listStatements(stmt.getSubject(), TBX.partOfSpeech, TBX.noun).toList();
                    
            		assert (stats3.size() == 1);
            		
            		stats3 = model.listStatements(stmt.getSubject(), TBX.grammaticalNumber, TBX.singular).toList();
                    
            		assert (stats3.size() == 1);
            	}
            }
        }
        
        assert(found);
        
        found = false;
        
        for(Statement stat : stats) {
            final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.canonicalForm, (RDFNode)null).toList();
           
            assert (stats2.size() == 1);
            
            for (Statement stmt: stats2)
            {
            	if (stmt.getObject().asLiteral().getString().equals("States"))
            	{
            		found = true;
            		
            		List<Statement> stats3 = model.listStatements(stmt.getSubject(), TBX.partOfSpeech, TBX.noun).toList();
                    
            		assert (stats3.size() == 1);
            		
            		stats3 = model.listStatements(stmt.getSubject(), TBX.grammaticalNumber, TBX.singular).toList();
                    
            		assert (stats3.size() == 1);
            		
            	}
            }
        }
        
        assert(found);
    
    	
    }
}

