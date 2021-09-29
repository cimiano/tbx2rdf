package tbx2rdf.types;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.XSD;

import java.util.ArrayList;
import java.util.List;

import tbx2rdf.DatatypePropertyMapping;
import tbx2rdf.ObjectPropertyMapping;
import tbx2rdf.vocab.PROVO;
import tbx2rdf.vocab.TBX;

/**
 *
 * @author John McCrae
 */
public class TransacGrp extends NoteLinkInfo {

	public final Transaction transaction;

	public final List<TransacNote> transacNotes = new ArrayList<TransacNote>();

	public String date;

	public TransacGrp(Transaction transaction) {
		super("en", null);
		this.transaction = transaction;
	}

	private boolean isEmpty() {
		return transacNotes.isEmpty() && AdminInfos.isEmpty() && References.isEmpty()
				&& Transactions.isEmpty() && Xreferences.isEmpty();
	}

	@Override
	public void toRDF(Model model, Resource resource) {
		final Resource descripRes = getRes(model);
		resource.addProperty(TBX.transaction, descripRes);
		descripRes.addProperty(RDF.type, TBX.Transaction);
		descripRes.addProperty(RDF.type, PROVO.Activity);
		if (date != null) {
			descripRes.addProperty(PROVO.endedAtTime, date, NodeFactory.getType(XSD.dateTime.getURI()));
		}
		for (TransacNote note : transacNotes) {
			note.toRDF(model, descripRes);
		}
		transaction.toRDF(model, descripRes);
		super.toRDF(model, descripRes);
	}
}
