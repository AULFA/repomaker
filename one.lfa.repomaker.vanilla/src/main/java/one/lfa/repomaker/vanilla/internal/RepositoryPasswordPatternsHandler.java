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

package one.lfa.repomaker.vanilla.internal;

import com.io7m.blackthorne.api.BTElementHandlerConstructorType;
import com.io7m.blackthorne.api.BTElementHandlerType;
import com.io7m.blackthorne.api.BTElementParsingContextType;
import com.io7m.blackthorne.api.BTQualifiedName;
import one.lfa.repomaker.api.RepositoryPasswordPattern;
import one.lfa.repomaker.api.RepositoryPasswordPatterns;

import java.util.Map;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static one.lfa.repomaker.vanilla.RepositoryPasswordPatternsSchemas.name1;

public final class RepositoryPasswordPatternsHandler implements
  BTElementHandlerType<Object, RepositoryPasswordPatterns>
{
  private RepositoryPasswordPatterns.Builder patterns;

  public RepositoryPasswordPatternsHandler(
    final BTElementParsingContextType context)
  {
    this.patterns = RepositoryPasswordPatterns.builder();
  }

  @Override
  public Map<BTQualifiedName, BTElementHandlerConstructorType<?, ?>> onChildHandlersRequested(
    final BTElementParsingContextType context)
  {
    return ofEntries(
      entry(
        name1("PasswordPattern"),
        RepositoryPasswordPatternHandler::new)
    );
  }

  @Override
  public void onChildValueProduced(
    final BTElementParsingContextType context,
    final Object result)
  {
    if (result instanceof RepositoryPasswordPattern) {
      this.patterns.addPatterns((RepositoryPasswordPattern) result);
      return;
    }

    throw new IllegalStateException(String.format(
      "Unrecognized value: %s",
      result)
    );
  }

  @Override
  public RepositoryPasswordPatterns onElementFinished(
    final BTElementParsingContextType context)
  {
    return this.patterns.build();
  }
}
