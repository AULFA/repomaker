/*
 * Copyright © 2019 Library For All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package one.lfa.repomaker.serializer.api;

import one.lfa.repomaker.api.Repository;

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
   * @param repository    The repository to serialize
   * @param target        The URI of the output, for diagnostics
   * @param stream        The output stream
   * @param formatVersion The format version to use
   *
   * @return A new serializer
   */

  RepositorySerializerType createSerializer(
    Repository repository,
    URI target,
    OutputStream stream,
    int formatVersion);
}
