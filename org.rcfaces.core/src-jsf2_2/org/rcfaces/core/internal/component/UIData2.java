/*
 * $Id: UIData2.java,v 1.1 2014/02/05 16:05:52 jbmeslin Exp $
 */

/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the License at
 * https://javaserverfaces.dev.java.net/CDDL.html or legal/CDDLv1.0.txt. See the
 * License for the specific language governing permission and limitations under
 * the License.
 * 
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at legal/CDDLv1.0.txt. If applicable, add the
 * following below the CDDL Header, with the fields enclosed by brackets []
 * replaced by your own identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * [Name of File] [ver.__] [Date]
 * 
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package org.rcfaces.core.internal.component;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.StateHelper;
import javax.faces.component.StateHolder;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.faces.model.DataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IAdditionalInformationContainer;
import org.rcfaces.core.internal.capability.IComponentEngine;
import org.rcfaces.core.internal.capability.IRCFacesComponent;

/**
 * <p>
 * <strong>UIData</strong> is a {@link UIComponent} that supports data binding
 * to a collection of data objects represented by a {@link DataModel} instance,
 * which is the current value of this component itself (typically established
 * via a {@link ValueExpression}). During iterative processing over the rows of
 * data in the data model, the object for the current row is exposed as a
 * request attribute under the key specified by the <code>var</code> property.
 * </p>
 * 
 * <p>
 * Only children of type {@link UIColumn} should be processed by renderers
 * associated with this component.
 * </p>
 * 
 * <p>
 * By default, the <code>rendererType</code> property is set to
 * <code>javax.faces.Table</code>. This value can be changed by calling the
 * <code>setRendererType()</code> method.
 * </p>
 */

public class UIData2 extends UIData0 {

    private static final Log LOG = LogFactory.getLog(UIData2.class);

    private static boolean DEBUG_ENABLED = LOG.isDebugEnabled();

    private transient List<int[]> decodedIndexes;

    private boolean saveCompleteState = true;

    private boolean iterateMode;

    private int iterateModeFirst;

    private int iterateModeIndex;

    private int iterateModeRows;

    public UIData2() {
        // Certains logs sont positionnés APRES !
        DEBUG_ENABLED = LOG.isDebugEnabled();
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {

        decodedIndexes = null;

        super.encodeBegin(context);
    }

    @Override
    protected void iterate(FacesContext context, PhaseId phaseId) {
        if (decodedIndexes == null ) {//|| decodedIndexes.isEmpty()) {
            super.iterate(context, phaseId);
            return;
        }

        if (true) {
//            super.iterate(context, phaseId);
//            return;
        }

        iterateModeFirst = 0;
        iterateModeRows = 0;
        iterateModeIndex = 0;

        for (int[] is : decodedIndexes) {
            iterateModeRows += is[1];
        }

        iterateMode = true;
        try {

            super.iterate(context, phaseId);

        } finally {
            iterateMode = false;
        }
    }

    @Override
    protected boolean renderColumn(UIColumn column, PhaseId phaseId) {
        if (column instanceof IAdditionalInformationContainer) {
            return decodeAdditionalInformation((IAdditionalInformationContainer) column);
        }

        return true;
    }

    @Override
    public int getFirst() {
        if (iterateMode == false) {
            int first = super.getFirst();

            if (DEBUG_ENABLED) {
                LOG.debug("getFirst returns " + first);
            }

            return first;
        }

        if (DEBUG_ENABLED) {
            LOG.debug("Iterate translate mode: return first="
                    + iterateModeFirst);
        }

        return iterateModeFirst;
    }

    @Override
    public int getRowCount() {
        int rowCount = super.getRowCount();

        if (DEBUG_ENABLED) {
            LOG.debug("getRowCount() returns " + rowCount);
        }

        return rowCount;
    }

    @Override
    public int getRowIndex() {
        int rowIndex = super.getRowIndex();

        if (DEBUG_ENABLED) {
            LOG.debug("getRowIndex() returns " + rowIndex);
        }

        return rowIndex;
    }

    @Override
    public boolean isRowAvailable() {
    	
    	if (decodedIndexes != null && decodedIndexes.isEmpty() ) {
    		return false;
    	}
    	
        boolean rowAvailable = super.isRowAvailable();

        if (DEBUG_ENABLED) {
            LOG.debug("isRowAvailable() returns " + rowAvailable);
        }

        return rowAvailable;
    }

    @Override
    public void setFirst(int first) {

        if (DEBUG_ENABLED) {
            LOG.debug("setFirst(" + first + ")");
        }

        super.setFirst(first);
    }

    @Override
    public void setRows(int rows) {

        if (DEBUG_ENABLED) {
            LOG.debug("setRows(" + rows + ")");
        }

        super.setRows(rows);
    }

    @Override
    public int getRows() {
        if (iterateMode == false) {
            int rows = super.getRows();

            if (DEBUG_ENABLED) {
                LOG.debug("getRows() returns " + rows);
            }

            return rows;
        }

        if (DEBUG_ENABLED) {
            LOG.debug("getRows(): Iterate translate mode returns rows="
                    + iterateModeRows);
        }

        return iterateModeRows;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        if (iterateMode == false || rowIndex < 0) {

            if (DEBUG_ENABLED) {
                LOG.debug("setRowIndex(" + rowIndex + ")");
            }

            super.setRowIndex(rowIndex);
            return;
        }

        int translatedRowIndex = 0;
        for (int[] is : decodedIndexes) {
            if (rowIndex >= is[1]) {
                rowIndex -= is[1];
                continue;
            }

            translatedRowIndex = is[0] + rowIndex;
            break;
        }

        if (DEBUG_ENABLED) {
            LOG.debug("setRowIndex(" + rowIndex
                    + ") Iterate translate mode => rowIndex="
                    + translatedRowIndex);
        }

        super.setRowIndex(translatedRowIndex);
    }

    public void addDecodedIndexes(int first, int rows) {

        if (DEBUG_ENABLED) {
            LOG.debug("Add decoded indexes first=" + first + " rows=" + rows);
        }

        if (decodedIndexes == null) {
            decodedIndexes = new ArrayList<int[]>();
        }

        if (rows > 0) {
            decodedIndexes.add(new int[] { first, rows });
        }
    }


    /**
     * <p>
     * Restore state information for the specified component and its
     * descendants.
     * </p>
     * 
     * @param component
     *            Component for which to restore state information
     * @param context
     *            {@link FacesContext} for the current request
     */
    @Override
    protected void restoreDescendantState(UIComponent component,
            FacesContext context) {

        Map<String, SavedState2> saved = (Map<String, SavedState2>) getStateHelper()
                .get(Properties.SAVED_STATE);

        // Reset the client identifier for this component
        String id = component.getId();
        component.setId(id); // Forces client id to be reset

        if (component instanceof IRCFacesComponent) {
            String clientId = component.getClientId(context);
            SavedState2 state = saved.get(clientId);

            if (state != null) {
                IComponentEngine componentEngine = ((IComponentEngineAccessor) component)
                        .getComponentEngine();

                StateHelper stateHelper = componentEngine.getStateHelper();
                stateHelper.restoreState(context, state.serializedComponentEngine);
                //component.restoreState(context,  state.serializedComponentEngine);

            } else {
                ((IComponentEngineAccessor) component).cloneComponentEngine();
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Restore state of '" + clientId + "' => " + state);
            }
        }

        super.restoreDescendantState(component, context);
    }

    /**
     * <p>
     * Save state information for the specified component and its descendants.
     * </p>
     * 
     * @param component
     *            Component for which to save state information
     * @param context
     *            {@link FacesContext} for the current request
     */
    @Override
    protected void saveDescendantState(UIComponent component,
            FacesContext context) {

        super.saveDescendantState(component, context);
       
        Map<String, SavedState2> saved = (Map<String, SavedState2>) getStateHelper()
                .get(Properties.SAVED_STATE);

        if (isSaveCompleteState()) {
            if (component instanceof IRCFacesComponent) {

                String clientId = component.getClientId(context);

                SavedState2 state = null;
                if (saved != null) {
                	state = saved.get(clientId);
                }
                if (state == null) {
                    state = new SavedState2();
                    getStateHelper().put(Properties.SAVED_STATE, clientId,
                            state);
                }
                
              //  state.serializedComponentEngine = component.saveState(context);
                
                IComponentEngine componentEngine = ((IComponentEngineAccessor) component)
                        .getComponentEngine();

                StateHelper stateHelper = componentEngine.getStateHelper();
                state.serializedComponentEngine =stateHelper.saveState(context);

                

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Save state of '" + clientId + "' => " + state);
                }
            }
        }
    }

    public final boolean isSaveCompleteState() {
        return saveCompleteState;
    }

    public final void setSaveCompleteState(boolean saveCompleteState) {
        this.saveCompleteState = saveCompleteState;
    }

    // Private class to represent saved state information
    public static class SavedState2 implements Externalizable, StateHolder {

        private Object serializedComponentEngine;

        public SavedState2() {

        }

        @Override
        public String toString() {
            return "[SavedState2]";
        }

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {

            serializedComponentEngine = in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {

            out.writeObject(serializedComponentEngine);
        }

        public Object saveState(FacesContext context) {

            return serializedComponentEngine;
        }

        public void restoreState(FacesContext context, Object state) {
            serializedComponentEngine = state;
        }

        public boolean isTransient() {
            return false;
        }

        public void setTransient(boolean newTransientValue) {
        }

    }

    public boolean decodeAdditionalInformation(
            IAdditionalInformationContainer additionalInformationComponent) {
        if (decodedIndexes == null || decodedIndexes.isEmpty()) {
            if (DEBUG_ENABLED) {
                int rowIndex = getRowIndex();

                LOG.debug("Decode additional #" + rowIndex + " ("
                        + decodedIndexesToString() + ") => FALSE");
            }
            return true;
        }

        int rowIndex = getRowIndex();

        for (int[] is : decodedIndexes) {
            if (rowIndex >= is[0] && rowIndex < is[0] + is[1]) {
                if (DEBUG_ENABLED) {
                    LOG.debug("Decode additional #" + rowIndex + " ("
                            + decodedIndexesToString() + ") => TRUE");
                }
                return true;
            }
        }

        if (DEBUG_ENABLED) {
            LOG.debug("Decode additional #" + rowIndex + " ("
                    + decodedIndexesToString() + ") => FALSE");
        }

        return false;
    }

    private String decodedIndexesToString() {
        if (decodedIndexes == null) {
            return "null";
        }

        if (decodedIndexes.isEmpty()) {
            return "[]";
        }

        StringBuffer sb = new StringBuffer();

        for (int is[] : decodedIndexes) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append("[" + is[0] + "->" + (is[0] + is[1] - 1) + "]");
        }

        return sb.toString();
    }
    
    public void clearDecodedIndex() {
    	decodedIndexes = null; 
    }
}