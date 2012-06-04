/*******************************************************************************
 * Copyright (c) 2011 Stephan Schwiebert. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Stephan Schwiebert - initial API and implementation
 *******************************************************************************/
package org.eclipse.zest.examples.cloudio.application.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.cloudio.TagCloud;
import org.eclipse.zest.cloudio.TagCloudViewer;
import org.eclipse.zest.cloudio.layout.DefaultLayouter;

public class TagCloudViewPart extends ViewPart {

	private ArrayList<TagCloudViewer> viewers = new ArrayList<TagCloudViewer>();
	private ScrolledComposite scrolledComposite;

	public TagCloudViewPart() {
	}

	@Override
	public void createPartControl(Composite parent) {
		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setLayout(new GridLayout(1, true));
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setMinWidth(1000);
		scrolledComposite.setAlwaysShowScrollBars(true);
	}

	private static TagCloudViewer createViewer(Composite parent) {
		TagCloud cloud = new TagCloud(parent, SWT.NONE);
		final TagCloudViewer viewer = new TagCloudViewer(cloud) {
//			@Override
//			protected void initMouseWheelListener() {
//			}
		};

		GridData gridData = new GridData();
		gridData.widthHint = 500;
		gridData.heightHint = 500;
		gridData.minimumWidth = 300;
		gridData.minimumHeight = 300;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		cloud.setLayoutData(gridData);

		cloud.setLayouter(new DefaultLayouter(20, 10));
		final TypeLabelProvider labelProvider = new TypeLabelProvider();
		viewer.setLabelProvider(labelProvider);
		viewer.setContentProvider(new IStructuredContentProvider() {

			@Override
			public void inputChanged(Viewer v, Object oldInput, Object newInput) {
				List<?> list = (List<?>) newInput;
				if (list == null || list.size() == 0)
					return;

				double max = Double.MIN_VALUE, min = Double.MAX_VALUE;

				for (Object o : list) {
					double occurrences = ((CloudEntry) o).getValue();
					if (occurrences > max) {
						max = occurrences;
					} else if (occurrences < min) {
						min = occurrences;
					}
				}

				labelProvider.setMaxOccurrences(max);
				labelProvider.setMinOccurrences(min);
			}

			@Override
			public void dispose() {
				labelProvider.dispose();
			}

			@Override
			public Object[] getElements(Object inputElement) {
				CloudEntry[] types = ((List<?>) inputElement).toArray(new CloudEntry[0]);
				Arrays.sort(types, new Comparator<CloudEntry>() {

					@Override
					public int compare(CloudEntry o1, CloudEntry o2) {
						return - /* descending! */(int) (o1.getValue() - o2.getValue());
					}
				});

				return types;
			}
		});

		cloud.setMaxFontSize(100);
		cloud.setMinFontSize(15);
		labelProvider.setFonts(Arrays.asList(cloud.getFont().getFontData()[0]));
		viewer.setMaxWords(30);
		viewer.getCloud().setBackground(viewer.getCloud().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		return viewer;
	}

	@Override
	public void setFocus() {
		scrolledComposite.setFocus();
	}

	@Override
	public void dispose() {
		removeCloudViewers();
	}

	public void setInput(ArrayList<ArrayList<CloudEntry>> types, ArrayList<String> labels, IProgressMonitor pm) {

		removeCloudViewers();

		Composite content = new Composite(scrolledComposite, SWT.NONE);
		content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		scrolledComposite.setContent(content);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 50;
		layout.numColumns = types.size();

		content.setLayout(layout);
		content.setBackground(content.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		for (String l : labels) {
			Label label = new Label(content, SWT.None);
			//label.setForeground(content.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			label.setBackground(content.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			GridData gridData = new GridData();
			gridData.horizontalAlignment = SWT.CENTER;
			label.setLayoutData(gridData);
			label.setText("" + l);
		}

		for (ArrayList<CloudEntry> cloudData : types) {
			TagCloudViewer viewer = createViewer(content);
			viewer.setInput(cloudData, pm);
			viewers.add(viewer);
			pm.worked(1);
		}

		scrolledComposite.setMinSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		for (TagCloudViewer view : viewers) {
			view.zoomFit();
		}
		
		content.layout();
		scrolledComposite.layout();
	}

	private void removeCloudViewers() {
		for (TagCloudViewer view : viewers) {
			view.getCloud().dispose();
		}

		viewers.clear();
	}

	public static void showInView(final IProgressMonitor pm, final ArrayList<ArrayList<CloudEntry>> types, final ArrayList<String> labels) {
		try {
			IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView("ch.misto.sourcecloud.core.view1");

			pm.beginTask("Creating Clouds:", types.size());

			((TagCloudViewPart) view).setInput(types, labels, pm);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
