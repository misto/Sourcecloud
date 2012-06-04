package ch.misto.sourcecloud.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

public class SelectVersionsWizardPage extends WizardPage {

	private final IProject project;
	private final SelectionListener selectionListener;

	public SelectVersionsWizardPage(IProject project, SelectionListener selectionListener) {
		super("wizardPage");
		this.project = project;
		this.selectionListener = selectionListener;
		setTitle("Wizard Page title");
		setDescription("Wizard Page description");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(1, false));
		
		Label lblPleaseSelectThe = new Label(container, SWT.NONE);
		lblPleaseSelectThe.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblPleaseSelectThe.setText("Please select the versions you want to visualize:");
		
		final List list = new List(container, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		for(Ref ref : GitRepoAccess.getAllTags(project).values()) {
			list.add(ref.getName());
		}
		
		list.addSelectionListener(selectionListener);
	}
}
