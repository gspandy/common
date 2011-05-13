package com.porpoise.common.xml;

import java.io.InputStream;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * 
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
        final InputStream is = getClass().getResourceAsStream("/" + systemId);
        lsInput.setByteStream(is);
        lsInput.setSystemId(systemId);
        return lsInput;
    }

    /**
     * @return
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    LSInput getLsInput() {
        DOMImplementationRegistry registry;
        try {
            registry = DOMImplementationRegistry.newInstance();
        } catch (final ClassCastException e) {
            throw new RuntimeException(e);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (final InstantiationException e) {
            throw new RuntimeException(e);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        final DOMImplementation impl = registry.getDOMImplementation("LS 3.0");
        final DOMImplementationLS ls = (DOMImplementationLS) impl;
        final LSInput lsInput = ls.createLSInput();
        return lsInput;
    }

}
