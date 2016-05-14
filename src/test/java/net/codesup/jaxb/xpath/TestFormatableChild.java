package net.codesup.jaxb.xpath;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by klemm0 on 2015-01-22.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name ="test-formatable-child", propOrder = {"name", "displayName", "value", "spacing", "underwritingAmount"})
public class TestFormatableChild {
	@XmlAttribute
	private String name;
	@XmlAttribute(name="display-name")
	private String displayName;
	@XmlElement
	private BigInteger value;
	@XmlElement
	private BigDecimal spacing;
	@XmlElement(name = "underwriting-amount")
	private double underwritingAmount;

	public TestFormatableChild(final String name, final String displayName, final BigInteger value, final BigDecimal spacing, final double underwritingAmount) {
		this.name = name;
		this.displayName = displayName;
		this.value = value;
		this.spacing = spacing;
		this.underwritingAmount = underwritingAmount;
	}

	public TestFormatableChild() {
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(final String displayName) {
		this.displayName = displayName;
	}

	public BigInteger getValue() {
		return this.value;
	}

	public void setValue(final BigInteger value) {
		this.value = value;
	}

	public BigDecimal getSpacing() {
		return this.spacing;
	}

	public void setSpacing(final BigDecimal spacing) {
		this.spacing = spacing;
	}

	public double getUnderwritingAmount() {
		return this.underwritingAmount;
	}

	public void setUnderwritingAmount(final double underwritingAmount) {
		this.underwritingAmount = underwritingAmount;
	}
}
