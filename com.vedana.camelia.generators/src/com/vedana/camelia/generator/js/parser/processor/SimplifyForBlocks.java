/*
 * $Id: SimplifyForBlocks.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser.processor;

import com.vedana.camelia.generator.js.parser.IJsFile;
import com.vedana.camelia.generator.js.parser.JsStats;
import com.vedana.js.Visitors;
import com.vedana.js.dom.Block;
import com.vedana.js.dom.ILoop;
import com.vedana.js.dom.Statement;

public class SimplifyForBlocks implements IJsFileProcessor {

    public boolean process(JsStats stats, IJsFile jsFile) {

        boolean modified = false;

        ILoop loops[] = Visitors.visitLoop(
                jsFile.getDocument().getStatements(), false);

        for (ILoop loop : loops) {

            Statement body = loop.getBody();
            if ((body instanceof Block) == false) {
                continue;
            }

            Block block = (Block) body;

            if (block.getStatements().size() != 1) {
                continue;
            }

            loop.setBody((Statement) block.getStatements().get(0));

            modified = true;
        }

        return modified;
    }
}
