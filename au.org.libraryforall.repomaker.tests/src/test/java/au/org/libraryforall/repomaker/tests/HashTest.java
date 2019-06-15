package au.org.libraryforall.repomaker.tests;

import au.org.libraryforall.repomaker.api.Hash;
import nl.jqno.equalsverifier.EqualsVerifier;
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
