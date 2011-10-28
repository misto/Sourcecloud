package ch.misto.sourcecloud.core;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

public class VisualizeHistory implements IObjectActionDelegate {

	private ArrayList<IResource> selected;

	public VisualizeHistory() {
	}

	@Override
	public void run(final IAction action) {

		if (selected.isEmpty()) {
			return;
		}

		IProject project = selected.get(0).getProject();

		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new VisualizeHistoryWizard(
				project));
		dialog.create();
		dialog.open();
	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		selected = new ArrayList<IResource>();
		if (selection instanceof IStructuredSelection) {
			for (final Object o : ((IStructuredSelection) selection).toArray()) {
				if (o instanceof IResource) {
					selected.add((IResource) o);
				}
			}
		}
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
}
