package one.lfa.repomaker.cmdline;

final class NTLogLevelUnrecognized extends RuntimeException
{
  NTLogLevelUnrecognized(final String message)
  {
    super(message);
  }
}
