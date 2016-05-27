package net.codesup.jaxb.xpath;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;

/**
 * @author Mirko Klemm 2016-05-15
 */
class SimpleNamespaceContext implements NamespaceContext {
	private final String[][] mappings;

	public SimpleNamespaceContext(final String[][] mappings) {
		this.mappings = mappings;
	}

	@Override
	public String getNamespaceURI(final String prefix) {
		for (final String[] mapping : this.mappings) {
			if (mapping[0].equals(prefix)) {
				return mapping[1];
			}
		}
		return null;
	}

	@Override
	public String getPrefix(final String namespaceURI) {
		for (final String[] mapping : this.mappings) {
			if (mapping[1].equals(namespaceURI)) {
				return mapping[0];
			}
		}
		return null;
	}

	@Override
	public Iterator getPrefixes(final String namespaceURI) {
		final List<String> prefixes = new ArrayList<>();
		for (final String[] mapping : this.mappings) {
			if (mapping[1].equals(namespaceURI)) {
				prefixes.add(mapping[0]);
			}
		}
		return prefixes.iterator();
	}
}
