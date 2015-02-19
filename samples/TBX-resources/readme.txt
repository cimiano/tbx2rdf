*** TBX Resources Package ***

Last updated: January 23, 2013
Address enquiries to: akmtrg@byu.edu

This package contains the following files:

TBXXCSV02.xcs - the XCS file for the data categories for the TBX-default
terminological markup language

tbxxcsdtd.dtd - the DTD that validates XCS files

TBXcoreStructV02.dtd - the DTD that validates the core structure of a TBX document instance

TBX_RNGV02.rng - the integrated RNG schema that combines the constraints of the core structure DTD
and the XCS for the TBX-default terminological markup language.

A series of files with the ".tbx" extension. These are sample files, one is perfectly valid
and the remaining demonstrate various validation and conformance errors.

You can use the DTD and the XCS file to validate a TBX document instance by using the TBX Checker
of which the latest version is available for download from SourceForge:
http://sourceforge.net/projects/tbxutil/

You can use the integrated RNG schema to validate a TBX document instance by using an XML
parser that supports the RelaxNG and Schematron languages, such as oXygen.

