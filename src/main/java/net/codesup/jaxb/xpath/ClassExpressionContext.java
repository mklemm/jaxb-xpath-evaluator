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
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Mirko Klemm 2016-05-15
 */
public class ClassExpressionContext {
	private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
	private static final XPathFactory X_PATH_FACTORY = XPathFactory.newInstance();

	private final DocumentBuilder documentBuilder;
	private final Map<String,XPathExpression> expressions = new HashMap<>();
	private final Locale locale;
	private final JAXBContext jaxbContext;
	private final boolean assumeImmutable;

	static {
		ClassExpressionContext.DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
	}

	public ClassExpressionContext(final Class<?> targetClass, final Locale locale, final boolean assumeImmutable) {
		try {
			this.locale = locale;
			this.assumeImmutable = assumeImmutable;
			this.jaxbContext = JAXBContext.newInstance(targetClass);
			this.documentBuilder = ClassExpressionContext.DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
		} catch (final Exception e) {
			throw new IllegalArgumentException(e);
		}

	}

	public ClassExpressionContext(final Class<?> targetClass, final boolean assumeImmutable) {
		this(targetClass, Locale.getDefault(), assumeImmutable);
	}

	public ClassExpressionContext(final Class<?> targetClass, final Locale locale) {
		this(targetClass, locale, false);
	}

	public ClassExpressionContext(final Class<?> targetClass) {
		this(targetClass, Locale.getDefault(), false);
	}

	public InstanceExpressionContext createEvaluator(final Object root) {
		return new InstanceExpressionContext(this, root);
	}

	XPathExpression getExpression(final String expressionString, final String[][] namespaceMappings) throws XPathExpressionException {
		XPathExpression expression = this.expressions.get(expressionString);
		if(expression == null) {
			final XPath xPath = ClassExpressionContext.X_PATH_FACTORY.newXPath();
			xPath.setXPathFunctionResolver(new XPathExtensionFunctions(xPath.getXPathFunctionResolver(), this.locale));
			xPath.setNamespaceContext(new SimpleNamespaceContext(namespaceMappings));
			expression = xPath.compile(expressionString);
			this.expressions.put(expressionString, expression);
		}
		return expression;
	}

	Document createDocument() {
		return this.documentBuilder.newDocument();
	}

	Binder<Node> createBinder() {
		return this.jaxbContext.createBinder(Node.class);
	}

	boolean isAssumeImmutable() {
		return this.assumeImmutable;
	}
}
