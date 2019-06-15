package au.org.libraryforall.repomaker.vanilla;

import au.org.libraryforall.repomaker.api.RepositoryDirectoryBuilderProviderType;
import au.org.libraryforall.repomaker.manager.api.RepositoryManagerConfiguration;
import au.org.libraryforall.repomaker.manager.api.RepositoryManagerProviderType;
import au.org.libraryforall.repomaker.manager.api.RepositoryManagerType;
import au.org.libraryforall.repomaker.serializer.api.RepositorySerializerProviderType;

import java.util.Objects;
import java.util.ServiceLoader;

/**
 * A provider of repository managers.
 */

public final class RepositoryManagerProvider implements RepositoryManagerProviderType
{
  private RepositorySerializerProviderType serializers;
  private RepositoryDirectoryBuilderProviderType builders;

  /**
   * Construct a provider.
   *
   * @param in_builders    A provider of repository builders
   * @param in_serializers A provider of repository serializers
   */

  public RepositoryManagerProvider(
    final RepositorySerializerProviderType in_serializers,
    final RepositoryDirectoryBuilderProviderType in_builders)
  {
    this.serializers =
      Objects.requireNonNull(in_serializers, "serializers");
    this.builders =
      Objects.requireNonNull(in_builders, "builders");
  }

  /**
   * Construct a provider.
   */

  public RepositoryManagerProvider()
  {
    this(
      ServiceLoader.load(RepositorySerializerProviderType.class)
        .findFirst()
        .orElseThrow(),
      ServiceLoader.load(RepositoryDirectoryBuilderProviderType.class)
        .findFirst()
        .orElseThrow());
  }

  @Override
  public RepositoryManagerType createManager(
    final RepositoryManagerConfiguration configuration)
  {
    return new RepositoryManager(configuration, this.serializers, this.builders);
  }
}
