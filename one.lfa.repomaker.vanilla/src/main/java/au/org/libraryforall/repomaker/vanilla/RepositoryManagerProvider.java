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
