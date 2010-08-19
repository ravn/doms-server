package dk.statsbiblioteket.doms.central;

import dk.statsbiblioteket.doms.centralWebservice.*;

import javax.jws.WebParam;
import javax.activation.DataHandler;
import javax.xml.ws.Holder;
import java.util.List;
import java.lang.String;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Aug 18, 2010
 * Time: 2:01:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class CentralWebserviceImpl implements CentralWebservice {
    public String newObject(
            @WebParam(name = "pid", targetNamespace = "") String pid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deleteObject(
            @WebParam(name = "pid", targetNamespace = "") String pid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void markPublishedObject(
            @WebParam(name = "pid", targetNamespace = "") String pid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void markInProgressObject(
            @WebParam(name = "pid", targetNamespace = "") String pid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<String> listDatastreamsObject(
            @WebParam(name = "pid", targetNamespace = "") String pid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getTitleObject(
            @WebParam(name = "pid", targetNamespace = "") String pid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void newDatastream(
            @WebParam(name = "pid", targetNamespace = "") String pid,
            @WebParam(name = "datastream", targetNamespace = "")
            String datastream,
            @WebParam(name = "contents", targetNamespace = "")
            String contents) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deleteDatastream(
            @WebParam(name = "pid", targetNamespace = "") String pid,
            @WebParam(name
                    =
                    "datastream",
                      targetNamespace
                              =
                              "") String datastream) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void modifyDatastream(
            @WebParam(name = "pid", targetNamespace = "") String pid,
            @WebParam(name
                    =
                    "datastream",
                      targetNamespace
                              =
                              "") String datastream,
            @WebParam(name = "contents", targetNamespace = "")
            String contents) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getDatastreamContents(
            @WebParam(name = "pid", targetNamespace = "") String pid,
            @WebParam(name
                    =
                    "datastream",
                      targetNamespace
                              =
                              "") String datastream) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void getDatastreamProfile(
            @WebParam(name = "pid", targetNamespace = "") String pid,
            @WebParam(name
                    =
                    "datastream",
                      targetNamespace
                              =
                              "") String datastream) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addFile(
            @WebParam(name = "pid", targetNamespace = "") String pid,
            @WebParam(name = "filename", targetNamespace = "")
            String filename,
            @WebParam(name = "md5sum", targetNamespace = "")
            String md5Sum,
            @WebParam(name = "contents", targetNamespace = "")
            DataHandler contents) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deleteFile(
            @WebParam(name = "pid", targetNamespace = "") String pid) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getFileURL(
            @WebParam(name = "pid", targetNamespace = "") String pid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addRelation(
            @WebParam(name = "subject", targetNamespace = "") String subject,
            @WebParam(name = "property", targetNamespace = "")
            String property,
            @WebParam(name = "object", targetNamespace = "")
            String object) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Relation> getRelations(
            @WebParam(name = "pid", targetNamespace = "") String pid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<Relation> getNamedRelations(
            @WebParam(name = "pid", targetNamespace = "") String pid) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void deleteRelation(
            @WebParam(name = "subject", targetNamespace = "") String subject,
            @WebParam(name = "property", targetNamespace = "")
            String property,
            @WebParam(name = "object", targetNamespace = "")
            String object) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
