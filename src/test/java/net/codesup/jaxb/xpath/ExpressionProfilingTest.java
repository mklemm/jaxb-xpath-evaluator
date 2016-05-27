package net.codesup.jaxb.xpath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

/**
 * @author Mirko Klemm 2016-05-15
 */
public class ExpressionProfilingTest {
	public static final int ITERATIONS = 10000;
	private final String[][] nsMap = {{"format", XPathExtensionFunctions.NAMESPACE_URI}};

	@Test
	public void testJXPathEvaluateComplexMany() throws Exception {
		final net.codesup.jxpath.formatter.Evaluator objectFormatter = new net.codesup.jxpath.formatter.Evaluator(new TestFormatableObject("FormatableObject", "A", new Date(115,1,28), new TestFormatableChild("Child", "Formatable Child", new BigInteger("2345678900786454112345"), new BigDecimal("8346524.83465564774555"), 9837836365454554.354736745523435d)), Locale.UK);
		final List<Object> obs = new ArrayList<>(ITERATIONS);
		final long startTime = System.currentTimeMillis();
		for(int i = 0; i < ITERATIONS; i++) {
			final Object formatted = objectFormatter.evaluate(EvaluatorTest.COMPLEX_EXPRESSION_JX);
			obs.add(formatted);
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("JXPath many evaluations Time: " + (endTime - startTime) + "ms");
	}

	@Test
	public void testJXpathEvaluateManyObjects() {
		final List<Object> obs = new ArrayList<>(ITERATIONS);
		final long startTime = System.currentTimeMillis();
		for(int i = 0; i < ITERATIONS; i++) {
			final net.codesup.jxpath.formatter.Evaluator objectFormatter = new net.codesup.jxpath.formatter.Evaluator(new TestFormatableObject("FormatableObject", "A", new Date(115,1,28), new TestFormatableChild("Child", "Formatable Child", new BigInteger("2345678900786454112345"), new BigDecimal("8346524.83465564774555"), 9837836365454554.354736745523435d)), Locale.UK);
			obs.add(objectFormatter.evaluate(EvaluatorTest.COMPLEX_EXPRESSION_JX));
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("JXPath many objects Time: " + (endTime - startTime) + "ms");
	}

	@Test
	public void testJaxbEvaluateComplexMany() throws Exception {
		final Evaluator objectFormatter = new Evaluator(new TestFormatableObject("FormatableObject", "A", new Date(115,1,28), new TestFormatableChild("Child", "Formatable Child", new BigInteger("2345678900786454112345"), new BigDecimal("8346524.83465564774555"), 9837836365454554.354736745523435d)), this.nsMap, Locale.UK);
		final List<Object> obs = new ArrayList<>(ITERATIONS);
		final long startTime = System.currentTimeMillis();
		for(int i = 0; i < ITERATIONS; i++) {
			final Object formatted = objectFormatter.evaluate(EvaluatorTest.COMPLEX_EXPRESSION);
			obs.add(formatted);
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("JAXB many evaluations Time: " + (endTime - startTime) + "ms");
	}
	@Test
	public void testJaxbEvaluateComplexManyImmutable() throws Exception {
		final Evaluator objectFormatter = new Evaluator(new TestFormatableObject("FormatableObject", "A", new Date(115,1,28), new TestFormatableChild("Child", "Formatable Child", new BigInteger("2345678900786454112345"), new BigDecimal("8346524.83465564774555"), 9837836365454554.354736745523435d)), this.nsMap, Locale.UK, true);
		final List<Object> obs = new ArrayList<>(ITERATIONS);
		final long startTime = System.currentTimeMillis();
		for(int i = 0; i < ITERATIONS; i++) {
			final Object formatted = objectFormatter.evaluate(EvaluatorTest.COMPLEX_EXPRESSION);
			obs.add(formatted);
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("JAXB many immutable objects: " + (endTime - startTime) + "ms");
	}

	@Test
	public void testJaxbEvaluateManyObjects() {
		final List<Object> obs = new ArrayList<>(ITERATIONS);
		final long startTime = System.currentTimeMillis();
		for(int i = 0; i < ITERATIONS; i++) {
			final Evaluator objectFormatter = new Evaluator(new TestFormatableObject("FormatableObject", "A", new Date(115,1,28), new TestFormatableChild("Child", "Formatable Child", new BigInteger("2345678900786454112345"), new BigDecimal("8346524.83465564774555"), 9837836365454554.354736745523435d)), this.nsMap, Locale.UK);
			obs.add(objectFormatter.evaluate(EvaluatorTest.COMPLEX_EXPRESSION));
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("JAXB many objects Time: " + (endTime - startTime) + "ms");
	}

	@Test
	public void testJaxbEvaluateManyObjectsClassInit() {
		final List<Object> obs = new ArrayList<>(ITERATIONS);
		final long startTime = System.currentTimeMillis();
		final ClassExpressionContext classExpressionContext = new ClassExpressionContext(TestFormatableObject.class);
		for(int i = 0; i < ITERATIONS; i++) {
			final InstanceExpressionContext objectFormatter = classExpressionContext.createEvaluator(new TestFormatableObject("FormatableObject", "A", new Date(115,1,28), new TestFormatableChild("Child", "Formatable Child", new BigInteger("2345678900786454112345"), new BigDecimal("8346524.83465564774555"), 9837836365454554.354736745523435d)));
			obs.add(objectFormatter.evaluate(EvaluatorTest.COMPLEX_EXPRESSION, nsMap));
		}
		final long endTime = System.currentTimeMillis();
		System.out.println("JAXB many objects with class init: " + (endTime - startTime) + "ms");
	}

}
