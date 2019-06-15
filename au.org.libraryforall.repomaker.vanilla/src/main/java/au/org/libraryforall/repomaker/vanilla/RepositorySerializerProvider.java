package au.org.libraryforall.repomaker.vanilla;

import au.org.libraryforall.repomaker.api.Repository;
import au.org.libraryforall.repomaker.serializer.api.RepositorySerializerProviderType;
import au.org.libraryforall.repomaker.serializer.api.RepositorySerializerType;

import java.io.OutputStream;
import java.net.URI;

/**
 * A provider of repository serializers.
 */

public final class RepositorySerializerProvider implements RepositorySerializerProviderType
{
  /**
   * Construct a provider.
   */

  public RepositorySerializerProvider()
  {

  }

  @Override
  public RepositorySerializerType createSerializer(
    final Repository repository,
    final URI target,
    final OutputStream stream)
  {
    return new RepositorySerializer(repository, target, stream);
  }
}
