package au.org.libraryforall.repomaker.api;

/**
 * A provider of directory-based repository builders.
 */

public interface RepositoryDirectoryBuilderProviderType
{
  /**
   * @return A new builder
   */

  RepositoryDirectoryBuilderType createBuilder();
}
