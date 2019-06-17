package au.org.libraryforall.repomaker.api;

import java.io.IOException;

/**
 * A directory-based repository builder.
 */

public interface RepositoryDirectoryBuilderType
{
  /**
   * Generate a repository based on the given configuration.
   *
   * @param configuration The configuration
   *
   * @return A new repository
   *
   * @throws IOException On I/O errors
   */

  RepositoryDirectoryBuilderResult build(
    RepositoryDirectoryBuilderConfiguration configuration)
    throws IOException;
}
