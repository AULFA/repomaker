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

import one.lfa.repomaker.api.Repository;
import one.lfa.repomaker.api.RepositoryAndroidPackage;
import one.lfa.repomaker.api.RepositoryOPDSPackage;
import one.lfa.repomaker.serializer.api.RepositorySerializerType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Objects;
import java.util.Properties;

/**
 * A repository serializer.
 */

public final class RepositorySerializerV2 implements RepositorySerializerType
{
  private static final String NAMESPACE = "urn:au.org.libraryforall.updater.repository.xml:2.0";

  private final Repository repository;
  private final URI target;
  private final OutputStream stream;
  private final DocumentBuilderFactory documentBuilders;

  RepositorySerializerV2(
    final Repository in_repository,
    final URI in_target,
    final OutputStream in_stream)
  {
    this.repository =
      Objects.requireNonNull(in_repository, "repository");
    this.target =
      Objects.requireNonNull(in_target, "target");
    this.stream =
      Objects.requireNonNull(in_stream, "stream");
    this.documentBuilders =
      DocumentBuilderFactory.newInstance();
  }

  private static Element ofRepository(
    final Document document,
    final Repository repository)
  {
    final var root = document.createElementNS(NAMESPACE, "r:Repository");

    root.setAttribute("id", repository.id().toString());
    root.setAttribute("updated", repository.updated().toString());
    root.setAttribute("title", repository.title());
    root.setAttribute("self", repository.self().toString());

    for (final var pack : repository.items()) {
      if (pack instanceof RepositoryAndroidPackage) {
        root.appendChild(ofAndroidPackage(document, (RepositoryAndroidPackage) pack));
      } else if (pack instanceof RepositoryOPDSPackage) {
        root.appendChild(ofOPDSPackage(document, (RepositoryOPDSPackage) pack));
      }
    }
    return root;
  }

  private static Element ofOPDSPackage(
    final Document document,
    final RepositoryOPDSPackage pack)
  {
    final var root = document.createElementNS(NAMESPACE, "r:OPDSPackage");
    root.setAttribute("id", pack.id());
    root.setAttribute("name", pack.name());
    root.setAttribute("versionName", pack.versionName());
    root.setAttribute("versionCode", Long.toString(pack.versionCode()));
    root.setAttribute("sha256", pack.hash().text());
    root.setAttribute("source", pack.source().toString());
    return root;
  }

  private static Element ofAndroidPackage(
    final Document document,
    final RepositoryAndroidPackage pack)
  {
    final var root = document.createElementNS(NAMESPACE, "r:AndroidPackage");
    root.setAttribute("id", pack.id());
    root.setAttribute("name", pack.name());
    root.setAttribute("versionName", pack.versionName());
    root.setAttribute("versionCode", Long.toString(pack.versionCode()));
    root.setAttribute("sha256", pack.hash().text());
    root.setAttribute("source", pack.source().toString());
    return root;
  }

  private Document newDocument()
    throws ParserConfigurationException
  {
    final var builder = this.documentBuilders.newDocumentBuilder();
    final var document = builder.newDocument();
    document.setStrictErrorChecking(true);
    document.setXmlStandalone(true);
    return document;
  }

  @Override
  public void serialize()
    throws IOException
  {
    try {
      final var document = this.newDocument();
      final var root = ofRepository(document, this.repository);
      document.appendChild(root);

      final var factory = TransformerFactory.newInstance();
      final var transformer = factory.newTransformer();
      final var outFormat = new Properties();
      outFormat.setProperty(OutputKeys.INDENT, "yes");
      outFormat.setProperty(OutputKeys.METHOD, "xml");
      outFormat.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      outFormat.setProperty(OutputKeys.VERSION, "1.0");
      outFormat.setProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperties(outFormat);

      final var domSource = new DOMSource(document);
      final var result = new StreamResult(this.stream);
      transformer.transform(domSource, result);

      this.stream.flush();
    } catch (final ParserConfigurationException | TransformerException e) {
      throw new IOException(e);
    }
  }
}
