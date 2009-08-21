package dk.statsbiblioteket.doms;
/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Nov 10, 2008
 * Time: 11:42:14 AM
 * To change this template use File | Settings | File Templates.
 */

import junit.framework.TestCase;
import org.xml.sax.SAXException;

public class FoxMLTest extends TestCase {
    FoxML foxML;
    private String foxml;

    public void testExtractPid() throws Exception {
        try {
            foxML.extractPid(foxml);
            fail("Should have thrown an Exception");
        } catch (SAXException ex) {
        }
    }
}