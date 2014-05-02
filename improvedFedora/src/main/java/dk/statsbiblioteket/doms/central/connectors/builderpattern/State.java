package dk.statsbiblioteket.doms.central.connectors.builderpattern;

public  enum State {
    Active("A"),
    Inactive("I"),
    Deleted("D");

    State(String shortForm) {
        this.shortForm = shortForm;
    }

    private String shortForm;

    public String getShortForm() {
        return shortForm;
    }
}