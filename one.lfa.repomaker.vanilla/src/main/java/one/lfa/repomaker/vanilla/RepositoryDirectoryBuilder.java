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

import com.io7m.jlexing.core.LexicalPosition;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import net.dongliu.apk.parser.ApkFile;
import net.dongliu.apk.parser.bean.ApkMeta;
import one.lfa.opdsget.api.OPDSManifestParseError;
import one.lfa.opdsget.api.OPDSManifestReaderProviderType;
import one.lfa.repomaker.api.Hash;
import one.lfa.repomaker.api.Repository;
import one.lfa.repomaker.api.RepositoryAndroidPackage;
import one.lfa.repomaker.api.RepositoryDirectoryBuilderConfiguration;
import one.lfa.repomaker.api.RepositoryDirectoryBuilderResult;
import one.lfa.repomaker.api.RepositoryDirectoryBuilderType;
import one.lfa.repomaker.api.RepositoryItemType;
import one.lfa.repomaker.api.RepositoryOPDSPackage;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * A directory-based repository builder.
 */

public final class RepositoryDirectoryBuilder implements
  RepositoryDirectoryBuilderType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(RepositoryDirectoryBuilder.class);

  private static final DateTimeFormatter TIMECODE_FORMATTER =
    DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
  private static final DateTimeFormatter TIME_STRING_FORMATTER =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final OPDSManifestReaderProviderType opdsReaders;

  RepositoryDirectoryBuilder(
    final OPDSManifestReaderProviderType inOpdsReaders)
  {
    this.opdsReaders =
      Objects.requireNonNull(inOpdsReaders, "opdsReaders");
  }

  private static boolean appearsToBeAPK(
    final Path file)
  {
    final var name = file.getFileName().toString();
    return Files.isRegularFile(file) && name.endsWith(".apk");
  }

  private static boolean appearsToBeOPDSManifest(
    final Path file)
  {
    final var name = file.getFileName().toString();
    return Files.isRegularFile(file) && name.endsWith(".omx");
  }

  private RepositoryOPDSPackage buildOPDSPackageOfFile(
    final Path file)
    throws IOException
  {
    LOG.debug("checking: {}", file);

    final var errors = new LinkedList<OPDSManifestParseError>();
    final var hash = hashOf(file);

    try (var stream = Files.newInputStream(file)) {
      try (var reader =
             this.opdsReaders.createReader(errors::add, file.toUri(), stream)) {
        final var descriptionOpt = reader.read();
        if (descriptionOpt.isPresent()) {
          final var description = descriptionOpt.get();
          return RepositoryOPDSPackage.builder()
            .setId(description.id().toString())
            .setName(description.title())
            .setHash(hash)
            .setSource(URI.create(file.getFileName().toString()))
            .setVersionCode(timeCodeOf(description.updated()))
            .setVersionName(timeOf(description.updated()))
            .build();
        }

        for (final OPDSManifestParseError error : errors) {
          final LexicalPosition<URI> lexical = error.lexical();
          switch (error.severity()) {
            case WARNING:
              LOG.warn(
                "{}:{}:{}: {}: ",
                lexical.file(),
                Integer.valueOf(lexical.line()),
                Integer.valueOf(lexical.column()),
                error.message(),
                error.exception().orElse(null));
              break;
            case ERROR:
              LOG.error(
                "{}:{}:{}: {}: ", lexical.file(),
                Integer.valueOf(lexical.line()),
                Integer.valueOf(lexical.column()),
                error.message(),
                error.exception().orElse(null));
              break;
          }
        }

        throw new IOException("OPDS manifest was unparseable");
      }
    }
  }

  private static long timeCodeOf(
    final OffsetDateTime time)
  {
    return Long.parseUnsignedLong(TIMECODE_FORMATTER.format(time));
  }

  private static String timeOf(
    final OffsetDateTime time)
  {
    return TIME_STRING_FORMATTER.format(time);
  }

  private static RepositoryAndroidPackage buildAndroidPackageOfFile(
    final Path file)
    throws IOException
  {
    LOG.debug("checking: {}", file);

    try (var apkFile = new ApkFile(file.toFile())) {
      final var apkMeta = apkFile.getApkMeta();
      final var label = labelOf(apkMeta);
      final var hash = hashOf(file);

      return RepositoryAndroidPackage.builder()
        .setId(apkMeta.getPackageName())
        .setName(label)
        .setHash(hash)
        .setSource(URI.create(file.getFileName().toString()))
        .setVersionCode(apkMeta.getVersionCode().longValue())
        .setVersionName(apkMeta.getVersionName())
        .build();
    }
  }

  private static Hash hashOf(
    final Path file)
    throws IOException
  {
    return Hash.builder()
      .setText(digestFile(file))
      .build();
  }

  private static String digestFile(
    final Path file)
    throws IOException
  {
    try (var stream = Files.newInputStream(
      file,
      StandardOpenOption.READ)) {
      final var digest = MessageDigest.getInstance("SHA-256");
      try (var digestStream = new DigestInputStream(stream, digest)) {
        digestStream.transferTo(OutputStream.nullOutputStream());
        return Hex.encodeHexString(digest.digest());
      }
    } catch (final NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
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

  /**
   * For each package in the repository, remove the oldest releases (assuming that a limit has been
   * specified for releases).
   */

  private static RepositoryDirectoryBuilderResult trimOldReleases(
    final RepositoryDirectoryBuilderConfiguration configuration,
    final RepositoryDirectoryBuilderResult.Builder resultBuilder,
    final Map<URI, Path> uriToFile,
    final Repository initialRepository)
  {
    final var limitReleasesOpt = configuration.limitReleases();
    if (limitReleasesOpt.isPresent()) {
      final var limitRelease = limitReleasesOpt.getAsInt();
      final var limitedPackages = new ArrayList<RepositoryItemType>(32);
      final var byName = initialRepository.itemsById();
      final var ignored = new HashSet<Path>();

      for (final var key : byName.keySet()) {
        final var versions = byName.get(key);
        final var limit = Math.min(limitRelease, versions.size());
        final var start = versions.size() - limit;
        final var limited = versions.subList(start, versions.size());
        limitedPackages.addAll(limited);

        for (var index = 0; index < start; ++index) {
          final var file = uriToFile.get(versions.get(index).source());
          ignored.add(file);
        }
      }
      resultBuilder.setIgnoredAPKs(ignored);
      resultBuilder.setRepository(initialRepository.withItems(limitedPackages));
    } else {
      resultBuilder.setIgnoredAPKs(Collections.emptyList());
      resultBuilder.setRepository(initialRepository);
    }

    return resultBuilder.build();
  }

  @Override
  public RepositoryDirectoryBuilderResult build(
    final RepositoryDirectoryBuilderConfiguration configuration)
    throws IOException
  {
    Objects.requireNonNull(configuration, "configuration");

    final var repositoryBuilder =
      Repository.builder()
        .setId(configuration.uuid())
        .setSelf(configuration.self())
        .setUpdated(LocalDateTime.now())
        .setTitle(configuration.title());

    final List<Path> files;
    try (var stream = Files.list(configuration.path())) {
      files = stream.sorted().collect(Collectors.toList());
    }

    final var resultBuilder =
      RepositoryDirectoryBuilderResult.builder();

    final var uriToFile = new TreeMap<URI, Path>();
    for (final var file : files) {
      if (appearsToBeOPDSManifest(file)) {
        final var repositoryPackage = this.buildOPDSPackageOfFile(file);
        uriToFile.put(repositoryPackage.source(), file);
        repositoryBuilder.addItems(repositoryPackage);
        continue;
      }
      if (appearsToBeAPK(file)) {
        final var repositoryPackage = buildAndroidPackageOfFile(file);
        uriToFile.put(repositoryPackage.source(), file);
        repositoryBuilder.addItems(repositoryPackage);
      }
    }

    final var initialRepository = repositoryBuilder.build();
    return trimOldReleases(
      configuration,
      resultBuilder,
      uriToFile,
      initialRepository);
  }
}
