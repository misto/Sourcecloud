package ch.akuhn.hapax.index;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.zest.examples.cloudio.application.ui.CloudEntry;

public class Terms {

	private final Map<String, Integer> entries = new HashMap<String, Integer>();
	private final int size;

	public Terms(Collection<Entry<String, Integer>> entries) {
		int size = 0;
		for (Entry<String, Integer> entry : entries) {
			this.entries.put(entry.getKey(), entry.getValue());
			size += entry.getValue();
		}
		this.size = size;
	}

	public Terms(Terms t1, Terms t2) {
		int size = 0;
		entries.putAll(t1.entries);
		for (Entry<String, Integer> e : t2.entries.entrySet()) {
			Integer n = entries.containsKey(e.getKey()) ? entries.get(e.getKey()) : 0;
			size += e.getValue() + n;
			entries.put(e.getKey(), e.getValue() + n);
		}
		this.size = size;
	}

	public int occurrences(String term) {
		return entries.get(term);
	}

	public double size() {
		return size;
	}

	public Set<String> elementSet() {
		return entries.keySet();
	}

}
