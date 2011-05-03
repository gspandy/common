package com.porpoise.common.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;

/**
 * Xml utilities
 */
public enum Xml {
	; // unintantiable

	private static final DocumentBuilderFactory	BUILDER_FACTOR	    = DocumentBuilderFactory.newInstance();

	private static final TransformerFactory	    TRANSFORMER_FACTORY	= TransformerFactory.newInstance();

	public static Collection<Node> evalXPath(final String xml, final String xpath) {
		final Document doc = xmlToDoc(xml);
		final NodeList result = findW3C(doc, xpath);
		return extractNodes(result);
	}

	/**
	 * @param result
	 * @return
	 */
	public static Collection<Node> extractNodes(final NodeList result) {
		final Collection<Node> found = Lists.newArrayList();
		for (int i = 0; i < result.getLength(); i++) {
			found.add(result.item(i));
		}
		return found;
	}

	/**
	 * @param doc
	 *            the xml to search
	 * @param xpathQuery
	 *            the xpath query
	 * @return the result
	 */
	public static NodeList findW3C(final org.w3c.dom.Document doc, final String xpathQuery) {
		final XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			final XPathExpression expr = xpath.compile(xpathQuery);
			return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		} catch (final XPathExpressionException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static org.w3c.dom.Document xmlToDoc(final String xml) {
		try {
			return BUILDER_FACTOR.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
		} catch (final ParserConfigurationException e) {
			throw new IllegalArgumentException(e);
		} catch (final SAXException e) {
			throw new IllegalArgumentException(e);
		} catch (final IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Format the given xml
	 * 
	 * @param xml
	 *            the xml to format
	 * @return the formatted xml
	 */
	public static String prettyPrint(final String xml) {
		final org.w3c.dom.Document node = xmlToDoc(xml);
		return prettyPrint(node);
	}

	/**
	 * @param node
	 * @return the node as a string
	 */
	public static String prettyPrint(final org.w3c.dom.Node node) {
		final StringWriter writer = prettyPrint(node, new StringWriter());
		return writer.getBuffer().toString();
	}

	public static <T extends Writer> T prettyPrint(final org.w3c.dom.Node doc, final T writer) {
		Transformer prettyPrintTransformer;
		try {
			prettyPrintTransformer = TRANSFORMER_FACTORY.newTransformer();
			prettyPrintTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
			prettyPrintTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

			prettyPrintTransformer.transform(new DOMSource(doc), new StreamResult(writer));
			writer.flush();
		} catch (final TransformerException e) {
			throw new IllegalStateException(e);
		} catch (final IOException e) {
			throw new IllegalStateException(e);
		}
		return writer;
	}
}
