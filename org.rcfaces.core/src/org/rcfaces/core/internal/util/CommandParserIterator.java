/*
 * $Id: CommandParserIterator.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.FacesException;

import org.rcfaces.core.internal.util.CommandParserIterator.ICommand;
import org.rcfaces.core.validator.IParameter;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public class CommandParserIterator implements Iterator<ICommand> {

    private static final IParameter[] EMPTY_PARAMETERS = new IParameter[0];

    private final Iterator<Command> iterator;

    public CommandParserIterator(String validatorCommand)
            throws SyntaxException {
        this.iterator = parseCommand(validatorCommand);
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public ICommand next() {
        return nextCommand();
    }

    public ICommand nextCommand() {
        return iterator.next();
    }

    public void remove() {
        iterator.remove();
    }

    private Iterator<Command> parseCommand(String validatorCommand)
            throws SyntaxException {
        char command[] = validatorCommand.toCharArray();

        List<Command> list = new ArrayList<Command>();

        Command cmd = null;

        for (int i = 0; i < command.length;) {
            char ch = command[i];

            if (Character.isJavaIdentifierStart(ch)) {
                cmd = new Command();
                list.add(cmd);

                i = startCommandString(cmd, command, i);

            } else if (ch == '(') {
                if (cmd == null) {
                    throw new SyntaxException(
                            "Debut des parametres avant nom de fonction.",
                            command, i);
                }
                i = startParameterString(cmd, command, i + 1);
                cmd = null;

            } else if (ch == ';') {
                cmd = null;
                i++;

            } else if (Character.isWhitespace(ch)) {
                i++;

            } else {
                throw new SyntaxException("Caractere non prevu (ascii="
                        + ((int) ch) + ") '" + ch + "'.", command, i);
            }
        }

        return list.iterator();
    }

    private int startParameterString(Command cmd, char[] command, int i)
            throws SyntaxException {
        int start = i;
        boolean nextParameter = false;

        for (; i < command.length;) {
            char ch = command[i];

            if (ch == ')') {
                return i + 1;

            } else if (Character.isWhitespace(ch)) {
                i++;
                continue;

            } else if (ch == '"' || ch == '\'') {
                if (nextParameter) {
                    throw new SyntaxException(
                            "Mauvais separateur de parametres.", command, i);
                }

                nextParameter = true;

                i = startLitteralString(cmd, command, i);

            } else if (Character.isDigit(ch)) {
                if (nextParameter) {
                    throw new SyntaxException(
                            "Mauvais separateur de parametres.", command, i);
                }
                nextParameter = true;
                i = startNumberString(cmd, command, i);

            } else if (Character.isJavaIdentifierStart(ch)) {
                if (nextParameter) {
                    throw new SyntaxException(
                            "Mauvais separateur de parametres.", command, i);
                }
                nextParameter = true;
                i = startIdentifierString(cmd, command, i);

            } else if (ch == ',') {
                nextParameter = false;
                i++;
            } else {

                throw new SyntaxException("Caractere non prevu.", command, i);
            }

        }
        throw new SyntaxException("Pas de fermeture des parametres.", command,
                start);
    }

    private int startIdentifierString(Command cmd, char[] command, int i)
            throws SyntaxException {
        int start = i;

        for (; i < command.length; i++) {
            char ch = command[i];

            if (Character.isWhitespace(ch) || ch == ',') {
                cmd.addParameter(new String(command, start, i - start));
                return i;
            }

            if (Character.isJavaIdentifierPart(ch)) {
                continue;
            }

            throw new SyntaxException("Mauvaise syntaxe du nombre.", command,
                    start);
        }

        throw new SyntaxException("La chaine de caractere ne se ferme pas.",
                command, start);
    }

    private int startNumberString(Command cmd, char[] command, int i)
            throws SyntaxException {
        int start = i;

        for (; i < command.length; i++) {
            char ch = command[i];

            if (Character.isDigit(ch) || ch == '.') {
                continue;
            }

            break;
        }

        cmd.addParameter(new String(command, start, i - start));
        return i;
    }

    private int startLitteralString(Command cmd, char[] command, int i)
            throws SyntaxException {
        char end = command[i++];
        int start = i;

        for (; i < command.length; i++) {
            char ch = command[i];

            if (ch == end) {
                cmd.addParameter(new String(command, start, i - start));
                return i + 1;
            }
            if (ch == '\\') {
                i++; // ignore le prochain caractere !
            }
        }

        throw new SyntaxException("La chaine de caractere ne se ferme pas.",
                command, start);
    }

    private int startCommandString(Command cmd, char[] command, int i) {
        int start = i;

        for (; i < command.length; i++) {
            char ch = command[i];

            if (Character.isJavaIdentifierPart(ch) == false && ch != '.') {
                break;
            }
        }

        // meme si c'est la fin: pas d'erreur
        cmd.setName(new String(command, start, i - start));

        return i;
    }

    /**
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    public interface ICommand {
        String getName();

        IParameter[] listParameters();
    }

    private static class Command implements ICommand {

        private String name;

        private List<String> parameters;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void addParameter(String parameterValue) {
            if (parameters == null) {
                parameters = new LinkedList<String>();
            }
            parameters.add(parameterValue);
        }

        public IParameter[] listParameters() {
            if (parameters == null || parameters.size() < 1) {
                return EMPTY_PARAMETERS;
            }

            IParameter ps[] = new IParameter[parameters.size()];
            int i = 0;
            for (Iterator<String> it = parameters.iterator(); it.hasNext();) {
                String value = it.next();

                ps[i] = new Parameter(i, value);
                i++;
            }

            return ps;
        }
    }

    private static class Parameter implements IParameter {

        private final String name;

        private final String value;

        public Parameter(int index, String value) {
            this.name = "param" + index;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
     */
    public static class SyntaxException extends FacesException {

        private static final long serialVersionUID = 2866070330212947352L;

        private String command;

        private int position;

        SyntaxException(String message, char command[], int position) {
            this(message, new String(command), position);
        }

        SyntaxException(String message, String command, int position) {
            super(message + " [message='" + command + "' position='" + position
                    + "']", null);

            this.command = command;
            this.position = position;
        }

        public final String getCommand() {
            return command;
        }

        public final int getPosition() {
            return position;
        }
    }

}
