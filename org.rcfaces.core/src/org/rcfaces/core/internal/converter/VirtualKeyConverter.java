/*
 * $Id: VirtualKeyConverter.java,v 1.2 2013/07/03 12:25:03 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.rcfaces.core.model.AbstractConverter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:03 $
 */
public class VirtualKeyConverter extends AbstractConverter {
    

    public static final Converter SINGLETON = new VirtualKeyConverter();

    private final static Map KEYS = new HashMap(255);

    private final static Map NAMES = new HashMap(255);
    static {
        KEYS.put("CANCEL", new Integer(0x03));
        KEYS.put("HELP", new Integer(0x06));
        KEYS.put("BACK_SPACE", new Integer(0x08));
        KEYS.put("BACKSPACE", new Integer(0x08));
        KEYS.put("TAB", new Integer(0x09));
        KEYS.put("CLEAR", new Integer(0x0C));

        KEYS.put("RETURN", new Integer(0x0D));

        KEYS.put("ENTER", new Integer(0x0E));

        KEYS.put("SHIFT", new Integer(0x10));

        KEYS.put("CONTROL", new Integer(0x11));
        KEYS.put("ALT", new Integer(0x12));
        KEYS.put("PAUSE", new Integer(0x13));
        KEYS.put("CAPS_LOCK", new Integer(0x14));
        KEYS.put("CAPSLOCK", new Integer(0x14));
        KEYS.put("ESCAPE", new Integer(0x1B));
        KEYS.put("SPACE", new Integer(0x20));
        KEYS.put("PAGE_UP", new Integer(0x21));
        KEYS.put("PAGEUP", new Integer(0x21));
        KEYS.put("PAGE_DOWN", new Integer(0x22));
        KEYS.put("PAGEDOWN", new Integer(0x22));
        KEYS.put("END", new Integer(0x23));
        KEYS.put("HOME", new Integer(0x24));
        KEYS.put("LEFT", new Integer(0x25));
        KEYS.put("UP", new Integer(0x26));
        KEYS.put("RIGHT", new Integer(0x27));
        KEYS.put("DOWN", new Integer(0x28));

        KEYS.put("PRINT_SCREEN", new Integer(0x2C));
        KEYS.put("PRINTSCREEN", new Integer(0x2C));

        KEYS.put("INSERT", new Integer(0x2D));
        KEYS.put("DELETE", new Integer(0x2E));

        KEYS.put("SEMICOLON", new Integer(0x3B));
        KEYS.put("EQUALS", new Integer(0x3D));
        KEYS.put("NUMPAD0", new Integer(0x60));
        KEYS.put("NUMPAD1", new Integer(0x61));
        KEYS.put("NUMPAD2", new Integer(0x62));
        KEYS.put("NUMPAD3", new Integer(0x63));
        KEYS.put("NUMPAD4", new Integer(0x64));
        KEYS.put("NUMPAD5", new Integer(0x65));
        KEYS.put("NUMPAD6", new Integer(0x66));
        KEYS.put("NUMPAD7", new Integer(0x67));
        KEYS.put("NUMPAD8", new Integer(0x68));
        KEYS.put("NUMPAD9", new Integer(0x69));
        KEYS.put("MULTIPLY", new Integer(0x6A));
        KEYS.put("ADD", new Integer(0x6B));
        KEYS.put("SEPARATOR", new Integer(0x6C));
        KEYS.put("SUBTRACT", new Integer(0x6D));
        KEYS.put("DECIMAL", new Integer(0x6E));
        KEYS.put("DIVIDE", new Integer(0x6F));
        KEYS.put("F1", new Integer(0x70));
        KEYS.put("F2", new Integer(0x71));
        KEYS.put("F3", new Integer(0x72));
        KEYS.put("F4", new Integer(0x73));
        KEYS.put("F5", new Integer(0x74));
        KEYS.put("F6", new Integer(0x75));
        KEYS.put("F7", new Integer(0x76));
        KEYS.put("F8", new Integer(0x77));
        KEYS.put("F9", new Integer(0x78));
        KEYS.put("F10", new Integer(0x79));
        KEYS.put("F11", new Integer(0x7A));
        KEYS.put("F12", new Integer(0x7B));
        KEYS.put("F13", new Integer(0x7C));
        KEYS.put("F14", new Integer(0x7D));
        KEYS.put("F15", new Integer(0x7E));
        KEYS.put("F16", new Integer(0x7F));
        KEYS.put("F17", new Integer(0x80));
        KEYS.put("F18", new Integer(0x81));
        KEYS.put("F19", new Integer(0x82));
        KEYS.put("F20", new Integer(0x83));
        KEYS.put("F21", new Integer(0x84));
        KEYS.put("F22", new Integer(0x85));
        KEYS.put("F23", new Integer(0x86));
        KEYS.put("F24", new Integer(0x87));

        KEYS.put("NUM_LOCK", new Integer(0x90));
        KEYS.put("NUMLOCK", new Integer(0x90));
        KEYS.put("SCROLL_LOCK", new Integer(0x91));
        KEYS.put("SCROLLLOCK", new Integer(0x91));

        KEYS.put("COMMA", new Integer(0xBC));
        KEYS.put("PERIOD", new Integer(0xBE));
        KEYS.put("SLASH", new Integer(0xBF));

        KEYS.put("BACK_QUOTE", new Integer(0xC0));
        KEYS.put("BACKQUOTE", new Integer(0xC0));

        KEYS.put("OPEN_BRACKET", new Integer(0xDB));
        KEYS.put("OPENBRACKET", new Integer(0xDB));

        KEYS.put("BACK_SLASH", new Integer(0xDC));
        KEYS.put("BACKSLASH", new Integer(0xDC));

        KEYS.put("CLOSE_BRACKET", new Integer(0xDD));
        KEYS.put("CLOSEBRACKET", new Integer(0xDD));

        KEYS.put("QUOTE", new Integer(0xDE));

        KEYS.put("META", new Integer(0xE0));

        for (Iterator it = KEYS.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();

            String key = (String) entry.getKey();
            Integer vkey = (Integer) entry.getValue();

            key = Character.toUpperCase(key.charAt(0))
                    + key.substring(1).toLowerCase();

            NAMES.put(vkey, key);
        }
    }

    public Object getAsObject(FacesContext context, UIComponent component,
            String value) {

        if (value == null || value.length() < 1) {
            return null;
        }

        value = value.toUpperCase();

        return KEYS.get(value);
    }

    public String getAsString(FacesContext context, UIComponent component,
            Object value) {
        return (String) NAMES.get(value);
    }

    public static Integer convertUpperCase(String value) {
        return (Integer) KEYS.get(value);
    }

    public static String convertInt(int virtualKey) {
        return (String) NAMES.get(new Integer(virtualKey));
    }
}
