package org.biosemantics.eviped.tools.service.attribute;

import static org.biosemantics.eviped.tools.service.attribute.RegexReaderImpl.WORD_BOUNDRY;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.biosemantics.eviped.tools.service.Annotation;
import org.biosemantics.eviped.tools.service.AnnotationTypeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CountryRegexImpl implements AttributeExtractorService {

	private List<Pattern> countryPatterns = new ArrayList<Pattern>();

	private String[] missingValues = new String[] { "Europe", "Asia", "America", "Africa", "North America",
			"South America", "Australia", "UK" };

	private static final Logger logger = LoggerFactory.getLogger(CountryRegexImpl.class);

	public void init() {
		Locale[] locales = Locale.getAvailableLocales();
		Set<String> patternStrings = new HashSet<String>();
		for (Locale locale : locales) {
//			if (!StringUtils.isBlank(locale.getISO3Country())) {
//				patternStrings.add(locale.getISO3Country());
//			}
			if (!StringUtils.isBlank(locale.getCountry())) {
				patternStrings.add(locale.getCountry());
			}
			if (!StringUtils.isBlank(locale.getDisplayCountry())) {
				patternStrings.add(locale.getDisplayCountry());
			}
		}
		for (String missingValue : missingValues) {
			patternStrings.add(missingValue);
		}

		for (String string : patternStrings) {
			countryPatterns.add(Pattern.compile(WORD_BOUNDRY + string + WORD_BOUNDRY, Pattern.CASE_INSENSITIVE));
		}

		for (Pattern pattern : countryPatterns) {
			logger.info("{}", pattern.pattern());
		}

	}

	@Override
	public List<Annotation> getAnnotations(String text, int sentenceNumber) {
		List<Annotation> annotations = new ArrayList<Annotation>();
		for (Pattern dosagePattern : countryPatterns) {
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
