package io.openliberty.devex;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * Property tester to check if the active editor's current selection is on a method with @Test annotation.
 */
public class ActiveEditorMethodPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (!"hasTestAnnotationAtCursor".equals(property)) {
			return false;
		}
		
		try {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window == null) {
				return false;
			}
			
			IWorkbenchPage page = window.getActivePage();
			if (page == null) {
				return false;
			}
			
			IEditorPart editor = page.getActiveEditor();
			if (editor == null) {
				return false;
			}
			
			IEditorInput editorInput = editor.getEditorInput();
			IJavaElement element = JavaUI.getEditorInputJavaElement(editorInput);
			
			if (!(element instanceof ICompilationUnit)) {
				return false;
			}
			
			ICompilationUnit cu = (ICompilationUnit) element;
			
			// Get the text selection from the editor
			if (receiver instanceof ITextSelection) {
				ITextSelection textSelection = (ITextSelection) receiver;
				IJavaElement selectedElement = cu.getElementAt(textSelection.getOffset());
				
				// Only check if the selected element is directly a method declared in this compilation unit
				if (selectedElement instanceof IMethod) {
					IMethod method = (IMethod) selectedElement;
					
					// Verify the method is declared in the current compilation unit (not a reference to another method)
					if (method.getCompilationUnit() != null && method.getCompilationUnit().equals(cu)) {
						// Check if the cursor is on the method name itself, not inside the method body
						// The method's name range is where the method name appears in the declaration
						int offset = textSelection.getOffset();
						int nameStart = method.getNameRange().getOffset();
						int nameEnd = nameStart + method.getNameRange().getLength();
						
						// Only show menu if cursor is on the method name
						if (offset >= nameStart && offset <= nameEnd) {
							return hasTestAnnotation(method);
						}
					}
				}
			}
		} catch (Exception e) {
			// Silently fail - property tester should not throw exceptions
		}
		
		return false;
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
				// Check for both simple name "Test" and fully qualified names for JUnit 4 and 5
				if ("Test".equals(annotationName) ||
				    "org.junit.Test".equals(annotationName) ||
				    "org.junit.jupiter.api.Test".equals(annotationName)) {
					return true;
				}
			}
		} catch (JavaModelException e) {
			// Silently fail
		}
		return false;
	}
}

// Made with Bob
