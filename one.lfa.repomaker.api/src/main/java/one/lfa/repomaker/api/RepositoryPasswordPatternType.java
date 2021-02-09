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
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A pattern associating a password with one or more repository entries.
 */

@ImmutablesStyleType
@Value.Immutable
public interface RepositoryPasswordPatternType
{
  /**
   * @return A pattern matching against a filename
   */

  Pattern fileNamePattern();

  /**
   * @return The password
   */

  String password();

  /**
   * Determine if the given file matches this pattern.
   *
   * @param file The file
   *
   * @return {@code true} if the file matches
   */

  default boolean matches(
    final Path file)
  {
    Objects.requireNonNull(file, "file");

    final var fileName = file.getFileName();
    if (fileName == null) {
      return false;
    }

    final var matcher =
      this.fileNamePattern().matcher(fileName.toString());
    return matcher.matches();
  }
}
