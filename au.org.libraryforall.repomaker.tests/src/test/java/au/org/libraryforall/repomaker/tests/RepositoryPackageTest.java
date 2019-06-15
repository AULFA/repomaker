package au.org.libraryforall.repomaker.tests;

import au.org.libraryforall.repomaker.api.RepositoryPackage;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public final class RepositoryPackageTest
{
  @Test
  public void testEquals()
  {
    EqualsVerifier.forClass(RepositoryPackage.class)
      .withNonnullFields("id", "versionCode", "versionName", "name", "source", "hash")
      .verify();
  }
}
