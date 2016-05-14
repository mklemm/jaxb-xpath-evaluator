package net.codesup.jaxb.xpath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.NamespaceContext;
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
 * @author Mirko Klemm 2016-05-14
 */
public class Evaluator {
	private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();
	private static final XPathFactory X_PATH_FACTORY = XPathFactory.newInstance();

	private final Map<String,XPathExpression> expressions = new HashMap<>();
	private final Binder<Node> binder;
	private final Document document;
	private final Object root;
	private final XPath xPath;

	static {
		Evaluator.DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
	}

	public Evaluator(final Object root, final String[][] namespaceMappings) {
		this(root, namespaceMappings, Locale.getDefault());
	}

	public Evaluator(final Object root, final String[][] namespaceMappings, final Locale locale) {
		try {
			this.root = root;
			final DocumentBuilder builder = Evaluator.DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
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
			XPathExpression xPathExpression = this.expressions.get(expressionString);
			if(xPathExpression == null) {
				xPathExpression = this.xPath.compile(expressionString);
				this.expressions.put(expressionString, xPathExpression);
			}
			final Node doc = this.binder.updateXML(this.root, this.document.getDocumentElement());
			return xPathExpression.evaluate(doc);
		} catch (final Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static class SimpleNamespaceContext implements NamespaceContext {
		private final String[][] mappings;

		public SimpleNamespaceContext(final String[][] mappings) {
			this.mappings = mappings;
		}

		@Override
		public String getNamespaceURI(final String prefix) {
			for(final String[] mapping: this.mappings) {
				if(mapping[0].equals(prefix)) {
					return mapping[1];
				}
			}
			return null;
		}

		@Override
		public String getPrefix(final String namespaceURI) {
			for(final String[] mapping: this.mappings) {
				if(mapping[1].equals(namespaceURI)) {
					return mapping[0];
				}
			}
			return null;
		}

		@Override
		public Iterator getPrefixes(final String namespaceURI) {
			final List<String> prefixes = new ArrayList<>();
			for(final String[] mapping: this.mappings) {
				if(mapping[1].equals(namespaceURI)) {
					prefixes.add(mapping[0]);
				}
			}
			return prefixes.iterator();
		}
	}
}
