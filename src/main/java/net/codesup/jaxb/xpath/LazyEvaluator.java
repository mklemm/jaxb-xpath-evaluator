package net.codesup.jaxb.xpath;

import java.util.HashMap;
import java.util.Map;

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
	private static final Map<Class<?>, Evaluator> CONTEXT_CACHE = new HashMap<>();

	private Evaluator evaluator = null;

	public LazyEvaluator(final Object root, final String[][] namespaceMappings) {
		this.root = root;
		this.namespaceMappings = namespaceMappings;
	}

	public Object evaluate(final String expressionString) {
		if(this.evaluator == null) {
			this.evaluator = new Evaluator(this.root, this.namespaceMappings);
		}
		return this.evaluator.evaluate(expressionString);
	}
}
