package io.openliberty.devex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;

/**
 * Property tester to check if a project is a source project.
 * Checks for bnd.bnd file WITHOUT "fat.project: true" line.
 */
public class SourceProjectPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (!"isSourceProject".equals(property)) {
			return false;
		}
		
		IProject project = null;
		if (receiver instanceof IJavaProject) {
			IJavaProject javaProject = (IJavaProject) receiver;
			project = javaProject.getProject();
		} else if (receiver instanceof IProject) {
			project = (IProject) receiver;
		}
		
		if (project == null) {
			return false;
		}
		
		return isSourceProject(project);
	}

	/**
	 * Check if the project is a source project by reading bnd.bnd file.
	 * @param project The project to check
	 * @return true if bnd.bnd exists and does NOT contain "fat.project: true"
	 */
	private boolean isSourceProject(IProject project) {
		try {
			IFile bndFile = project.getFile("bnd.bnd");
			if (!bndFile.exists()) {
				return false;
			}
			
			// Check if it has fat.project: true
			try (InputStream is = bndFile.getContents();
			     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
				String line;
				while ((line = reader.readLine()) != null) {
					line = line.trim();
					// Skip empty lines and comments
					if (line.isEmpty() || line.startsWith("#")) {
						continue;
					}
					if (line.startsWith("fat.project:")) {
						String value = line.substring("fat.project:".length()).trim();
						// Return true only if fat.project is NOT true
						return !"true".equalsIgnoreCase(value);
					}
				}
			}
			// If bnd.bnd exists but doesn't have fat.project line, it's a source project
			return true;
		} catch (Exception e) {
			// Silently fail - property tester should not throw exceptions
		}
		return false;
	}
}

// Made with Bob