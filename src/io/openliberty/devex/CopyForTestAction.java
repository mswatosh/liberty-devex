package io.openliberty.devex;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;


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
        	System.out.println("Selection: " + ((ITextSelection) selection).getText());
        return null;
	}

}