package au.org.libraryforall.repomaker.api;

import com.io7m.immutables.styles.ImmutablesStyleType;
import org.immutables.value.Value;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A repository.
 */

@ImmutablesStyleType
@Value.Immutable
public interface RepositoryType
{
  /**
   * @return The unique ID of the repository
   */

  UUID id();

  /**
   * @return The title of the repository
   */

  String title();

  /**
   * @return The date/time that the repository was last updated
   */

  LocalDateTime updated();

  /**
   * @return The list of packages in the repository
   */

  List<RepositoryPackage> packages();

  /**
   * @return The URI of the repository
   */

  URI self();

  /**
   * @return The available packages organized by package name
   */

  @Value.Auxiliary
  @Value.Derived
  default Map<String, List<RepositoryPackage>> packagesByName()
  {
    final var map = new HashMap<String, List<RepositoryPackage>>(this.packages().size());
    for (final var pack : this.packages()) {
      var packs = map.get(pack.id());
      if (packs == null) {
        packs = new ArrayList<>(16);
      }
      packs.add(pack);
      map.put(pack.id(), packs);
    }

    for (final var key : map.keySet()) {
      final var packs = map.get(key);
      packs.sort(Comparator.comparingInt(RepositoryPackage::versionCode));
      map.put(key, Collections.unmodifiableList(packs));
    }

    return Collections.unmodifiableMap(map);
  }
}
