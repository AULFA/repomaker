package au.org.libraryforall.repomaker.manager.api;

/**
 * A provider of repository managers.
 */

public interface RepositoryManagerProviderType
{
  /**
   * Create a new repository manager based on the given configuration.
   *
   * @param configuration The repository manager configuration
   *
   * @return A new manager
   */

  RepositoryManagerType createManager(
    RepositoryManagerConfiguration configuration);
}
