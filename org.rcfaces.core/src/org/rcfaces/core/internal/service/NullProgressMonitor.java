/*
 * $Id: NullProgressMonitor.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 */
package org.rcfaces.core.internal.service;

import org.rcfaces.core.progressMonitor.IProgressMonitor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public class NullProgressMonitor implements IProgressMonitor {
    

    private boolean canceled;

    public void beginTask(String name, int totalWork) {
    }

    public void done() {
    }

    public void internalWorked(double work) {
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public void setTaskName(String name) {
    }

    public void subTask(String name) {
    }

    public void worked(int work) {
    }

}
