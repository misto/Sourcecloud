package ch.misto.sourcecloud.core;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;

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
}
