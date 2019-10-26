package one.lfa.repomaker.tests;

import one.lfa.repomaker.api.RepositoryDirectoryBuilderConfiguration;
import one.lfa.repomaker.vanilla.RepositoryDirectoryBuilderProvider;
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
        "/one/lfa/repomaker/tests/lfa-updater-0.0.3-501-debug.apk"),
      directory.resolve("0.apk"));
    Files.copy(
      RepositoryDirectoryBuilderTest.class.getResourceAsStream(
        "/one/lfa/repomaker/tests/lfa-1.3.7-178-release.apk"),
      directory.resolve("1.apk"));
    Files.copy(
      RepositoryDirectoryBuilderTest.class.getResourceAsStream(
        "/one/lfa/repomaker/tests/lfa-1.3.7-180-release.apk"),
      directory.resolve("2.apk"));
    Files.copy(
      RepositoryDirectoryBuilderTest.class.getResourceAsStream(
        "/one/lfa/repomaker/tests/lfa-1.3.7-181-release.apk"),
      directory.resolve("3.apk"));
    Files.copy(
      RepositoryDirectoryBuilderTest.class.getResourceAsStream(
        "/one/lfa/repomaker/tests/lfa-1.3.7-231-release.apk"),
      directory.resolve("4.apk"));

    Files.writeString(directory.resolve("not_an_apk"), "Hello.");

    final var builders =
      new RepositoryDirectoryBuilderProvider();
    final var builder =
      builders.createBuilder();
    final var uuid =
      UUID.randomUUID();

    final var config =
      RepositoryDirectoryBuilderConfiguration.builder()
        .setPath(directory)
        .setSelf(URI.create("urn:x"))
        .setUuid(uuid)
        .setTitle("Releases")
        .setFormatVersion(2)
        .build();

    final var result = builder.build(config);
    final var repos = result.repository();

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(uuid, repos.id());
        Assertions.assertEquals(URI.create("urn:x"), repos.self());
        Assertions.assertEquals("Releases", repos.title());
        Assertions.assertEquals(5, repos.items().size());
      },
      () -> {
        final var p = repos.items().get(0);
        Assertions.assertEquals("LFA Updater", p.name());
        Assertions.assertEquals(501, p.versionCode());
        Assertions.assertEquals("0.0.3", p.versionName());
        Assertions.assertEquals("au.org.libraryforall.updater.app", p.id());
        Assertions.assertEquals(
          "a421abc9d911305b08b0e175e23de02229c40b4f6bd04827401ea111ff1668f4",
          p.hash().text());
      },
      () -> {
        final var p = repos.items().get(1);
        Assertions.assertEquals("LFA", p.name());
        Assertions.assertEquals(178, p.versionCode());
        Assertions.assertEquals("1.3.7", p.versionName());
        Assertions.assertEquals("au.org.libraryforall.reader", p.id());
        Assertions.assertEquals(
          "ebc8d8232b31c4543904fc390d7e8e64e20e34bb07e28da5500f24bc6d4cafc0",
          p.hash().text());
      },
      () -> {
        final var p = repos.items().get(2);
        Assertions.assertEquals("LFA", p.name());
        Assertions.assertEquals(180, p.versionCode());
        Assertions.assertEquals("1.3.7", p.versionName());
        Assertions.assertEquals("au.org.libraryforall.reader", p.id());
        Assertions.assertEquals(
          "10d2e28bbe78e7b301170119a085e7b460c79e8330be3a253c96d54f1a59c36d",
          p.hash().text());
      },
      () -> {
        final var p = repos.items().get(3);
        Assertions.assertEquals("LFA", p.name());
        Assertions.assertEquals(181, p.versionCode());
        Assertions.assertEquals("1.3.7", p.versionName());
        Assertions.assertEquals("au.org.libraryforall.reader", p.id());
        Assertions.assertEquals(
          "4ffa55aca13a989be9c0fa1681d972966d95b5969189affbe2f794e39339db48",
          p.hash().text());
      },
      () -> {
        final var p = repos.items().get(4);
        Assertions.assertEquals("LFA", p.name());
        Assertions.assertEquals(231, p.versionCode());
        Assertions.assertEquals("1.3.7", p.versionName());
        Assertions.assertEquals("au.org.libraryforall.reader", p.id());
        Assertions.assertEquals(
          "733c84ed82c182f0e22758301065cc7c315e4616c2cbe19bde3cdb79724904e2",
          p.hash().text());
      }
    );
  }

  @Test
  public void testBuildLimited()
    throws Exception
  {
    final var directory = Files.createTempDirectory("repomaker-tests-");
    Files.copy(
      RepositoryDirectoryBuilderTest.class.getResourceAsStream(
        "/one/lfa/repomaker/tests/lfa-updater-0.0.3-501-debug.apk"),
      directory.resolve("0.apk"));
    Files.copy(
      RepositoryDirectoryBuilderTest.class.getResourceAsStream(
        "/one/lfa/repomaker/tests/lfa-1.3.7-178-release.apk"),
      directory.resolve("1.apk"));
    Files.copy(
      RepositoryDirectoryBuilderTest.class.getResourceAsStream(
        "/one/lfa/repomaker/tests/lfa-1.3.7-180-release.apk"),
      directory.resolve("2.apk"));
    Files.copy(
      RepositoryDirectoryBuilderTest.class.getResourceAsStream(
        "/one/lfa/repomaker/tests/lfa-1.3.7-181-release.apk"),
      directory.resolve("3.apk"));
    Files.copy(
      RepositoryDirectoryBuilderTest.class.getResourceAsStream(
        "/one/lfa/repomaker/tests/lfa-1.3.7-231-release.apk"),
      directory.resolve("4.apk"));

    Files.writeString(directory.resolve("not_an_apk"), "Hello.");

    final var builders =
      new RepositoryDirectoryBuilderProvider();
    final var builder =
      builders.createBuilder();
    final var uuid =
      UUID.randomUUID();

    final var config =
      RepositoryDirectoryBuilderConfiguration.builder()
        .setLimitReleases(1)
        .setPath(directory)
        .setSelf(URI.create("urn:x"))
        .setUuid(uuid)
        .setTitle("Releases")
        .setFormatVersion(2)
        .build();

    final var result = builder.build(config);
    final var repos = result.repository();

    Assertions.assertAll(
      () -> {
        Assertions.assertEquals(uuid, repos.id());
        Assertions.assertEquals(URI.create("urn:x"), repos.self());
        Assertions.assertEquals("Releases", repos.title());
        Assertions.assertEquals(2, repos.items().size());
      },
      () -> {
        final var p = repos.items().get(1);
        Assertions.assertEquals("LFA Updater", p.name());
        Assertions.assertEquals(501, p.versionCode());
        Assertions.assertEquals("0.0.3", p.versionName());
        Assertions.assertEquals("au.org.libraryforall.updater.app", p.id());
        Assertions.assertEquals(
          "a421abc9d911305b08b0e175e23de02229c40b4f6bd04827401ea111ff1668f4",
          p.hash().text());
      },
      () -> {
        final var p = repos.items().get(0);
        Assertions.assertEquals("LFA", p.name());
        Assertions.assertEquals(231, p.versionCode());
        Assertions.assertEquals("1.3.7", p.versionName());
        Assertions.assertEquals("au.org.libraryforall.reader", p.id());
        Assertions.assertEquals(
          "733c84ed82c182f0e22758301065cc7c315e4616c2cbe19bde3cdb79724904e2",
          p.hash().text());
      },
      () -> {
        Assertions.assertEquals(3, result.ignoredAPKs().size());
        Assertions.assertTrue(result.ignoredAPKs().contains(directory.resolve("1.apk")));
        Assertions.assertTrue(result.ignoredAPKs().contains(directory.resolve("2.apk")));
        Assertions.assertTrue(result.ignoredAPKs().contains(directory.resolve("3.apk")));
      }
    );
  }
}
