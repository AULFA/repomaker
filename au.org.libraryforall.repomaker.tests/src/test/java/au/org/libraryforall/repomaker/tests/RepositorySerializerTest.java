package au.org.libraryforall.repomaker.tests;

import au.org.libraryforall.repomaker.api.Hash;
import au.org.libraryforall.repomaker.api.Repository;
import au.org.libraryforall.repomaker.api.RepositoryPackage;
import au.org.libraryforall.repomaker.vanilla.RepositorySerializerProvider;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

public final class RepositorySerializerTest
{
  private static final Logger LOG = LoggerFactory.getLogger(RepositorySerializerTest.class);

  @Test
  public void testValidate()
    throws Exception
  {
    final var package0 =
      RepositoryPackage.builder()
        .setHash(
          Hash.builder()
            .setText("01ba4719c80b6fe911b091a7c05124b64eeece964e09c058ef8f9805daca546b")
            .build())
        .setVersionName("1.0.0")
        .setVersionCode(1)
        .setSource(URI.create("urn:package-0"))
        .setName("Package 0")
        .setId("com.example.package0")
        .build();

    final var package1 =
      RepositoryPackage.builder()
        .setHash(
          Hash.builder()
            .setText("73cb3858a687a8494ca3323053016282f3dad39d42cf62ca4e79dda2aac7d9ac")
            .build())
        .setVersionName("2.0.0")
        .setVersionCode(2)
        .setSource(URI.create("urn:package-2"))
        .setName("Package 2")
        .setId("com.example.package2")
        .build();

    final var package2 =
      RepositoryPackage.builder()
        .setHash(
          Hash.builder()
            .setText("3bb2abb69ebb27fbfe63c7639624c6ec5e331b841a5bc8c3ebc10b9285e90877")
            .build())
        .setVersionName("3.0.0")
        .setVersionCode(3)
        .setSource(URI.create("urn:package-3"))
        .setName("Package 3")
        .setId("com.example.package3")
        .build();

    final var repository =
      Repository.builder()
        .setUpdated(LocalDateTime.now())
        .setId(UUID.randomUUID())
        .setSelf(URI.create("urn:self"))
        .setTitle("Releases")
        .addPackages(package0)
        .addPackages(package1)
        .addPackages(package2)
        .build();

    final var output =
      new ByteArrayOutputStream();
    final var provider =
      new RepositorySerializerProvider();
    final var serializer =
      provider.createSerializer(repository, URI.create("urn:x"), output);

    serializer.serialize();

    try (var stream = new ByteArrayInputStream(output.toByteArray())) {
      final var schemaFactory = SchemaFactory.newDefaultInstance();
      final var schema =
        schemaFactory.newSchema(
          RepositorySerializerTest.class.getResource(
            "/au/org/libraryforall/repomaker/tests/schema-1.0.xsd"));

      final var validator = schema.newValidator();
      validator.validate(new StreamSource(stream));
    }
  }
}