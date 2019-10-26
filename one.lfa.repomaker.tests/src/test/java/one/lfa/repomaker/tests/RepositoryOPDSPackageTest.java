package one.lfa.repomaker.tests;

import nl.jqno.equalsverifier.EqualsVerifier;
import one.lfa.repomaker.api.RepositoryOPDSPackage;
import org.junit.jupiter.api.Test;

public final class RepositoryOPDSPackageTest
{
  @Test
  public void testEquals()
  {
    EqualsVerifier.forClass(RepositoryOPDSPackage.class)
      .withNonnullFields("id", "versionCode", "versionName", "name", "source", "hash")
      .verify();
  }
}
