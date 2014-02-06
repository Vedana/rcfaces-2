/*
 * $Id: AsyncRenderModeConverter.java,v 1.2 2013/01/11 15:47:00 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.component.capability.IAsyncRenderModeCapability;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.tools.AsyncModeTools;
import org.rcfaces.core.model.AbstractConverter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:47:00 $
 */
public class AsyncRenderModeConverter extends AbstractConverter {

	private static final String NONE_ASYNC_RENDER_MODE_NAME = "none";
	private static final String FALSE_ASYNC_RENDER_MODE_NAME = "false";

	private static final String BUFFER_ASYNC_RENDER_MODE_NAME = "buffer";

	private static final String TREE_ASYNC_RENDER_MODE_NAME = "tree";
	private static final String TRUE_ASYNC_RENDER_MODE_NAME = "true";

	private static final Integer DEFAULT_ASYNC_RENDER_MODE = new Integer(
			Constants.DEFAULT_ASYNC_MODE);

	private static final Integer ASYNC_MODE_ENABLE_VALUE = new Integer(
			Constants.ENABLE_ASYNC_MODE_VALUE);

	public static final Converter SINGLETON = new AsyncRenderModeConverter();

	private static Map<String, Integer> ASYNC_RENDER_MODES = new HashMap<String, Integer>(
			8);
	static {
		ASYNC_RENDER_MODES.put(NONE_ASYNC_RENDER_MODE_NAME, new Integer(
				IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE));
		ASYNC_RENDER_MODES.put(FALSE_ASYNC_RENDER_MODE_NAME, new Integer(
				IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE));

		ASYNC_RENDER_MODES.put(BUFFER_ASYNC_RENDER_MODE_NAME, new Integer(
				IAsyncRenderModeCapability.BUFFER_ASYNC_RENDER_MODE));

		ASYNC_RENDER_MODES.put(TREE_ASYNC_RENDER_MODE_NAME, new Integer(
				IAsyncRenderModeCapability.TREE_ASYNC_RENDER_MODE));
		ASYNC_RENDER_MODES.put(TRUE_ASYNC_RENDER_MODE_NAME, new Integer(
				IAsyncRenderModeCapability.TREE_ASYNC_RENDER_MODE));
	}

	@SuppressWarnings("unused")
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {

		if (value == null || value.length() < 1) {
			return DEFAULT_ASYNC_RENDER_MODE;
		}

		value = value.toLowerCase();

        Integer i = ASYNC_RENDER_MODES.get(value);
		if (i != null) {
			return i;
		}

		if ("default".equalsIgnoreCase(value)) {
			return DEFAULT_ASYNC_RENDER_MODE;
		}

		if ("enabled".equalsIgnoreCase(value)) {
			if (Constants.FACELETS_SUPPORT) {
				return new Integer(AsyncModeTools.getEnableValue(context));
			}

			return ASYNC_MODE_ENABLE_VALUE;
		}

		throw new IllegalArgumentException("Keyword '" + value
				+ "' is not supported for a async-render type !");
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {

		if (value == null) {
			value = DEFAULT_ASYNC_RENDER_MODE;
		}

		if ((value instanceof Integer) == false) {
			throw new IllegalArgumentException("Value must be an Integer !");
		}

		for (Map.Entry<String, Integer> entry : ASYNC_RENDER_MODES.entrySet()) {

			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}

		throw new IllegalArgumentException("Value '" + value
				+ "' is not supported for a async-render-mode type !");
	}

	public static final String getName(int asyncRenderMode) {
		switch (asyncRenderMode) {
		case IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE:
			return NONE_ASYNC_RENDER_MODE_NAME;

		case IAsyncRenderModeCapability.BUFFER_ASYNC_RENDER_MODE:
			return BUFFER_ASYNC_RENDER_MODE_NAME;

		case IAsyncRenderModeCapability.TREE_ASYNC_RENDER_MODE:
			return TREE_ASYNC_RENDER_MODE_NAME;
		}

		return null;
	}
}
