package net.codesup.jaxb.xpath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Locale;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Mirko Klemm 2016-05-14
 */
public class XPathExtensionFunctionsTest {
	private static final String[][] nsMap = {{"format", XPathExtensionFunctions.NAMESPACE_URI}};
	public static final TestFormatableObject TEST_OBJECT = new TestFormatableObject(
			"FormatableObject",
			"A",
			new Date(115,1,28),
			new TestFormatableChild("Child", "Formatable Child", new BigInteger("2345678900786454112345"), new BigDecimal("8346524.83465564774555"), 9837836365454554.354736745523435d)
	);
	private final Evaluator evaluator = new Evaluator(XPathExtensionFunctionsTest.TEST_OBJECT, XPathExtensionFunctionsTest.nsMap, Locale.UK);

	@Test
	public void testIsoDate() {
		assertEquals("2015-02-28", this.evaluator.evaluate("format:isoDate(valid-from)"));
	}

	@Test
	public void testIsoDateTime() {
		assertEquals("2015-02-28T00:00:00+0100", this.evaluator.evaluate("format:isoDateTime(valid-from)"));
	}

	@Test
	public void testFormat() {
		assertEquals("15 FormatableObject : 2345678900786454112345,00", this.evaluator.evaluate("format:format('{0,date,yy} {1} : {2,number,#.00}' , format:date(valid-from), string(@can-name), format:decimal(child/value))"));
	}

	@Test
	public void testDecode() {
		assertEquals("2015-02-28T00:00:00+0100", this.evaluator.evaluate("format:decode(string(@can-name), 'FormatableObject', format:isoDateTime(valid-from))"));
	}
}
