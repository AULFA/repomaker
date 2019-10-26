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

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.net.URI;

/**
 * A package in a repository.
 */

@ImmutablesStyleType
@Value.Immutable
public interface RepositoryPackageType
{
  /**
   * @return The package ID
   */

  String id();

  /**
   * @return The package version
   */

  int versionCode();

  /**
   * @return The humanly-readable version name
   */

  String versionName();

  /**
   * @return The humanly-readable package label
   */

  String name();

  /**
   * @return The source URI of the package
   */

  URI source();

  /**
   * @return The hash of the package
   */

  Hash hash();
}
