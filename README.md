# jaxb-xpath-evaluator
Evaluates XPath expressions on JAXB-serializable objects.
This implementation - other than jxpath-object-formatter - uses
the standard JAXB, JAXP and XPath APIs to evaluate the XPath.
This is somewhat slower than the JXPath approach, but much more accurate
and reliable, since XPath is actually evaluated on he XML representation
of the JAXB objects, which will of course have to be at least partially
serialized to a DOM tree, whereas JXPath works directly on the beans in memory.

JXPath doesn't support namespaces and the object model it is working on
differs in many aspects from the true XML DOM that you would see when
looking at the serialized XML of the target object. Plus, JXPath
has some nasty bugs in the current scenario.
So, despite being much slower, the JAXB binder approach used in this module
is cleaner, more predictable, and more accurate than the JXPath implementation.

Specify the class
    net.codesup.jaxb.xpath.Evaluator
in an "evaluator" element of a jaxb-format-plugin configuration, then
annotate your XSD complexTypes with the "expression" binding customization,
setting the "select" attribute to an XPath expression, and it will generate
an additional method in your generated class that returns the result of
the XPath expression evaluation on the current object.


