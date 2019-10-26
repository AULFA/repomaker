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

package one.lfa.repomaker.manager.api;

/**
 * A provider of repository managers.
 */

public interface RepositoryManagerProviderType
{
  /**
   * Create a new repository manager based on the given configuration.
   *
   * @param configuration The repository manager configuration
   *
   * @return A new manager
   */

  RepositoryManagerType createManager(
    RepositoryManagerConfiguration configuration);
}