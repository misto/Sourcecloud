package ch.misto.sourcecloud.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.egit.core.project.RepositoryMapping;
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

public class GitRepoAccess {

	public static Map<String, Ref> getAllTags(IProject project) {
		Repository repository = getRepository(project);

		if (repository == null) {
			return Collections.emptyMap();
		}

		return repository.getAllRefs();
	}

	public static Repository getRepository(IProject project) {
		RepositoryMapping mapping = RepositoryMapping.getMapping(project);

		if (mapping == null) {
			return null;
		}

		return mapping.getRepository();
	}

	public static ArrayList<ObjectId> collectObjectsForTag(Repository repository, String tag) {
		
		ArrayList<ObjectId> result = new ArrayList<ObjectId>();

		try {
			Ref ref = repository.getRef(tag);
			ObjectId lastCommitId = ref.getObjectId();
			RevWalk revWalk = new RevWalk(repository);
			RevCommit commit = revWalk.parseCommit(lastCommitId);
			RevTree tree = commit.getTree();
			TreeWalk treeWalk = new TreeWalk(repository);
			treeWalk.setRecursive(true);
			treeWalk.setFilter(TreeFilter.ALL);
			treeWalk.addTree(tree);

			while (treeWalk.next()) {
				result.add(treeWalk.getObjectId(0));
			}

		} catch (MissingObjectException e) {
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
