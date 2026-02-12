package io.openliberty.devex;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Property tester to check if a method has the @Test annotation.
 */
public class TestMethodPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (!"hasTestAnnotation".equals(property)) {
			return false;
		}
		
		if (!(receiver instanceof IMethod)) {
			return false;
		}
		
		IMethod method = (IMethod) receiver;
		return hasTestAnnotation(method);
	}

	/**
	 * Check if the method has the @Test annotation.
	 * @param method The method to check
	 * @return true if the method has @Test annotation
	 */
	private boolean hasTestAnnotation(IMethod method) {
		try {
			IAnnotation[] annotations = method.getAnnotations();
			for (IAnnotation annotation : annotations) {
				String annotationName = annotation.getElementName();
				// Check for both simple name "Test" and fully qualified name
				if ("Test".equals(annotationName) || "org.junit.Test".equals(annotationName)) {
					return true;
				}
			}
		} catch (JavaModelException e) {
			// Silently fail - property tester should not throw exceptions
		}
		return false;
	}
}

// Made with Bob
