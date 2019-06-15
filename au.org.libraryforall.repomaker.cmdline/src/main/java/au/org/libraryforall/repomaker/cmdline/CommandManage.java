package au.org.libraryforall.repomaker.cmdline;

import au.org.libraryforall.repomaker.manager.api.RepositoryManagerConfiguration;
import au.org.libraryforall.repomaker.manager.api.RepositoryManagerProviderType;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.net.URI;
import java.nio.file.Path;
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

    final var manager =
      managerProvider.createManager(
        RepositoryManagerConfiguration.builder()
          .setSelf(this.uri)
          .setId(this.uuid)
          .setTitle(this.title)
          .setPath(this.directory)
          .build());

    manager.start();
    return null;
  }
}
