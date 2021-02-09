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

package one.lfa.repomaker.vanilla;

import com.io7m.blackthorne.api.BTQualifiedName;
import com.io7m.jxe.core.JXESchemaDefinition;
import com.io7m.jxe.core.JXESchemaResolutionMappings;

import java.net.URI;

/**
 * Schemas for password patterns files.
 */

public final class RepositoryPasswordPatternsSchemas
{
  /**
   * The version 1 schema.
   */

  public static final JXESchemaDefinition SCHEMA_1 =
    JXESchemaDefinition.of(
      URI.create("urn:one.lfa.repomaker.password_patterns.xml:1"),
      "passwordPatterns-1.xsd",
      RepositoryPasswordPatternsParser.class.getResource(
        "passwordPatterns-1.xsd")
    );

  /**
   * The set of schemas for all versions.
   */

  public static final JXESchemaResolutionMappings SCHEMAS =
    JXESchemaResolutionMappings.builder()
      .putMappings(SCHEMA_1.namespace(), SCHEMA_1)
      .build();

  /**
   * Construct a qualified name in the v1 namespace.
   *
   * @param localName The local element name
   *
   * @return The qualified name
   */

  public static BTQualifiedName name1(
    final String localName)
  {
    return BTQualifiedName.of(SCHEMA_1.namespace().toString(), localName);
  }

  private RepositoryPasswordPatternsSchemas()
  {

  }
}
