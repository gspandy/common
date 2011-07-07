package com.porpoise.common.xml;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * resolver which will resolve imported schema files to files on the classpath
 */
class ClasspathResolver implements LSResourceResolver {

    /*
     * (non-Javadoc)
     * 
     * @see org.w3c.dom.ls.LSResourceResolver#resolveResource(java.lang.String, java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public LSInput resolveResource(final String type, final String namespaceURI, final String publicId,
            final String systemId, final String baseURI) {
        final LSInput lsInput = getLsInput();
        lsInput.setByteStream(getClass().getResourceAsStream("/" + systemId));
        lsInput.setSystemId(systemId);
        return lsInput;
    }

    LSInput getLsInput() {
        final DOMImplementationLS ls = (DOMImplementationLS) registry().getDOMImplementation("LS 3.0");
        return ls.createLSInput();
    }

    private DOMImplementationRegistry registry() {
        try {
            return DOMImplementationRegistry.newInstance();
        } catch (final ClassCastException e) {
            throw new RuntimeException(e);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (final InstantiationException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
