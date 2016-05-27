/*
 * MIT License
 *
 * Copyright (c) 2014 Klemm Software Consulting, Mirko Klemm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.codesup.jaxb.xpath;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionException;
import javax.xml.xpath.XPathFunctionResolver;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Mirko Klemm 2015-01-22
 */
public class XPathExtensionFunctions implements XPathFunctionResolver {
	public static final String NAMESPACE_URI = "http://www.codesup.net/jaxb/functions";
	public static final DatatypeFactory DATATYPE_FACTORY;
	private final Map<String, XPathFunction> functions = new HashMap<>();
	private final XPathFunctionResolver nextResolver;
	private final Locale locale;

	static {
		try {
			DATATYPE_FACTORY = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}

	public XPathExtensionFunctions(final XPathFunctionResolver nextResolver, final Locale locale) {
		this.nextResolver = nextResolver;
		this.locale = locale;
		addFunction(new IsoDate());
		addFunction(new IsoDateTime());
		addFunction(new Decode());
		addFunction(new Coalesce());
		addFunction(new Format());
		addFunction(new FormatDate());
		addFunction(new ToDate());
		addFunction(new Hash());
		addFunction(new FormatNumber());
		addFunction(new Decimal());
	}

	private void addFunction(final Func func) {
		this.functions.put(func.name, func);
	}

	public String isoDate(final Date date) {
		return formatDate(date, "yyyy-MM-dd");
	}

	public String isoDateTime(final Date date) {
		return formatDate(date, "yyyy-MM-dd'T'HH:mm:ssZ");
	}

	public Object decode(final Object ref, final List<?> keyValues, final Object def) {
		if (ref == null) {
			for (int i = 0; i < keyValues.size(); i += 2) {
				final Object key = keyValues.get(i);
				final Object val = keyValues.get(i + 1);
				if (key == null) {
					return val;
				}
			}
			return def;
		} else {
			for (int i = 0; i < keyValues.size(); i += 2) {
				final Object key = keyValues.get(i);
				final Object val = keyValues.get(i + 1);
				if (ref.equals(key)) {
					return val;
				}
			}
			return def;
		}
	}

	public Object coalesce(final List<?> refs) {
		for (final Object ref : refs) {
			if (ref != null) return ref;
		}
		return null;
	}

	private String format(final String pattern, final List<?> params) {
		final Object[] convertedParams = new Object[params.size()];
		for (int i = 0; i < params.size(); i++) {
			if (params.get(i) instanceof List) {
				final List<?> list = (List<?>)params.get(i);
				if (!list.isEmpty()) {
					convertedParams[i] = list.get(0);
				} else {
					convertedParams[i] = params.get(i);
				}
			} else {
				convertedParams[i] = params.get(i);
			}
		}
		return MessageFormat.format(pattern, convertedParams);
	}

	public String formatNumber(final Number number, final String format) {
		final DecimalFormat fmt = new DecimalFormat(format, new DecimalFormatSymbols(this.locale));
		return fmt.format(number);
	}

	public String formatDate(final Date date, final String pattern) {
		return new SimpleDateFormat(pattern).format(date);
	}

	public Date date(final Object arg) {
		return asDate(arg);
	}

	public BigDecimal decimal(final Object arg) {
		return asNumber(arg);
	}

	public int hash(final Object o) {
		return o == null ? 0 : o.hashCode();
	}

	@Override
	public XPathFunction resolveFunction(final QName functionName, final int arity) {
		final XPathFunction function = functionName.getNamespaceURI().equals(XPathExtensionFunctions.NAMESPACE_URI) ? this.functions.get(functionName.getLocalPart()) : null;
		return function == null ? this.nextResolver == null ? null : this.nextResolver.resolveFunction(functionName, arity) : function;
	}

	private static abstract class Func implements XPathFunction {
		private final String name;
		private final String sig;
		private final int minArgNum;
		private final int maxArgNum;

		protected Func(final String name, final String sig, final int minArgNum, final int maxArgNum) {
			this.name = name;
			this.sig = sig;
			this.minArgNum = minArgNum;
			this.maxArgNum = maxArgNum;
		}

		protected Func(final String name, final String sig, final int minArgNum) {
			this.name = name;
			this.sig = sig;
			this.minArgNum = minArgNum;
			this.maxArgNum = Integer.MAX_VALUE;
		}

		protected Func(final String name, final String sig) {
			this.name = name;
			this.sig = sig;
			this.minArgNum = 0;
			this.maxArgNum = Integer.MAX_VALUE;
		}

		@Override
		public Object evaluate(final List nullableArgs) throws XPathFunctionException {
			final List args = nullableArgs == null ? Collections.emptyList() : nullableArgs;
			if (args.size() < this.minArgNum || args.size() > this.maxArgNum) {
				throw new XPathFunctionException("XPath function \"" + this.name + "\": Invalid number of arguments. Usage: "+this.name+"("+ this.sig +")");
			}
			return invoke(args);
		}
		public abstract Object invoke(final List<?> args);
	}

	private class IsoDate extends Func {
		public IsoDate() {
			super("isoDate", "date", 1, 1);
		}

		@Override
		public Object invoke(final List<?> args) {
			return isoDate(asDate(args.get(0)));
		}
	}

	private class IsoDateTime extends Func {
		public IsoDateTime() {
			super("isoDateTime", "date", 1, 1);
		}

		@Override
		public Object invoke(final List<?> args) {
			return isoDateTime(asDate(args.get(0)));
		}
	}

	private class Decode extends Func {
		public Decode() {
			super("decode", "test [, key, value]+ [, default]?", 3);
		}

		@Override
		public Object invoke(final List<?> args) {
			if (args.size() % 2 == 1) {
				args.add(null);
			}
			final Object ref = args.get(0);
			final List<?> keyValues = args.size() > 1 ? args.subList(1, args.size() - 1) : Collections.emptyList();
			final Object def = args.get(args.size() - 1);
			return decode(ref, keyValues, def);
		}
	}

	private class Coalesce extends Func {
		public Coalesce() {
			super("coalesce", "val+", 1);
		}

		@Override
		public Object invoke(final List<?> args) {
			return coalesce(args);
		}
	}

	private class Format extends Func {
		public Format() {
			super("format", "format:string [, arg]+", 2);
		}

		@Override
		public Object invoke(final List<?> args) {
			return format(asString(args.get(0)), args.subList(1, args.size()));
		}
	}

	private class FormatNumber extends Func {
		public FormatNumber() {
			super("format-number", "number:number, format:string", 2);
		}

		@Override
		public Object invoke(final List<?> args) {
			return formatNumber(asNumber(args.get(0)), asString(args.get(1)));
		}
	}

	private class ToDate extends Func {
		public ToDate() {
			super("date", "val:string", 1, 1);
		}

		@Override
		public Object invoke(final List<?> args) {
			return date(args.get(0));
		}
	}

	private class FormatDate extends Func {
		public FormatDate() {
			super("format-date", "date:dateTime, format:string", 2, 2);
		}

		@Override
		public Object invoke(final List<?> args) {
			return formatDate(asDate(args.get(0)), asString(args.get(1)));
		}
	}

	private class Hash extends Func {
		public Hash() {
			super("hash", "val", 1, 1);
		}

		@Override
		public Object invoke(final List<?> args) {
			return hash(args.get(0));
		}
	}

	private class Decimal extends Func {
		public Decimal() {
			super("decimal", "val", 1, 1);
		}

		@Override
		public Object invoke(final List<?> args) {
			return decimal(args.get(0));
		}
	}

	private static String asString(final Object o) {
		if(o == null) return null;
		if(o instanceof String) return (String)o;
		if(o instanceof Node) return ((Node)o).getTextContent();
		if(o instanceof NodeList) {
			return ((NodeList)o) .getLength() > 0 ? ((NodeList)o) .item(0).getTextContent() : null;
		} else {
			return null;
		}
	}

	private static BigDecimal asNumber(final Object o) {
		if(o == null) return null;
		if(o instanceof BigDecimal) return (BigDecimal)o;
		return new BigDecimal(asString(o));
	}

	private static Date asDate(final Object o) {
		if(o == null) return null;
		if(o instanceof Date) return (Date)o;
		return XPathExtensionFunctions.DATATYPE_FACTORY.newXMLGregorianCalendar(asString(o)).toGregorianCalendar().getTime();
	}

	private static List<String> asStrings(final NodeList nodeList) {
		if(nodeList == null) return null;
		final List<String> stringList = new ArrayList<>();
		for(int i = 0; i < nodeList.getLength(); i++) {
			stringList.add(nodeList.item(i).getTextContent());
		}
		return stringList;
	}

}
