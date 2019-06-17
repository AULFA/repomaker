package au.org.libraryforall.repomaker.api;

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;

/**
 * The result of generating a repository.
 */

@ImmutablesStyleType
@Value.Immutable
public interface RepositoryDirectoryBuilderResultType
{
  /**
   * @return The generated repository
   */

  Repository repository();

  /**
   * @return The APK files that were ignored due to release limits
   */

  Set<Path> ignoredAPKs();
}
