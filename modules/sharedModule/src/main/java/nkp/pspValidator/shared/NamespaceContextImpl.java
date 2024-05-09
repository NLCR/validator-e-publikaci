package nkp.pspValidator.shared;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.*;

/**
 * Created by Martin Řehánek on 17.11.16.
 */
public class NamespaceContextImpl implements NamespaceContext {
    private final Map<String, String> namespaceByPrefix = new HashMap<>();
    private String defaultNsUri = null;

    public void setNamespace(String prefix, String uri) {
        //System.err.println("setNamespace: '" + prefix + "': " + uri);
        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            this.defaultNsUri = uri;
            namespaceByPrefix.put(prefix, uri);
        } else {
            if (namespaceByPrefix.containsKey(prefix)) {
                throw new IllegalStateException(String.format("prefix '%s' je již registrován a to pro uri '%s'", prefix, namespaceByPrefix.get(prefix)));
            } else {
                namespaceByPrefix.put(prefix, uri);
            }
        }
    }

    @Override
    public String getNamespaceURI(String prefix) {
        //System.err.println("getNamespaceURI: " + prefix);
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        } else if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) { //default NS
            if (defaultNsUri != null) {
                return defaultNsUri;
            } else {
                return XMLConstants.NULL_NS_URI;
            }
        } else if (prefix.equals(XMLConstants.XML_NS_PREFIX)) { //xmln -> http://www.w3.org/XML/1998/namespace
            return XMLConstants.XML_NS_URI;
        } else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) { //xmlns -> http://www.w3.org/2000/xmlns/
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        } else if (namespaceByPrefix.containsKey(prefix)) { //bound Ns
            return namespaceByPrefix.get(prefix);
        } else { //unbound NS
            return XMLConstants.NULL_NS_URI;
        }
    }

    @Override
    public String getPrefix(String namespaceURI) {
        //System.err.println("getPrefix: " + namespaceURI);
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI cannot be null");
        } else if (this.defaultNsUri != null && this.defaultNsUri.equals(namespaceURI)) { //default NS
            return XMLConstants.DEFAULT_NS_PREFIX;
        } else if (namespaceByPrefix.values().contains(namespaceURI)) { //bound NS
            for (String prefix : namespaceByPrefix.keySet()) {
                if (namespaceByPrefix.get(prefix).equals(namespaceURI)) {
                    return prefix;
                }
            }
            //should never happen
            throw new RuntimeException();
        } else if (namespaceURI.equals(XMLConstants.XML_NS_URI)) { //http://www.w3.org/XML/1998/namespace
            return XMLConstants.XML_NS_PREFIX;
        } else if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) { //http://www.w3.org/2000/xmlns/
            return XMLConstants.XMLNS_ATTRIBUTE;
        } else { //unbound NS
            return null;
        }
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        Set<String> prefixes = new HashSet<>();
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI cannot be null");
        }
        if (namespaceURI.equals(XMLConstants.XML_NS_URI)) { //http://www.w3.org/XML/1998/namespace -> xmln
            prefixes.add(XMLConstants.XML_NS_PREFIX);
        }
        if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) { //http://www.w3.org/2000/xmlns/ -> xmlns
            prefixes.add(XMLConstants.XMLNS_ATTRIBUTE);
        }
        for (String prefix : namespaceByPrefix.keySet()) {
            if (namespaceByPrefix.get(prefix).equals(namespaceURI)) {
                prefixes.add(prefix);
            }
        }
        if (defaultNsUri != null && namespaceURI.equals(defaultNsUri)) {
            prefixes.add(XMLConstants.DEFAULT_NS_PREFIX);
        }
        return prefixes.iterator();
    }

}
