package org.eclipse.zest.examples.cloudio.application.ui;

public class CloudEntry {

	private final String string;
	private final int occurrences;

	public CloudEntry(String string, int occurrences) {
		this.string = string;
		this.occurrences = occurrences;
	}

	public String getString() {
		return string;
	}

	public int getOccurrences() {
		return occurrences;
	}
}
