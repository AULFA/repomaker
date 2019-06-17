package au.org.libraryforall.repomaker.cmdline;

import au.org.libraryforall.repomaker.api.RepositoryDirectoryBuilderConfiguration;
import au.org.libraryforall.repomaker.manager.api.RepositoryManagerConfiguration;
import au.org.libraryforall.repomaker.manager.api.RepositoryManagerProviderType;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.net.URI;
import java.nio.file.Path;
import java.util.OptionalInt;
import java.util.ServiceLoader;
import java.util.UUID;

@Parameters(commandDescription = "Manage a single repository directory")
final class CommandManage extends CommandRoot
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
  private OptionalInt releases = OptionalInt.empty();

  // CHECKSTYLE:ON

  CommandManage()
  {

  }

  @Override
  public Void call()
    throws Exception
  {
    super.call();

    final var managerProvider =
      ServiceLoader.load(RepositoryManagerProviderType.class)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No available services of type " + RepositoryManagerProviderType.class));

    final var configuration =
      RepositoryDirectoryBuilderConfiguration.builder()
        .setLimitReleases(this.releases)
        .setTitle(this.title)
        .setUuid(this.uuid)
        .setSelf(this.uri)
        .setPath(this.directory)
        .build();

    final var manager =
      managerProvider.createManager(
        RepositoryManagerConfiguration.builder()
          .setBuilderConfiguration(configuration)
          .build());

    manager.start();
    return null;
  }
}
