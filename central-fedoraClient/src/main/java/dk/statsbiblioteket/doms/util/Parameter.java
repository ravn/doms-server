package dk.statsbiblioteket.doms.util;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 3/21/13
 * Time: 1:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Parameter {

    protected String name;
    protected String parameterprefix;
    protected boolean required;
    protected boolean repeatable;
    protected String type;
    protected String config;
    protected String defaultValue;
    protected boolean encode = true;

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Parameter(String name) {
        this.name = name;
    }

    public Parameter(String name, String parameterprefix, boolean required, boolean repeatable, String type, String config) {
        this.name = name;
        this.parameterprefix = parameterprefix;
        this.required = required;
        this.repeatable = repeatable;
        this.type = type;
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParameterprefix() {
        return parameterprefix;
    }

    public void setParameterprefix(String parameterprefix) {
        this.parameterprefix = parameterprefix;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public boolean isEncode() {
        return encode;
    }

    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Parameter)) return false;

        Parameter parameter = (Parameter) o;

        if (!name.equals(parameter.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
