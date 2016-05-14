# jaxb-jxpath-evaluator
Evaluates XPath expressions on JAXB-serializable objects.
This implementation - other than jxpath-object-formatter - uses
the standard JAXB, JAXP and XPath APIs to evaluate the XPath.
This is somewhat slower than the JXPath approach, but much more accurate
and reliable, since XPath is actually evaluated on he XML representation
of the JAXB objects, which will of course have to be at least partially
serialized to a DOM tree, whereas JXPath works directly on the beans in memory.

Specify the class
    net.codesup.jaxb.xpath.Evaluator
in an "evaluator" element of a jaxb-format-plugin configuration, then
annotate your XSD complexTypes with the "expression" binding customization,
setting the "select" attribute to an XPath expression, and it will generate
an additional method in your generated class that returns the result of
the XPath expression evaluation on the current object.


