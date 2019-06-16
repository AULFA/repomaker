package au.org.libraryforall.repomaker.vanilla;

import au.org.libraryforall.repomaker.api.RepositoryDirectoryBuilderProviderType;
import au.org.libraryforall.repomaker.manager.api.RepositoryManagerConfiguration;
import au.org.libraryforall.repomaker.manager.api.RepositoryManagerType;
import au.org.libraryforall.repomaker.serializer.api.RepositorySerializerProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.WatchEvent;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

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
      this.configuration.path().toAbsolutePath();
    this.releases =
      this.path.resolve("releases.xml")
        .toAbsolutePath();
    this.releasesTemp =
      this.path.resolve("releases.xml.tmp")
        .toAbsolutePath();
  }

  @Override
  public void start()
    throws IOException
  {
    final var watchService = this.path.getFileSystem().newWatchService();
    this.path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

    while (true) {
      try {
        if (!this.releasesIsUpToDate()) {
          LOG.debug("releases file is older than one of the directory files");
          this.doRegeneration();
        }

        LOG.debug("polling");
        final var watchKey = watchService.poll(5L, TimeUnit.SECONDS);
        if (watchKey != null) {
          for (final var event : watchKey.pollEvents()) {
            this.handleEvent(event);
          }

          if (!watchKey.reset()) {
            watchKey.cancel();
            watchService.close();
            break;
          }
        }
      } catch (final IOException e) {
        LOG.error("i/o error: ", e);
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  private boolean releasesIsUpToDate()
    throws IOException
  {
    final var releasesTime =
      Files.getLastModifiedTime(this.releases).toInstant();

    try (var stream = Files.list(this.path)) {
      final var newestFile =
        stream
          .sorted()
          .filter(file -> !this.isReleaseFile(file))
          .map(file -> fileTimeOrDefault(releasesTime, file))
          .max(Instant::compareTo)
          .orElse(releasesTime);

      return releasesTime.isAfter(newestFile);
    }
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

  private boolean isReleaseFile(final Path file)
  {
    return file.equals(this.releases) || file.equals(this.releasesTemp);
  }

  private void handleEvent(final WatchEvent<?> event)
    throws IOException
  {
    final var kind = event.kind();
    if (kind.equals(ENTRY_CREATE)) {
      final var pathCreated = (Path) event.context();
      this.checkIfSignificantFileChanged(this.path.resolve(pathCreated));
    } else if (kind.equals(ENTRY_DELETE)) {
      final var pathDeleted = (Path) event.context();
      this.checkIfSignificantFileChanged(this.path.resolve(pathDeleted));
    } else if (kind.equals(ENTRY_MODIFY)) {
      final var pathModified = (Path) event.context();
      this.checkIfSignificantFileChanged(this.path.resolve(pathModified));
    }
  }

  private void checkIfSignificantFileChanged(
    final Path path)
    throws IOException
  {
    if (!this.isReleaseFile(path)) {
      LOG.debug("file {} changed, regenerating repository", path);
      this.doRegeneration();
    }
  }

  private void doRegeneration()
    throws IOException
  {
    try {
      try (var output = Files.newOutputStream(this.releasesTemp, CREATE_NEW, WRITE)) {
        final var builder = this.builders.createBuilder();

        final var repos =
          builder.build(
            this.configuration.path(),
            this.configuration.self(),
            this.configuration.id(),
            this.configuration.title());

        final var target = this.configuration.path().toUri();
        final var serializer = this.serializers.createSerializer(repos, target, output);
        serializer.serialize();
      }

      Files.move(this.releasesTemp, this.releases, StandardCopyOption.ATOMIC_MOVE);
    } catch (final FileAlreadyExistsException e) {
      throw e;
    } catch (final IOException e) {
      Files.deleteIfExists(this.releasesTemp);
      throw e;
    }
  }
}
