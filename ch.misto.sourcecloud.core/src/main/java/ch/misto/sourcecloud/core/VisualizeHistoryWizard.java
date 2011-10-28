package ch.misto.sourcecloud.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.zest.examples.cloudio.application.ui.CloudEntry;
import org.eclipse.zest.examples.cloudio.application.ui.TagCloudViewPart;

public class VisualizeHistoryWizard extends Wizard {

	private final IProject project;
	private final ArrayList<String> selectedTags = new ArrayList<String>();

	public VisualizeHistoryWizard(IProject project) {
		this.project = project;
		setWindowTitle("Visualize History");
	}

	@Override
	public void addPages() {
		addPage(new SelectVersionsWizardPage(project, new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selection = ((List) e.widget).getSelection();
				selectedTags.clear();
				selectedTags.addAll(Arrays.asList(selection));
			}
		}));
	}

	@Override
	public boolean performFinish() {

		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(null);
		dialog.setBlockOnOpen(false);
		dialog.open();
		IProgressMonitor pm = dialog.getProgressMonitor();
		pm.beginTask("Analyzing...", selectedTags.size() * 2);

		final ArrayList<ArrayList<CloudEntry>> types = new ArrayList<ArrayList<CloudEntry>>();

		for (String tag : selectedTags) {
			pm.subTask(tag);

			Repository repository = GitRepoAccess.getRepository(project);
			types.add(WordStatistics.mapToInputElements(countWordsForTag(tag, repository)));

			pm.worked(1);
		}

		TagCloudViewPart.showInView(new SubProgressMonitor(pm, selectedTags.size(), SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK), types, selectedTags);

		dialog.close();

		return true;
	}

	private Map<String, Integer> countWordsForTag(String tag, Repository repository) {
		ArrayList<ObjectId> objects = GitRepoAccess.collectObjectsForTag(repository, tag);
		final Map<String, Integer> stats = new HashMap<String, Integer>();
		for (ObjectId objectId : objects) {
			try {
				ObjectLoader loader = repository.open(objectId);
				if (!RawText.isBinary(loader.getCachedBytes())) {
					WordStatistics.reduce(WordStatistics.findWordsInFile(loader.openStream()), stats);
				}
			} catch (MissingObjectException e) {
			} catch (IOException e) {
			}
		}
		return stats;
	}
}
