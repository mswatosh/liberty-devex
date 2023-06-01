package io.openliberty.devex;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.text.BlockTextSelection;
import org.eclipse.jface.text.IMarkSelection;
import org.eclipse.jface.text.IMultiTextSelection;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;


/**
 * CopyForTestAction takes the selected text and copies a gradle command to clipboard to run this as a test in OpenLiberty.
 * @author mswatosh
 *
 */
public class CopyForTestAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getSelectionService().getSelection();
        if (selection instanceof ITextSelection)
        	System.out.println("ITextSelection: " + ((ITextSelection) selection).getText());
        //if (selection instanceof ITreeSelection)
        //	System.out.println("ITreeSelection: " + ((ITreeSelection) selection).getPaths().toString());
        if (selection instanceof IStructuredSelection) {
        	IStructuredSelection ss = (IStructuredSelection) selection;
        	System.out.println("IStructuredSelection: " + ss.getFirstElement().getClass().toString());
        	if (ss.getFirstElement() instanceof IJavaProject) {
        		IJavaProject jp = (IJavaProject) ss.getFirstElement();
        		System.out.println("IJavaProject: " +jp.getElementName());
        		try {
					System.out.println("IJavaProject: " + jp.getRequiredProjectNames()[0]);
				} catch (JavaModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        	System.out.println("IStructuredSelection: " + ((IStructuredSelection) selection).getFirstElement().toString());
        	//System.out.println("IStructuredSelection: " + ((IStructuredSelection) selection).getFirstElement().getClass().toString());
        	
        }
        if (selection instanceof IMarkSelection)
        	System.out.println("IMarkSelection: " + ((IMarkSelection) selection).getDocument().get());
        //if (selection instanceof IMultiTextSelection)
        //	System.out.println("IMultiTextSelection: " + ((IMultiTextSelection) selection).getText());
        //if (selection instanceof BlockTextSelection)
        //	System.out.println("BlockTextSelection: " + ((BlockTextSelection) selection).getText());
        return null;
	}

}