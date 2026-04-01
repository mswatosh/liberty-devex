package io.openliberty.devex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * CopyForTestMethodAction copies a gradle command to clipboard to run a specific test method in OpenLiberty.
 * @author mswatosh
 */
public class CopyForTestMethodAction extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IMethod method = null;
		
		// Try to get method from structured selection (e.g., Outline view)
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getSelectionService().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			if (ss.getFirstElement() instanceof IMethod) {
				method = (IMethod) ss.getFirstElement();
			}
		}
		
		// Try to get method from active editor if not found in selection
		if (method == null) {
			try {
				IEditorPart editor = HandlerUtil.getActiveEditor(event);
				if (editor != null) {
					IJavaElement element = JavaUI.getEditorInputJavaElement(editor.getEditorInput());
					if (selection instanceof ITextSelection && element != null) {
						ITextSelection textSelection = (ITextSelection) selection;
						// element should be an ICompilationUnit, which has getElementAt()
						if (element instanceof ICompilationUnit) {
							ICompilationUnit cu = (ICompilationUnit) element;
							IJavaElement selectedElement = cu.getElementAt(textSelection.getOffset());
							if (selectedElement instanceof IMethod) {
								method = (IMethod) selectedElement;
							} else if (selectedElement != null) {
								// If we're inside a method but not directly on it, try to find the enclosing method
								IJavaElement parent = selectedElement.getAncestor(IJavaElement.METHOD);
								if (parent instanceof IMethod) {
									method = (IMethod) parent;
								}
							}
						}
					}
				}
			} catch (Exception e) {
				// Silently fail - we'll just not execute if we can't find the method
			}
		}
		
		// Execute the action if we found a method
		if (method != null) {
			IJavaProject javaProject = method.getJavaProject();
			String projectName = javaProject.getElementName();
			String methodName = method.getElementName();
			
			// Check if this is a FAT test project
			if (isFatProject(javaProject)) {
				// Build the gradle command with the test method parameter
				String gradleCommand = "./gradlew " + projectName + ":buildandrun -Dfat.test.method.name=" + methodName;
				
				// Copy to clipboard
				copyToClipboard(gradleCommand);
			}
		}
		
		return null;
	}
	
	/**
	 * Check if the project is a FAT project by reading bnd.bnd file.
	 * @param project The Java project to check
	 * @return true if bnd.bnd exists and contains "fat.project: true"
	 */
	private boolean isFatProject(IJavaProject project) {
		try {
			IFile bndFile = project.getProject().getFile("bnd.bnd");
			if (!bndFile.exists()) {
				return false;
			}
			
			try (InputStream is = bndFile.getContents();
			     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					if (line.startsWith("fat.project:")) {
						String value = line.substring("fat.project:".length()).trim();
						return "true".equalsIgnoreCase(value);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Copy the given text to the system clipboard.
	 * @param text The text to copy
	 */
	private void copyToClipboard(String text) {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		
		Clipboard clipboard = new Clipboard(display);
		try {
			TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new Object[] { text }, new Transfer[] { textTransfer });
		} finally {
			clipboard.dispose();
		}
	}
}

// Made with Bob
