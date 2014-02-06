/*
 * $Id: KeyTools.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.util;

import java.util.StringTokenizer;

import javax.faces.FacesException;

import org.rcfaces.core.internal.converter.KeyFlagsConverter;
import org.rcfaces.core.internal.converter.VirtualKeyConverter;
import org.rcfaces.core.internal.lang.StringAppender;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class KeyTools {
    

    public static State parseKeyBinding(String keyBinding) {
        StringTokenizer st = new StringTokenizer(keyBinding, "+ ", true);

        boolean compose = false;

        State state = new State(keyBinding);

        for (; st.hasMoreTokens();) {
            String token = st.nextToken();

            if (token.length() == 1) {
                char c = token.charAt(0);

                if (c == '+') {
                    if (compose) {
                        compose = false;
                        continue;
                    }

                    state.setCharacter('c');
                    compose = true;
                    continue;
                }

                if (Character.isWhitespace(c)) {
                    continue;
                }

                compose = true;
                state.setCharacter(Character.toUpperCase(c));
                continue;
            }

            compose = true;
            String utoken = token.toUpperCase();

            Integer flag = KeyFlagsConverter.convertUpperCase(utoken);
            if (flag != null) {
                state.addFlag(flag.intValue());
                continue;
            }

            Integer vkey = VirtualKeyConverter.convertUpperCase(utoken);
            if (vkey != null) {
                state.setVirtualKey(vkey);
                continue;
            }

            throw new FacesException("Too many character for '" + token
                    + "' in expression '" + keyBinding + "'.");
        }

        if (compose == false) {
            throw new FacesException("Invalid keyBinding expression '"
                    + keyBinding + "'.");
        }

        return state;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
     */
    public static final class State {
        

        private final String keyBinding;

        public int keyFlags;

        public Integer virtualKey;

        public char character;

        public State(String keyBinding) {
            this.keyBinding = keyBinding;
        }

        public void setCharacter(char character) {
            if (this.character != 0) {
                throw new FacesException(
                        "Can not specify more one character in keyBinding '"
                                + keyBinding + "'.");
            }
            if (virtualKey != null) {
                throw new FacesException(
                        "Can not specify a character when a virtualKey is already defined in keyBinding '"
                                + keyBinding + "'.");
            }

            this.character = character;
        }

        public void addFlag(int keyFlags) {
            this.keyFlags |= keyFlags;
        }

        public void setVirtualKey(Integer virtualKey) {
            if (this.virtualKey != null) {
                throw new FacesException(
                        "Only one virtual key can be defined in keyBinding '"
                                + keyBinding + "'.");
            }
            if (character != 0) {
                throw new FacesException(
                        "Can not specify a virtual key when a character is already defined in keyBinding '"
                                + keyBinding + "'.");
            }
            this.virtualKey = virtualKey;
        }

        public String format() {
            StringAppender sb = new StringAppender(32);

            if ((keyFlags & KeyFlagsConverter.CONTROL_FLAG) > 0) {
                sb.append("Ctrl");
            }
            if ((keyFlags & KeyFlagsConverter.META_FLAG) > 0) {
                if (sb.length() > 0) {
                    sb.append('+');
                }
                sb.append("Meta");
            }
            if ((keyFlags & KeyFlagsConverter.ALT_FLAG) > 0) {
                if (sb.length() > 0) {
                    sb.append('+');
                }
                sb.append("Alt");
            }
            if ((keyFlags & KeyFlagsConverter.SHIFT_FLAG) > 0) {
                if (sb.length() > 0) {
                    sb.append('+');
                }
                sb.append("Shift");
            }

            if (character > 0) {
                if (sb.length() > 0) {
                    sb.append('+');
                }
                sb.append(character);

            } else if (virtualKey != null) {
                if (sb.length() > 0) {
                    sb.append('+');
                }
                sb.append(VirtualKeyConverter.SINGLETON.getAsString(null, null,
                        virtualKey));
            }

            return sb.toString();
        }
    }
}
