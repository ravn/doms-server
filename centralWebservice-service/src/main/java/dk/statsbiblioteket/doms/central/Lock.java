package dk.statsbiblioteket.doms.central;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 3/14/11
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Lock {

    Set<java.lang.Long> writeLocks = new HashSet<java.lang.Long>();

    private java.lang.Long nextToken = 0L;

    private boolean repo_locked_for_writing = false;

    public Lock() {
    }

    public synchronized void lockForWriting() {

        //intern sync

        //unlock sync


        repo_locked_for_writing = true;

        while (writeLocks.size() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }
    }

    public synchronized void unlockForWriting() {
        repo_locked_for_writing = false;
    }

    public synchronized long getReadAndWritePerm() {

        //intern sync

        //sync med lock
        while (repo_locked_for_writing) {
            try {
                wait(100);
            } catch (InterruptedException e) {

            }
        }
        writeLocks.add(++nextToken);
        return nextToken;

    }

    public void releaseReadAndWritePerm(long token) {
        writeLocks.remove(token);
    }
}
