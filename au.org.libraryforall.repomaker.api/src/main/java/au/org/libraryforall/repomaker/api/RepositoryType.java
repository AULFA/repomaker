package au.org.libraryforall.repomaker.api;

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * A repository.
 */

@ImmutablesStyleType
@Value.Immutable
public interface RepositoryType
{
  /**
   * @return The unique ID of the repository
   */

  UUID id();

  /**
   * @return The title of the repository
   */

  String title();

  /**
   * @return The date/time that the repository was last updated
   */

  LocalDateTime updated();

  /**
   * @return The list of packages in the repository
   */

  List<RepositoryPackage> packages();

  /**
   * @return The URI of the repository
   */

  URI self();
}
