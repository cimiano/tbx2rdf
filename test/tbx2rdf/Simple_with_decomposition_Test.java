package tbx2rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import java.io.FileReader;
import java.util.List;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.jena.riot.Lang;
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
public class Simple_with_decomposition_Test {

	private Model model;

	public Simple_with_decomposition_Test() {
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
		final TBX_Terminology terminology = new TBX2RDF_Converter().convert(new FileReader("samples/simple_with_decomposition.xml"), mappings);
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
			final List<Statement> stats3 = model.listStatements(stat.getSubject(), RDFS.label, (RDFNode) null).toList();
			assert (!stats2.isEmpty() || !stats3.isEmpty());
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
			final List<Statement> stats3 = model.listStatements(stat.getSubject(), RDFS.label, (RDFNode) null).toList();
			assert (stats2.size() == 1 || !stats3.isEmpty());
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

	/* check that there are exactly three lexical entries
	 * 
	 */
	@Test
	public void checkNumberofEntries() throws Exception {
		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();

		Assert.assertEquals(6, stats.size());

	}

	/* check that there are exactly six lexical entries
	 * 
	 */
	@Test
	public void checkNumberofLexicons() throws Exception {
		final List<Statement> stats = model.listStatements(null, RDF.type, LIME.Lexicon).toList();

		assert (stats.size() == 1);

	}

	@Test
	public void checkReliabilityCode() throws Exception {
		/*final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();
		 for (Statement stat : stats) {
		 final List<Statement> stats2 = model.listStatements(stat.getSubject(), RDFS.label, (String)null).toList();
		 if(stats2.isEmpty()) {
		 final List<Statement> stats3 = model.listStatements(stat.getSubject(), TBX.reliabilityCode, (RDFNode) null).toList();
	
		 assert (stats3.size() == 1);
		 }

		 }*/
	}

	/* checks that there there are three lexical entries with the appropriate canonical form
	 */
	@Test
	public void checkDecomposition() throws Exception {

		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();

		boolean found;

		found = false;

		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), ONTOLEX.canonicalForm, (RDFNode) null).toList();
			final List<Statement> stats4 = model.listStatements(stat.getSubject(), RDFS.label, (RDFNode) null).toList();

			if (stats4.isEmpty()) {
				assert (stats2.size() == 1);

				for (Statement st : stats2) {
					final List<Statement> stats5 = model.listStatements((Resource) st.getObject(), ONTOLEX.writtenRep, (RDFNode) null).toList();
					for (Statement stmt : stats5) {
						if (stmt.getObject().asLiteral().getString().equals("competence of the Member States")) {
							found = true;

							List<Statement> stats3 = model.listStatements(stat.getSubject(), TBX.termType, (RDFNode) null).toList();

							assert (stats3.size() == 1);

						}
					}

				}
			}
		}
		assert (found);
	}

	private Resource findComp(Statement stat) {
		final List<Statement> stats = model.listStatements(null, ONTOLEX.identifies, stat.getSubject()).toList();
		assert (stats.size() == 1);
		return stats.get(0).getSubject();
	}

	@Test
	public void checkDecomposition2() throws Exception {

		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();

		boolean found = false;

		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), RDFS.label, (RDFNode) null).toList();

			if (stats2.size() == 1) {
				if (stats2.get(0).getObject().asLiteral().getString().equals("competence")) {
					found = true;

					List<Statement> stats3 = model.listStatements(findComp(stat), TBX.partOfSpeech, TBX.noun).toList();

					assert (stats3.size() == 1);

					stats3 = model.listStatements(findComp(stat), TBX.grammaticalNumber, TBX.singular).toList();

					assert (stats3.size() == 1);

				}
			}
		}

		assert (found);
	}

	@Test
	public void testDecomposition3() throws Exception {

		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();

		boolean found = false;

		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), RDFS.label, (RDFNode) null).toList();

			if (stats2.size() == 1 && stats2.get(0).getObject().asLiteral().getString().equals("of")) {
				found = true;
			}
		}

		assert (found);
	}

	@Test
	public void testDecomposition4() throws Exception {

		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();

		boolean found = false;

		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), RDFS.label, (RDFNode) null).toList();

			if (stats2.size() == 1 && stats2.get(0).getObject().asLiteral().getString().equals("the")) {
				found = true;

				List<Statement> stats3 = model.listStatements(findComp(stat), TBX.partOfSpeech, TBX.other).toList();

				assert (stats3.size() == 1);

			}
		}

		assert (found);
	}

	@Test
	public void testDecomposition5() throws Exception {

		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();

		boolean found = false;

		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), RDFS.label, (RDFNode) null).toList();

			if (stats2.size() == 1 && stats2.get(0).getObject().asLiteral().getString().equals("Member")) {
				found = true;

				List<Statement> stats3 = model.listStatements(findComp(stat), TBX.partOfSpeech, TBX.noun).toList();

				assert (stats3.size() == 1);

				stats3 = model.listStatements(findComp(stat), TBX.grammaticalNumber, TBX.singular).toList();

				assert (stats3.size() == 1);
			}
		}

		assert (found);
	}

	@Test
	public void testDecomposition6() throws Exception {

		final List<Statement> stats = model.listStatements(null, RDF.type, ONTOLEX.LexicalEntry).toList();

		boolean found = false;

		for (Statement stat : stats) {
			final List<Statement> stats2 = model.listStatements(stat.getSubject(), RDFS.label, (RDFNode) null).toList();

			if (stats2.size() == 1 && stats2.get(0).getObject().asLiteral().getString().equals("States")) {
				found = true;

				List<Statement> stats3 = model.listStatements(findComp(stat), TBX.partOfSpeech, TBX.noun).toList();

				assert (stats3.size() == 1);

				stats3 = model.listStatements(findComp(stat), TBX.grammaticalNumber, TBX.plural).toList();

				assert (stats3.size() == 1);

			}
		}

		assert (found);

	}
}
