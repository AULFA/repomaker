package au.org.libraryforall.repomaker.manager.api;

import au.org.libraryforall.repomaker.api.RepositoryDirectoryBuilderConfiguration;
import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

/**
 * Configuration data for a repository manager.
 *
 * A repository manager watches a single directory and generates a repository definition each time
 * the directory changes. Subdirectories are not watched. By convention, repository manager
 * implementations generate a file {@code releases.xml} inside the directory.
 */

@ImmutablesStyleType
@Value.Immutable
public interface RepositoryManagerConfigurationType
{
  /**
   * @return The directory-based repository builder configuration
   */

  RepositoryDirectoryBuilderConfiguration builderConfiguration();
}
