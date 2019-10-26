package one.lfa.repomaker.cmdline;

import com.beust.jcommander.IStringConverter;

import java.util.UUID;

/**
 * A converter for UUID values.
 */

public final class UUIDConverter implements IStringConverter<UUID>
{
  /**
   * Construct a converter.
   */

  public UUIDConverter()
  {

  }

  @Override
  public UUID convert(final String value)
  {
    return UUID.fromString(value);
  }
}
