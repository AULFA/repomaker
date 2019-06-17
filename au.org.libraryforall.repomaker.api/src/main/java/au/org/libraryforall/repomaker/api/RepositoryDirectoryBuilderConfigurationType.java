package au.org.libraryforall.repomaker.api;

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.net.URI;
import java.nio.file.Path;
import java.util.OptionalInt;
import java.util.UUID;

/**
 * Configuration information for the directory builder.
 */

@ImmutablesStyleType
@Value.Immutable
public interface RepositoryDirectoryBuilderConfigurationType
{
  /**
   * @return The directory containing APK files
   */

  Path path();

  /**
   * @return The URI of the generated repository
   */

  URI self();

  /**
   * @return The UUID used to identify the repository
   */

  UUID uuid();

  /**
   * @return The title of the repository
   */

  String title();

  /**
   * A limit on the number of releases of each APK that should appear in repositories.
   *
   * @return A limit on the number of releases
   */

  OptionalInt limitReleases();
}
