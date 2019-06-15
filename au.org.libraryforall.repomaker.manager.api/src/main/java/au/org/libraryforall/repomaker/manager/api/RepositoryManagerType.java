package au.org.libraryforall.repomaker.manager.api;

import java.io.IOException;

/**
 * The type of repository managers.
 */

public interface RepositoryManagerType
{
  /**
   * Start managing a repository on the current thread.
   *
   * @throws IOException On I/O errors
   */

  void start()
    throws IOException;
}
