package dk.statsbiblioteket.doms.central.connectors.fedora.methods;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.fedora.Fedora;
import dk.statsbiblioteket.doms.central.connectors.fedora.inheritance.ContentModelInheritance;
import dk.statsbiblioteket.doms.central.connectors.fedora.methods.generated.Method;
import dk.statsbiblioteket.doms.central.connectors.fedora.tripleStore.TripleStore;
import dk.statsbiblioteket.util.Pair;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 9/13/12
 * Time: 10:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class MethodsImpl implements Methods{


    Fedora fedora;
    TripleStore ts;
    ContentModelInheritance inheritance;
    private Unmarshaller jaxb;


    public MethodsImpl(Fedora fedora, TripleStore ts, ContentModelInheritance inheritance) throws JAXBException {
        this.fedora = fedora;
        this.ts = ts;
        this.inheritance = inheritance;
        jaxb = JAXBContext.newInstance("dk.statsbiblioteket.doms.central.connectors.fedora.methods.generated").createUnmarshaller();
    }

    @Override
    public List<Method> getMethods(String cmpid) throws BackendInvalidCredsException, BackendMethodFailedException, BackendInvalidResourceException {
        String methodsXml = null;
        try {
            methodsXml = fedora.getXMLDatastreamContents(cmpid, "METHODS");
        } catch (BackendInvalidResourceException e) {
            return new ArrayList<Method>();
        }

        try {
            JAXBElement<dk.statsbiblioteket.doms.central.connectors.fedora.methods.generated.Methods> parsed = (JAXBElement<dk.statsbiblioteket.doms.central.connectors.fedora.methods.generated.Methods>) jaxb.unmarshal(new StringReader(methodsXml));
            return parsed.getValue().getMethod();
        } catch (JAXBException e) {
            throw new BackendMethodFailedException("failed to parse Methods definition",e);
        }
    }

    @Override
    public String invokeMethod(String cmpid,String methodName,List<Pair<String,String>> parameters, String logMessage) throws BackendInvalidResourceException, BackendInvalidCredsException, BackendMethodFailedException {
        //TODO figure out username and password used for this connection
        //TODO extract the command string from the method
        //TODO replace the parameter values into the command string
        //Run the command string
        //If exit 0, return the std out
        //else return stdout+stderr
        List<Method> methods = getMethods(cmpid);
        Method chosenMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                chosenMethod = method;
                break;
            }
        }
        if (chosenMethod == null){
            throw new BackendInvalidResourceException("Failed to find specified method");
        }
        String command = chosenMethod.getCommand();

        for (Pair<String, String> parameter : parameters) {
            String name = parameter.getLeft();
            name = name.replaceAll("\\s","");
            String value = parameter.getRight();
            value = value.replaceAll("[\"'`]","");
            value = "\""+value+"\"";
            command = command.replaceAll("%%"+name+"%%",value);


        }
        //TODO defaulted params, such as fedoraUser and fedoraPass

        List<String> commandList = new ArrayList<String>();
        commandList.add("/bin/bash");
        commandList.add("-c");
        commandList.add(command);

        ProcessBuilder procesBuider = new ProcessBuilder(commandList);

        try {
            Process process = procesBuider.start();
            int returnCode = process.waitFor();
            StringWriter writer = new StringWriter();
            IOUtils.copy(process.getInputStream(),writer);

            if (returnCode == 0){
            } else {
                IOUtils.copy(process.getErrorStream(),writer);
            }
            String result = writer.toString();
            return result;
        } catch (Exception e){
            throw new BackendMethodFailedException("Failed to run command "+commandList.toString(),e);
        }
    }
}
