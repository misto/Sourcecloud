package ch.misto.sourcecloud.core;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;

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
		System.out.println(selectedTags);

		return true;
	}
}
