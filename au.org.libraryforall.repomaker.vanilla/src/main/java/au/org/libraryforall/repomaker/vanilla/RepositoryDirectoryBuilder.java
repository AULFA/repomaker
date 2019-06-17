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

package au.org.libraryforall.repomaker.vanilla;

import au.org.libraryforall.repomaker.api.Hash;
import au.org.libraryforall.repomaker.api.Repository;
import au.org.libraryforall.repomaker.api.RepositoryDirectoryBuilderConfiguration;
import au.org.libraryforall.repomaker.api.RepositoryDirectoryBuilderResult;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
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
      if (appearsToBeAPK(file)) {
        final var repositoryPackage = buildPackageOfFile(file);
        uriToFile.put(repositoryPackage.source(), file);
        repositoryBuilder.addPackages(repositoryPackage);
      }
    }

    final var initialRepository = repositoryBuilder.build();

    final var limitReleasesOpt = configuration.limitReleases();
    if (limitReleasesOpt.isPresent()) {
      final var limitRelease = limitReleasesOpt.getAsInt();
      final var limitedPackages = new ArrayList<RepositoryPackage>(32);
      final var byName = initialRepository.packagesByName();
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
      resultBuilder.setRepository(initialRepository.withPackages(limitedPackages));
    } else {
      resultBuilder.setIgnoredAPKs(Collections.emptyList());
      resultBuilder.setRepository(initialRepository);
    }

    return resultBuilder.build();
  }
}
