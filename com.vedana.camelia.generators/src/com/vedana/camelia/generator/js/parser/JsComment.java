/*
 * $Id: JsComment.java,v 1.2 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.vedana.js.IComment;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.ASTVisitor;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.NodeList;
import com.vedana.js.dom.Value;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.2 $ $Date: 2009/01/15 15:16:52 $
 */
public class JsComment {

    private final IComment comment;

    private final String normalizedForm;

    private ASTNode node;

    private String commentType;

    private String commentTypeValue;

    private String commentTitle;

    private List<String> declarations = new ArrayList<String>();

    private String returnTypeValue;

    private List<String> parameters = new ArrayList<String>();

    public JsComment(IComment comment, ASTNode node, JsStats stats,
            String filename) {

        this.comment = comment;

        this.node = node;

        String cm = comment.getText();

        StringBuffer sb = new StringBuffer(1025);
        for (StringTokenizer st = new StringTokenizer(cm, "\n\r"); st
                .hasMoreTokens();) {
            String tok = st.nextToken();
            if (tok.trim().startsWith("*")) {
                tok = tok.trim().substring(1);
            }

            if (tok.length() > 0) {
                sb.append(tok);

            } else if (sb.length() < 1) {
                continue;
            }

            sb.append('\n');
        }

        if (sb.length() < 1) {
            normalizedForm = null;
            return;
        }

        normalizedForm = sb.toString().trim();

        boolean paramState = false;
        int idx = 0;
        int length = normalizedForm.length();
        for (;;) {
            StringBuffer psb = new StringBuffer();

            for (; idx < length;) {
                char c = normalizedForm.charAt(idx++);

                if (c == '{') {
                    psb.append(c);

                    for (; idx < length;) {
                        c = normalizedForm.charAt(idx++);
                        psb.append(c);

                        if (c == '}') {
                            break;
                        }
                    }

                    continue;
                }

                if (c == '@') {
                    break;
                }

                if (Character.isWhitespace(c)) {
                    if (psb.length() > 0
                            && Character.isWhitespace(psb
                                    .charAt(psb.length() - 1))) {
                        continue;
                    }

                    c = ' ';
                }

                psb.append(c);
            }

            String params = psb.toString().trim();

            if (paramState == false) {
                paramState = true;
                commentTitle = params;
                continue;
            }

            if (params.length() < 1) {
                break;
            }

            StringTokenizer st = new StringTokenizer(params.trim());

            declarations.add(st.nextToken().toLowerCase().trim());

            if (st.hasMoreTokens()) {
                declarations.add(st.nextToken("\n").trim()); // Le reste donc !

            } else {
                declarations.add("");
            }
        }

        for (Iterator<String> it = declarations.iterator(); it.hasNext();) {
            String tok = it.next();
            String tok2 = it.next();

            if (tok.equals("method") || tok.equals("field")
                    || tok.equals("class") || tok.equals("aspect")) {

                if (commentType != null || commentTypeValue != null) {
                    stats.errorLog.error("Type already declared ", node,
                            filename);
                }

                commentType = tok;
                commentTypeValue = tok2;
            }

            if (tok.equals("reserve")) {
                for (StringTokenizer st = new StringTokenizer(tok2, " \n\t\r,;"); st
                        .hasMoreTokens();) {
                    String n = st.nextToken();
                    stats.addCameliaReserverd(n);
                }
            }

            if (tok.equals("return")) {
                TypeTokenizer st = new TypeTokenizer(tok2);
                if (st.hasMoreTokens()) {
                    String type = st.nextToken().trim();
                    if (type.length() > 0) {
                        returnTypeValue = type;
                    }
                }
            }

            if (tok.equals("param")) {
                parameters.add(tok2.trim());
            }
        }
    }

    public String getType() {
        return commentType;
    }

    public String getValue() {
        return commentTypeValue;
    }

    public String getReturnValue() {
        return returnTypeValue;
    }

    public final IComment getComment() {
        return comment;
    }

    public final ASTNode getNode() {
        return node;
    }

    public static JsComment createCommentContent(IComment comment,
            NodeList nodeList, JsStats stats, String filename) {
        if (comment.getType() != IComment.BLOCK_COMMENT) {
            return null;
        }

        String cm = comment.getText();
        if (cm.length() < 3 || cm.charAt(0) != '*'
                || Character.isWhitespace(cm.charAt(1)) == false) {
            return null;
        }

        ASTNode node = searchNextDeclaration(comment, nodeList);

        return new JsComment(comment, node, stats, filename);
    }

    private static ASTNode searchNextDeclaration(IComment comment,
            NodeList nodeList) {

        final int offset = comment.getOffset();

        final boolean intoFunction[] = new boolean[1];
        final ASTNode ret[] = new ASTNode[1];
        nodeList.accept(new ASTVisitor() {

            @Override
            public boolean visit(FunctionDeclaration node) {
                if (node.getRegionOffset() < offset) {
                    NodeList statements = node.getBody().getStatements();

                    if (statements.size() > 0) {

                        ASTNode last = (ASTNode) statements.get(statements
                                .size() - 1);

                        if (last.getRegionOffset() + last.getRegionLength() > offset) {
                            intoFunction[0] = true;
                            return false;
                        }
                    }
                }

                if (ret[0] != null) {
                    return false;
                }

                if (node.getRegionOffset() > offset) {
                    ret[0] = node;
                    return false;
                }

                return true;
            }

            @Override
            public boolean visit(Assignment node) {
                if (ret[0] != null) {
                    return false;
                }

                if (node.getRegionOffset() > offset) {
                    ret[0] = node;
                    return false;
                }

                return true;
            }

            @Override
            public boolean visit(Value node) {
                return visit((Assignment) node);
            }
        });

        if (intoFunction[0]) {
            return null;
        }

        return ret[0];
    }

    private static String fillProperty(String params) {
        StringTokenizer st = new StringTokenizer(params);

        String tok = st.nextToken();
        if (tok.equalsIgnoreCase("method") || tok.equalsIgnoreCase("field")
                || tok.equalsIgnoreCase("class")
                || tok.equalsIgnoreCase("aspect")) {
            return params.substring(params.indexOf(tok) + tok.length()).trim();
        }

        return null;
    }

    private static String getPropertyType(String params) {
        StringTokenizer st = new StringTokenizer(params);

        String tok = st.nextToken();
        if (tok.equalsIgnoreCase("method") || tok.equalsIgnoreCase("field")
                || tok.equalsIgnoreCase("class")
                || tok.equalsIgnoreCase("aspect")) {
            return tok.toLowerCase();
        }

        return null;
    }

    public String getReturnType() {
        return returnTypeValue;
    }

    public String[] listParams() {
        return parameters.toArray(new String[parameters.size()]);
    }

    public String[] listDeclarations() {
        return declarations.toArray(new String[declarations.size()]);
    }

    public String getTitle() {
        return commentTitle;
    }
}