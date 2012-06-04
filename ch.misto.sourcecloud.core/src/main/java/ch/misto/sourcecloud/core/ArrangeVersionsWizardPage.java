package ch.misto.sourcecloud.core;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import ch.misto.sourcecloud.core.VisualizeHistoryWizard.DataChanged;

public class ArrangeVersionsWizardPage extends WizardPage {

	private final DataChanged callback;
	private List list;

	/**
	 * Create the wizard.
	 * 
	 * @param dataChanged
	 */
	public ArrangeVersionsWizardPage(DataChanged dataChanged) {
		super("wizardPage");
		this.callback = dataChanged;
		setTitle("Wizard Page title");
		setDescription("Wizard Page description");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		list = new List(container, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE);
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));

		Button btnUp = new Button(container, SWT.NONE);
		btnUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = list.getSelectionIndex();
				if (i < 1)
					return;
				String selected = list.getSelection()[0];
				String[] items = list.getItems();
				items[i] = items[i - 1];
				items[i - 1] = selected;
				list.setItems(items);
				list.setSelection(i - 1);
				callback.apply(items);
			}
		});
		btnUp.setText("↑");

		Button btnDown = new Button(container, SWT.NONE);
		btnDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = list.getSelectionIndex();
				if (i >= list.getItemCount() - 1)
					return;
				String selected = list.getSelection()[0];
				String[] items = list.getItems();
				items[i] = items[i + 1];
				items[i + 1] = selected;
				list.setItems(items);
				list.setSelection(i + 1);
				callback.apply(items);
			}
		});
		btnDown.setText("↓");
		new Label(container, SWT.NONE);
	}

	public void setData(String[] data) {
		list.setItems(data);
	}
}
