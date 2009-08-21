package dk.statsbiblioteket.doms;

import dk.statsbiblioteket.util.InvalidPropertiesException;
import dk.statsbiblioteket.util.XProperties;
import dk.statsbiblioteket.util.qa.QAInfo;
import fedora.client.FedoraClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

/** Token identifying a DOMS user, giving access to DOMS services. */
@QAInfo(level = QAInfo.Level.NORMAL,
        state = QAInfo.State.QA_NEEDED,
        author = "abr",
        reviewers = {""})
public class DomsUserToken {
    private static final Log log = LogFactory.getLog(DomsUserToken.class);
    private static final String PROPERTIES_FILE_NAME
            = "DomsUserToken.properties.xml";
    private XProperties xprop;

    private final String serverurl;
    private final String username;
    private final String password;

    private FedoraClient client;

    /** This method provides a FedoraUtils with read-only access to the DOMS repository. */
    public DomsUserToken() {
        init();
        serverurl = xprop.getString("PROTOCOL") + "://"
                + xprop.getString("SERVER") + ":" + xprop.getInteger("PORT")
                + xprop.getString("FEDORA_SERVICE");

        username = xprop.getString("USER");
        password = xprop.getString("PASS");


    }

    public String getServerurl() {
        return serverurl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void clearAllState() {
        client = null;
    }

    public void setFedoraClient(FedoraClient client) {
        this.client = client;
    }

    public FedoraClient getCachedFedoraClient() {
        return client;
    }

    /**
     * This method proves a FedoraUtils with the restrictions placed on the given username
     *
     * @param user      The username to connect with
     * @param pass      The password to use
     * @param fedoraurl The proper adress of the server. Example http://localhost:8080/fedora
     */
    public DomsUserToken(String user, String pass, String fedoraurl) {
        init();
        serverurl = fedoraurl;
        username = user;
        password = pass;
    }

    private void init() {
        xprop = new XProperties();
        xprop.putDefault("PROTOCOL", "http");
        xprop.putDefault("SERVER", "localhost");
        xprop.putDefault("PORT", 8080);
        xprop.putDefault("FEDORA_SERVICE", "/fedora");

        xprop.putDefault("USER", "fedoraAdmin");
        xprop.putDefault("PASS", "fedoraAdminPass");

        try {
            xprop.load(PROPERTIES_FILE_NAME, false, false);
        } catch (InvalidPropertiesException e) {
            log.warn(
                    "Unable to parse the properties file, using default"
                            + " properties", e);
        } catch (IOException e) {
            log.warn(
                    "ClassLoader could not read " + PROPERTIES_FILE_NAME
                            + ", using default properties", e);
        }
    }


}
