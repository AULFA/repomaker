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

package one.lfa.repomaker.vanilla;

import one.lfa.repomaker.api.Repository;
import one.lfa.repomaker.serializer.api.RepositorySerializerProviderType;
import one.lfa.repomaker.serializer.api.RepositorySerializerType;

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
