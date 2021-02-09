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

package one.lfa.repomaker.cmdline;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import one.lfa.repomaker.api.RepositoryDirectoryBuilderConfiguration;
import one.lfa.repomaker.api.RepositoryDirectoryBuilderProviderType;
import one.lfa.repomaker.serializer.api.RepositorySerializerProviderType;
import one.lfa.repomaker.vanilla.RepositoryPasswordPatternsParser;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.OptionalInt;
import java.util.ServiceLoader;
import java.util.UUID;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

@Parameters(commandDescription = "Generate a single repository from a directory")
final class CommandGenerate extends CommandRoot
{
  // CHECKSTYLE:OFF

  @Parameter(
    names = "--directory",
    description = "The directory that contains input APK files",
    required = true)
  private Path directory;

  @Parameter(
    names = "--source",
    description = "The source URI that will be used in the repository",
    required = true)
  private URI uri;

  @Parameter(
    names = "--id",
    description = "The UUID that will be used to identify the repository",
    converter = UUIDConverter.class,
    required = true)
  private UUID uuid;

  @Parameter(
    names = "--title",
    description = "The repository title",
    required = true)
  private String title;

  @Parameter(
    names = "--releases-per-package",
    description = "The number of releases per package to include (includes all releases if not specified)",
    required = false)
  private int releases = Integer.MAX_VALUE;

  @Parameter(
    names = "--repository-format-version",
    description = "The file format version the generated repository will use",
    required = false)
  private int formatVersion = 2;

  @Parameter(
    names = "--repository-passwords",
    description = "The password file for repository items",
    required = false)
  private Path repositoryPasswords;

  @Parameter(
    names = "--output",
    description = "The output file",
    required = true)
  private Path output;

  // CHECKSTYLE:ON

  CommandGenerate()
  {

  }

  @Override
  public Void call()
    throws Exception
  {
    super.call();

    final var builderProvider =
      ServiceLoader.load(RepositoryDirectoryBuilderProviderType.class)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(String.format(
          "No available services of type %s",
          RepositoryDirectoryBuilderProviderType.class)));

    final var serializerProvider =
      ServiceLoader.load(RepositorySerializerProviderType.class)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(String.format(
          "No available services of type %s",
          RepositorySerializerProviderType.class)));

    try (var output =
           Files.newOutputStream(
             this.output,
             CREATE,
             WRITE,
             TRUNCATE_EXISTING)) {
      final var builder =
        builderProvider.createBuilder();

      final var configBuilder =
        RepositoryDirectoryBuilderConfiguration.builder();

      configBuilder
        .setLimitReleases(OptionalInt.of(this.releases))
        .setTitle(this.title)
        .setUuid(this.uuid)
        .setSelf(this.uri)
        .setPath(this.directory)
        .setFormatVersion(this.formatVersion);

      if (this.repositoryPasswords != null) {
        configBuilder.setPasswordPatterns(
          RepositoryPasswordPatternsParser.parse(this.repositoryPasswords)
        );
      }

      final var configuration =
        configBuilder.build();

      final var result =
        builder.build(configuration);

      final var repos = result.repository();
      final var target = URI.create("urn:stdout");
      final var serializer =
        serializerProvider.createSerializer(
          repos, target, output, this.formatVersion);
      serializer.serialize();
    }

    return null;
  }
}
