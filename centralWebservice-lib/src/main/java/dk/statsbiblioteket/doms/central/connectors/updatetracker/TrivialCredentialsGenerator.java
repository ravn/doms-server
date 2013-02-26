package dk.statsbiblioteket.doms.central.connectors.updatetracker;

import dk.statsbiblioteket.doms.updatetracker.CredentialsGenerator;
import dk.statsbiblioteket.doms.webservices.authentication.Credentials;

public class TrivialCredentialsGenerator implements CredentialsGenerator {
    private final Credentials creds;

    public TrivialCredentialsGenerator(Credentials creds) {
        this.creds = creds;
    }

    @Override
    public Credentials getCredentials() {
        return creds;
    }
}
