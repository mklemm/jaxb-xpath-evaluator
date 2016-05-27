package net.codesup.jaxb.xpath;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Evaluates arbitrary XPath expressions on a JAXB object.
 * The XPath context is established by marshalling the
 * object to a DOM tree, on which XPath is executed.
 *
 * This is a simple implementation that does all
 * initialization work in the constructor, so there
 * is no way to re-use instance-independent state.
 * For an implementation that seperates class and
 * instance initialization, have a look at
 * {@see ClassExpressionContext} abd {@see InstanceExpressionContext}
 * @author Mirko Klemm 2016-05-14
 */
public class Evaluator {
	private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
	private static final XPathFactory X_PATH_FACTORY = XPathFactory.newInstance();

	private final Map<String,XPathExpression> expressions = new HashMap<>();
	private final Map<String,Object> expressionResults = new HashMap<>();
	private final Binder<Node> binder;
	private final Document document;
	private final Object root;
	private final XPath xPath;
	private final boolean assumeImmutable;

	static {
		Evaluator.DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
	}

	public Evaluator(final Object root, final String[][] namespaceMappings) {
		this(root, namespaceMappings, Locale.getDefault(), false);
	}
	public Evaluator(final Object root, final String[][] namespaceMappings, final boolean assumeImmutable) {
		this(root, namespaceMappings, Locale.getDefault(), assumeImmutable);
	}
	public Evaluator(final Object root, final String[][] namespaceMappings, final Locale locale) {
		this(root, namespaceMappings, locale, false);
	}
	public Evaluator(final Object root, final String[][] namespaceMappings, final Locale locale, final boolean assumeImmutable) {
		try {
			this.assumeImmutable = assumeImmutable;
			final DocumentBuilder builder = Evaluator.DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
			this.root = root;
			this.document = builder.newDocument();
			final JAXBContext jaxbContext = JAXBContext.newInstance(root.getClass());
			this.binder = jaxbContext.createBinder(Node.class);
			this.binder.marshal(root, this.document);
			this.xPath = Evaluator.X_PATH_FACTORY.newXPath();
			this.xPath.setXPathFunctionResolver(new XPathExtensionFunctions(this.xPath.getXPathFunctionResolver(), locale));
			this.xPath.setNamespaceContext(new SimpleNamespaceContext(namespaceMappings));
		} catch(final Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Evaluate expression
	 * @param expressionString XPath expression
	 * @return the result object of the XPath expression evaluation
	 */
	public Object evaluate(final String expressionString) {
		try {
			if(this.assumeImmutable) {
				Object result = this.expressionResults.get(expressionString);
				if(result == null) {
					result = this.xPath.evaluate(expressionString, this.document.getDocumentElement());
					this.expressionResults.put(expressionString, result);
				}
				return result;
			} else {
				XPathExpression xPathExpression = this.expressions.get(expressionString);
				if (xPathExpression == null) {
					xPathExpression = this.xPath.compile(expressionString);
					this.expressions.put(expressionString, xPathExpression);
				}
				final Node doc = this.binder.updateXML(this.root, this.document.getDocumentElement());
				return xPathExpression.evaluate(doc);
			}
		} catch (final Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
}
