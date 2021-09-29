package tbx2rdf;

//JAVA
import java.io.FileInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;

//JENA
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;

//TBX2RDF
import java.io.PrintStream;
import java.util.Collections;
import org.apache.log4j.Logger;
import tbx2rdf.datasets.iate.SubjectFields;
import tbx2rdf.vocab.ONTOLEX;
import tbx2rdf.vocab.SKOS;
import tbx2rdf.vocab.TBX;
import tbx2rdf.types.LexicalEntry;
import tbx2rdf.types.Describable;
import tbx2rdf.types.MartifHeader;
import tbx2rdf.types.TBX_Terminology;
import tbx2rdf.types.Descrip;
import tbx2rdf.types.XReference;
import tbx2rdf.types.Term;
import tbx2rdf.types.AdminGrp;
import tbx2rdf.types.AdminInfo;
import tbx2rdf.types.DescripGrp;
import tbx2rdf.types.DescripNote;
import tbx2rdf.types.MartifHeader.*;
import tbx2rdf.types.Note;
import tbx2rdf.types.NoteLinkInfo;
import tbx2rdf.types.Reference;
import tbx2rdf.types.TermComp;
import tbx2rdf.types.TermCompGrp;
import tbx2rdf.types.TermCompList;
import tbx2rdf.types.TermNote;
import tbx2rdf.types.TermNoteGrp;
import tbx2rdf.types.TransacGrp;
import tbx2rdf.types.TransacNote;
import tbx2rdf.types.Transaction;
import tbx2rdf.types.abs.impID;
import tbx2rdf.types.abs.impIDLangTypeTgtDtyp;
import tbx2rdf.utils.XMLUtils;
import tbx2rdf.vocab.DC;
import tbx2rdf.vocab.IATE;
import tbx2rdf.vocab.LIME;


/**
 * Entry point of the TBX2RDF converter
 *
 * TBX: framework consisting of a core structure, and a formalism (eXtensible
 * Constraint Specification) for identifying a set of data-categories and their
 * constraints, both expressed in XML
 *
 * Several of the remaining data categories, including definition, context, part
 * of speech, and subject field are very important and should be included in a
 * terminology whenever possible. The most important non-mandatory data category
 * is part of speech.
 *
 *
 * A very nice reference for the basic model can be found here:
 * http://www.terminorgs.net/downloads/TBX_Basic_Version_3.pdf
 *
 * @author Philipp Cimiano - Universität Bielefeld
 * @author Victor Rodriguez - Universidad Politécnica de Madrid
 */
public class TBX2RDF_Converter {

    private final static Logger logger = Logger.getLogger(TBX2RDF_Converter.class);
    
    
    /**
     * Do not construct
     */
    public TBX2RDF_Converter() {
    }

    /**
     * Converts a TBX string into a RDF. Parses the XML searching for termEntry
     * elements.
     *
     * Then, Serializes Terms and Lexicons
     *
     * @param str The TBX XML as a String.
     * @return str A Turtle string with the equivalent information
     */
    public String convert(String str, Mappings mappings, String resourceURI) throws Exception {
        TBX_Terminology result = convert(new StringReader(str), mappings);
        StringWriter sw = new StringWriter();
        RDFDataMgr.write(sw, result.getModel(resourceURI), RDFFormat.TURTLE_PRETTY);
        return sw.toString();
    }

    /**
     * Makes the conversion given a certain input and a set of mappings. This is done with a 
     * @param input Input 
     * @param mappings Mappings
     */
    public TBX_Terminology convert(Reader input, Mappings mappings) throws IOException, ParserConfigurationException, TBXFormatException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        TransacNote.mapAgents.clear();
        db.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    if (systemId.endsWith(".dtd")) {
                        return new InputSource(new StringReader(""));
                    } else {
                        return null;
                    }
                }
            });

        // parse the input document
        Document doc = db.parse(new InputSource(input));

        // extract here martif metadata
        Element root = doc.getDocumentElement();

        return createTerminology(root, mappings);

    }

    /**
     * Processes the whole TBX file from the root XML element (once built the DOM model)
     * @param root The root element
     */
    TBX_Terminology createTerminology(Element root, Mappings mappings) throws IOException, SAXException {
        
        MartifHeader header = processMartifHeader(XMLUtils.child(root, "martifHeader"), mappings); 
        final TBX_Terminology terminology = new TBX_Terminology(root.getAttribute("type"), header);
        mappings.defaultLanguage = "en";
        
        for (Element e : XMLUtils.children(root)) {
            if (e.getTagName().equalsIgnoreCase("text")) {
                for (Term t : processText(e, mappings)) {
                    terminology.addTerm(t);
                }
            } else if (!e.getTagName().equalsIgnoreCase("martifHeader")) {
                unexpected(root);
            }
        }
        return terminology;
    }

    
        
    /**
     * Given a XML root element, processes the Martif Header
     * @param root XML root element
     * @param mappings Mappings
     */
    MartifHeader processMartifHeader(Element root, Mappings mappings) throws IOException, SAXException {
        final MartifHeader header = new MartifHeader(processFileDescrip(XMLUtils.child(root, "fileDesc"), mappings));
        processID(header, root);
        for (Element e : XMLUtils.children(root)) {
            if (e.getTagName().equalsIgnoreCase("encodingDesc")) {
                header.encodingDesc = e.getChildNodes();
            } else if (e.getTagName().equalsIgnoreCase("revisionDesc")) {
                header.revisionDesc = e.getChildNodes();
            } else if (!e.getTagName().equalsIgnoreCase("fileDesc")) {
                unexpected(e);
            }
        }
        return header;
    }

    /**
     * Obtains a FileDesc object by parsing a XML element.
     * <filedesc>: A nesting element containing child elements that describe the TBX document instance.
     */
    public FileDesc processFileDescrip(Element root, Mappings mappings) throws IOException, SAXException {
        final FileDesc fileDesc = new FileDesc();

        for (Element e : XMLUtils.children(root)) {
            if (e.getTagName().equalsIgnoreCase("titleStmt")) {
                fileDesc.titleStmt = processTitleStmt(e, mappings);
            } else if (e.getTagName().equalsIgnoreCase("publicationStmt")) {
                fileDesc.publicationStmt = e;
            } else if (e.getTagName().equalsIgnoreCase("sourceDesc")) {
                fileDesc.sourceDesc.add(e);
            } else {
                unexpected(e);
            }

        }
        return fileDesc;

    }

    /**
     * Processes some metadata elements from the root element
     */
    private TitleStmt processTitleStmt(Element root, Mappings mappings) {
        final TitleStmt titleStmt = new TitleStmt(XMLUtils.child(root, "title").getTextContent());
        if (root.hasAttribute("xml:lang")) {
            titleStmt.lang = root.getAttribute("xml:lang");
        }
        if (root.hasAttribute("id")) {
            titleStmt.id = root.getAttribute("id");
        }
        final Element title = XMLUtils.child(root, "title");
        if (title.hasAttribute("xml:lang")) {
            titleStmt.title_lang = title.getAttribute("xml:lang");
        }
        if (root.hasAttribute("id")) {
            titleStmt.title_id = title.getAttribute("id");
        }
        for (Element e : XMLUtils.children(root)) {
            if (e.getTagName().equalsIgnoreCase("note")) {
                titleStmt.notes.add(e);
            }
        }

        return titleStmt;
    }

    /**
     * Processes the body element and the back element.
     * We arrive here with a <text> element.
     */
    Collection<Term> processText(Element root, Mappings mappings) throws IOException, SAXException {
        final Collection<Term> terms = new HashSet<Term>();
        for (Element e : XMLUtils.children(root)) {
            if (e.getTagName().equalsIgnoreCase("body")) {
                terms.addAll(processBody(e, mappings));
            } else if (e.getTagName().equalsIgnoreCase("back")) {
                terms.addAll(processBack(e, mappings));
            } else {
                unexpected(e);
            }

        }
        return terms;
    }

    /**
     * Processes the collection of terms
     * We arreive here with a <body> element
     */
    private Collection<? extends Term> processBody(Element root, Mappings mappings) {
        final Collection<Term> terms = new HashSet<Term>();
        for (Element e : XMLUtils.children(root)) {
            if (e.getTagName().equalsIgnoreCase("termEntry")) {
                terms.add(processTermEntry(e, mappings));
            } else {
                unexpected(e);
            }
        }
        return terms;
    }

    private Collection<? extends Term> processBack(Element root, Mappings mappings) {
		// TODO: This should do something right?
        return Collections.EMPTY_LIST;
    }

    /**
     * Processes, from a node, a termEntry
     * @return A Term
     */
    public Term processTermEntry(Element node, Mappings mappings) {
        // create new Term 
        // add subjectField
        // add ID

        // <!ELEMENT termEntry  ((%auxInfo;),(langSet+)) >
        // <!ATTLIST termEntry
        // id ID #IMPLIED >
        // <!ENTITY % auxInfo '(descrip | descripGrp | admin | adminGrp | transacGrp | note | ref | xref)*' >
        Term term = new Term();

        int langsetcount = 0;

        String sid=node.getAttribute("id");
        term.setID(sid);

        for (Element sub : XMLUtils.children(node)) {
            final String name = sub.getTagName();

            if (name.equalsIgnoreCase("langSet")) {
                langsetcount++;
                this.processLangSet(term, sub, mappings);
            } else {
                processAuxInfo(term, sub, mappings);
            }
        }

        if (langsetcount == 0) {
            logger.warn("No langSet element in termEntry");
//            throw new TBXFormatException("No langSet element in termEntry");
        }

        return term;
    }

    void processReference(NoteLinkInfo descr, Element sub, Mappings mappings) {
        // <!ELEMENT ref (#PCDATA) >
        // <!ATTLIST ref
        //    %impIDLangTypTgtDtyp;
        // >

        //<!ENTITY % impIDLangTypTgtDtyp ' id ID #IMPLIED
        //xml:lang CDATA #IMPLIED 
        // type CDATA #REQUIRED 
        // target IDREF #IMPLIED 
        // datatype CDATA #IMPLIED
        //'>
        final Reference ref = new Reference(processType(sub, mappings, true), sub.getAttribute("xml:lang"), mappings, sub.getChildNodes());
        if (sub.hasAttribute("id")) {
            ref.setID(sub.getAttribute("id"));
        }
        if (sub.hasAttribute("target")) {
            ref.target = sub.getAttribute("target");
        }
        if (sub.hasAttribute("datatype")) {
            ref.datatype = sub.getAttribute("datatype");
        }
        descr.References.add(ref);
    }

    void processAdminGrp(NoteLinkInfo descr, Element node, Mappings mappings) {
        // <!ELEMENT adminGrp (admin, (adminNote|note|ref|xref)*) >
        // <!ATTLIST adminGrp
        // id ID #IMPLIED >


        processID((impID) descr, node);

        int i = 0;
        for (Element tig_child : XMLUtils.children(node)) {

            String name = tig_child.getNodeName();

            if (i == 0 && !name.equals("admin")) {
                throw new TBXFormatException("First element of TIG is not term !\n");
            }

            if (name.equals("admin")) {
                processAdmin(descr, tig_child, mappings);
            } else if (name.equals("adminNote")) {
                processAdminGrp(descr, tig_child, mappings);
            } else if (name.equals("note")) {
                processNote(descr, tig_child, mappings);
            } else if (name.equals("ref")) {
                this.processReference(descr, tig_child, mappings);
            } else if (name.equals("xref")) {
                this.processXReference(descr, tig_child, mappings);
            } else {
                throw new TBXFormatException("Element " + name + "not defined by TBX standard");
            }
            i++;
        }

    }

    /**
     * Processes the langset (xml:lang)
     *
     * @return a LexicalEntry
     */
    Term processLangSet(Term term, Element langSet, Mappings mappings) {

        // <!ELEMENT langSet ((%auxInfo;), (tig | ntig)+) >
        // <!ATTLIST langSet
        // id ID #IMPLIED
        // xml:lang CDATA #REQUIRED >

        LexicalEntry entry;
        String language = XMLUtils.getValueOfAttribute(langSet, "xml:lang");

        if (language == null) {
            throw new TBXFormatException("Language not specified for langSet!");
        }

        int termCount = 0;

        processID(term, langSet);

        for (Element sub : XMLUtils.children(langSet)) {

            final String name = sub.getNodeName();

            if (name.equals("ntig")) {
                termCount++;
                entry = new LexicalEntry(language, mappings);
                this.processNTIG(entry, sub, mappings);
                term.Lex_entries.add(entry);
            } else if (name.equals("tig")) {
                termCount++;
                entry = new LexicalEntry(language, mappings);
                this.processTIG(entry, sub, mappings);
                term.Lex_entries.add(entry);
            } else {
                processAuxInfo(term, sub, mappings);
            }
        }

        if (termCount == 0) {
            throw new TBXFormatException("No TIG nor NTIG in langSet !");
        }

        return term;
    }

    void processTIG(LexicalEntry entry, Element tig, Mappings mappings) {

        // <!ELEMENT tig (term, (termNote)*, %auxInfo;) >
        // <!ATTLIST tig
        // id ID #IMPLIED >
        int i = 0;

        processID(entry, tig);
        Iterable<Element> children = XMLUtils.children(tig);
        for (Element tig_child : children) {

            String name = tig_child.getNodeName();

            if (i == 0 && !name.equals("term")) {
                throw new TBXFormatException("First element of TIG is not term !\n");
            }

            if (name.equals("term")) {
                this.processTerm(entry, tig_child, mappings);
            } else if (name.equals("termNote")) {
                entry.TermNotes.add(new TermNoteGrp(this.processTermNote(tig_child, mappings), mappings.defaultLanguage, mappings));
            } else {
                processAuxInfo(entry, tig_child, mappings);
            }
            i++;
        }

    }

    /**
     * Processes a term within a termEntry
     */
    void processTerm(LexicalEntry entry, Element node, Mappings mappings) {

        // <!ELEMENT term %basicText; >
        // <!ATTLIST term
        // id ID #IMPLIED >
        entry.Lemma = node.getTextContent();
    }

    TermNote processTermNote(Element tig_child, Mappings mappings) {

        // <!ELEMENT termNote %noteText; >
        // <!ATTLIST termNote
        //    %impIDLangTypTgtDtyp;
        // >
        // <!ENTITY % impIDLangTypTgtDtyp ' id ID #IMPLIED
        // xml:lang CDATA #IMPLIED type CDATA #REQUIRED target IDREF #IMPLIED datatype CDATA #IMPLIED
        // '>
        final TermNote note = new TermNote(tig_child.getChildNodes(), processType(tig_child, mappings, true), tig_child.getAttribute("xml:lang"), mappings);
        processImpIDLangTypeTgtDType(note, tig_child, mappings);
        return note;
    }

    void processNTIG(LexicalEntry entry, Node ntig, Mappings mappings) {

        // <!ELEMENT ntig (termGrp, %auxInfo;) >
        // <!ATTLIST ntig
        // id ID #IMPLIED	
        // >
        int i = 0;
        for (Element ntig_child : XMLUtils.children(ntig)) {

            String name = ntig_child.getNodeName();

            if (i == 0 && !name.equals("termGrp")) {
                if (Main.lenient==false)
                    throw new TBXFormatException("First element of NTIG is not termGrp !\n");
            }

            if (name.equals("termGrp")) {
                this.processTermGroup(entry, ntig_child, mappings);
            } else {
                processAuxInfo(entry, ntig_child, mappings);
            }
            i++;
        }
    }

    void processXReference(NoteLinkInfo descr, Element node, Mappings mappings) {

        // <!ELEMENT xref (#PCDATA) >
        // <!ATTLIST xref
        // %impIDType;
        // target CDATA #REQUIRED >
        XReference xref = new XReference(XMLUtils.getValueOfAttribute(node, "target"), node.getTextContent());

        processID(xref, node);
        xref.type = processType(node, mappings, false);
        descr.Xreferences.add(xref);
    }

    void processDescripGroup(Describable descr, Element node, Mappings mappings) {

        // The DTD for a DescripGroup is as follows
        // <!ELEMENT descripGrp (descrip, (descripNote|admin|adminGrp|transacGrp|note|ref|xref)*)
        // >
        // <!ATTLIST descripGrp
        //  id ID #IMPLIED >

        DescripGrp descrip = new DescripGrp(processDescrip(XMLUtils.firstChild("descrip", node), mappings));
        processID(descrip, node);
        // get first child that needs to be a descrip
        // process other XMLUtils.children that can be: descripNote, admin, adminGroup, transacGrp, note, ref and xref
        for (Element sub : XMLUtils.children(node)) {
            final String name = sub.getTagName();
            if (name.equalsIgnoreCase("descrip")) {
                // ignore
            } else if (name.equalsIgnoreCase("descripNote")) {
                processDescripNote(descrip, sub, mappings);
            } else if (name.equalsIgnoreCase("admin")) {
                this.processAdmin(descrip, sub, mappings);
            } else if (name.equalsIgnoreCase("adminGrp")) {
                this.processAdminGrp(descrip, sub, mappings);
            } else if (name.equalsIgnoreCase("transacGrp")) {
                this.processTransactionGroup(descrip, sub, mappings);
            } else if (name.equalsIgnoreCase("note")) {
                this.processTransactionGroup(descrip, sub, mappings);
            } else if (name.equalsIgnoreCase("ref")) {
                this.processReference(descrip, sub, mappings);
            } else if (name.equalsIgnoreCase("xref")) {
                this.processXReference(descrip, sub, mappings);
            } else {
                throw new TBXFormatException("Unexpected subnode " + node.getTagName());
            }
        }

        descr.Descriptions.add(descrip);
    }

    void processAdmin(NoteLinkInfo descr, Element node, Mappings mappings) {
        // <!ELEMENT admin %noteText; >
        // <!ATTLIST admin
        //  %impIDLangTypTgtDtyp;
        //>
        final AdminInfo admin = new AdminInfo(node.getChildNodes(), processType(node, mappings, true), node.getAttribute("xml:lang"), mappings);
        processImpIDLangTypeTgtDType(admin, node, mappings);
        descr.AdminInfos.add(new AdminGrp(admin));
    }

    /**
     * Processes a Transaction Group www.isocat.org/datcat/DC-162 A transacGrp
     * element can contain either one transacNote element, or one date element,
     * or both. Example:
     * <transacGrp>
     * <transac type="transactionType">creation</transac>
     * <transacNote type="responsibility" target="CA5365">John
     * Harris</transacNote>
     * <date>2008‐05‐12</date>
     * </transacGrp>
     *
     * @param transacGroup A Transaction group in XML // According to the TBX
     * DTD, a transacGroup looks as follows: // <!ELEMENT transacGrp (transac,
     * (transacNote|date|note|ref|xref)* ) >
     * // <!ATTLIST transacGrp // id ID #IMPLIED >
     * // Transaction transaction = new Transaction(lex);
     */
    void processTransactionGroup(NoteLinkInfo descr, Element elem, Mappings mappings) {

        // <!ELEMENT transacGrp (transac, (transacNote|date|note|ref|xref)* ) >
        // <!ATTLIST transacGrp
        // id ID #IMPLIED >
        Element elemTransac = null;
        try {
            elemTransac = XMLUtils.firstChild("transac", elem);
        } catch (Exception e) {
            return;
        }
        final TransacGrp transacGrp = new TransacGrp(processTransac(elemTransac, mappings));

        int i = 0;
        for (Element child : XMLUtils.children(elem)) {

            String name = child.getNodeName();

            if (i == 0 && !name.equals("transac")) {
                throw new TBXFormatException("First element of transacGrp is not termGrp !\n");
            }

            if (name.equals("transac")) {
                //processTransac(transacGrp, child, mappings);
            } else if (name.equals("transacNote")) {
                processTransacNote(transacGrp, child, mappings);
            } else if (name.equals("date")) {
                processDate(transacGrp, child, mappings);
            } else if (name.equals("note")) {
                processNote(transacGrp, child, mappings);
            } else if (name.equals("xref")) {
                processXReference(transacGrp, child, mappings);
            } else if (name.equals("ref")) {
                this.processReference(transacGrp, child, mappings);
            } else {
                throw new TBXFormatException("Element " + name + " not defined by TBX standard\n");
            }
            i++;
        }
        descr.Transactions.add(transacGrp);
    }

    void processTermGroup(LexicalEntry entry, Element node, Mappings mappings) {
        // <!ELEMENT termGrp (term, (termNote|termNoteGrp)*, (termCompList)* ) >
        // <!ATTLIST termGrp
        //  id ID #IMPLIED
        //>
        for (Element elem : XMLUtils.children(node)) {
            final String name = elem.getTagName();
            if (name.equalsIgnoreCase("term")) {
                processTerm(entry, elem, mappings);
            } else if (name.equalsIgnoreCase("termNote")) {
                entry.TermNotes.add(new TermNoteGrp(processTermNote(elem, mappings), mappings.defaultLanguage, mappings));
            } else if (name.equalsIgnoreCase("termNoteGrp")) {
                entry.TermNotes.add(processTermNoteGrp(elem, mappings));
            } else if (name.equalsIgnoreCase("termCompList")) {
                processTermCompList(entry, elem, mappings);
            }
        }
    }

    void processNote(NoteLinkInfo descr, Element elem, Mappings mappings) {
        //<!ELEMENT note %noteText; >
        //<!ATTLIST note %impIDLang;
        //>
        final Note note = new Note(elem.getChildNodes(), elem.getAttribute("xml:lang"), mappings);
        processID(note, elem);
        descr.notes.add(note);
    }

    Descrip processDescrip(Element elem, Mappings mappings) {
        //<!ELEMENT descrip %noteText; >
        //<!ATTLIST descrip
        //%impIDLangTypTgtDtyp;
        //>
        final Descrip descrip = new Descrip(elem.getChildNodes(), processType(elem, mappings, true), elem.getAttribute("xml:lang"), mappings);
        processImpIDLangTypeTgtDType(descrip, elem, mappings);
        return descrip;
    }

    void processDescripNote(DescripGrp descrip, Element sub, Mappings mappings) {
        // <!ELEMENT descripNote (#PCDATA) >
        //<!ATTLIST descripNote
        //%impIDLangTypTgtDtyp;
        //> 
        final DescripNote descripNote = new DescripNote(sub.getChildNodes(), processType(sub, mappings, true), sub.getAttribute("xml:lang"), mappings);
        processImpIDLangTypeTgtDType(descripNote, sub, mappings);
        descrip.descripNote.add(descripNote);
    }

    Transaction processTransac(Element child, Mappings mappings) {
        //  <!ELEMENT transac (#PCDATA) >
        //<!ATTLIST transac
        //%impIDLangTypTgtDtyp;
        //>
        final Transaction transaction = new Transaction(child.getChildNodes(), processType(child, mappings, true), child.getAttribute("xml:lang"), mappings);
        processImpIDLangTypeTgtDType(transaction, child, mappings);
        return transaction;
    }

    void processTransacNote(TransacGrp transacGrp, Element child, Mappings mappings) {

        //<!ELEMENT transacNote (#PCDATA) >
        //<!ATTLIST transacNote
        //%impIDLangTypTgtDtyp;
        //> 
        final TransacNote transacNote = new TransacNote(child.getChildNodes(), processType(child, mappings, true), child.getAttribute("xml:lang"), mappings);
        processImpIDLangTypeTgtDType(transacNote, child, mappings);
        transacGrp.transacNotes.add(transacNote);
    }

    void processDate(TransacGrp transacGrp, Element child, Mappings mappings) {
        //  <!ELEMENT date (#PCDATA) >
        //<!ATTLIST date
        //id ID #IMPLIED
        //> 
        transacGrp.date = child.getTextContent();
    }

    TermNoteGrp processTermNoteGrp(Element elem, Mappings mappings) {
        //  <!ELEMENT termNoteGrp (termNote, %noteLinkInfo;) >
        //<!ATTLIST termNoteGrp
        //id ID #IMPLIED
        //> 
        final TermNoteGrp termNoteGrp = new TermNoteGrp(processTermNote(XMLUtils.firstChild("termNote", elem), mappings), elem.getAttribute("xml:lang"), mappings);
        for (Element e : XMLUtils.children(elem)) {
            final String name = e.getTagName();
            if (name.equalsIgnoreCase("termNote")) {
                // Do nothing
            } else if (name.equalsIgnoreCase("admin")) {
                processAdmin(termNoteGrp, e, mappings);
            } else if (name.equalsIgnoreCase("adminGrp")) {
                processAdminGrp(termNoteGrp, e, mappings);
            } else if (name.equalsIgnoreCase("transacGrp")) {
                processTransactionGroup(termNoteGrp, e, mappings);
            } else if (name.equalsIgnoreCase("note")) {
                processNote(termNoteGrp, e, mappings);
            } else if (name.equalsIgnoreCase("ref")) {
                processReference(termNoteGrp, e, mappings);
            } else if (name.equalsIgnoreCase("xref")) {
                processXReference(termNoteGrp, e, mappings);
            }
        }
        return termNoteGrp;
    }

    void processTermCompList(LexicalEntry entry, Element elem, Mappings mappings) {
        // <!ELEMENT termCompList ((%auxInfo;), (termComp | termCompGrp)+) >
        //<!ATTLIST termCompList
        //id ID #IMPLIED
        //type CDATA #REQUIRED
        //>
        final TermCompList termCompList = new TermCompList(mappings.getMapping("termCompList", "type", elem.getAttribute("type")));
        processID(termCompList, elem);
        for (Element e : XMLUtils.children(elem)) {
            final String name = e.getTagName();
            if (name.equalsIgnoreCase("termComp")) {
                final TermComp termComp = processTermComp(e, mappings);
                termCompList.termComp.add(new TermCompGrp(termComp, null, mappings));
            } else if (name.equalsIgnoreCase("termCompGrp")) {
                processTermCompGrp(termCompList, e, mappings);
            } else if (name.equalsIgnoreCase("admin")) {
                processAdmin(termCompList, e, mappings);
            } else if (name.equalsIgnoreCase("adminGrp")) {
                processAdminGrp(termCompList, e, mappings);
            } else if (name.equalsIgnoreCase("transacGrp")) {
                processTransactionGroup(termCompList, e, mappings);
            } else if (name.equalsIgnoreCase("note")) {
                processNote(termCompList, e, mappings);
            } else if (name.equalsIgnoreCase("ref")) {
                processReference(termCompList, e, mappings);
            } else if (name.equalsIgnoreCase("xref")) {
                processXReference(termCompList, e, mappings);
            }
        }
        entry.Decomposition.add(termCompList);

    }

    TermComp processTermComp(Element e, Mappings mappings) {
        //<!ELEMENT termComp (#PCDATA) >
        //<!ATTLIST termComp
        // %impIDLang;
        //>
        final TermComp termComp = new TermComp(e.getTextContent(), e.getAttribute("xml:lang"), mappings);
        processID(termComp, e);
        return termComp;
    }

    void processTermCompGrp(TermCompList termCompList, Element elem, Mappings mappings) {
        //<!ELEMENT termCompGrp (termComp, (termNote|termNoteGrp)*, %noteLinkInfo;) >
        //<!ATTLIST termCompGrp
        //id ID #IMPLIED
        //>
        final TermCompGrp termCompGrp = new TermCompGrp(processTermComp(XMLUtils.firstChild("termComp", elem), mappings), null, mappings);
        for (Element e : XMLUtils.children(elem)) {
            final String name = e.getTagName();
            if (name.equalsIgnoreCase("termNote")) {
                termCompGrp.termNoteGrps.add(new TermNoteGrp(processTermNote(e, mappings), null, mappings));
            } else if (name.equalsIgnoreCase("termNoteGrp")) {
                termCompGrp.termNoteGrps.add(processTermNoteGrp(e, mappings));
            } else if (name.equalsIgnoreCase("admin")) {
                processAdmin(termCompList, e, mappings);
            } else if (name.equalsIgnoreCase("adminGrp")) {
                processAdminGrp(termCompList, e, mappings);
            } else if (name.equalsIgnoreCase("transacGrp")) {
                processTransactionGroup(termCompList, e, mappings);
            } else if (name.equalsIgnoreCase("note")) {
                processNote(termCompList, e, mappings);
            } else if (name.equalsIgnoreCase("ref")) {
                processReference(termCompList, e, mappings);
            } else if (name.equalsIgnoreCase("xref")) {
                processXReference(termCompList, e, mappings);
            }
        }
        termCompList.termComp.add(termCompGrp);
    }





    /**
     * 
     */
    private void unexpected(Node n) {
        if (n instanceof Element) {
            throw new TBXFormatException("Unexpected " + ((Element) n).getTagName());
        } else {
            throw new TBXFormatException("Unexpected");
        }
    }

    private void processID(impID elem, Element node) {
        if (node.hasAttribute("id")) {
            elem.setID(node.getAttribute("id"));
        }
    }

    /**
     * 
     */
    private void processImpIDLangTypeTgtDType(impIDLangTypeTgtDtyp ref, Element sub, Mappings mappings) {
        // <!ENTITY % impIDLangTypTgtDtyp '
        //  id ID #IMPLIED
        //  xml:lang CDATA #IMPLIED
        //  type CDATA #REQUIRED
        //  target IDREF #IMPLIED
        //  datatype CDATA #IMPLIED
        // '>
        if (sub.hasAttribute("id")) {
            ref.setID(sub.getAttribute("id"));
        }
        if (sub.hasAttribute("target")) {
            ref.target = sub.getAttribute("target");
        }
        if (sub.hasAttribute("datatype")) {
            ref.datatype = sub.getAttribute("datatype");
        }
        if (sub.hasAttribute("subjectField"))
        {
//            System.out.println("uy");
        }
    }

    private void processAuxInfo(Describable term, Element sub, Mappings mappings) {
        //   <!ENTITY % auxInfo '(descrip | descripGrp | admin | adminGrp | transacGrp | note | ref
        //        | xref)*' >
        final String name = sub.getTagName();
        if (name.equalsIgnoreCase("descrip")) {
            term.Descriptions.add(new DescripGrp(processDescrip(sub, mappings)));
        } else if (name.equalsIgnoreCase("descripGrp")) {
            this.processDescripGroup(term, sub, mappings);
        } else if (name.equalsIgnoreCase("admin")) {
            this.processAdmin(term, sub, mappings);
        } else if (name.equalsIgnoreCase("adminGrp")) {
            this.processAdminGrp(term, sub, mappings);
        } else if (name.equalsIgnoreCase("transacGrp")) {
            this.processTransactionGroup(term, sub, mappings);
        } else if (name.equalsIgnoreCase("note")) {
            this.processNote(term, sub, mappings);
        } else if (name.equalsIgnoreCase("ref")) {
            this.processReference(term, sub, mappings);
        } else if (name.equalsIgnoreCase("xref")) {
            this.processXReference(term, sub, mappings);
        } else {
            throw new TBXFormatException("Element " + name + " not defined by TBX standard");
        }

    }


    /**
     * 
     */
    private Mapping processType(Element sub, Mappings mappings, boolean required) {
        if (sub.hasAttribute("type")) {
            final Mapping m = mappings.getMapping(sub.getTagName(), "type", sub.getAttribute("type"));
            if (m == null && required) {
                logger.warn("Unrecognised mapping for <" + sub.getTagName() + " type=\"" + sub.getAttribute("type") + "\">");
            }
            return m;
        } else if (required) {
            throw new TBXFormatException("type expected");
        } else {
            System.err.println("Null type on " + sub.getTagName());
            return null;
        }
    }
    
    
    /**
     * Converts a XML TBX file (handling large files...)
     * It does not hold in memory the whole dataset, but parses it as it comes.
     * 
     * A TBX file root element is called "martif". It has two childre: marthifHeader and text
     * 
     * 
     * @param file Path to the input file
     * @param mappings Mappings
     * @return The TBX terminology
     */
    public TBX_Terminology convertAndSerializeLargeFile(String file, PrintStream fos, Mappings mappings, String namespace) {
        String resourceURI = new String(namespace);
        FileInputStream inputStream = null;
        Scanner sc = null;
        int count = 0;
        int errors = 0;

        //We first count the lexicons we have
        SAXHandler handler = null;
        HashMap<String, Resource> lexicons = new HashMap();
        try {
            InputStream xmlInput = new FileInputStream(file);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            handler = new SAXHandler(mappings);
            saxParser.parse(xmlInput, handler);
            lexicons = handler.getLexicons(namespace);
            xmlInput.close();
        } catch (Exception e) {
        	logger.warn("There was an error while reading/creting the lexicons, this could affect the rest of the code");
            logger.warn(e.getMessage());
        }

        //WE PROCESS HERE THE MARTIF HEADER
        MartifHeader martifheader = extractAndReadMartifHeader(file, mappings);
        

        if (martifheader==null)
            return null;
        
        //First we serialize the header
        Model mdataset = ModelFactory.createDefaultModel();
        //The whole dataset!
        final Resource rdataset = mdataset.createResource(resourceURI);
        rdataset.addProperty(DCTerms.type, handler.getMartifType());
        //This should be generalized
        rdataset.addProperty(RDF.type, mdataset.createResource("http://www.w3.org/ns/dcat#Dataset"));
        rdataset.addProperty(DC.rights, IATE.rights);
        rdataset.addProperty(DC.source, IATE.iate);
        rdataset.addProperty(DC.attribution, "Download IATE, European Union, 2014");
        martifheader.toRDF(mdataset, rdataset);
        RDFDataMgr.write(fos, mdataset, Lang.NTRIPLES);

        
        Model msubjectFields = SubjectFields.generateSubjectFields();
        RDFDataMgr.write(fos, msubjectFields, Lang.NTRIPLES);
        
        

        //We declare that every lexicon belongs to 
        Iterator it = lexicons.entrySet().iterator();
        Property prootresource=mdataset.createProperty("http://www.w3.org/TR/void/rootResource");
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            Resource rlexicon = (Resource) e.getValue();
            rlexicon.addProperty(prootresource, rdataset);
        }
        
        
        boolean dentro = false;
        try {
            inputStream = new FileInputStream(file);
            sc = new Scanner(inputStream, "UTF-8");
            String xml = "";

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                //We identify the terms by scanning the strings. Not a very nice practice, though.
                int index = line.indexOf("<termEntry");
                if (index != -1) {
                    dentro = true;
                    xml = line.substring(index) + "\n";
                }
                if (dentro == true && index == -1) {
                    xml = xml + line + "\n";
                }
                index = line.indexOf("</termEntry>");
                if (index != -1) {
                    xml = xml + line.substring(0, index) + "\n";
                    count++;
                    //We do a partial parsing of this XML fragment
                    Document doc = loadXMLFromString(xml);
                    if (doc == null) {
                        continue;
                    }
                    Element root = doc.getDocumentElement();
                    if (root != null) {
                        try {
                            Term term = processTermEntry(root, mappings);
                            Model model = ModelFactory.createDefaultModel();
                            TBX.addPrefixesToModel(model);
                            model.setNsPrefix("", Main.DATA_NAMESPACE);
                            final Resource rterm = term.getRes(model);
                            rterm.addProperty(RDF.type, ONTOLEX.Concept);
                            term.toRDF(model, rterm);
                            for (LexicalEntry le : term.Lex_entries) {
                                final Resource lexicon = lexicons.get(le.lang);
                                lexicon.addProperty(LIME.entry, le.getRes(model));
                                le.toRDF(model, rterm);
                            }
                            RDFDataMgr.write(fos, model, Lang.NTRIPLES);
                        } catch (Exception e) {
                            errors++;
                            System.err.println("Error " + e.getMessage());
                        }
                        if (count % 1000 == 0) {
                            System.err.println("Total: " + count + " Errors: " + errors);
                        }
                    }
                    xml = "";
                }
            } //end of while

            //Now we serialize the lexicons
            RDFDataMgr.write(fos, handler.getLexiconsModel(), Lang.NTRIPLES);



            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
        return null;
    }        
    /**
     * Gently loads a DOM XML document from a XML fragment.
     * If it fails, it returns null;
     */
    public static Document loadXMLFromString(String xml) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId)
                        throws SAXException, IOException {
                    if (systemId.endsWith(".dtd")) {
                        return new InputSource(new StringReader(""));
                    } else {
                        return null;
                    }
                }
            });
            InputSource is = new InputSource(new StringReader(xml));
            return builder.parse(is);
        } catch (Exception e) {
            return null;
        }
    }        
    

    /**
     * Parses the text manually, extracting as text the fragment where the MartifHeader is and then parses it as XML.
     */
    public MartifHeader extractAndReadMartifHeader(String file, Mappings mappings)
    {
        MartifHeader martifheader = null;
        boolean dentro = false;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            Scanner sc = new Scanner(inputStream, "UTF-8");
            String xml = "";
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                //We identify the terms by scanning the strings. Not a very nice practice, though.
                int index = line.indexOf("<martifHeader");
                if (index != -1) {
                    dentro = true;
                    xml = line.substring(index) + "\n";
                }
                if (dentro == true && index == -1) {
                    xml = xml + line + "\n";
                }
                index = line.indexOf("</martifHeader>");
                if (index != -1) {
                    xml = xml + line.substring(0, index) + "\n";
                    //We do a partial parsing of this XML fragment
                    Document doc = loadXMLFromString(xml);
                    Element root = doc.getDocumentElement();
                    martifheader = this.processMartifHeader(root, mappings);
                    break;
                }

            }
            inputStream.close();
        } catch (Exception e) {
            logger.warn("Could not parse well the general metadata (MartifHeader)" + e.getMessage());
        }
        return martifheader;
}    
    
}
