/*
 * $Id: PerformanceTimingProvider.java,v 1.2 2013/11/26 13:55:57 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.timing;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.FactoryFinder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.provider.AbstractProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/26 13:55:57 $
 */
public class PerformanceTimingProvider extends AbstractProvider {
    private static final Log LOG = LogFactory
            .getLog(PerformanceTimingProvider.class);

    private static final IPerformanceTiming NO_PERFORMANCE_TIMING = new PerformanceTiming();

    private static final String PERFORMANCE_TIMING_PROPERTY = "org.rcfaces.html.PERFORMANCE_TIMING";

    private static final String PROVIDER_ID = "org.rcfaces.html.PERFORMANCE_TIMING";

    private static final String PERFORMANCE_TIMING_ENABLED_PARAMETER = "org.rcfaces.renderkit.html.client.ENABLE_PERFORMANCE_TIMING";

    private static final String LOG_PERFORMANCE_TIMING_PARAMETER = "org.rcfaces.renderkit.html.LOG_PERFORMANCE_TIMING";

    private static final String URL_DECODER_CHARSET = "UTF-8";

    private static final String PERFORMANCE_TIMING_PROCESSOR_PROPERTY = "org.rcfaces.html.PERFORMANCE_TIMING_PROCESSOR";

    private final Map<String, Method> writerMethodsByName = new HashMap<String, Method>(
            20);

    private int clientPerformanceTimingFeatures;

    private boolean logPerformanceTiming;

    private IPerformanceTimingProcessor timingProcessor;

    public PerformanceTimingProvider() {
        LOG.info("Start performance timing manager.");

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(PerformanceTiming.class);
        } catch (IntrospectionException ex) {
            throw new IllegalStateException("Can not inspect class '"
                    + PerformanceTiming.class.getName() + "'", ex);
        }

        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            writerMethodsByName.put(pd.getName(), pd.getWriteMethod());
        }
    }

    public String getId() {
        return PROVIDER_ID;
    }

    @SuppressWarnings({ "unchecked", "cast" })
    @Override
    public void startup(FacesContext facesContext) {
        super.startup(facesContext);

        ExternalContext externalContext = facesContext.getExternalContext();

        Map<String, Object> applicationMap = (Map<String, Object>) externalContext
                .getInitParameterMap();

        String ptep = (String) applicationMap
                .get(PERFORMANCE_TIMING_ENABLED_PARAMETER);
        if (ptep != null && ptep.trim().length() > 0
                && "false".equalsIgnoreCase(ptep) == false) {

            clientPerformanceTimingFeatures = 0x01;
            StringTokenizer st = new StringTokenizer(ptep, ", ");
            for (; st.hasMoreTokens();) {
                String token = st.nextToken().toLowerCase();

                if ("target".equals(token)) {
                    clientPerformanceTimingFeatures |= 0x02;
                    continue;
                }

                if ("destination".equals(token)) {
                    clientPerformanceTimingFeatures |= 0x04;
                    continue;
                }

                if ("agent".equals(token)) {
                    clientPerformanceTimingFeatures |= 0x08;
                    continue;
                }

                if ("all".equals(token)) {
                    clientPerformanceTimingFeatures |= 0xFF;
                    break;
                }
            }

            LOG.info("Enable client performance timing (" + ptep
                    + ") => features=0x"
                    + Integer.toHexString(clientPerformanceTimingFeatures));

            installLifecycleListener(facesContext);
        }
        if ("true".equalsIgnoreCase((String) applicationMap
                .get(LOG_PERFORMANCE_TIMING_PARAMETER))) {
            setPerformanceTimingProcessor(new PerformanceTimingLogger());
        }

        IPerformanceTimingProcessor processor = (IPerformanceTimingProcessor) externalContext
                .getApplicationMap().get(PERFORMANCE_TIMING_PROCESSOR_PROPERTY);
        if (processor != null) {
            setPerformanceTimingProcessor(processor);
        }
    }

    public int getClientPerformanceTimingFeatures() {
        return clientPerformanceTimingFeatures;
    }

    public static PerformanceTimingProvider get(FacesContext facesContext) {
        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);
        if (rcfacesContext == null) {
            return null;
        }

        return get(rcfacesContext);
    }

    public static PerformanceTimingProvider get(RcfacesContext rcfacesContext) {
        return (PerformanceTimingProvider) rcfacesContext
                .getProvidersRegistry().getProvider(PROVIDER_ID);
    }

    private void installLifecycleListener(FacesContext facesContext) {

        LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
                .getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        String lifecycleId = facesContext.getExternalContext()
                .getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);
        if (lifecycleId == null) {
            lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
        }
        Lifecycle lifecycle = lifecycleFactory.getLifecycle(lifecycleId);

        lifecycle.addPhaseListener(new PhaseListener() {

            private static final long serialVersionUID = -129201215149816547L;

            public PhaseId getPhaseId() {
                return PhaseId.RESTORE_VIEW;
            }

            public void beforePhase(PhaseEvent event) {
            }

            public void afterPhase(PhaseEvent event) {
                logRequestPerformanceTiming(event.getFacesContext());
            }
        });
    }

    protected void logRequestPerformanceTiming(FacesContext facesContext) {
        IPerformanceTiming performanceTiming = getRequestPerformanceTiming(facesContext);

        if (LOG.isInfoEnabled()) {
            LOG.info("Performance timing=" + performanceTiming);
        }
    }

    public IPerformanceTiming getRequestPerformanceTiming() {
        return getRequestPerformanceTiming(null);
    }

    public IPerformanceTiming getRequestPerformanceTiming(
            FacesContext facesContext) {

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        Map<String, Object> requestMap = facesContext.getExternalContext()
                .getRequestMap();

        IPerformanceTiming performanceTiming = (IPerformanceTiming) requestMap
                .get(PERFORMANCE_TIMING_PROPERTY);
        if (performanceTiming != null) {
            return performanceTiming;
        }

        performanceTiming = createPerformanceTiming(facesContext);
        if (performanceTiming == null) {
            requestMap.put(PERFORMANCE_TIMING_PROPERTY, NO_PERFORMANCE_TIMING);
            return null;
        }

        if (performanceTiming == NO_PERFORMANCE_TIMING) {
            return null;
        }

        requestMap.put(PERFORMANCE_TIMING_PROPERTY, performanceTiming);

        return performanceTiming;
    }

    protected IPerformanceTiming createPerformanceTiming(
            FacesContext facesContext) {
        String[] timings = facesContext.getExternalContext()
                .getRequestParameterValuesMap().get("RCFACES_TIMING");
        if (timings == null || timings.length == 0) {
            return null;
        }

        // List<IPerformanceTiming> performanceTimings = new
        // ArrayList<IPerformanceTiming>(4);

        IPerformanceTiming requestPerformanceTiming = null;

        for (String timing : timings) {
            PerformanceTiming performanceTiming = new PerformanceTiming();
            // performanceTimings.add(performanceTiming);

            Object request = facesContext.getExternalContext().getRequest();
            if (request instanceof ServletRequest) {
                String remoteAddr = ((ServletRequest) request).getRemoteAddr();

                performanceTiming.setRemoteAddress(remoteAddr);
            }

            StringTokenizer st = new StringTokenizer(timing, ", ");
            for (; st.hasMoreTokens();) {
                String token = st.nextToken();
                int idx = token.indexOf('=');
                if (idx < 0) {
                    continue;
                }

                String name = token.substring(0, idx);
                String svalue = token.substring(idx + 1);
                if (svalue.length() == 0) {
                    continue;
                }

                Object value = null;

                if (svalue.charAt(0) == '\'') {
                    try {
                        value = URLDecoder.decode(
                                svalue.substring(1, svalue.length() - 1),
                                URL_DECODER_CHARSET);

                    } catch (UnsupportedEncodingException ex) {
                        LOG.error("Can not decode '" + svalue + "'", ex);
                        continue;
                    }
                } else {
                	 try {
                         value = Long.valueOf(svalue);

                     } catch (NumberFormatException ex) {
                         LOG.error(
                                 "Invalid performance timing '" + svalue + "'",
                                 ex);
                         continue;
                     }
                }

                if (name.equals("clientDate")) {
                    value = new Date(((Long) value).longValue());
                }

                Method method = writerMethodsByName.get(name);
                if (method == null) {
                    LOG.debug("Unknown method for field '" + name + "'");
                    continue;
                }

                try {
                    method.invoke(performanceTiming, value);

                } catch (Throwable ex) {
                    LOG.error("Can not set performanceTiming '" + name
                            + "' value=" + value);
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Construct performanceTiming=" + performanceTiming);
            }

            if (performanceTiming.getPageType() == null) {
                requestPerformanceTiming = performanceTiming;
            }

            if (timingProcessor != null) {
                try {
                    timingProcessor.process(facesContext, performanceTiming);

                } catch (Throwable th) {
                    LOG.error("Processor throws exception  (timing="
                            + performanceTiming + ")", th);
                }
            }
        }

        return requestPerformanceTiming;
    }

    private void setPerformanceTimingProcessor(
            IPerformanceTimingProcessor processor) {
        LOG.debug("Set performance timing processor to " + processor);

        this.timingProcessor = processor;
    }

    public static void addPerformanceTimingProcessor(FacesContext facesContext,
            IPerformanceTimingProcessor processor) {
        PerformanceTimingProvider performanceTimingProvider = get(facesContext);
        if (performanceTimingProvider != null) {
            performanceTimingProvider.setPerformanceTimingProcessor(processor);
            return;
        }
        facesContext.getExternalContext().getApplicationMap()
                .put(PERFORMANCE_TIMING_PROCESSOR_PROPERTY, processor);
    }

    public static void addPerformanceTimingProcessor(
            ServletContext servletContext, IPerformanceTimingProcessor processor) {
        RcfacesContext rcfacesContext = RcfacesContext.getInstance(
                servletContext, null, null);
        if (rcfacesContext != null) {
            PerformanceTimingProvider performanceTimingProvider = get(rcfacesContext);
            if (performanceTimingProvider != null) {
                performanceTimingProvider
                        .setPerformanceTimingProcessor(processor);
                return;
            }
        }

        servletContext.setAttribute(PERFORMANCE_TIMING_PROCESSOR_PROPERTY,
                processor);

    }
}
