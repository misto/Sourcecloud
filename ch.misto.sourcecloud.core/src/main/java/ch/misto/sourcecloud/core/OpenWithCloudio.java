package ch.misto.sourcecloud.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.examples.cloudio.application.ui.CloudEntry;
import org.eclipse.zest.examples.cloudio.application.ui.TagCloudViewPart;

public class OpenWithCloudio implements IObjectActionDelegate {

	private ArrayList<IResource> selected = new ArrayList<IResource>();

	public OpenWithCloudio() {
	}

	@Override
	public void run(final IAction action) {

		final ArrayList<IFile> files = WordStatistics.findAllFiles(selected);

		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(null);
		dialog.setBlockOnOpen(false);
		dialog.open();
		IProgressMonitor pm = dialog.getProgressMonitor();
		pm.beginTask("Analyzing...", files.size());

		final ArrayList<InputStream> inputs = new ArrayList<InputStream>();
		for (IFile file : files) {
			try {
				inputs.add(file.getContents(true));
			} catch (CoreException e) {
			}
		}

		final Map<String, Integer> stats = WordStatistics.extractWordsFromFiles(inputs, pm);

		ArrayList<ArrayList<CloudEntry>> types = new ArrayList<ArrayList<CloudEntry>>();
		types.add(WordStatistics.mapToInputElements(stats));
		TagCloudViewPart.showInView(pm, types, new ArrayList<String>());

		dialog.close();
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
