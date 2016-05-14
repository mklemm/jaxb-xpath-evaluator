# jxpath-object-formatter
Thin wrapper for apache commons-jxpath to provide a simple interface for creating string representations of objects using XPath expressions.

This uses a [patched version](http://github.com/mklemm/commons-jxpath) of apache commons-jxpath to allow for XPath expressions that use the XML names of JAXB-serializable objects instead of pure java bean property names, so this module ist best used with the JAXB XJC plugin [jaxb-format-plugin](http://github.com/mklemm/jaxb-format-plugin).

Specify the class
    com.kscs.util.jaxb.ObjectFormatter
as an argument to the "-formatter" command line option of the jaxb-format-plugin, then annotate your XSD complexTypes
with the "expression" binding customization, setting the "select" attribute to an XPath expression that evaluates to a string value.


