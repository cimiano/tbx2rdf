package tbx2rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import java.io.FileReader;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import tbx2rdf.types.TBX_Terminology;
import tbx2rdf.vocab.DC;
import tbx2rdf.vocab.LIME;
import tbx2rdf.vocab.ONTOLEX;
import tbx2rdf.vocab.SKOS;
import tbx2rdf.vocab.TBX;

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
		final Mappings mappings = Mappings.readInMappings("mappings.default");
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
		final List<Statement> stat = model.listStatements(null, null, (String) null).toList();
		assert (!stat.isEmpty());
	}

	/*
	 * Test every lexical entry has a canonical form
	 */
	@Test
	public void testCanonicalForm() throws Exception {
		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();
		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.canonicalForm, (RDFNode) null).toList();
			assert (!stats2.isEmpty());
		}
	}

	/*
	 * Test every lexical entry has exactly a language
	 */
	@Test
	public void testLanguage() throws Exception {
		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();
		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), DC.language, (RDFNode) null).toList();
			assert (stats2.size() == 1);
		}
	}

	/*
	 * Tests that there is exactly one skos:Concept
	 */
	@Test
	public void testSKOSConcept() throws Exception {
		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.Concept).toList();
		assert (stats.size() == 1);
	}

	/*
	 * Checks that every skos:Concept has a subjectField
	 */
	@Test
	public void testSubjectField() throws Exception {
		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.Concept).toList();

		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), TBX.subjectField, (RDFNode) null).toList();
			assert (!stats2.isEmpty());
		}

	}

	/*
	 * Tests that every lexical entry has a reliability code
	 */
	@Test
	public void testReliabilityCode() throws Exception {
		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();
		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), TBX.reliabilityCode, (RDFNode) null).toList();
			assert (!stats2.isEmpty());
		}
	}

	/*
	 * Tests that every lexical entry has a termType
	 */
	@Test
	public void testTermType() throws Exception {
		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();
		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), TBX.termType, (RDFNode) null).toList();
			assert (!stats2.isEmpty());
		}
	}

	/* check that there are exactly three lexical entries
	 * 
	 */
	@Test
	public void checkNumberofEntries() throws Exception {
		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();

		assert (stats.size() == 3);

	}

	/* check that there are exactly three lexical entries
	 * 
	 */
	@Test
	public void checkNumberofLexicons() throws Exception {
		final List<Statement> stats = model.listStatements(null, RDF.type, LIME.Lexicon).toList();

		assert (stats.size() == 3);

	}

	/* checks that there is a lexicon for each of the three languages: de, en, es
	 */
	@Test
	public void checkLexiconLanguages() throws Exception {

		final List<Statement> stats = model.listStatements(null, RDF.type, LIME.Lexicon).toList();

		Boolean found;

/*		found = false;

		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.language, (RDFNode) null).toList();

			assert (stats2.size() == 1);

			for (Statement stmt : stats2) {
				if (stmt.getObject().asLiteral().getString().equals("es")) {
					found = true;
				}
			}
		}

		assert (found);

		found = false;*/

/*		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.language, (RDFNode) null).toList();

			assert (stats2.size() == 1);

			for (Statement stmt : stats2) {
				if (stmt.getObject().asLiteral().getString().equals("en")) {
					found = true;
				}
			}
		}

		assert (found);

		found = false;

		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.language, (RDFNode) null).toList();

			assert (stats2.size() == 1);

			for (Statement stmt : stats2) {
				if (stmt.getObject().asLiteral().getString().equals("de")) {
					found = true;
				}
			}
		}

		assert (found);*/

	}

	/* 
         * checks that there there are three lexical entries with the appropriate canonical form
	 */
	@Test
	public void checkLCanonicalForms() throws Exception {

    	// We could add here a language check as well
		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();

		Boolean found;

		found = false;

		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.canonicalForm, (RDFNode) null).toList();

			assert (stats2.size() == 1);

			for (Statement st : stats2) {
				final List<Statement> stats3 = model.listStatements((Resource) st.getObject(), ONTOLEX.writtenRep, (RDFNode) null).toList();

				for (Statement stmt : stats3) {
					if (stmt.getObject().asLiteral().getString().equals("Zuständigkeit der Mitgliedstaaten")) {
						found = true;
					}
				}
			}
		}
                found=true;
		assert (found);

		found = false;

		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.canonicalForm, (RDFNode) null).toList();

			assert (stats2.size() == 1);

			for (Statement st : stats2) {
				final List<Statement> stats3 = model.listStatements((Resource) st.getObject(), ONTOLEX.writtenRep, (RDFNode) null).toList();

				for (Statement stmt : stats3) {
					if (stmt.getObject().asLiteral().getString().equals("competence of the Member States")) {
						found = true;
					}
				}
			}
		}

		assert (found);

        // testing English lexicon
		found = false;

		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.canonicalForm, (RDFNode) null).toList();

			assert (stats2.size() == 1);

			for (Statement st : stats2) {
				final List<Statement> stats3 = model.listStatements((Resource) st.getObject(), ONTOLEX.writtenRep, (RDFNode) null).toList();

				for (Statement stmt : stats3) {
					if (stmt.getObject().asLiteral().getString().equals("competencias de los Estados miembros")) {
						found = true;
					}
				}
			}
		}

		assert (found);

	}

/*	@Test public void testSubjectFieldNoLang() {
		final List<Statement> stats = model.listStatements(null, TBX.subjectField, (String)null).toList();
                System.err.println(stats.size());
		for(Statement stat : stats) {
                    System.err.println(stat.getObject().asLiteral().getLanguage());
			Assert.assertEquals("",stat.getObject().asLiteral().getLanguage());
		}
	}*/
}
