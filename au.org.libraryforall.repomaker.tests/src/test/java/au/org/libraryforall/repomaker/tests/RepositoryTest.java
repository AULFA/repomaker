package au.org.libraryforall.repomaker.tests;

import au.org.libraryforall.repomaker.api.Repository;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public final class RepositoryTest
{
  @Test
  public void testEquals()
  {
    EqualsVerifier.forClass(Repository.class)
      .withNonnullFields("id", "title", "updated", "packages", "self", "packagesByName")
      .verify();
  }
}
