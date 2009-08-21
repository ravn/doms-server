package dk.statsbiblioteket.doms.relations;
/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Nov 13, 2008
 * Time: 11:46:20 AM
 * To change this template use File | Settings | File Templates.
 */

import dk.statsbiblioteket.doms.DomsUserToken;
import junit.framework.TestCase;

import java.util.Arrays;

public class RelationUtilsTest extends TestCase {
    RelationUtils relationUtils;

    DomsUserToken client = new DomsUserToken();

    public void testAllMainObjects() throws Exception {
        try {
            DigitalObject[] mains = relationUtils.allMainObjects(client);
            System.out.println(Arrays.toString(mains));
        } catch (Exception ex) {
        }
    }
}