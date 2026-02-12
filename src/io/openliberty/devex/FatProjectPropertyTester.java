package io.openliberty.devex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;

/**
 * Property tester to check if a project is a FAT test project.
 * Checks for bnd.bnd file with "fat.project: true" line.
 */
public class FatProjectPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (!"isFatProject".equals(property)) {
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
		
		return isFatProject(project);
	}

	/**
	 * Check if the project is a FAT project by reading bnd.bnd file.
	 * @param project The project to check
	 * @return true if bnd.bnd exists and contains "fat.project: true"
	 */
	private boolean isFatProject(IProject project) {
		try {
			IFile bndFile = project.getFile("bnd.bnd");
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
			// Silently fail - property tester should not throw exceptions
		}
		return false;
	}
}

// Made with Bob
