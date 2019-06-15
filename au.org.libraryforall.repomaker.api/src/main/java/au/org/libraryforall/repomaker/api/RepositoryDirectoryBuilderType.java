package au.org.libraryforall.repomaker.api;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.UUID;

/**
 * A directory-based repository builder.
 */

public interface RepositoryDirectoryBuilderType
{
  /**
   * Scan the directory at {@code path} and generate a repository from the contents.
   *
   * @param path  The source directory
   * @param self  The URI of the generated repository
   * @param uuid  The UUID that will identify the repository
   * @param title The title of the repository
   *
   * @return A new repository
   *
   * @throws IOException On I/O errors
   */

  Repository build(
    Path path,
    URI self,
    UUID uuid,
    String title)
    throws IOException;
}
