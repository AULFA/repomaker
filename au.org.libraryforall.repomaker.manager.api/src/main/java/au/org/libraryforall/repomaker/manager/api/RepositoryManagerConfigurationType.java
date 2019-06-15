package au.org.libraryforall.repomaker.manager.api;

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.net.URI;
import java.nio.file.Path;
import java.util.UUID;

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
   * @return The path that will be managed
   */

  Path path();

  /**
   * @return The unique ID of the repository
   */

  UUID id();

  /**
   * @return The title of the repository
   */

  String title();

  /**
   * @return The URI of the repository
   */

  URI self();
}
