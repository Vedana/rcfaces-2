/*
 * $Id: VerifyVariables.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.RuntimeVisitors;
import com.vedana.js.Visitors;
import com.vedana.js.RuntimeVisitors.IRuntimeProblemHandler;
import com.vedana.js.dom.ASTNode;
import com.vedana.js.dom.Assignment;
import com.vedana.js.dom.BreakStatement;
import com.vedana.js.dom.ContinueStatement;
import com.vedana.js.dom.DefName;
import com.vedana.js.dom.FunctionDeclaration;
import com.vedana.js.dom.RefName;
import com.vedana.js.dom.ReturnStatement;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
 */
public class VerifyVariables implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile file) {

        FunctionDeclaration functions[] = Visitors.visitFunctions(file
                .getDocument().getStatements(), true);

        for (int i = 0; i < functions.length; i++) {
            FunctionDeclaration function = functions[i];

            System.out.println("----- " + function.getName());

            verifyVariables(stats, function);
        }

        return false;
    }

    private void verifyVariables(JsStats stats, FunctionDeclaration function) {
        Set<String> statics = new HashSet<String>();

        statics.addAll(stats.listStaticObjects());
        statics.add("arguments");
        statics.add("window");
        statics.add("document");
        statics.add("eval");
        statics.add("parseInt");
        statics.add("parseFloat");
        statics.add("String");
        statics.add("Date");
        statics.add("Array");
        statics.add("Object");
        statics.add("decodeURIComponent");
        statics.add("encodeURIComponent");
        statics.add("decodeURI");
        statics.add("encodeURI");
        statics.add("Error");
        statics.add("Function");
        statics.add("alert");
        statics.add("cameliaVersion");
        statics.add("RegExp");
        statics.add("Math");
        statics.add("escape");
        statics.add("unescape");
        statics.add("XMLHttpRequest");
        statics.add("ActiveXObject");
        statics.add("NaN");
        statics.add("isNaN");
        statics.add("__SYMBOL");
        statics.add("Image");
        statics.add("DOMParser");

        RuntimeVisitors.searchProblems(function, new IRuntimeProblemHandler() {

            public void duplicateDeclaration(DefName previousName,
                    DefName newName) {
                System.err.println("Duplicate ! '" + previousName + "' '"
                        + newName + "'");
            }

            public void notDefined(RefName ref) {
                System.err.println("Not defined node='" + ref + "'");
                return;

            }

            public void notInitialized(RefName node, DefName def,
                    boolean interFunction) {
                System.err.println("Not initialized node='" + node + "' def='"
                        + def + "' interFunction=" + interFunction);
            }

            public void alreadyBreaked(ASTNode node,
                    BreakStatement breakStatements[]) {
                System.err.println("Already breaked statement node='" + node
                        + "' break=" + Arrays.asList(breakStatements));
            }

            public void alreadyContinued(ASTNode node,
                    ContinueStatement continuedStatement) {
                System.err.println("Already continued statement node='" + node
                        + "' continue=" + continuedStatement);
            }

            public void alreadyReturned(ASTNode node,
                    ReturnStatement returnedStatements[]) {
                System.err.println("Already returned statement node='" + node
                        + "' return=" + Arrays.asList(returnedStatements));
            }

            public void neverRead(ASTNode lastWrite, ASTNode currentWrite) {
                System.err.println("Value never read node='" + currentWrite
                        + "' lastWrite=" + lastWrite);
            }

            public void possibleAccidentalBooleanAssignment(Assignment operation) {
                System.err
                        .println("Possible accidental boolean assignment node="
                                + operation);
            }

            public void loopNeverStop(ASTNode node) {
                System.err.println("Loop never stop. node=" + node);
            }

        }, statics);
    }

}
