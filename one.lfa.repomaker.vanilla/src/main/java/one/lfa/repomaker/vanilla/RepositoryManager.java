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

import one.lfa.repomaker.api.RepositoryDirectoryBuilderProviderType;
import one.lfa.repomaker.api.RepositoryDirectoryBuilderResult;
import one.lfa.repomaker.manager.api.RepositoryManagerConfiguration;
import one.lfa.repomaker.manager.api.RepositoryManagerType;
import one.lfa.repomaker.serializer.api.RepositorySerializerProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Objects;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * A repository manager.
 */

public final class RepositoryManager implements RepositoryManagerType
{
  private static final Logger LOG = LoggerFactory.getLogger(RepositoryManager.class);

  private final RepositorySerializerProviderType serializers;
  private final RepositoryDirectoryBuilderProviderType builders;
  private final Path releases;
  private final Path releasesTemp;
  private final RepositoryManagerConfiguration configuration;
  private final Path path;

  RepositoryManager(
    final RepositoryManagerConfiguration in_configuration,
    final RepositorySerializerProviderType in_serializers,
    final RepositoryDirectoryBuilderProviderType in_builders)
  {
    this.configuration =
      Objects.requireNonNull(in_configuration, "configuration");
    this.serializers =
      Objects.requireNonNull(in_serializers, "serializers");
    this.builders =
      Objects.requireNonNull(in_builders, "builders");

    this.path =
      this.configuration.builderConfiguration().path().toAbsolutePath();
    this.releases =
      this.path.resolve("releases.xml")
        .toAbsolutePath();
    this.releasesTemp =
      this.path.resolve("releases.xml.tmp")
        .toAbsolutePath();
  }

  private static Instant fileTimeOrDefault(
    final Instant defaultTime,
    final Path file)
  {
    try {
      return Files.getLastModifiedTime(file).toInstant();
    } catch (final IOException e) {
      return defaultTime;
    }
  }

  @Override
  public void start()
  {
    while (true) {
      try {
        if (!this.releasesIsUpToDate()) {
          LOG.debug("releases file is older than one of the directory files");
          final var result = this.doRegeneration();
          this.deleteOldAPKFiles(result);
        }

        LOG.debug("polling");
        Thread.sleep(5_000L);
      } catch (final IOException e) {
        LOG.error("i/o error: ", e);
        try {
          Thread.sleep(5_000L);
        } catch (final InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  private void deleteOldAPKFiles(
    final RepositoryDirectoryBuilderResult result)
    throws IOException
  {
    for (final var apk : result.ignoredAPKs()) {
      if (this.configuration.deleteOldReleases()) {
        LOG.debug("deleting {}", apk);
        Files.deleteIfExists(apk);
      } else {
        LOG.debug("would have deleted {} if deletion was enabled", apk);
      }
    }
  }

  private boolean releasesIsUpToDate()
    throws IOException
  {
    final Instant releasesTime;
    try {
      releasesTime = Files.getLastModifiedTime(this.releases).toInstant();
    } catch (final NoSuchFileException e) {
      return false;
    }

    try (var stream = Files.newDirectoryStream(this.path)) {
      final var iterator = stream.iterator();
      while (iterator.hasNext()) {
        final var file = iterator.next();
        if (this.isReleaseFile(file)) {
          continue;
        }
        final var time = fileTimeOrDefault(releasesTime, file);
        if (!releasesTime.isAfter(time)) {
          return false;
        }
      }
    }

    return true;
  }

  private boolean isReleaseFile(final Path file)
  {
    return file.equals(this.releases) || file.equals(this.releasesTemp);
  }

  private RepositoryDirectoryBuilderResult doRegeneration()
    throws IOException
  {
    LOG.info("generating new repository file");

    try {
      final RepositoryDirectoryBuilderResult result;
      try (var output = Files.newOutputStream(this.releasesTemp, CREATE_NEW, WRITE)) {
        final var builder = this.builders.createBuilder();
        result = builder.build(this.configuration.builderConfiguration());
        final var repos = result.repository();
        final var target = this.configuration.builderConfiguration().path().toUri();
        final var serializer = this.serializers.createSerializer(repos, target, output);
        serializer.serialize();
      }

      Files.move(this.releasesTemp, this.releases, StandardCopyOption.ATOMIC_MOVE);
      return result;
    } catch (final FileAlreadyExistsException e) {
      throw e;
    } catch (final IOException e) {
      Files.deleteIfExists(this.releasesTemp);
      throw e;
    }
  }
}
