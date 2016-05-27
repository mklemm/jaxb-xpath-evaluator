package net.codesup.jaxb.xpath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Mirko Klemm 2016-05-14
 */
public class EvaluatorTest {
	private final String[][] nsMap = {{"format", XPathExtensionFunctions.NAMESPACE_URI}};
	public static final String SIMPLE_EXPRESSION = "concat('Formatted Object: \"',@can-name,':',@order-key, ' - ', format:isoDateTime(valid-from) ,'\"')";
	public static final String SIMPLE_EXPECTED_RESULT = "Formatted Object: \"test object:1 - 2015-01-01T00:00:00+0100\"";

	public static final String COMPLEX_EXPRESSION = "concat('Complex Object: \"',@can-name,':',child/@display-name,' - ', child/@name, ' - ', format:format-number(child/underwriting-amount,'#.000'),' (spacing=', format:format-number(child/spacing,'0.0000%'),') \"')";
	public static final String COMPLEX_EXPRESSION_JX = "concat('Complex Object: \"',@can-name,':',child/@display-name,' - ', child/@name, ' - ', format-number(child/underwriting-amount,'#.000'),' (spacing=', format-number(child/spacing,'0.0000%'),') \"')";
	public static final String COMPLEX_EXPECTED_RESULT = "Complex Object: \"FormatableObject:Formatable Child - Child - 9837836365454554.000 (spacing=834652483.4656%) \"";

	@Test
	@Ignore
	public void testEvaluate() throws Exception {
		final net.codesup.jxpath.formatter.Evaluator objectFormatter = new net.codesup.jxpath.formatter.Evaluator(new TestFormatableObject("test object", "1", new Date(115,0,1), new TestFormatableChild("Child", "Formatable Child", new BigInteger("2345678900786454112345"), new BigDecimal("8346524.83465564774555"), 9837836365454554.354736745523435d)), Locale.UK);
		final Object formatted = objectFormatter.evaluate(EvaluatorTest.SIMPLE_EXPRESSION);
		System.out.println(formatted);
		assertEquals(EvaluatorTest.SIMPLE_EXPECTED_RESULT, formatted);
	}

	@Test
	@Ignore
	public void testEvaluateComplex() throws Exception {
		final net.codesup.jxpath.formatter.Evaluator objectFormatter = new net.codesup.jxpath.formatter.Evaluator(new TestFormatableObject("FormatableObject", "A", new Date(115,1,28), new TestFormatableChild("Child", "Formatable Child", new BigInteger("2345678900786454112345"), new BigDecimal("8346524.83465564774555"), 9837836365454554.354736745523435d)), Locale.UK);
		final Object formatted = objectFormatter.evaluate(EvaluatorTest.COMPLEX_EXPRESSION_JX);
		System.out.println(formatted);
		assertEquals(EvaluatorTest.COMPLEX_EXPECTED_RESULT, formatted);
	}

	@Test
	public void testEvaluateNew() throws Exception {
		final Evaluator objectFormatter = new Evaluator(new TestFormatableObject("test object", "1", new Date(115,0,1), new TestFormatableChild("Child", "Formatable Child", new BigInteger("2345678900786454112345"), new BigDecimal("8346524.83465564774555"), 9837836365454554.354736745523435d)), this.nsMap, Locale.UK);
		final Object formatted = objectFormatter.evaluate(EvaluatorTest.SIMPLE_EXPRESSION);
		System.out.println(formatted);
		assertEquals(EvaluatorTest.SIMPLE_EXPECTED_RESULT, formatted);
	}

	@Test
	public void testEvaluateComplexNew() throws Exception {
		final Evaluator objectFormatter = new Evaluator(new TestFormatableObject("FormatableObject", "A", new Date(115,1,28), new TestFormatableChild("Child", "Formatable Child", new BigInteger("2345678900786454112345"), new BigDecimal("8346524.83465564774555"), 9837836365454554.354736745523435d)), this.nsMap, Locale.UK);
		final Object formatted = objectFormatter.evaluate(EvaluatorTest.COMPLEX_EXPRESSION);
		System.out.println(formatted);
		assertEquals(EvaluatorTest.COMPLEX_EXPECTED_RESULT, formatted);
	}

}
