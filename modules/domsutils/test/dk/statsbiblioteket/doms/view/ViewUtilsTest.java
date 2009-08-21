package dk.statsbiblioteket.doms.view;
/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Nov 10, 2008
 * Time: 11:54:12 AM
 * To change this template use File | Settings | File Templates.
 */

import dk.statsbiblioteket.doms.DomsUserToken;
import dk.statsbiblioteket.doms.FoxML;
import junit.framework.TestCase;
import org.w3c.dom.Document;

public class ViewUtilsTest extends TestCase {

    DomsUserToken m = new DomsUserToken();

    public void testGetViewBundle() throws Exception {

        Document view = ViewUtils
                .getViewBundle("doms:example_Adv_recording1", "GUI", m);

        String result = FoxML.documentToString(view);

        if (result.contains("PID=\"doms:example_Adv_recording1\"") && result
                .contains("PID=\"doms:example_Adv_BWFb1\"")) {

        } else {
            fail("The output did not contain the needed objects");
        }

    }

    public void testGetViewBundleNoPid() throws Exception {

        try {
            Document view = ViewUtils
                    .getViewBundle("doms:No_Such_PID", "GUI", m);
            fail("Method should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {

        }

    }

    public void testGetViewBundleNoPid2() throws Exception {

        try {
            Document view = ViewUtils.getViewBundle(null, "GUI", m);
            fail("Method should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {

        }

    }

    public void testGetViewBundleBadView() throws Exception {

        Document view = ViewUtils
                .getViewBundle("doms:example_Adv_recording1", "GHI", m);
        String result = FoxML.documentToString(view);
        //Fail if this throws any exceptions

    }


}