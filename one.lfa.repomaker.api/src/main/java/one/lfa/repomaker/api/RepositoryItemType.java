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

package one.lfa.repomaker.api;

import java.net.URI;
import java.util.Optional;

/**
 * An item in a repository.
 */

public interface RepositoryItemType
{
  /**
   * @return The item ID
   */

  String id();

  /**
   * @return The sortable version code
   */

  long versionCode();

  /**
   * @return The humanly-readable version name
   */

  String versionName();

  /**
   * @return The humanly-readable item label
   */

  String name();

  /**
   * @return The source URI of the item
   */

  URI source();

  /**
   * @return The hash of the item
   */

  Hash hash();

  /**
   * @return The password protection for the item
   */

  Optional<String> password();
}
