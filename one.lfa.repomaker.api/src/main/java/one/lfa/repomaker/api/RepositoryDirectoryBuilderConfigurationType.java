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
import java.nio.file.Path;
import java.util.OptionalInt;
import java.util.UUID;

/**
 * Configuration information for the directory builder.
 */

@ImmutablesStyleType
@Value.Immutable
public interface RepositoryDirectoryBuilderConfigurationType
{
  /**
   * @return The directory containing items
   */

  Path path();

  /**
   * @return The URI of the generated repository
   */

  URI self();

  /**
   * @return The UUID used to identify the repository
   */

  UUID uuid();

  /**
   * @return The title of the repository
   */

  String title();

  /**
   * A limit on the number of releases of each item that should appear in repositories.
   *
   * @return A limit on the number of releases
   */

  OptionalInt limitReleases();

  /**
   * @return The repository format version to use
   */

  int formatVersion();

  /**
   * @return The set of package → password mappings.
   */

  @Value.Default
  default RepositoryPasswordPatterns passwordPatterns()
  {
    return RepositoryPasswordPatterns.builder()
      .build();
  }

  /**
   * Check preconditions for the type.
   */

  @Value.Check
  default void checkPreconditions()
  {
    final var oldFormat =
      this.formatVersion() < 3;
    final var usingPatterns =
      !this.passwordPatterns().patterns().isEmpty();

    if (oldFormat && usingPatterns) {
      final var lineSeparator = System.lineSeparator();
      throw new UnsupportedOperationException(
        new StringBuilder(128)
          .append(
            "Password protected items require a higher repository format version.")
          .append(lineSeparator)
          .append("  Received: Format version ")
          .append(this.formatVersion())
          .append(lineSeparator)
          .append("  Required: Format version ≥ 3")
          .append(lineSeparator)
          .toString()
      );
    }
  }
}
