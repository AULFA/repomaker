package au.org.libraryforall.repomaker.api;

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.util.regex.Pattern;

/**
 * A cryptographic hash of a piece of data.
 */

@ImmutablesStyleType
@Value.Immutable
public interface HashType
{
  /**
   * The pattern defining valid hash values.
   */

  Pattern VALID_HASH = Pattern.compile("[a-f0-9]{64}");

  /**
   * @return The hash algorithm used
   */

  default String algorithm()
  {
    return "SHA-256";
  }

  /**
   * @return A lowercase, ASCII-encoded hash value
   */

  String text();
}
