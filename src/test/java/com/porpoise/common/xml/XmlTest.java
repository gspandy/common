package com.porpoise.common.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.validation.Schema;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.google.common.io.Resources;

/**
 * Tests for {@link Xml}
 */
public class XmlTest {

    /**
     * test {@link Xml#xsd(InputStream)} can produce a schema which will resolve it's xs:import declarations from the
     * classpath
     * 
     * @throws IOException
     * @throws SAXException
     */
    @Test
    public void testXsdCanProduceASchemaWhichCanValidateXml() throws IOException, SAXException {
        final InputStream input = Resources.getResource("xsd/test-one.xsd").openStream();

        // call the method under test
        final Schema schema = Xml.xsd(input);

        // calls the method under test
        validate(schema, "eight");

        try {
            // calls the method under test
            validate(schema, "this content is too long");
            Assert.fail("should fail validation");
        } catch (final SAXParseException e) {
            // expected
        }
    }

    private static void validate(final Schema schema, final String value) throws SAXException, IOException {
        schema.newValidator().validate(src(value));
    }

    private static Source src(final String value) {
        return Xml.xmlAsSource(newXml(value));
    }

    private static String newXml(final String value) {
        return String.format("<root %s xmlns=\"http://www.porpoiseltd.com\"><value>%s</value></root>", Xml.XSI, value);
    }
}
