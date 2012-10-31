package org.biosemantics.eviped.tools.service.attribute;

import static org.biosemantics.eviped.tools.service.attribute.RegexReaderImpl.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.biosemantics.eviped.tools.service.Annotation;
import org.biosemantics.eviped.tools.service.AnnotationTypeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class DosageRegexImpl implements AttributeExtractorService {

	private RegexReaderImpl regexReaderImpl;

	private List<Pattern> dosagePatterns = new ArrayList<Pattern>();

	@Required
	public void setRegexReaderImpl(RegexReaderImpl regexImpl) {
		this.regexReaderImpl = regexImpl;
	}

	private static final Logger logger = LoggerFactory.getLogger(DosageRegexImpl.class);

	public void init() {
		// single|double|triple dose
		dosagePatterns.add(Pattern.compile(
				START_BRACKET + regexReaderImpl.valueOf("single") + OR + regexReaderImpl.valueOf("double") + OR
						+ regexReaderImpl.valueOf("triple") + END_BRACKET + WHITESPACE_ANY
						+ regexReaderImpl.valueOf("dose"), Pattern.CASE_INSENSITIVE));
		// decimal dosage_unit
		dosagePatterns.add(Pattern.compile(
				regexReaderImpl.getAnyPositiveNumber() + WHITESPACE_ANY + regexReaderImpl.getDosageUnit(),
				Pattern.CASE_INSENSITIVE));
		// decimal dosage_unit/quantity
		dosagePatterns.add(Pattern.compile(
				regexReaderImpl.getAnyPositiveNumber() + WHITESPACE_ANY + regexReaderImpl.getDosageUnit()
						+ FORWARD_SLASH + regexReaderImpl.getQuantity(), Pattern.CASE_INSENSITIVE));
		// decimal dosage_unit/quantity/day
		dosagePatterns.add(Pattern.compile(
				regexReaderImpl.getAnyPositiveNumber() + WHITESPACE_ANY + regexReaderImpl.getDosageUnit()
						+ FORWARD_SLASH + regexReaderImpl.getQuantity() + FORWARD_SLASH + regexReaderImpl.getDay(),
				Pattern.CASE_INSENSITIVE));
		// decimal dosage_unit/day
		dosagePatterns.add(Pattern.compile(
				regexReaderImpl.getAnyPositiveNumber() + WHITESPACE_ANY + regexReaderImpl.getDosageUnit()
						+ FORWARD_SLASH + regexReaderImpl.getDay(), Pattern.CASE_INSENSITIVE));
		// decimal-dosage
		dosagePatterns.add(Pattern.compile(
				regexReaderImpl.getAnyPositiveNumber() + HYPHEN + regexReaderImpl.getDosageUnit(),
				Pattern.CASE_INSENSITIVE));
		// Number (Number)dosage_unit/quantity
		dosagePatterns.add(Pattern.compile(regexReaderImpl.getAnyPositiveNumber() + WHITESPACE_ANY
				+ ESCAPED_START_BRACKET + regexReaderImpl.getAnyPositiveNumber() + ESCAPED_END_BRACKET + WHITESPACE_ANY
				+ regexReaderImpl.getDosageUnit() + FORWARD_SLASH + regexReaderImpl.getQuantity(),
				Pattern.CASE_INSENSITIVE));
		// Number +/- Number
		dosagePatterns.add(Pattern.compile(regexReaderImpl.getAnyPositiveNumber() + WHITESPACE_ANY + ESCAPED_PLUS
				+ FORWARD_SLASH + ESCAPED_MINUS + WHITESPACE_ANY + regexReaderImpl.getAnyPositiveNumber(),
				Pattern.CASE_INSENSITIVE));
		for (Pattern pattern : this.dosagePatterns) {
			logger.info("{}", pattern.pattern());
		}

	}

	@Override
	public List<Annotation> getAnnotations(String text, int sentenceNumber) {
		List<Annotation> annotations = new ArrayList<Annotation>();
		for (Pattern dosagePattern : dosagePatterns) {
			Matcher matcher = dosagePattern.matcher(text);
			while (matcher.find()) {
				Annotation annotation = new Annotation(AnnotationTypeConstant.DOSAGE, matcher.start(), matcher.end(),
						0, matcher.group());
				CommonUtility.addAvoidingOverlap(annotations, annotation);
			}
		}
		return annotations;
	}

	
}
