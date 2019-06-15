package au.org.libraryforall.repomaker.api;

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.net.URI;

/**
 * A package in a repository.
 */

@ImmutablesStyleType
@Value.Immutable
public interface RepositoryPackageType
{
  /**
   * @return The package ID
   */

  String id();

  /**
   * @return The package version
   */

  int versionCode();

  /**
   * @return The humanly-readable version name
   */

  String versionName();

  /**
   * @return The humanly-readable package label
   */

  String name();

  /**
   * @return The source URI of the package
   */

  URI source();

  /**
   * @return The hash of the package
   */

  Hash hash();
}
