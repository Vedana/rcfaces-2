/*
 * $Id: RemoveAccentTranslator.java,v 1.3 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.validator.impl;

import java.util.BitSet;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.validator.IClientValidatorContext;
import org.rcfaces.core.validator.ITranslatorTask;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:06 $
 */
public class RemoveAccentTranslator extends AbstractClientValidatorTask
        implements ITranslatorTask {
    

    private static final Log LOG = LogFactory
            .getLog(RemoveAccentTranslator.class);

    private static final BitSet accentsHash = new BitSet(512);

    private static final AccentTranslator ACCENTS_MAPPERS[] = new AccentTranslator[] {
            new AccentTranslator("������???", 'a'),
            new AccentTranslator("????�", 'c'),
            new AccentTranslator("??", 'd'),
            new AccentTranslator("����?????", 'e'),
            new AccentTranslator("????", 'g'), new AccentTranslator("??", 'h'),
            new AccentTranslator("����?????", 'i'),
            new AccentTranslator("�?????", 'n'),
            new AccentTranslator("�����???", 'o'),
            new AccentTranslator("����??????", 'u'),
            new AccentTranslator("��?", 'y'),
            new AccentTranslator("������???", 'A'),
            new AccentTranslator("�????", 'C'),
            new AccentTranslator("??", 'D'),
            new AccentTranslator("����?????", 'E'),
            new AccentTranslator("????", 'G'), new AccentTranslator("??", 'H'),
            new AccentTranslator("����?????", 'I'),
            new AccentTranslator("�????", 'N'),
            new AccentTranslator("�����???", 'O'),
            new AccentTranslator("����??????", 'U'),
            new AccentTranslator("�?�", 'Y') };

    public char applyTranslator(IClientValidatorContext context, char keyChar) {

        if (accentsHash.get(computeHashCode(keyChar)) == false) {
            return keyChar;
        }

        String ch = String.valueOf(keyChar);

        for (int i = 0; i < ACCENTS_MAPPERS.length; i++) {
            AccentTranslator at = ACCENTS_MAPPERS[i];

            if (at.pattern.matcher(ch).find()) {
                return at.ch;
            }
        }

        return keyChar;
    }

    private static int computeHashCode(char keyChar) {
        return ((keyChar * 2777) >> 3) & 0x03ff;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:06 $
     */
    private static class AccentTranslator {
        

        private final Pattern pattern;

        private final char ch;

        public AccentTranslator(String regEx, char ch) {
            this.pattern = Pattern.compile("[" + regEx + "]");
            this.ch = ch;

            for (int i = 0; i < regEx.length(); i++) {
                accentsHash.set(computeHashCode(regEx.charAt(i)));
            }
        }
    }
}