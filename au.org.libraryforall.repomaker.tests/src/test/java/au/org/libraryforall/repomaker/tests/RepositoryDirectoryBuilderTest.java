package au.org.libraryforall.repomaker.tests;

import au.org.libraryforall.repomaker.vanilla.RepositoryDirectoryBuilderProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Files;
import java.util.UUID;

public final class RepositoryDirectoryBuilderTest
{
  private static final Logger LOG = LoggerFactory.getLogger(RepositoryDirectoryBuilderTest.class);

  @Test
  public void testBuild()
    throws Exception
  {
    final var directory = Files.createTempDirectory("repomaker-tests-");
    Files.copy(
      RepositoryDirectoryBuilderTest.class.getResourceAsStream(
        "/au/org/libraryforall/repomaker/tests/lfa-updater-0.0.3-501-debug.apk"),
      directory.resolve("0.apk"));

    Files.writeString(directory.resolve("not_an_apk"), "Hello.");

    final var builders =
      new RepositoryDirectoryBuilderProvider();
    final var builder =
      builders.createBuilder();
    final var uuid =
      UUID.randomUUID();

    final var repos =
      builder.build(directory, URI.create("urn:x"), uuid, "Releases");

    Assertions.assertEquals(uuid, repos.id());
    Assertions.assertEquals(URI.create("urn:x"), repos.self());
    Assertions.assertEquals("Releases", repos.title());
    Assertions.assertEquals(1, repos.packages().size());

    final var package0 = repos.packages().get(0);
    Assertions.assertEquals("LFA Updater", package0.name());
    Assertions.assertEquals(501, package0.versionCode());
    Assertions.assertEquals("0.0.3", package0.versionName());
    Assertions.assertEquals("au.org.libraryforall.updater.app", package0.id());
    Assertions.assertEquals(
      "a421abc9d911305b08b0e175e23de02229c40b4f6bd04827401ea111ff1668f4",
      package0.hash().text());
  }
}
