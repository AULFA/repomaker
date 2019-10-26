package au.org.libraryforall.repomaker.cmdline;

final class NTLogLevelUnrecognized extends RuntimeException
{
  NTLogLevelUnrecognized(final String message)
  {
    super(message);
  }
}
