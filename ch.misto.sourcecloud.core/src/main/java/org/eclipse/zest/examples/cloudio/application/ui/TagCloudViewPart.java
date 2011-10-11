/*******************************************************************************
 * Copyright (c) 2011 Stephan Schwiebert. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Stephan Schwiebert - initial API and implementation
 *******************************************************************************/
package org.eclipse.zest.examples.cloudio.application.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.cloudio.TagCloud;
import org.eclipse.zest.cloudio.TagCloudViewer;
import org.eclipse.zest.cloudio.Word;
import org.eclipse.zest.cloudio.layout.DefaultLayouter;
import org.eclipse.zest.cloudio.layout.ILayouter;
import org.eclipse.zest.examples.cloudio.application.data.Type;

public class TagCloudViewPart extends ViewPart {

	private TagCloudViewer viewer1;
	private TagCloudViewer viewer2;
	private TagCloudViewer viewer3;
	private TagCloud cloud;
	private TypeLabelProvider labelProvider;

	public TagCloudViewPart() {
	}

	@Override
	public void createPartControl(Composite parent) {
		ScrolledComposite cloudComp = new ScrolledComposite(parent,
				SWT.H_SCROLL);
		cloudComp.setExpandHorizontal(true);
		cloudComp.setExpandVertical(true);
		cloudComp.setMinWidth(1000);
		final Composite c2 = new Composite(cloudComp, SWT.NONE);
		cloudComp.setContent(c2);

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing = 100;
		c2.setLayout(layout);
		c2.setBackground(c2.getDisplay().getSystemColor(SWT.COLOR_BLACK));

		final ArrayList<Type> types = new ArrayList<Type>();
		types.add(new Type(
				"Use 'Create Tag Cloud' from the context menu to populate.", 1));

		viewer1 = createViewer(c2);
		viewer2 = createViewer(c2);
		viewer3 = createViewer(c2);
		viewer1.setInput(types, new NullProgressMonitor());
	}

	private static TagCloudViewer createViewer(Composite parent) {
		TagCloud cloud = new TagCloud(parent, SWT.NONE);
		final TagCloudViewer viewer = new TagCloudViewer(cloud);

		GridData gridData = new GridData();
		gridData.widthHint = 300;
		gridData.heightHint = 300;
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

				int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;

				for (Object o : list) {
					int occurrences = ((Type) o).getOccurrences();
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

			}

			@Override
			public Object[] getElements(Object inputElement) {
				Type[] types = ((List<?>) inputElement).toArray(new Type[0]);
				Arrays.sort(types, new Comparator<Type>() {

					@Override
					public int compare(Type o1, Type o2) {
						return - /* descending! */(o1.getOccurrences() - o2
								.getOccurrences());
					}
				});

				return types;
			}
		});

		cloud.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		cloud.addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				viewer.getCloud().zoomFit();
			}

			@Override
			public void controlMoved(ControlEvent e) {
			}
		});

		cloud.setMaxFontSize(100);
		cloud.setMinFontSize(15);
		labelProvider.setColors(Arrays.asList(new RGB(1, 175, 255), new RGB(57,
				99, 213), new RGB(21, 49, 213), new RGB(30, 125, 42)));
		labelProvider.setFonts(Arrays.asList(cloud.getFont().getFontData()[0]));
		viewer.setMaxWords(50);
		return viewer;
	}

	@Override
	public void setFocus() {
		// viewer1.getCloud().setFocus();
	}

	@Override
	public void dispose() {
		// viewer1.getCloud().dispose();
		// labelProvider.dispose();
	}

	public void setInput(ArrayList<Type> types, IProgressMonitor pm) {

		// cloud.setWords(Arrays.asList(
		// createWord(labelProvider, "Hello", 50),
		// createWord(labelProvider, "World", 100)), pm);
		//
		// cloud.layoutCloud(pm, true);
		// cloud.zoomFit();

		viewer1.setInput(types, pm);
		viewer2.setInput(types, pm);
		viewer3.setInput(types, pm);
	}
}
