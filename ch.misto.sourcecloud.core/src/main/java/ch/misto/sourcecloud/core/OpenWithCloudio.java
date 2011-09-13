package ch.misto.sourcecloud.core;

import java.io.BufferedReader;
import java.io.IOException;
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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.examples.cloudio.application.data.Type;
import org.eclipse.zest.examples.cloudio.application.ui.TagCloudViewPart;

public class OpenWithCloudio implements IObjectActionDelegate {

  private static final Pattern PATTERN = Pattern.compile("[a-zA-Z]+");

  private static Map<String, Integer> extractWordsFromFiles(
      final ArrayList<IFile> files, final IProgressMonitor pm) {
    final Map<String, Integer> stats = new HashMap<String, Integer>();

    for (IFile f : files) {

      if (!pm.isCanceled()) {
        stats.putAll(reduce(findWordsInFile(f)));

        pm.worked(1);
      }
    }
    return stats;
  };

  private static ArrayList<IFile> findAllFiles(Collection<IResource> selected) {
    final ArrayList<IFile> files = new ArrayList<IFile>();

    for (IResource r : selected) {
      try {
        r.accept(new IResourceVisitor() {

          private boolean shouldResourceBeIncludedInVisualization(IResource r) {
            return r instanceof IFile && !r.isHidden(IResource.CHECK_ANCESTORS)
                && !r.isDerived() && r.isAccessible();
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

  private static ArrayList<String> findWordsInFile(IFile f) {
    final ArrayList<String> words = new ArrayList<String>();

    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(
          f.getContents(/* force */true)));

      String line;
      while ((line = reader.readLine()) != null) {
        Matcher matcher = PATTERN.matcher(line);
        while (matcher.find()) {
          words.add(matcher.group());
        }
      }

    } catch (CoreException e) {
    } catch (IOException e) {
    }
    return words;
  }

  private static ArrayList<Type> mapToInputElements(
      final Map<String, Integer> stats) {
    final ArrayList<Type> types = new ArrayList<Type>();

    for (Entry<String, Integer> e : stats.entrySet()) {
      types.add(new Type(e.getKey(), e.getValue()));
    }
    return types;
  }

  private static Map<String, Integer> reduce(final ArrayList<String> words) {
    final Map<String, Integer> stats = new HashMap<String, Integer>();

    for (String w : words) {
      stats.put(w, (stats.containsKey(w) ? stats.get(w) : 0) + 1);
    }
    return stats;
  }

  private static void showInView(final IProgressMonitor pm,
      final ArrayList<Type> types) {
    try {
      IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
          .getActivePage().showView("ch.misto.sourcecloud.core.view1");

      ((TagCloudViewPart) view).getViewer().setInput(types, pm);

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
    }
  }

  private ArrayList<IResource> selected = new ArrayList<IResource>();

  public OpenWithCloudio() {
  }

  @Override
  public void run(final IAction action) {

    final ArrayList<IFile> files = findAllFiles(selected);

    final ProgressMonitorDialog dialog = new ProgressMonitorDialog(null);
    dialog.setBlockOnOpen(false);
    dialog.open();
    IProgressMonitor pm = dialog.getProgressMonitor();
    pm.beginTask("Analyzing...", files.size());

    final Map<String, Integer> stats = extractWordsFromFiles(files, pm);

    final ArrayList<Type> types = mapToInputElements(stats);
    showInView(pm, types);

    dialog.close();
  }

  @Override
  public void selectionChanged(final IAction action, final ISelection selection) {
    selected = new ArrayList<IResource>();
    if (selection instanceof IStructuredSelection) {
      for (final Object o : ((IStructuredSelection) selection).toArray()) {
        if (o instanceof IResource) {
          selected.add((IResource) o);
        }
      }
    }
  }

  @Override
  public void setActivePart(IAction action, IWorkbenchPart targetPart) {
  }
}
