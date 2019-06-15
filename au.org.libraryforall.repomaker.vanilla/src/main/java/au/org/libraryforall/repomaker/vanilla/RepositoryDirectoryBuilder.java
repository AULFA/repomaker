package au.org.libraryforall.repomaker.vanilla;

import au.org.libraryforall.repomaker.api.Hash;
import au.org.libraryforall.repomaker.api.Repository;
import au.org.libraryforall.repomaker.api.RepositoryDirectoryBuilderType;
import au.org.libraryforall.repomaker.api.RepositoryPackage;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A directory-based repository builder.
 */

public final class RepositoryDirectoryBuilder implements RepositoryDirectoryBuilderType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(RepositoryDirectoryBuilder.class);

  RepositoryDirectoryBuilder()
  {

  }

  private static boolean appearsToBeAPK(
    final Path file)
  {
    final var name = file.getFileName().toString();
    return Files.isRegularFile(file) && name.endsWith(".apk");
  }

  private static RepositoryPackage buildPackageOfFile(final Path file)
    throws IOException
  {
    LOG.debug("checking: {}", file);

    try (var apkFile = new ApkFile(file.toFile())) {
      final var apkMeta = apkFile.getApkMeta();

      final String digestText;
      try (var stream = Files.newInputStream(file, StandardOpenOption.READ)) {
        final var digest = MessageDigest.getInstance("SHA-256");
        try (var digestStream = new DigestInputStream(stream, digest)) {
          digestStream.transferTo(OutputStream.nullOutputStream());
          digestText = Hex.encodeHexString(digest.digest());
        }
      } catch (final NoSuchAlgorithmException e) {
        throw new IllegalStateException(e);
      }

      final var label = labelOf(apkMeta);
      return RepositoryPackage.builder()
        .setId(apkMeta.getPackageName())
        .setName(label)
        .setHash(Hash.builder()
                   .setText(digestText)
                   .build())
        .setSource(URI.create(file.getFileName().toString()))
        .setVersionCode(apkMeta.getVersionCode().intValue())
        .setVersionName(apkMeta.getVersionName())
        .build();
    }
  }

  private static String labelOf(final ApkMeta apkMeta)
  {
    final var label = apkMeta.getLabel();
    if (label == null) {
      return "";
    }
    return label;
  }

  @Override
  public Repository build(
    final Path path,
    final URI self,
    final UUID uuid,
    final String title)
    throws IOException
  {
    Objects.requireNonNull(path, "path");
    Objects.requireNonNull(self, "self");
    Objects.requireNonNull(uuid, "uuid");
    Objects.requireNonNull(title, "title");

    final var builder =
      Repository.builder()
        .setId(uuid)
        .setSelf(self)
        .setUpdated(LocalDateTime.now())
        .setTitle(title);

    final var files =
      Files.list(path)
        .sorted()
        .collect(Collectors.toList());

    for (final var file : files) {
      if (appearsToBeAPK(file)) {
        builder.addPackages(buildPackageOfFile(file));
      }
    }

    return builder.build();
  }
}
