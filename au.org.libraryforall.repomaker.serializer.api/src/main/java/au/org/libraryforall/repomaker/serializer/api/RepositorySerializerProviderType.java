package au.org.libraryforall.repomaker.serializer.api;

import au.org.libraryforall.repomaker.api.Repository;

import java.io.OutputStream;
import java.net.URI;

/**
 * A provider of repository serializers.
 */

public interface RepositorySerializerProviderType
{
  /**
   * Create a new serializer.
   *
   * @param repository The repository to serialize
   * @param target     The URI of the output, for diagnostics
   * @param stream     The output stream
   *
   * @return A new serializer
   */

  RepositorySerializerType createSerializer(
    Repository repository,
    URI target,
    OutputStream stream);
}
