package org.biosemantics.eviped.tools.service.attribute;

import java.util.Iterator;
import java.util.List;

import org.biosemantics.eviped.tools.service.Annotation;

public class CommonUtility {
	
	public static void addAvoidingOverlap(List<Annotation> annotations, Annotation annotation) {
		boolean addToList = true;
		Iterator<Annotation> iterator = annotations.iterator();
		while (iterator.hasNext()) {
			Annotation availableAnnotation = iterator.next();
			if (availableAnnotation.getStartPos() <= annotation.getStartPos()
					&& availableAnnotation.getEndPos() >= annotation.getEndPos()) {
				// found a bigger annotation: do not add to list
				addToList = false;
				break;
			} else if (annotation.getStartPos() <= availableAnnotation.getStartPos()
					&& annotation.getEndPos() >= availableAnnotation.getEndPos()) {
				// remove from list
				iterator.remove();
			}
		}
		if (addToList) {
			annotations.add(annotation);
		}

	}

}
