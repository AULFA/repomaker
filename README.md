repomaker
===

[![Maven Central](https://img.shields.io/maven-central/v/one.lfa/one.lfa.repomaker.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22one.lfa.repomaker%22)
[![Maven Central (snapshot)](https://img.shields.io/nexus/s/https/oss.sonatype.org/one.lfa/one.lfa.repomaker.svg?style=flat-square)](https://oss.sonatype.org/content/repositories/snapshots/one/lfa/repomaker/)
[![Codecov](https://img.shields.io/codecov/c/github/AULFA/repomaker.svg?style=flat-square)](https://codecov.io/gh/AULFA/repomaker)

A lightweight repository generator for the [updater](https://github.com/AULFA/updater).

![repomaker](./src/site/resources/repomaker.jpg?raw=true)

| JVM             | Platform | Status |
|-----------------|----------|--------|
| OpenJDK LTS     | Linux    | [![Build (OpenJDK LTS, Linux)](https://img.shields.io/github/workflow/status/AULFA/repomaker/main-openjdk_lts-linux)](https://github.com/AULFA/repomaker/actions?query=workflow%3Amain-openjdk_lts-linux) |
| OpenJDK Current | Linux    | [![Build (OpenJDK Current, Linux)](https://img.shields.io/github/workflow/status/AULFA/repomaker/main-openjdk_current-linux)](https://github.com/AULFA/repomaker/actions?query=workflow%3Amain-openjdk_current-linux)
| OpenJDK Current | Windows  | [![Build (OpenJDK Current, Windows)](https://img.shields.io/github/workflow/status/AULFA/repomaker/main-openjdk_current-windows)](https://github.com/AULFA/repomaker/actions?query=workflow%3Amain-openjdk_current-windows)

## Features

* Efficient generation of repository files from directories of APK files.
* Efficient generation of repository files from lists of OPDS package files.
* Directory-watching service for continuous generation and publishing of repository files.
* Well designed modular API for use in Java 11+ programs
* Lightweight: Designed to run with tiny (32mb+) JVM heaps on inexpensive VPS hosting.
* Command line interface

## Requirements

* Java 11+

## How To Build

```
$ mvn clean package
```

If the above fails, it's a bug. Report it!

## Usage

```
Usage: repomaker [options] [command] [command options]
  Options:
    --verbose
      Set the minimum logging verbosity level
      Default: info
      Possible Values: [trace, debug, info, warn, error]
  Commands:
    generate      Generate a single repository from a directory
      Usage: generate [options]
        Options:
        * --directory
            The directory that contains input APK files
        * --id
            The UUID that will be used to identify the repository
        * --output
            The output file
          --releases-per-package
            The number of releases per package to include (includes all 
            releases if not specified)
            Default: 2147483647
        * --source
            The source URI that will be used in the repository
        * --title
            The repository title
          --verbose
            Set the minimum logging verbosity level
            Default: info
            Possible Values: [trace, debug, info, warn, error]

    manage      Manage a single repository directory
      Usage: manage [options]
        Options:
        * --directory
            The directory that contains input APK files
        * --id
            The UUID that will be used to identify the repository
          --releases-delete-old
            Delete releases that are older than the limit specified by 
            --releases-per-package 
            Default: false
          --releases-per-package
            The number of releases per package to include (includes all 
            releases if not specified)
            Default: 2147483647
        * --source
            The source URI that will be used in the repository
        * --title
            The repository title
          --verbose
            Set the minimum logging verbosity level
            Default: info
            Possible Values: [trace, debug, info, warn, error]
```

The `repomaker` tool generates an XML file suitable for consumption by the [LFA Updater](https://github.com/AULFA/updater)
application. Currently, it features two commands: `generate` and `manage`.

The `repomaker` tool uses [jcommander](http://jcommander.org) to
parse command line arguments and therefore also supports placing
command line options into a file that can be referenced with `@`:

```
$ cat arguments.txt
manage
--directory
/out
--id
270f1914-2528-49e8-935c-694cf9779063
--source
https://example.com/releases.xml
--title
Releases
--verbose
debug
--releases-delete-old
--releases-per-package
3

$ java -jar repomaker.jar @arguments.txt
```

### Repository Generation

The `generate` command scans all APK files in a given directory and
generates a repository file.

### Repository Management

The `management` command supervises a single directory and, conceptually,
runs the `generate` command to regenerate a repository file in that directory
every time an APK file appears, disappears, or is modified. The command can,
via the `--releases-delete-old` option, delete old APK files when more than
`--releases-per-package` are present for a particular package.

The `management` command is designed to run under a process supervision system
such as [runit](http://smarden.org/runit/).

