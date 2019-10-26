package au.org.libraryforall.repomaker.cmdline;

import com.beust.jcommander.IStringConverter;

import java.util.Objects;

/**
 * A converter for {@link NTLogLevel} values.
 */

public final class NTLogLevelConverter implements IStringConverter<NTLogLevel>
{
  /**
   * Construct a new converter.
   */

  public NTLogLevelConverter()
  {

  }

  @Override
  public NTLogLevel convert(final String value)
  {
    for (final var v : NTLogLevel.values()) {
      if (Objects.equals(value, v.getName())) {
        return v;
      }
    }

    throw new NTLogLevelUnrecognized(
      "Unrecognized verbosity level: " + value);
  }
}
