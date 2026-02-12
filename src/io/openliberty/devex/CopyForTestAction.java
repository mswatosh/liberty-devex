package io.openliberty.devex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;


/**
 * CopyForTestAction takes the selected text and copies a gradle command to clipboard to run this as a test in OpenLiberty.
 * @author mswatosh
 *
 */
public class CopyForTestAction extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
	       ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getSelectionService().getSelection();
	       
	       if (selection instanceof IStructuredSelection) {
	       	IStructuredSelection ss = (IStructuredSelection) selection;
	       	if (ss.getFirstElement() instanceof IJavaProject) {
	       		IJavaProject jp = (IJavaProject) ss.getFirstElement();
	       		String projectName = jp.getElementName();
	       		
	       		// Check if this is a FAT test project by checking for bnd.bnd with fat.project: true
	       		if (isFatProject(jp)) {
	       			// Build the gradle command
	       			String gradleCommand = "./gradlew " + projectName + ":buildandrun";
	       			
	       			// Copy to clipboard
	       			copyToClipboard(gradleCommand);
	       		}
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