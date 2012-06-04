package org.eclipse.zest.examples.cloudio.application.ui;

import org.eclipse.swt.graphics.Color;

public class CloudEntry {

	private final String string;
	private final double value;
	private final Color color;

	public CloudEntry(String string, double value, Color color) {
		this.string = string;
		this.value = value;
		this.color = color;
	}

	public String getString() {
		return string;
	}

	public double getValue() {
		return value;
	}
	
	public Color getColor() {
		return color;
	}
}
