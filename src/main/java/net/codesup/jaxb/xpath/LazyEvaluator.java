package net.codesup.jaxb.xpath;

import java.util.Locale;

/**
 * Lazy Variant of the evaluator, use when
 * it is unclear whether an expression will ever be evaluated, and
 * when it isn't required that all initialization
 * happens up front.
 * @author Mirko Klemm 2016-05-14
 */
public class LazyEvaluator {
	private final Object root;
	private final String[][] namespaceMappings;
	private final boolean assumeImmutable;
	private final Locale locale;
	private Evaluator evaluator = null;

	public LazyEvaluator(final Object root, final String[][] namespaceMappings, final Locale locale, final boolean assumeImmutable) {
		this.root = root;
		this.namespaceMappings = namespaceMappings;
		this.locale = locale;
		this.assumeImmutable = assumeImmutable;
	}
	public LazyEvaluator(final Object root, final String[][] namespaceMappings, final Locale locale) {
		this.root = root;
		this.namespaceMappings = namespaceMappings;
		this.locale = locale;
		this.assumeImmutable = false;
	}
	public LazyEvaluator(final Object root, final String[][] namespaceMappings) {
		this.root = root;
		this.namespaceMappings = namespaceMappings;
		this.locale = Locale.getDefault();
		this.assumeImmutable = false;
	}
	public LazyEvaluator(final Object root, final String[][] namespaceMappings, final boolean assumeImmutable) {
		this.root = root;
		this.namespaceMappings = namespaceMappings;
		this.locale = Locale.getDefault();
		this.assumeImmutable = assumeImmutable;
	}

	public Object evaluate(final String expressionString) {
		if(this.evaluator == null) {
			this.evaluator = new Evaluator(this.root, this.namespaceMappings, this.locale, this.assumeImmutable);
		}
		return this.evaluator.evaluate(expressionString);
	}
}
