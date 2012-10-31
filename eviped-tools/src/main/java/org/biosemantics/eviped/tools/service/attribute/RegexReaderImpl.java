package org.biosemantics.eviped.tools.service.attribute;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class RegexReaderImpl {

	private String regexFile;
	private Properties properties;
	static final String OR = "|";
	static final String SPACE = " ";
	static final String WHITESPACE = "\\s";
	static final String WHITESPACE_ANY = "\\s+";
	static final String FORWARD_SLASH = "/";
	static final String ESCAPED_START_BRACKET = "\\(";
	static final String START_BRACKET = "(";
	static final String ESCAPED_END_BRACKET = "\\)";
	static final String ESCAPED_PLUS = "\\+";
	static final String ESCAPED_MINUS = "\\-";
	static final String END_BRACKET = ")";
	static final String DIGIT = "//d";
	static final String HYPHEN = "-";
	static final String WORD_BOUNDRY = "\\b";
	private String dosageUnit;
	private String quantity;
	private String anyPositiveNumber;
	private String day;

	public RegexReaderImpl(String regexFile) throws FileNotFoundException, IOException {
		this.regexFile = regexFile;
		properties = new Properties();
		properties.load(new FileInputStream(regexFile));
		dosageUnit = START_BRACKET + properties.getProperty("ug") + OR + properties.getProperty("mg") + OR
				+ properties.getProperty("iu") + OR + properties.getProperty("iu") + OR + properties.getProperty("ng")
				+ OR + properties.getProperty("g") + END_BRACKET;
		quantity = START_BRACKET + properties.getProperty("g") + OR + properties.getProperty("kg") + OR
				+ properties.getProperty("d") + OR + properties.getProperty("m2") + OR + properties.getProperty("nl")
				+ OR + properties.getProperty("ml") + END_BRACKET;
		anyPositiveNumber = START_BRACKET + properties.getProperty("positive_decimal") + OR
				+ properties.getProperty("number_with_separator") + OR + properties.getProperty("number_with_space")
				+ OR + DIGIT + END_BRACKET;
		day = START_BRACKET + properties.getProperty("day") + OR + properties.getProperty("week") + OR
				+ properties.getProperty("month") + OR + properties.getProperty("year") + END_BRACKET;
	}

	public String valueOf(String key) {
		return properties.getProperty(key);
	}

	public String getRegexFile() {
		return regexFile;
	}

	public String getDosageUnit() {
		return dosageUnit;
	}

	public String getQuantity() {
		return quantity;
	}

	public String getAnyPositiveNumber() {
		return anyPositiveNumber;
	}

	public String getDay() {
		return day;
	}
	
	

}
