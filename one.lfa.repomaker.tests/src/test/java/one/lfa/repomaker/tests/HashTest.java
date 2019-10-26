package one.lfa.repomaker.tests;

import nl.jqno.equalsverifier.EqualsVerifier;
import one.lfa.repomaker.api.Hash;
import org.junit.jupiter.api.Test;

public final class HashTest
{
  @Test
  public void testEquals()
  {
    EqualsVerifier.forClass(Hash.class)
      .withNonnullFields("text")
      .verify();
  }
}
