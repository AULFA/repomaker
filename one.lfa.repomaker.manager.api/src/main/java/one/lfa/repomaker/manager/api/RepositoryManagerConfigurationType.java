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

import one.lfa.repomaker.api.RepositoryDirectoryBuilderConfiguration;
import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

/**
 * Configuration data for a repository manager.
 *
 * A repository manager watches a single directory and generates a repository definition each time
 * the directory changes. Subdirectories are not watched. By convention, repository manager
 * implementations generate a file {@code releases.xml} inside the directory.
 */

@ImmutablesStyleType
@Value.Immutable
public interface RepositoryManagerConfigurationType
{
  /**
   * @return The directory-based repository builder configuration
   */

  RepositoryDirectoryBuilderConfiguration builderConfiguration();

  /**
   * @return {@code true} if any APK ignored by the directory builder should be deleted
   */

  @Value.Default
  default boolean deleteOldReleases()
  {
    return false;
  }
}
