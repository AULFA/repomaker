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

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A set of patterns associating passwords with repository entries.
 */

@ImmutablesStyleType
@Value.Immutable
public interface RepositoryPasswordPatternsType
{
  /**
   * @return A list of patterns to be matched against filenames
   */

  List<RepositoryPasswordPattern> patterns();

  /**
   * Retrieve the first pattern that matches the given file.
   *
   * @param file The file
   *
   * @return The pattern, or nothing if no pattern matches
   */

  default Optional<RepositoryPasswordPattern> patternFor(
    final Path file)
  {
    Objects.requireNonNull(file, "file");

    for (final var pattern : this.patterns()) {
      if (pattern.matches(file)) {
        return Optional.of(pattern);
      }
    }
    return Optional.empty();
  }
}
