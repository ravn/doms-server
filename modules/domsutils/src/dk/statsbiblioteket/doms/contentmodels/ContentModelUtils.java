package dk.statsbiblioteket.doms.contentmodels;

import dk.statsbiblioteket.doms.DomsUserToken;
import dk.statsbiblioteket.doms.exceptions.FedoraConnectionException;
import dk.statsbiblioteket.doms.exceptions.FedoraIllegalContentException;
import dk.statsbiblioteket.doms.fedora.FedoraUtils;
import dk.statsbiblioteket.doms.namespace.NamespaceConstants;
import dk.statsbiblioteket.doms.namespace.NamespaceUtils;
import dk.statsbiblioteket.util.qa.QAInfo;
import fedora.common.Models;
import fedora.server.types.gen.DatastreamDef;
import fedora.server.types.gen.ObjectProfile;
import fedora.server.types.gen.RelationshipTuple;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Utility methods for working with content models. */
@QAInfo(level = QAInfo.Level.NORMAL,
        state = QAInfo.State.QA_OK,
        author = "kfc",
        reviewers = {"jrg"})
public class ContentModelUtils {
    /** Utility class, do not instantiate. */
    private ContentModelUtils() {
    }

    /**
     * Get the compound content model for an object. This method will generate
     * an abstract representation of the Compund Content Model for an object,
     * including datastreams (with GUI and SCHEMA extensions), ontology, and
     * view information.
     *
     * The method can be used either on a PID of an object, in which case you
     * get the compound content model for the object, or a PID for a content
     * model, in which case you get the compound content model for the content
     * model and all its ancestors.
     *
     * @param pid The pid for an object or a content model.
     * @return The compound content model for the object or content model.
     *
     * @throws FedoraConnectionException     on trouble communicating with
     *                                       Fedora.
     * @throws FedoraIllegalContentException if content model contains illegal
     *                                       information (i.e. non-existing PIDs
     *                                       referred, illegal XML, etc.)
     */
    public static CompoundContentModel getCompoundContentModel(String pid) {
        // Initialise a model object
        DomsUserToken utils = new DomsUserToken();
        return getCompoundContentModel(pid, utils);
    }

    /**
     * Get the compound content model for an object. This method will generate
     * an abstract representation of the Compund Content Model for an object,
     * including datastreams (with GUI and SCHEMA extensions), ontology, and
     * view information.
     *
     * The method can be used either on a PID of an object, in which case you
     * get the compound content model for the object, or a PID for a content
     * model, in which case you get the compound content model for the content
     * model and all its ancestors.
     *
     * @param pid      The pid for an object or a content model.
     * @param user     The username to connect to fedora
     * @param password The password for said username
     * @param server   The url of the fedora server. Example http://localhost:8080/fedora
     * @return The compound content model for the object or content model.
     *
     * @throws FedoraConnectionException     on trouble communicating with
     *                                       Fedora.
     * @throws FedoraIllegalContentException if content model contains illegal
     *                                       information (i.e. non-existing PIDs
     *                                       referred, illegal XML, etc.)
     */
    public static CompoundContentModel getCompoundContentModel(String pid,
                                                               String user,
                                                               String password,
                                                               String server) {
        DomsUserToken utils = new DomsUserToken(user, password, server);
        return getCompoundContentModel(pid, utils);
    }

    /**
     * Get the compound content model for an object. This method will generate
     * an abstract representation of the Compund Content Model for an object,
     * including datastreams (with GUI and SCHEMA extensions), ontology, and
     * view information.
     *
     * The method can be used either on a PID of an object, in which case you
     * get the compound content model for the object, or a PID for a content
     * model, in which case you get the compound content model for the content
     * model and all its ancestors.
     *
     * @param pid       The pid for an object or a content model.
     * @param userToken The UserToken object to connect to Fedora.
     * @return The compound content model for the object or content model.
     *
     * @throws FedoraConnectionException     on trouble communicating with
     *                                       Fedora.
     * @throws FedoraIllegalContentException if content model contains illegal
     *                                       information (i.e. non-existing PIDs
     *                                       referred, illegal XML, etc.)
     */
    public static CompoundContentModel getCompoundContentModel(String pid,
                                                               DomsUserToken userToken) {

        CompoundContentModel model = new CompoundContentModel();

        // Get starting point
        List<String> pids = getBaseListOfPids(pid, userToken);

        // Update content model with info from all models.
        // Note: The list looped over is extended during the loop, do not use an
        // iterator!
        for (int i = 0; i < pids.size(); i++) {
            String p = pids.get(i);

            // TODO: Check for existance of object with PID p?

            // Find inheritance relations
            RelationshipTuple[] tuples;
            try {
                tuples = FedoraUtils.getAPIM(userToken).getRelationships(
                        p, ContentModelConstants.RELATION_EXTENDS_MODEL);
            } catch (RemoteException e) {
                throw new FedoraConnectionException(
                        "Error getting relations for '" + p + "'", e);
            }

            // Add extended models to work list, and add inheritance relation to
            // the compound model ontology
            if (tuples != null) {
                for (RelationshipTuple t : tuples) {
                    String tuplepid = t.getObject();
                    // Add PID to list to resolve
                    addPid(pids, tuplepid);
                    //Add inheritance to ontology
                    addInheritanceRule(model.getOntology(), p, tuplepid);
                }
            }

            // Process datastreams to find ONTOLOGY, VIEW, and
            // DS-COMPOSITE-MODEL
            DatastreamDef[] datastreamDefs;
            try {
                datastreamDefs = FedoraUtils.getAPIA(userToken)
                        .listDatastreams(p, null);
            } catch (RemoteException e) {
                throw new FedoraConnectionException(
                        "Error getting datastreams for '" + p + "'", e);
            }
            for (DatastreamDef def : datastreamDefs) {
                if (def.getID().equals(
                        ContentModelConstants.ONTOLOGY_DATASTREAM)) {
                    // Merge datastream ontology into compound model ontology
                    Document newOntology = FedoraUtils.getDatastreamAsDocument(
                            p, ContentModelConstants.ONTOLOGY_DATASTREAM,
                            userToken);
                    updateOntology(model.getOntology(), newOntology);
                } else if (def.getID().equals(
                        ContentModelConstants.DS_COMPOSITE_MODEL_DATASTREAM)) {
                    // Add DS-COMPOSITE-MODEL datastream information to compound
                    // model datastreams
                    Document dsCompositeXml = FedoraUtils
                            .getDatastreamAsDocument(
                                    p,
                                    ContentModelConstants.DS_COMPOSITE_MODEL_DATASTREAM,
                                    userToken);
                    addDatastream(
                            model.getDatastreams(), dsCompositeXml, p,
                            userToken);
                } else if (def.getID()
                        .equals(ContentModelConstants.VIEW_DATASTREAM)) {
                    // Include views into compund model views, merging views
                    // with the same name.
                    Document viewXml = FedoraUtils.getDatastreamAsDocument(
                            p, ContentModelConstants.VIEW_DATASTREAM,
                            userToken);

                    updateView(model.getView(), viewXml);
                }
            }

            // Check if this is the content model for a main object in some view
            setMainView(p, model.getView(), userToken);

        }

        model.setPids(pids);
        return model;
    }

    /**
     * Given a PID, find out if it is a Content Model PID, or an object PID.
     * Based on this, calculate the starting PIDs for generating a compound
     * model. For a content model, it is simply the given PID. For an object, it
     * is the list of content models for that PID.
     *
     * @param pid The object PID
     * @return List of PIDs for resolving content model.
     *
     * @throws FedoraConnectionException     on trouble communicating with
     *                                       Fedora
     * @throws FedoraIllegalContentException if pid cannot be found, or is a
     *                                       dissemination object.
     */
    private static List<String> getBaseListOfPids(String pid,
                                                  DomsUserToken utils) {
        // TODO: Consider sorting this list, with children before parents?
        // Get object profile from Fedora
        ObjectProfile objectProfile;
        try {
            objectProfile = FedoraUtils.getAPIA(utils)
                    .getObjectProfile(FedoraUtils.ensurePID(pid), null);
        } catch (RemoteException e) {
            throw new FedoraIllegalContentException(
                    "Unable to get pid '" + pid + "' from Fedora", e);
        }

        // Initialise list of base content models
        List<String> pids = new ArrayList<String>();
        List<String> models = Arrays.asList(objectProfile.getObjModels());
        if (models.contains(Models.CONTENT_MODEL_3_0.uri)) {
            // Content model, simply return this PID
            pids.add(pid);
        } else if (models.contains(Models.SERVICE_DEFINITION_3_0.uri) || models
                .contains(Models.SERVICE_DEPLOYMENT_3_0.uri)) {
            // Dissemination object, illegal
            throw new FedoraIllegalContentException(
                    "The object with pid '" + pid + "' is"
                            + " a dissemination object");
        } else {
            // Fedora object, add all content models of this object.
            for (String p : models) {
                addPid(pids, p);
            }
        }
        return pids;
    }

    /**
     * Add a pid to the list, if it is not already in the list.
     *
     * @param pids   The pid to add.
     * @param newpid The list to add to.
     */
    private static void addPid(List<String> pids, String newpid) {
        newpid = FedoraUtils.ensurePID(newpid);
        if (!pids.contains(newpid)) {
            pids.add(newpid);
        }
    }

    /**
     * Given two content model pids that are subclass related, add a rule to the
     * ontoloty stating this.
     *
     * @param ontology   The ontology to update.
     * @param subClass   The subclass.
     * @param superClass The superclass.
     */
    private static void addInheritanceRule(Document ontology, String subClass,
                                           String superClass) {
        subClass = FedoraUtils.ensureURI(subClass);
        superClass = FedoraUtils.ensureURI(superClass);

        Element inheritanceRule = ontology.createElementNS(
                NamespaceConstants.NAMESPACE_OWL,
                ContentModelConstants.RDF_PROPERTY_OWL_CLASS);
        inheritanceRule.setAttributeNS(
                NamespaceConstants.NAMESPACE_RDF,
                ContentModelConstants.RDF_PROPERTY_RDF_ABOUT, subClass);

        Element subClassElement = ontology.createElementNS(
                NamespaceConstants.NAMESPACE_RDFS,
                ContentModelConstants.RDF_PROPERTY_RDFS_SUB_CLASS_OF);
        subClassElement.setAttributeNS(
                NamespaceConstants.NAMESPACE_RDF,
                ContentModelConstants.RDF_PROPERTY_RDF_RESOURCE, superClass);

        inheritanceRule.appendChild(subClassElement);
        ontology.getDocumentElement().appendChild(inheritanceRule);
    }

    /**
     * Update an ontology with all content found in another ontology.
     *
     * @param ontology    The ontology to update.
     * @param newOntology The ontology with rules to add.
     */
    private static void updateOntology(Document ontology,
                                       Document newOntology) {
        NodeList nodes = newOntology.getDocumentElement().getChildNodes();
        for (int n = 0; n < nodes.getLength(); n++) {
            Node node = nodes.item(n);
            ontology.getDocumentElement()
                    .appendChild(ontology.importNode(node, true));
        }
    }

    /**
     * Parse a DS-COMPOSITE-MODEL structure, and add the result to a list of
     * datastreams, if a datastream with that ID is not already in it. The
     * extensions GUI and SCHEMA are parsed, and updated in Datastream if not
     * already present.
     *
     * @param datastreams    The list to add datastream to.
     * @param dsCompositeXml The datastream to parse.
     * @param pid            The pid of the object containing the datastream.
     */
    private static void addDatastream(List<Datastream> datastreams,
                                      Document dsCompositeXml, String pid,
                                      DomsUserToken utils) {
        NodeList xpathResult;
        try {
            xpathResult = NamespaceUtils.xpathQuery(
                    dsCompositeXml, "ds:dsCompositeModel/ds:dsTypeModel");
        } catch (XPathExpressionException e) {
            throw new Error("XPath expression did not evaluate", e);
        }

        // Run through all defined datastreams
        for (int i = 0; i < xpathResult.getLength(); i++) {
            // Get the ID of the datastream
            Node dsTypeModelNode = xpathResult.item(i);
            if (dsTypeModelNode.getNodeType() != Document.ELEMENT_NODE) {
                continue;
            }
            Element dsTypeModel = (Element) dsTypeModelNode;
            String id = dsTypeModel.getAttribute("ID");
            if (id == null || id.equals("")) {
                continue;
            }

            Datastream datastream = null;
            // Find datastream if already defined
            for (Datastream d : datastreams) {
                if (d.getName().equals(id)) {
                    // already in list, skip
                    datastream = d;
                    break;
                }
            }

            if (datastream == null) {
                // Prepare new datastream description.
                datastream = new Datastream();
                datastream.setName(id);
                // Add the defined datastream
                datastreams.add(datastream);
            }

            // Get mimetype, if not already set, and add it to the datastream
            if (datastream.getMimetypes().size() == 0) {
                addMimetypesToDatastream(datastream, dsTypeModel);
            }

            // Get formaturi, if not already set, and add it to the datastream
            if (datastream.getFormatUris().size() == 0) {
                addFormatUrisToDatastream(datastream, dsTypeModel);
            }

            // Get GUI extension, if not already set, and add it to the
            // datastream
            if (datastream.getGuiRepresentation() == null) {
                addGuiRepresentationToDatastream(datastream, dsTypeModel);
            }

            // Get schema, if not already set, and add it to the datastream
            if (datastream.getXmlSchema() == null) {
                addSchemaInformationToDatastream(
                        datastream, dsTypeModel, pid, utils);
            }
        }
    }

    /**
     * Given a datastream description from DS-COMPOSITE-MODEL, add information
     * about the mimetypes to the datastream model.
     *
     * @param datastream  The datastream to add mimetype information to.
     * @param dsTypeModel The dsTypeModel element to read the information from.
     */
    private static void addMimetypesToDatastream(Datastream datastream,
                                                 Element dsTypeModel) {
        NodeList mimetypeattrs;
        try {
            mimetypeattrs = NamespaceUtils
                    .xpathQuery(dsTypeModel, "ds:form/@MIME");
        } catch (XPathExpressionException e1) {
            throw new Error("XPath expression did not evaluate", e1);
        }
        if (mimetypeattrs.getLength() > 0) {
            for (int a = 0; a < mimetypeattrs.getLength(); a++) {
                if (mimetypeattrs.item(a).getNodeType()
                        == Document.ATTRIBUTE_NODE) {
                    datastream.getMimetypes().add(
                            mimetypeattrs.item(a).getNodeValue());
                }
            }
        }
    }

    /**
     * Given a datastream description from DS-COMPOSITE-MODEL, add information
     * about the format uris to the datastream model.
     *
     * @param datastream  The datastream to add format uri information to.
     * @param dsTypeModel The dsTypeModel element to read the information from.
     */
    private static void addFormatUrisToDatastream(Datastream datastream,
                                                  Element dsTypeModel) {
        NodeList formaturis;
        try {
            formaturis = NamespaceUtils
                    .xpathQuery(dsTypeModel, "ds:form/@FORMAT_URI");
        } catch (XPathExpressionException e1) {
            throw new Error("XPath expression did not evaluate", e1);
        }
        if (formaturis.getLength() > 0) {
            for (int a = 0; a < formaturis.getLength(); a++) {
                if (formaturis.item(a).getNodeType()
                        == Document.ATTRIBUTE_NODE) {
                    String uri = formaturis.item(a).getNodeValue();
                    try {
                        datastream.getFormatUris().add(new URI(uri));
                    } catch (URISyntaxException e1) {
                        throw new FedoraIllegalContentException(
                                "URI '" + uri + "' is invalid FORMAT_URI", e1);
                    }
                }
            }
        }
    }

    /**
     * Given a datastream description from DS-COMPOSITE-MODEL, add information
     * about the gui representation to the datastream model.
     *
     * @param datastream  The datastream to add gui representation information
     *                    to.
     * @param dsTypeModel The dsTypeModel element to read the information from.
     */
    private static void addGuiRepresentationToDatastream(Datastream datastream,
                                                         Element dsTypeModel) {
        NodeList guirepresentations;
        try {
            guirepresentations = NamespaceUtils.xpathQuery(
                    dsTypeModel,
                    "ds:extensions[@name='GUI']/gui:guirepresentation/"
                            + "@presentAs");
        } catch (XPathExpressionException e1) {
            throw new Error("XPath expression did not evaluate", e1);
        }
        if (guirepresentations.getLength() > 0) {
            //TODO: Fail on more than one?
            for (int a = 0; a < guirepresentations.getLength(); a++) {
                if (guirepresentations.item(a).getNodeType()
                        == Document.ATTRIBUTE_NODE) {
                    datastream.setGuiRepresentation(
                            Datastream.GUIRepresentation.valueOf(
                                    guirepresentations.item(a)
                                            .getNodeValue().toUpperCase()));
                }
            }
        }
    }

    /**
     * Given a datastream description from DS-COMPOSITE-MODEL, add information
     * about schema to the datastream model.
     *
     * @param datastream  The datastream to add schema information to.
     * @param dsTypeModel The dsTypeModel element to read the information from.
     */
    private static void addSchemaInformationToDatastream(Datastream datastream,
                                                         Element dsTypeModel,
                                                         String pid,
                                                         DomsUserToken userToken) {
        NodeList schemas;
        try {
            schemas = NamespaceUtils.xpathQuery(
                    dsTypeModel, "ds:extensions[@name='SCHEMA']/"
                            + "schema:schema[@type='xsd']");
        } catch (XPathExpressionException e1) {
            throw new Error("XPath expression did not evaluate", e1);
        }
        for (int elm = 0; elm < schemas.getLength(); elm++) {
            if (schemas.item(elm).getNodeType() == Document.ELEMENT_NODE) {
                //TODO: Fail on more than one?
                String schemaDatastream = ((Element) schemas.item(elm))
                        .getAttribute("datastream");
                String schemaObject = ((Element) schemas.item(elm))
                        .getAttribute("object");
                if (schemaObject == null || schemaObject.equals("")) {
                    schemaObject = pid;
                }
                schemaObject = FedoraUtils.ensurePID(schemaObject);
                Document schema = FedoraUtils.getDatastreamAsDocument(
                        schemaObject, schemaDatastream, userToken);
                datastream.setXmlSchema(schema);
            }
        }
    }

    /**
     * Update the view with information from parsed viewdatastream. This
     * includes properties and inverseProperties which should be followed to
     * generate the view. It is all added under the view with the given name.
     *
     * @param views   The map of views to update.
     * @param viewXml The datastream with information to add.
     */
    private static void updateView(Map<String, View> views, Document viewXml) {
        NodeList xpathResult;
        // Get all views.
        try {
            xpathResult = NamespaceUtils
                    .xpathQuery(viewXml, "/view:views/view:view");
        } catch (XPathExpressionException e) {
            throw new Error("XPath expression did not evaluate", e);
        }
        for (int v = 0; v < xpathResult.getLength(); v++) {
            if (xpathResult.item(v).getNodeType() != Document.ELEMENT_NODE) {
                continue;
            }
            // Get the name
            Element e = (Element) xpathResult.item(v);
            String name = e.getAttribute("name");
            if (name != null && !name.equals("")) {
                // Views may not have names containing '
                if (name.contains("'")) {
                    throw new FedoraIllegalContentException(
                            "Views may not have names containing ',"
                                    + " but view name was \"" + name + "\"");
                }

                // Get or generate view for that name.
                View view = views.get(name);
                if (view == null) {
                    view = new View();
                    views.put(name, view);
                }
                // Update information for that view
                addViewRelations(view, viewXml, name);
                addViewInverseRelations(view, viewXml, name);
            }
        }
    }

    /**
     * Update list of properties for this view.
     *
     * @param view    The view to update.
     * @param viewXml The document to get the information from.
     * @param name    The name to update the view for.
     */
    private static void addViewRelations(View view, Document viewXml,
                                         String name) {
        NodeList xpathResult;
        try {
            // FIXME: Names with ' will throw errors
            xpathResult = NamespaceUtils.xpathQuery(
                    viewXml, "/view:views/view:view[@name='" + name
                            + "']/view:relations/*");
        } catch (XPathExpressionException e) {
            throw new Error("XPath expression did not evaluate", e);
        }
        for (int l = 0; l < xpathResult.getLength(); l++) {
            Node n = xpathResult.item(l);
            if (n.getNodeType() == Document.ELEMENT_NODE) {
                Element e = (Element) n;
                String viewelement = e.getNamespaceURI() + e.getLocalName();
                if (!view.getProperties().contains(viewelement)) {
                    view.getProperties().add(viewelement);
                }
            }
        }
    }

    /**
     * Update list of inverseProperties in this view.
     *
     * @param view    The view to update.
     * @param viewXml The document to get the information from.
     * @param name    The name to update the view for.
     */
    private static void addViewInverseRelations(View view, Document viewXml,
                                                String name) {
        NodeList xpathResult;
        try {
            // FIXME: Names with ' will throw errors
            xpathResult = NamespaceUtils.xpathQuery(
                    viewXml, "view:views/view:view[@name='" + name
                            + "']/view:inverseRelations/*");
        } catch (XPathExpressionException e) {
            throw new Error("XPath expression did not evaluate", e);
        }
        for (int l = 0; l < xpathResult.getLength(); l++) {
            Node n = xpathResult.item(l);
            if (n.getNodeType() == Document.ELEMENT_NODE) {
                Element e = (Element) n;
                String viewelement = e.getNamespaceURI() + e.getLocalName();
                if (!view.getInverseProperties().contains(viewelement)) {
                    view.getInverseProperties().add(viewelement);
                }
            }
        }
    }

    /**
     * Set whether this is a main view, by parsing the relations defining this.
     *
     * @param pid   The pid to parse relations for
     * @param views The views in the content model to update.
     * @throws dk.statsbiblioteket.doms.exceptions.FedoraConnectionException
     *          if relations cannot be retrieved.
     */
    private static void setMainView(String pid, Map<String, View> views,
                                    DomsUserToken utils) {
        RelationshipTuple[] tuples;
        try {
            tuples = FedoraUtils.getAPIM(utils).getRelationships(
                    pid, ContentModelConstants.RELATION_MAIN_VIEW);
        } catch (RemoteException e) {
            throw new FedoraConnectionException(
                    "Error getting relations for '" + pid + "'", e);
        }

        if (tuples != null) {
            for (RelationshipTuple t : tuples) {
                String viewname = t.getObject();
                View view = views.get(viewname);
                if (view == null) {
                    view = new View();
                    views.put(viewname, view);
                }
                view.setMain(true);
            }
        }
    }
}
