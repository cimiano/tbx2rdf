Custom Mappings for TBX2RDF
===========================

One of the key goals of mapping TBX to RDF is to add semantics (that is meaning)
to TBX documents. As such we need to assign each property of a TBX a single
globally unique identifier, that is a URL, where more semantic information about
this resource can be found. This is done by means of an external mapping file.
There are four kinds of mapping that we can do:

* __Value mapping__: This maps the contents of single tag into a URL, which can
    then be used to provide extra information such as definitions to users of
    your TBX vocabulary.
* __Data properties__: This maps a TBX property to a URL, where information such
    as definitions or even cardinality may be found
* __Object properties__: This is similar to TBX properties but limits the values
    to a fixed list of values, which must then be declared as value mappings.
* __Exceptional mapping__: This allows custom mapping function but requires
    recompiling the TBX2RDF converter.

The default mapping file used by the service is
[`mappings.default`](mappings.default).

Value mapping
-------------

Value mapping is a single line consisting of two elements, the value and the 
URI surrounded by angular brackes (`<` `>`) and separated by whitespace
characters. For example:

    noun <http://tbx2rdf.lider-project.eu/tbx#noun>
    verb <http://tbx2rdf.lider-project.eu/tbx#verb>
    adjective <http://tbx2rdf.lider-project.eu/tbx#adjective>
    adverb <http://tbx2rdf.lider-project.eu/tbx#adverb>
    properNoun <http://tbx2rdf.lider-project.eu/tbx#properNoun>
    other <http://tbx2rdf.lider-project.eu/tbx#other>

Ideally these URLs should refer to individuals in an
[ontology](http://en.wikipedia.org/wiki/Web_Ontology_Language), or at least you
should ensure that these URIs resolve for your service

Data property mappings
----------------------


Data property mappings is generally used for properties that contain text in
natural language or resource identifiers. consist of fields as follows

* The name of the tag where this property occurs
* The name of the attribute used to choose this property, (normally `type`)
* The name of the property in TBX
* The URL to assign for the property
* The field field must be exactly the text `DP`
* (Optional) the datatype of the resource, given by its URL in
    [XSD](http://www.w3.org/2001/XMLSchema#)

Examples of this mapping are as follows

    descrip	type	definition	<http://tbx2rdf.lider-project.eu/tbx#definition>	DP
    descrip	type	example	<http://tbx2rdf.lider-project.eu/tbx#example>	DP
    transacNote	type	usageCount	<http://tbx2rdf.lider-project.eu/tbx#usaageCount>		DP  <http://www.w3.org/2001/XMLSchema#integer>

The URL used should be a datatype property in an OWL ontology.

Object property mappings
------------------------

Object properties are used to assign the value to one of a fixed list of values,
the declaration consists of the following fields.

* The name of the tag where this property occurs
* The name of the attribute used to choose this property, (normally `type`)
* The name of the property in TBX
* The URL to assign for the property
* The field field must be exactly the text `OP`
* (Optional) The list of values in curly braces (`{` `}`) and separated by
    commas.

You may omit the list of values to disable range checking for this property.

    termNote	type	partOfSpeech	<http://tbx2rdf.lider-project.eu/tbx#partOfSpeech>	OP	{noun,verb,adjective,adverb,properNoun,other}
    termNote	directionality	bidirectional	<http://tbx2rdf.lider-project.eu/tbx#bidirectionalTranslation>	OP

The URL used should be an object property in an OWL ontology.

Exceptional mappings
--------------------

Exceptional mappings are as follows:

    descrip	type	subjectField	<subjectField>	EX

They are implemented by the method with the name given in angular brackets in 
the class
[`tbx2rdf.ExceptionMethods`](src/java/tbx2rdf/ExceptionMethods.java).
