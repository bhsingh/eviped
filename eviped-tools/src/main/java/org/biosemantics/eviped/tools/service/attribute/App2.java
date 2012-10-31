package org.biosemantics.eviped.tools.service.attribute;

import java.io.IOException;
import java.util.List;

import org.biosemantics.eviped.tools.service.Annotation;
import org.biosemantics.eviped.tools.service.AnnotationReaderImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.CollectionUtils;

public class App2 {

	private static final String ENTITY_TYPE = "Country";
	private static String[] contexts = new String[] { "org/biosemantics/eviped/tools/eviped-attribute-context.xml" };

	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(contexts);
		applicationContext.registerShutdownHook();
		AnnotationReaderImpl annotationReaderImpl = new AnnotationReaderImpl(
				"/Users/bhsingh/code/git/eviped/eviped-tools/src/main/resources/annotation-all.txt");
		List<String> strings = annotationReaderImpl.getAnnotationTextByType(ENTITY_TYPE);
		AttributeExtractorService attributeExtractorService = applicationContext.getBean(CountryRegexImpl.class);
		for (String string : strings) {
			// System.err.println(string);
			List<Annotation> annotations = attributeExtractorService.getAnnotations(string, 0);
			if (!CollectionUtils.isEmpty(annotations)) {
				for (Annotation annotation : annotations) {
					System.out.println(string + "\t" + annotation);
				}
			} else {
				System.err.println(string);
			}
		}

	}
}
