package one.lfa.repomaker.tests;

import nl.jqno.equalsverifier.EqualsVerifier;
import one.lfa.repomaker.api.Repository;
import org.junit.jupiter.api.Test;

public final class RepositoryTest
{
  @Test
  public void testEquals()
  {
    EqualsVerifier.forClass(Repository.class)
      .withNonnullFields("id", "title", "updated", "items", "self", "itemsById")
      .verify();
  }
}
