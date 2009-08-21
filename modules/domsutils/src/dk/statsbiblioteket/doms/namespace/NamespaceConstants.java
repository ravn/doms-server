package dk.statsbiblioteket.doms.namespace;

import dk.statsbiblioteket.util.qa.QAInfo;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** Namespace constants used in DOMS. Not yet complete. */
@QAInfo(level = QAInfo.Level.NORMAL,
        state = QAInfo.State.QA_NEEDED,
        author = "kfc",
        reviewers = {"jrg"})
public class NamespaceConstants {
    public static final String NAMESPACE_RDF
            = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    public static final String NAMESPACE_RDFS
            = "http://www.w3.org/2000/01/rdf-schema#";
    public static final String NAMESPACE_OWL = "http://www.w3.org/2002/07/owl#";
    public static final String NAMESPACE_XML_SCHEMA_INSTANCE
            = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String NAMESPACE_DC
            = "http://purl.org/dc/elements/1.1/";
    public static final String NAMESPACE_DCTERMS = "http://purl.org/dc/terms/";
    public static final String NAMESPACE_MARC_RELATORS
            = "http://www.loc.gov/loc.terms/relators/";
    public static final String NAMESPACE_BLAP
            = " http://labs.bl.uk/metadata/blap/terms.html";
    public static final String NAMESPACE_OAI
            = "http://www.openarchives.org/OAI/2.0/";
    public static final String NAMESPACE_OAIDC
            = "http://www.openarchives.org/OAI/2.0/oai_dc/";
    public static final String NAMESPACE_FEDORA_MODEL
            = "info:fedora/fedora-system:def/model#";
    public static final String NAMESPACE_FOXML
            = "info:fedora/fedora-system:def/foxml#";
    public static final String NAMESPACE_DS_COMPOSITE
            = "info:fedora/fedora-system:def/dsCompositeModel#";
    public static final String NAMESPACE_PREMIS = "info:lc/xmlns/premis-v2";
    public static final String NAMESPACE_RELATIONS
            = "http://doms.statsbiblioteket.dk/relations/default/0/1/#";
    public static final String NAMESPACE_VIEW
            = "http://doms.statsbiblioteket.dk/types/view/0/2/#";
    public static final String NAMESPACE_SCHEMA
            = "http://doms.statsbiblioteket.dk/types/dscompositeschema/0/1/#";
    public static final String NAMESPACE_CHARACTERISATION
            = "http://doms.statsbiblioteket.dk/types/characterisation/0/2/#";
    public static final String NAMESPACE_GUI
            = "http://doms.statsbiblioteket.dk/types/dscompositeschema/guirepresentation/0/1/#";
    public static final String NAMESPACE_DOMS_ORIGIN
            = "http://doms.statsbiblioteket.dk/types/doms-origin/0/1/#";
    public static final String NAMESPACE_DIGITAL_OBJECT_BUNDLE
            = "http://doms.statsbiblioteket.dk/types/digitalobjectbundle/0/1/#";

    public static final String[][] NAMESPACE_TABLE = {
            {XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI},
            {XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI},
            {XMLConstants.DEFAULT_NS_PREFIX, XMLConstants.NULL_NS_URI},
            {"rdf", NAMESPACE_RDF}, {"rdfs", NAMESPACE_RDFS},
            {"owl", NAMESPACE_OWL}, {"xsi", NAMESPACE_XML_SCHEMA_INSTANCE},
            {"dc", NAMESPACE_DC}, {"dcterms", NAMESPACE_DCTERMS},
            {"marcrelator", NAMESPACE_MARC_RELATORS}, {"blap", NAMESPACE_BLAP},
            {"oai", NAMESPACE_OAI}, {"oai_dc", NAMESPACE_OAIDC},
            {"view", NAMESPACE_VIEW}, {"fedora-model", NAMESPACE_FEDORA_MODEL},
            {"foxml", NAMESPACE_FOXML}, {"ds", NAMESPACE_DS_COMPOSITE},
            {"premis", NAMESPACE_PREMIS}, {"doms", NAMESPACE_RELATIONS},
            {"view", NAMESPACE_VIEW}, {"schema", NAMESPACE_SCHEMA},
            {"charac", NAMESPACE_CHARACTERISATION}, {"gui", NAMESPACE_GUI},
            {"doms-origin", NAMESPACE_DOMS_ORIGIN},
            {"dobundle", NAMESPACE_DIGITAL_OBJECT_BUNDLE}};

    public static final NamespaceContext DOMS_NAMESPACE_CONTEXT
            = new NamespaceContext() {
        Map<String, String> nsPrefixMap = new HashMap<String, String>(
                NAMESPACE_TABLE.length);
        Map<String, String> inverseNsPrefixMap = new HashMap<String, String>(
                NAMESPACE_TABLE.length);

        {
            for (String[] pair : NAMESPACE_TABLE) {
                nsPrefixMap.put(pair[0], pair[1]);
                inverseNsPrefixMap.put(pair[0], pair[1]);
            }
        }

        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new IllegalArgumentException("Prefix is null");
            }
            String uri = nsPrefixMap.get(prefix);
            if (uri != null) {
                return uri;
            } else {
                return XMLConstants.NULL_NS_URI;
            }
        }

        public String getPrefix(String namespaceURI) {
            if (namespaceURI == null) {
                throw new IllegalArgumentException(
                        "namespaceURI is null");
            }
            return inverseNsPrefixMap.get(namespaceURI);
        }

        public Iterator getPrefixes(String namespaceURI) {
            if (namespaceURI == null) {
                throw new IllegalArgumentException(
                        "namespaceURI is null");
            }
            String prefix = getPrefix(namespaceURI);
            if (prefix == null) {
                return Collections.emptyList().iterator();
            } else {
                return Collections.singletonList(prefix).iterator();
            }
        }
    };
}
