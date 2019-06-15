package au.org.libraryforall.repomaker.serializer.api;

import java.io.IOException;

/**
 * The type of repository serializers.
 */

public interface RepositorySerializerType
{
  /**
   * Serialize the repository.
   *
   * @throws IOException On I/O errors
   */

  void serialize()
    throws IOException;
}
