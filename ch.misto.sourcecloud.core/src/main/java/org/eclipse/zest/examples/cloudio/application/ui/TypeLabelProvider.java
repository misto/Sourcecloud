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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.zest.cloudio.ICloudLabelProvider;

/**
 * 
 * @author sschwieb
 * 
 */
public class TypeLabelProvider extends BaseLabelProvider implements ICloudLabelProvider {

	private double maxOccurrences;
	private double minOccurrences;

	private Map<Object, FontData[]> fonts = new HashMap<Object, FontData[]>();
	private Random random = new Random();
	protected List<Color> colorList;
	protected List<Font> fontList;
	protected List<Float> angles;

	public TypeLabelProvider() {
		colorList = new ArrayList<Color>();
		fontList = new ArrayList<Font>();
		angles = new ArrayList<Float>();
		angles.add(0F);
	}

	@Override
	public String getLabel(Object element) {
		return ((CloudEntry) element).getString();
	}

	@Override
	public double getWeight(Object element) {
		double count = (((CloudEntry) element).getValue() - minOccurrences + 1);
		count /= ((maxOccurrences));
		if (0 <= count && count <= 1)
			return count;
		else
			return 1;
	}

	@Override
	public Color getColor(Object element) {
		return ((CloudEntry) element).getColor();
	}

	public FontData[] getFontData(Object element) {
		FontData[] data = fonts.get(element);
		if (data == null) {
			data = fontList.get(random.nextInt(fontList.size())).getFontData();
			fonts.put(element, data);
		}
		return data;
	}

	public void setMaxOccurrences(double occurrences) {
		this.maxOccurrences = occurrences;
	}

	public void setMinOccurrences(double occurrences) {
		this.minOccurrences = occurrences;
	}

	@Override
	public void dispose() {
		for (Color color : colorList) {
			color.dispose();
		}
		for (Font font : fontList) {
			font.dispose();
		}
	}

	public void setAngles(List<Float> angles) {
		this.angles = angles;
	}

	@Override
	public float getAngle(Object element) {
		float angle = angles.get(random.nextInt(angles.size()));
		return angle;
	}

	public void setFonts(List<FontData> newFonts) {
		if (newFonts.isEmpty())
			return;
		for (Font font : fontList) {
			font.dispose();
		}
		fontList.clear();
		fonts.clear();
		for (FontData data : newFonts) {
			Font f = new Font(Display.getDefault(), data);
			fontList.add(f);
		}
	}

	@Override
	public String getToolTip(Object element) {
		return getLabel(element) + ": " + ((CloudEntry) element).getValue();
	}

}
