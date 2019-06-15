package au.org.libraryforall.repomaker.vanilla;

import au.org.libraryforall.repomaker.api.Repository;
import au.org.libraryforall.repomaker.api.RepositoryPackage;
import au.org.libraryforall.repomaker.serializer.api.RepositorySerializerType;
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

public final class RepositorySerializer implements RepositorySerializerType
{
  private static final String NAMESPACE = "urn:au.org.libraryforall.updater.repository.xml:1.0";

  private final Repository repository;
  private final URI target;
  private final OutputStream stream;
  private final DocumentBuilderFactory documentBuilders;

  RepositorySerializer(
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
    final var root = document.createElementNS(NAMESPACE, "r:repository");

    root.setAttribute("id", repository.id().toString());
    root.setAttribute("updated", repository.updated().toString());
    root.setAttribute("title", repository.title());
    root.setAttribute("self", repository.self().toString());

    for (final var pack : repository.packages()) {
      root.appendChild(ofPackage(document, pack));
    }

    return root;
  }

  private static Element ofPackage(
    final Document document,
    final RepositoryPackage pack)
  {
    final var root = document.createElementNS(NAMESPACE, "r:package");

    root.setAttribute("id", pack.id());
    root.setAttribute("name", pack.name());
    root.setAttribute("versionName", pack.versionName());
    root.setAttribute("versionCode", Integer.toString(pack.versionCode()));
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
