package ch.misto.sourcecloud.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class VisualizeHistory implements IObjectActionDelegate {

	private ArrayList<IResource> selected;

	public VisualizeHistory() {
	}

	@Override
	public void run(final IAction action) {

		if (selected.isEmpty()) {
			return;
		}

		IProject project = selected.get(0).getProject();
		RepositoryMapping mapping = RepositoryMapping.getMapping(project);
		if (mapping == null) {

		}

		Repository repository = mapping.getRepository();
		Map<String, Ref> refs = repository.getAllRefs();

		for (Ref ref : refs.values()) {
			try {
				ObjectId lastCommitId = ref.getObjectId();
				RevWalk revWalk = new RevWalk(repository);
				RevCommit commit = revWalk.parseCommit(lastCommitId);
				RevTree tree = commit.getTree();
				TreeWalk treeWalk = new TreeWalk(repository);
				treeWalk.setRecursive(true);
				treeWalk.setFilter(TreeFilter.ALL);
				treeWalk.addTree(tree);

				while (treeWalk.next()) {
					ObjectId objectId = treeWalk.getObjectId(0);
					ObjectLoader loader = repository.open(objectId);
					if (!RawText.isBinary(loader.getBytes())) {
						loader.copyTo(System.out);
					}
				}

			} catch (MissingObjectException e) {
				e.printStackTrace();
			} catch (IncorrectObjectTypeException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void selectionChanged(final IAction action,
			final ISelection selection) {
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
