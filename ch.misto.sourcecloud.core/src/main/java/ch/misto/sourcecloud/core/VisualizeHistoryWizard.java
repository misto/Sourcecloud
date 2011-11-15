package ch.misto.sourcecloud.core;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.zest.examples.cloudio.application.ui.CloudEntry;
import org.eclipse.zest.examples.cloudio.application.ui.TagCloudViewPart;

import ch.akuhn.hapax.index.LogLikelihood;
import ch.akuhn.hapax.index.Terms;

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

		final ArrayList<Terms> termsForEachTag = new ArrayList<Terms>();

		for (String tag : selectedTags) {
			pm.subTask(tag);

			Repository repository = GitRepoAccess.getRepository(project);
			Map<String, Integer> wordsForTag = countWordsForTag(tag, repository);
			termsForEachTag.add(new Terms(wordsForTag.entrySet()));
			pm.worked(1);
		}

		final ArrayList<ArrayList<CloudEntry>> allEntries = new ArrayList<ArrayList<CloudEntry>>();
		final ArrayList<String> labels = new ArrayList<String>();

		for (int i = 1; i < termsForEachTag.size(); i++) {

			final Terms currentTerms = termsForEachTag.get(i);
			final Terms allTerms = new Terms(currentTerms, termsForEachTag.get(i - 1));

			final ArrayList<CloudEntry> entries = new ArrayList<CloudEntry>();

			for (String t : currentTerms.elementSet()) {

				LogLikelihood logLikelihood = new LogLikelihood(currentTerms, allTerms, t);
				System.out.println(String.format("«%s» has a value of %f", t, logLikelihood.value()));
				RGB rgb = logLikelihood.value() < 0 ? new RGB(107, 0, 29) : new RGB(0, 74, 112);
				if (t.equals("0")) {
					System.out.println(t);
				}
				entries.add(new CloudEntry(t, Math.abs(logLikelihood.value()), new Color(Display.getDefault(), rgb)));
			}

			labels.add(selectedTags.get(i - 1) + " → " + selectedTags.get(i));
			allEntries.add(entries);
		}

		TagCloudViewPart.showInView(new SubProgressMonitor(pm, selectedTags.size(), SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK),
				allEntries, labels);

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
