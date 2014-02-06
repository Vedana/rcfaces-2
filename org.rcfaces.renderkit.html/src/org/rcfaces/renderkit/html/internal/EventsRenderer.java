/*
 * $Id: EventsRenderer.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.event.FacesListener;

import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.listener.AbstractScriptListener;
import org.rcfaces.core.internal.listener.IPartialRenderingListener;
import org.rcfaces.core.internal.listener.IScriptListener;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.util.ListenerTools;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
public class EventsRenderer {

    static final String DEFAULT_SUBMIT = "submit";

    // On conserve le mot clef coté client !
    private static final String DEFAULT_SUBMIT_JAVA_SCRIPT = "submit";

    private static final String DEFAULT_PARTIAL_RENDERING_JAVA_SCRIPT = "submitPPR";

    private static final String DEFAULT_SUBMIT_JAVA_SCRIPT_CLASSNAME = "fa_eventTarget";

    private static final String DEFAULT_SUBMIT_JAVA_SCRIPT_CB = "DefaultSubmit";

    private static final String DEFAULT_PARTIAL_RENDERING_JAVA_SCRIPT_CLASSNAME = "f_pprEngine";

    private static final String DEFAULT_PARTIAL_RENDERING_JAVA_SCRIPT_CB = "DefaultPPR";

    private static final String ADD_EVENT_LISTENERS = "f_addEventListener";

    private static final FacesListener DEFAULT_SUBMIT_FACES_LISTENER = new AbstractScriptListener(
            IHtmlRenderContext.JAVASCRIPT_TYPE, DEFAULT_SUBMIT) {

    };

    private static final FacesListener[] DEFAULT_SUBMIT_FACES_LISTENERS = new FacesListener[] { DEFAULT_SUBMIT_FACES_LISTENER };

    private static final IJavascriptMode ONFOCUS_JAVASCRIPT_ENABLE = new IJavascriptMode() {

        public void enableJavaScriptMode(IJavaScriptEnableMode mode) {
            // mode.enableOnFocus(com);
            // Normalement les IDs sont déjà positionnés !

            mode.enableOnFocus();
        }
    };

    private static final IJavascriptMode ONINIT_JAVASCRIPT_ENABLE = new IJavascriptMode() {

        public void enableJavaScriptMode(IJavaScriptEnableMode mode) {
            mode.enableOnInit();
        }
    };

    private static final IJavascriptMode NONE_JAVASCRIPT_ENABLE = new IJavascriptMode() {

        public void enableJavaScriptMode(IJavaScriptEnableMode mode) {
        }
    };

    private static final IJavascriptMode SUBMIT_JAVASCRIPT_ENABLE = new IJavascriptMode() {

        public void enableJavaScriptMode(IJavaScriptEnableMode mode) {
            mode.enableOnSubmit();
        }
    };

    private static final IJavascriptMode OVER_JAVASCRIPT_ENABLE = new IJavascriptMode() {

        public void enableJavaScriptMode(IJavaScriptEnableMode mode) {
            mode.enableOnOver();
        }
    };

    private static final int PPR_SUBMIT_TYPE = 2;

    private static final int GLOBAL_SUBMIT_TYPE = 1;

    private static final Map<String, IJavascriptMode> ENABLE_JAVASCRIPT_BY_LISTENER_TYPE = new HashMap<String, IJavascriptMode>(
            8);
    static {
        // INIT
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_INIT_ATTRIBUTE,
                ONINIT_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_VALIDATION_ATTRIBUTE,
                ONINIT_JAVASCRIPT_ENABLE);

        // OVER
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_MOUSEOVER, OVER_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_MOUSEOUT, OVER_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(JavaScriptClasses.EVENT_CLICK,
                OVER_JAVASCRIPT_ENABLE);

        // FOCUS
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_BLUR_ATTRIBUTE,
                ONFOCUS_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_EXPAND_ATTRIBUTE,
                ONFOCUS_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_FOCUS_ATTRIBUTE,
                ONFOCUS_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_KEYDOWN_ATTRIBUTE,
                ONFOCUS_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_KEYPRESS, ONFOCUS_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_KEYUP_ATTRIBUTE,
                ONFOCUS_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_PRE_SELECTION,
                ONFOCUS_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_SELECTION, ONFOCUS_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_DBLCLICK, ONFOCUS_JAVASCRIPT_ENABLE);

        // NONE
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_USER_ATTRIBUTE, NONE_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_LOAD_ATTRIBUTE, NONE_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE
                .put(JavaScriptClasses.EVENT_PROPERTY_CHANGE,
                        NONE_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_MENU_ATTRIBUTE, NONE_JAVASCRIPT_ENABLE);
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_VALUE_CHANGE_ATTRIBUTE,
                NONE_JAVASCRIPT_ENABLE);

        // SUBMIT/RESET
        ENABLE_JAVASCRIPT_BY_LISTENER_TYPE.put(
                JavaScriptClasses.EVENT_RESET_ATTRIBUTE,
                SUBMIT_JAVASCRIPT_ENABLE);

    }

    public static void encodeEventListeners(IJavaScriptWriter javascriptWriter,
            String varName, Map<String, FacesListener[]> listenersByType,
            String actionListenerType) throws WriterException {

        if (javascriptWriter.getComponentRenderContext().getRenderContext()
                .getProcessContext().isDesignerMode()) {
            return;
        }

        if (listenersByType.isEmpty() == false) {
            for (Map.Entry<String, FacesListener[]> entry : listenersByType
                    .entrySet()) {

                String listenerType = entry.getKey();
                FacesListener listeners[] = entry.getValue();

                boolean submitSupport = true;
                if (ListenerTools.ATTRIBUTE_NAME_SPACE.getValidationEventName()
                        .equals(listenerType)) {
                    submitSupport = false;
                }

                encodeEventListeners(javascriptWriter, varName, listenerType,
                        listeners, submitSupport);
            }
        }

        if (actionListenerType != null) {
            if (listenersByType.containsKey(actionListenerType) == false) {
                // Il faut provoquer un Submit alors ...
                encodeJavaScriptEventSubmit(javascriptWriter, varName,
                        actionListenerType);
            }
        }
    }

    private static void encodeEventListeners(IJavaScriptWriter js,
            String varName, String listenerType,
            FacesListener facesListeners[], boolean submitSupport)
            throws WriterException {

        boolean first = true;

        boolean clientSubmitted = false;
        int submitType = 0;

        IProcessContext processContext = js.getHtmlRenderContext()
                .getProcessContext();
        IJavaScriptRenderContext javaScriptRenderContext = js
                .getJavaScriptRenderContext();

        for (int i = 0; i < facesListeners.length; i++) {
            FacesListener fl = facesListeners[i];

            if ((fl instanceof IScriptListener) == false) {
                // C'est un listener serveur, il nous faut un submit coté client
                // !

                if (clientSubmitted) {
                    // Submit déjà généré : on arrete tout !
                    break;
                }

                if (submitSupport == false) {
                    continue;
                }

                if (fl instanceof IPartialRenderingListener) {
                    if (((IPartialRenderingListener) fl).isPartialRendering()) {
                        submitType = PPR_SUBMIT_TYPE;
                        continue;
                    }
                }

                // Il faut générer un submit en javascript !
                submitType = GLOBAL_SUBMIT_TYPE;
                continue;
            }

            if (clientSubmitted) {
                // Le client a déjà généré un submit : on ignore la suite !
                continue;
            }

            IScriptListener scriptListener = (IScriptListener) fl;

            if (IHtmlRenderContext.JAVASCRIPT_TYPE.equals(scriptListener
                    .getScriptType(processContext)) == false) {
                continue;
            }

            if (first) {
                first = false;
                js.writeCall(varName, ADD_EVENT_LISTENERS).write(listenerType);
            }

            js.write(',');

            String command = scriptListener.getCommand();

            if (DEFAULT_SUBMIT.equals(command)) {
                if (submitSupport == false) {
                    throw new FacesException("'" + DEFAULT_SUBMIT
                            + "' keyword is not supported byte listener type '"
                            + listenerType + "'.");
                }

                js.write(DEFAULT_SUBMIT_JAVA_SCRIPT_CLASSNAME).write('.');
                js.write(javaScriptRenderContext.convertSymbol(
                        DEFAULT_SUBMIT_JAVA_SCRIPT_CLASSNAME,
                        DEFAULT_SUBMIT_JAVA_SCRIPT_CB));

                clientSubmitted = true;

            } else {
                js.writeString(command);
            }
        }

        if (clientSubmitted == false && submitType != 0) {

            if (first) {
                first = false;
                js.writeCall(varName, ADD_EVENT_LISTENERS).write(listenerType);
            }

            js.write(',');

            if (submitType == GLOBAL_SUBMIT_TYPE) {
                js.write(DEFAULT_SUBMIT_JAVA_SCRIPT_CLASSNAME).write('.');
                js.write(javaScriptRenderContext.convertSymbol(
                        DEFAULT_SUBMIT_JAVA_SCRIPT_CLASSNAME,
                        DEFAULT_SUBMIT_JAVA_SCRIPT_CB));

            } else {
                js.write(DEFAULT_PARTIAL_RENDERING_JAVA_SCRIPT_CLASSNAME)
                        .write('.');
                js.write(javaScriptRenderContext.convertSymbol(
                        DEFAULT_PARTIAL_RENDERING_JAVA_SCRIPT_CLASSNAME,
                        DEFAULT_PARTIAL_RENDERING_JAVA_SCRIPT_CB));

                js.getJavaScriptRenderContext().appendRequiredClass(
                        DEFAULT_PARTIAL_RENDERING_JAVA_SCRIPT_CLASSNAME, null);
            }
        }

        if (first == false) {
            js.writeln(");");
        }
    }

    public static final void encodeJavaScriptEventSubmit(IJavaScriptWriter js,
            String varName, String listenerType) throws WriterException {
        js.writeCall(varName, ADD_EVENT_LISTENERS).write(listenerType);
        js.write(',');

        js.write(DEFAULT_SUBMIT_JAVA_SCRIPT_CLASSNAME).write('.');
        js.write(js.getJavaScriptRenderContext().convertSymbol(
                DEFAULT_SUBMIT_JAVA_SCRIPT_CLASSNAME,
                DEFAULT_SUBMIT_JAVA_SCRIPT_CB));

        js.writeln(");");
    }

    public static void encodeJavaScriptCommmand(IJavaScriptWriter js,
            IScriptListener scriptListener) throws WriterException {
        String command = scriptListener.getCommand();

        if (DEFAULT_SUBMIT.equals(command)) {
            js.write(DEFAULT_SUBMIT_JAVA_SCRIPT_CLASSNAME).write('.');
            js.write(js.getJavaScriptRenderContext().convertSymbol(
                    DEFAULT_SUBMIT_JAVA_SCRIPT_CLASSNAME,
                    DEFAULT_SUBMIT_JAVA_SCRIPT_CB));

            return;
        }

        js.writeString(command);
    }

    public static void encodeAttributeEventListeners(
            IRenderContext renderContext, StringAppender sb,
            String listenerType, FacesListener[] facesListeners,
            boolean submitSupport, IJavaScriptEnableMode javaScriptEnableMode) {

        int cnt = 0;

        boolean clientSubmitted = false;
        int submitType = 0;

        IProcessContext processContext = renderContext.getProcessContext();

        for (int i = 0; i < facesListeners.length; i++) {
            FacesListener fl = facesListeners[i];

            if ((fl instanceof IScriptListener) == false) {
                // C'est un listener serveur, il nous faut un submit coté client
                // !

                if (clientSubmitted) {
                    // Submit déjà généré : on arrete tout !
                    break;
                }

                if (submitSupport == false) {
                    continue;
                }

                if (fl instanceof IPartialRenderingListener) {
                    if (((IPartialRenderingListener) fl).isPartialRendering()) {
                        submitType = PPR_SUBMIT_TYPE;
                        continue;
                    }
                }

                // Il faut générer un submit en javascript !
                submitType = GLOBAL_SUBMIT_TYPE;
                continue;
            }

            if (clientSubmitted) {
                // Le client a déjà généré un submit : on ignore la suite !
                continue;
            }

            IScriptListener scriptListener = (IScriptListener) fl;

            if (IHtmlRenderContext.JAVASCRIPT_TYPE.equals(scriptListener
                    .getScriptType(processContext)) == false) {
                continue;
            }

            String command = scriptListener.getCommand();

            if (DEFAULT_SUBMIT.equals(command)) {
                if (submitSupport == false) {
                    throw new FacesException("'" + DEFAULT_SUBMIT
                            + "' keyword is not supported byte listener type '"
                            + listenerType + "'.");
                }

                command = DEFAULT_SUBMIT_JAVA_SCRIPT;
                clientSubmitted = true;
            }

            if (command.length() < 1) {
                continue;
            }

            if (cnt > 0) {
                sb.append(':');

            } else {
                if (sb.length() > 0) {
                    sb.append('|');
                }
                sb.append(listenerType);
                sb.append(':');
            }

            appendCommand(sb, command);

            cnt++;

            IJavascriptMode javascriptMode = ENABLE_JAVASCRIPT_BY_LISTENER_TYPE
                    .get(listenerType);
            if (javascriptMode != null) {
                javascriptMode.enableJavaScriptMode(javaScriptEnableMode);

            } else {
                javaScriptEnableMode.enableOnInit();
            }
        }

        if (clientSubmitted == false && submitType != 0) {
            // Des evenements serveurs mais pas Javascript
            // Aussi on ajoute un submit() histoire de soliciter les methodes
            // coté serveur
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append(listenerType);
            sb.append(':');
            if (submitType == GLOBAL_SUBMIT_TYPE) {
                sb.append(DEFAULT_SUBMIT_JAVA_SCRIPT);
            } else {
                sb.append(DEFAULT_PARTIAL_RENDERING_JAVA_SCRIPT);

                ((IJavaScriptRenderContext) renderContext
                        .getScriptRenderContext()).appendRequiredClass(
                        DEFAULT_PARTIAL_RENDERING_JAVA_SCRIPT_CLASSNAME, null);
            }

            IJavascriptMode javascriptMode = ENABLE_JAVASCRIPT_BY_LISTENER_TYPE
                    .get(listenerType);
            if (javascriptMode != null) {
                javascriptMode.enableJavaScriptMode(javaScriptEnableMode);

            } else {
                javaScriptEnableMode.enableOnInit();
            }
        }
    }

    public static void appendCommand(StringAppender sa, String command) {
        char chs[] = command.toCharArray();

        sa.ensure(chs.length * 3 / 2);

        int last = 0;
        for (int i = 0; i < chs.length; i++) {
            char c = chs[i];
            if (c != '|' && c != ':' && c != '%') {
                continue;
            }

            if (last < i) {
                sa.append(chs, last, i - last);
                last = i + 1;
            }

            switch (c) {
            case '|':
                sa.append("%7C");
                break;

            case ':':
                sa.append("%3A");
                break;

            case '%':
                sa.append("%25");
                break;
            }
        }
        if (last < chs.length) {
            sa.append(chs, last, chs.length - last);
        }
    }

    public static FacesListener[] getSubmitJavaScriptListeners() {
        return DEFAULT_SUBMIT_FACES_LISTENERS;
    }

    private static interface IJavascriptMode {
        void enableJavaScriptMode(IJavaScriptEnableMode mode);
    }
}
