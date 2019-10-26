package one.lfa.repomaker.tests;

import nl.jqno.equalsverifier.EqualsVerifier;
import one.lfa.repomaker.api.RepositoryAndroidPackage;
import org.junit.jupiter.api.Test;

public final class RepositoryAndroidPackageTest
{
  @Test
  public void testEquals()
  {
    EqualsVerifier.forClass(RepositoryAndroidPackage.class)
      .withNonnullFields("id", "versionCode", "versionName", "name", "source", "hash")
      .verify();
  }
}
