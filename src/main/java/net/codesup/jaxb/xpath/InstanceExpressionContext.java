package net.codesup.jaxb.xpath;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.Binder;
import javax.xml.xpath.XPathExpression;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Mirko Klemm 2016-05-15
 */
public class InstanceExpressionContext {
	private final ClassExpressionContext classExpressionContext;
	private final Object root;
	private final Document document;
	private final Binder<Node> binder;
	private final Map<String, Object> expressionResults = new HashMap<>();

	InstanceExpressionContext(final ClassExpressionContext classExpressionContext, final Object root) {
		try {
			this.classExpressionContext = classExpressionContext;
			this.root = root;
			this.document = this.classExpressionContext.createDocument();
			this.binder = this.classExpressionContext.createBinder();
			this.binder.marshal(root, this.document);
		} catch (final Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Evaluate expression
	 *
	 * @param expressionString XPath expression
	 * @return the result object of the XPath expression evaluation
	 */
	public Object evaluate(final String expressionString, final String[][] namespaceMappings) {
		try {
			if (this.classExpressionContext.isAssumeImmutable()) {
				Object result = this.expressionResults.get(expressionString);
				if (result == null) {
					final XPathExpression xPathExpression = this.classExpressionContext.getExpression(expressionString, namespaceMappings);
					final Node doc = this.binder.updateXML(this.root, this.document.getDocumentElement());
					result = xPathExpression.evaluate(doc);
					this.expressionResults.put(expressionString, result);
				}
				return result;
			} else {
				final XPathExpression xPathExpression = this.classExpressionContext.getExpression(expressionString, namespaceMappings);
				final Node doc = this.binder.updateXML(this.root, this.document.getDocumentElement());
				return xPathExpression.evaluate(doc);
			}
		} catch (final Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
}
