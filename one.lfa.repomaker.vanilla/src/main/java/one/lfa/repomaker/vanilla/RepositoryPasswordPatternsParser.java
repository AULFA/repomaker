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

import com.io7m.blackthorne.api.BTElementHandlerConstructorType;
import com.io7m.blackthorne.api.BTException;
import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.blackthorne.jxe.BlackthorneJXE;
import one.lfa.repomaker.api.RepositoryPasswordPatterns;
import one.lfa.repomaker.vanilla.internal.RepositoryPasswordPatternsHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

import static one.lfa.repomaker.vanilla.RepositoryPasswordPatternsSchemas.SCHEMAS;
import static one.lfa.repomaker.vanilla.RepositoryPasswordPatternsSchemas.name1;

/**
 * Functions to parse repository → password patterns from XML configuration files.
 */

public final class RepositoryPasswordPatternsParser
{
  private RepositoryPasswordPatternsParser()
  {

  }

  public static RepositoryPasswordPatterns parse(
    final URI source,
    final InputStream stream)
    throws IOException
  {
    Objects.requireNonNull(source, "source");
    Objects.requireNonNull(stream, "stream");

    try {
      final Map<BTQualifiedName, BTElementHandlerConstructorType<?, RepositoryPasswordPatterns>> handlers =
        Map.ofEntries(
          Map.entry(
            name1("PasswordPatterns"),
            RepositoryPasswordPatternsHandler::new)
        );
      return BlackthorneJXE.parse(source, stream, handlers, SCHEMAS);
    } catch (final BTException e) {
      throw new IOException(e);
    }
  }

  public static RepositoryPasswordPatterns parse(
    final Path file)
    throws IOException
  {
    Objects.requireNonNull(file, "file");

    try (var stream = Files.newInputStream(file)) {
      return parse(file.toUri(), stream);
    }
  }
}
