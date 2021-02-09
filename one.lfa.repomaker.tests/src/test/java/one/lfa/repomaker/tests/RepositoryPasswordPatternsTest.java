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

package one.lfa.repomaker.tests;

import one.lfa.repomaker.api.RepositoryPasswordPattern;
import one.lfa.repomaker.vanilla.RepositoryPasswordPatternsParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class RepositoryPasswordPatternsTest
{
  private Path directory;

  @BeforeEach
  public void setup()
    throws IOException
  {
    this.directory = Files.createTempDirectory("repomaker-tests-");
  }

  @Test
  public void testParse()
    throws IOException
  {
    final var fileTarget =
      this.directory.resolve("passwordPatterns.xml");

    Files.copy(
      RepositoryDirectoryBuilderTest.class.getResourceAsStream(
        "/one/lfa/repomaker/tests/passwordPatterns.xml"),
      fileTarget
    );

    final var patterns =
      RepositoryPasswordPatternsParser.parse(fileTarget);

    {
      final var p =
        patterns.patternFor(Paths.get("file-0.apk"));
      assertEquals(Optional.of("abcd1234"), p.map(RepositoryPasswordPattern::password));
    }

    {
      final var p =
        patterns.patternFor(Paths.get("file-1.apk"));
      assertEquals(Optional.of("abcd1234"), p.map(RepositoryPasswordPattern::password));
    }

    {
      final var p =
        patterns.patternFor(Paths.get("other-1.apk"));
      assertEquals(Optional.of("efgh1234"), p.map(RepositoryPasswordPattern::password));
    }

    {
      final var p =
        patterns.patternFor(Paths.get("nothing.apk"));
      assertEquals(Optional.empty(), p);
    }
  }

  @Test
  public void testParseGarbage()
    throws IOException
  {
    final var fileTarget =
      this.directory.resolve("passwordPatterns.xml");

    Files.copy(
      RepositoryDirectoryBuilderTest.class.getResourceAsStream(
        "/one/lfa/repomaker/tests/schema-1.0.xsd"),
      fileTarget
    );

    assertThrows(IOException.class, () -> {
      RepositoryPasswordPatternsParser.parse(fileTarget);
    });
  }
}
