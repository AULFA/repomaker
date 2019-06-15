package au.org.libraryforall.repomaker.vanilla;

import au.org.libraryforall.repomaker.api.RepositoryDirectoryBuilderProviderType;
import au.org.libraryforall.repomaker.api.RepositoryDirectoryBuilderType;

/**
 * A provider of repository builders.
 */

public final class RepositoryDirectoryBuilderProvider
  implements RepositoryDirectoryBuilderProviderType
{
  /**
   * Construct a provider.
   */

  public RepositoryDirectoryBuilderProvider()
  {

  }

  @Override
  public RepositoryDirectoryBuilderType createBuilder()
  {
    return new RepositoryDirectoryBuilder();
  }
}
