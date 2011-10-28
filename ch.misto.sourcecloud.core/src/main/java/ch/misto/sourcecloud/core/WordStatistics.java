package ch.misto.sourcecloud.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.zest.examples.cloudio.application.ui.CloudEntry;

public class WordStatistics {

	private static final Pattern PATTERN = Pattern.compile("\\w+");

	public static Map<String, Integer> extractWordsFromFiles(final ArrayList<InputStream> files, final IProgressMonitor pm) {
		final Map<String, Integer> stats = new HashMap<String, Integer>();

		for (InputStream in : files) {

			if (!pm.isCanceled()) {
				reduce(findWordsInFile(in), stats);
				pm.worked(1);
			}
		}
		return stats;
	}

	public static ArrayList<IFile> findAllFiles(Collection<IResource> selected) {
		final ArrayList<IFile> files = new ArrayList<IFile>();

		for (IResource r : selected) {
			try {
				r.accept(new IResourceVisitor() {

					private boolean shouldResourceBeIncludedInVisualization(IResource r) {
						return r instanceof IFile && !r.isHidden(IResource.CHECK_ANCESTORS) && !r.isDerived() && r.isAccessible();
					}

					@Override
					public boolean visit(IResource r) throws CoreException {
						if (shouldResourceBeIncludedInVisualization(r)) {
							files.add((IFile) r);
							return false;
						} else {
							return true;
						}
					}
				});
			} catch (CoreException e) {
			}
		}
		return files;
	}

	public static ArrayList<String> findWordsInFile(InputStream in) {
		final ArrayList<String> words = new ArrayList<String>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line;
			while ((line = reader.readLine()) != null) {
				Matcher matcher = PATTERN.matcher(line);
				while (matcher.find()) {
					words.add(matcher.group());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return words;
	}

	public static ArrayList<CloudEntry> mapToInputElements(final Map<String, Integer> stats) {
		final ArrayList<CloudEntry> types = new ArrayList<CloudEntry>();

		for (Entry<String, Integer> e : stats.entrySet()) {
			types.add(new CloudEntry(e.getKey(), e.getValue()));
		}
		return types;
	}

	public static void reduce(final ArrayList<String> words, final Map<String, Integer> stats) {
		for (String w : words) {
			stats.put(w, (stats.containsKey(w) ? stats.get(w) : 0) + 1);
		}
	}
}
