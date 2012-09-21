package dk.statsbiblioteket.doms.central.connectors.fedora.methods;

import dk.statsbiblioteket.doms.central.connectors.BackendInvalidCredsException;
import dk.statsbiblioteket.doms.central.connectors.BackendInvalidResourceException;
import dk.statsbiblioteket.doms.central.connectors.BackendMethodFailedException;
import dk.statsbiblioteket.doms.central.connectors.fedora.Fedora;
import dk.statsbiblioteket.doms.central.connectors.fedora.inheritance.ContentModelInheritance;
import dk.statsbiblioteket.doms.central.connectors.fedora.methods.generated.Method;
import dk.statsbiblioteket.doms.central.connectors.fedora.methods.generated.Parameter;
import dk.statsbiblioteket.doms.central.connectors.fedora.structures.ObjectProfile;
import dk.statsbiblioteket.doms.central.connectors.fedora.tripleStore.TripleStore;
import dk.statsbiblioteket.util.Pair;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 9/13/12
 * Time: 10:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class MethodsImpl implements Methods{


    Fedora fedora;
    private String thisLocation;
    private Unmarshaller jaxb;



    public MethodsImpl(Fedora fedora, String thisLocation) throws JAXBException {
        this.fedora = fedora;
        this.thisLocation = thisLocation;
        jaxb = JAXBContext.newInstance("dk.statsbiblioteket.doms.central.connectors.fedora.methods.generated").createUnmarshaller();
    }

    @Override
    public String invokeMethod(String pid,String methodName,List<Pair<String,String>> parameters, String logMessage) throws BackendInvalidResourceException, BackendInvalidCredsException, BackendMethodFailedException {
        //TODO figure out username and password used for this connection
        //TODO extract the command string from the method
        //TODO replace the parameter values into the command string
        //Run the command string
        //If exit 0, return the std out
        //else return stdout+stderr
        List<Method> methods = getStaticMethods(pid);

        boolean staticMethod = true;

        Method chosenMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                chosenMethod = method;
                break;
            }
        }


        if (chosenMethod == null){
            staticMethod = false;
            methods = getDynamicMethods(pid);
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    chosenMethod = method;
                    break;
                }
            }
        }
        if (chosenMethod == null){

            throw new BackendInvalidResourceException("Failed to find specified method");

        }
        String command = chosenMethod.getCommand();

        parameters.add(new Pair<String, String>("domsUser",fedora.getUsername()));
        parameters.add(new Pair<String, String>("domsPassword",fedora.getPassword()));
        parameters.add(new Pair<String, String>("domsLocation",thisLocation));

        HashMap<String, Parameter> parameterMapping = new HashMap<String, Parameter>();

        if (!staticMethod){
            parameters.add(new Pair<String, String>("domsPid",pid));
        }

        //Set defaults
        for (Parameter declaredParameter : chosenMethod.getParameters().getParameter()) {
            boolean set = false;
            for (Pair<String, String> setParameter : parameters) {
                if (setParameter.getLeft().equals(declaredParameter.getName())){
                    set = true;
                }
            }
            if (set){
                continue;
            }
            Pair<String, String> pair = new Pair<String, String>(
                    declaredParameter.getName(),
                    declaredParameter.getDefault());
            parameters.add(pair);
            parameterMapping.put(declaredParameter.getName(),declaredParameter);
        }
        //replace values
        for (Pair<String, String> parameter : parameters) {

            String name = parameter.getLeft();
            System.out.println(name);
            name = name.replaceAll("\\s","");
            Parameter declared = parameterMapping.get(name);
            if (declared != null){
                if (declared.getType().equals("TextBox")){
                    System.out.println(declared + " is textbox");
                }
            }

            String value = parameter.getRight();
            System.out.println(value);
            //value = value.replaceAll("[']","");
            //value = "'"+value+"'";
            value = Base64.encodeBase64String(value.getBytes());
            System.out.println(name);
            System.out.println(value);
            command = command.replaceAll("%%"+name+"%%",value);
        }
        System.out.println(command);
        //Remove all unused parameters
        command = command.replaceAll("%%[^%%]*%%","");
        System.out.println(command);

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

    public List<Method> getDynamicMethods(String objpid) throws BackendInvalidCredsException, BackendMethodFailedException, BackendInvalidResourceException {
        ObjectProfile profile = fedora.getObjectProfile(objpid);
        List<Method> result = new ArrayList<Method>();
        for (String contentModelPid : profile.getContentModels()) {
            List<Method> methods = getMethods(contentModelPid);
            for (Method method : methods) {
                if (method.getType().equals("dynamic")){
                    result.add(method);
                }
            }
        }
        return result;
    }


    private List<Method> getMethods(String cmpid) throws BackendInvalidCredsException, BackendMethodFailedException, BackendInvalidResourceException {
        String methodsXml = null;
        try {
            //TODO check that the model is in fact a content model??
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
    public List<Method> getStaticMethods(String cmpid) throws BackendInvalidCredsException, BackendMethodFailedException, BackendInvalidResourceException {
        List<Method> result = new ArrayList<Method>();
        List<Method> methods = getMethods(cmpid);
        for (Method method : methods) {
            if (method.getType().equals("static")){
                result.add(method);
            }
        }
        return result;

    }

}
