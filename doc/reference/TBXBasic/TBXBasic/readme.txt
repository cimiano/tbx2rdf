*** TBX-Basic Download Package ***

This download package will be updated regularly. 
Last update: February 26, 2009.

Recent changes:

February 26, 2009 - Updated this readme file with a new date and the two changes listed below:
January 26, 2009 - updated the RNG schema to allow a context sentence to occur in a descripGrp
January 23, 2009 - added a context source data category to the specification document and samples

Address enquiries to: kara@ca.ibm.com

This package contains the following files:

1. The TBX-Basic specification - TBX_Basic_datacategoriesV22.pdf
2. The TBX-Basic Core Structure DTD - TBXBasiccoreStructureV02.dtd
3. The TBX-Basic eXtensible Constraints Specification file - TBXBasicXCSV02.xcs
4. The DTD for validating the XCS file - tbxxcsdtd.dtd
5. The Integrated RNG Schema for TBX-Basic - TBXBasicRNGV02.rng
6. TBX-Basic sample files - all files ending with the "tbx" extension

The Core Structure DTD and the XCS file are intended for validating TBX-Basic document instances by
using the TBX Checker. The TBX Checker is a validating parser specifically designed for TBX files.
It is available from the following Web site.

http://sourceforge.net/projects/tbxutil/

As an alternative to using the TBX Checker to validate TBX-Basic files, you can use
the Integrated RNG Schema and any off-the-shelf XML validator that supports the RelaxNG
and Schematron anguages, such as oXygen.