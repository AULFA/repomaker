package au.org.libraryforall.repomaker.cmdline;

import au.org.libraryforall.repomaker.api.RepositoryDirectoryBuilderProviderType;
import au.org.libraryforall.repomaker.serializer.api.RepositorySerializerProviderType;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
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
    names = "--output",
    description = "The output file")
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
        .orElseThrow(() -> new IllegalStateException("No available services of type " + RepositoryDirectoryBuilderProviderType.class));

    final var serializerProvider =
      ServiceLoader.load(RepositorySerializerProviderType.class)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No available services of type " + RepositorySerializerProviderType.class));

    try (var output = Files.newOutputStream(this.output, CREATE, WRITE, TRUNCATE_EXISTING)) {
      final var builder =
        builderProvider.createBuilder();

      final var repos =
        builder.build(
          this.directory,
          this.uri,
          this.uuid,
          this.title);

      final var target = URI.create("urn:stdout");
      final var serializer = serializerProvider.createSerializer(repos, target, output);
      serializer.serialize();
    }

    return null;
  }
}
