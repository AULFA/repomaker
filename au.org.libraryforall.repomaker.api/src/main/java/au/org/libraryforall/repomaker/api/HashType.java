/*
 * Copyright © 2019 Library For All
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
