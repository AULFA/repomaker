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

import java.util.Objects;
import java.util.ServiceLoader;
import one.lfa.opdsget.api.OPDSManifestReaderProviderType;
import one.lfa.repomaker.api.RepositoryDirectoryBuilderProviderType;
import one.lfa.repomaker.api.RepositoryDirectoryBuilderType;

/**
 * A provider of repository builders.
 */

public final class RepositoryDirectoryBuilderProvider
  implements RepositoryDirectoryBuilderProviderType
{
  private final OPDSManifestReaderProviderType opdsReaders;

  /**
   * Construct a provider.
   */

  public RepositoryDirectoryBuilderProvider()
  {
    this(ServiceLoader.load(OPDSManifestReaderProviderType.class)
           .findFirst()
           .orElseThrow(() -> new IllegalStateException(
             "No available implementations of type " + OPDSManifestReaderProviderType.class)));
  }

  private RepositoryDirectoryBuilderProvider(
    final OPDSManifestReaderProviderType inOpdsReaders)
  {
    this.opdsReaders =
      Objects.requireNonNull(inOpdsReaders, "opdsReaders");
  }

  /**
   * Create a new directory builder provider.
   *
   * @param opdsReaders A provider of OPDS manifest reader provider
   *
   * @return A new provider
   */

  public static RepositoryDirectoryBuilderProviderType create(
    final OPDSManifestReaderProviderType opdsReaders)
  {
    return new RepositoryDirectoryBuilderProvider(opdsReaders);
  }

  @Override
  public RepositoryDirectoryBuilderType createBuilder()
  {
    return new RepositoryDirectoryBuilder(this.opdsReaders);
  }
}
