/*
 * $Id: ICssWriter.java,v 1.2 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import org.rcfaces.core.component.capability.IBackgroundImageCapability;
import org.rcfaces.core.component.capability.IFontCapability;
import org.rcfaces.core.component.capability.IForegroundBackgroundColorCapability;
import org.rcfaces.core.component.capability.IMarginCapability;
import org.rcfaces.core.component.capability.IPositionCapability;
import org.rcfaces.core.component.capability.ISizeCapability;
import org.rcfaces.core.component.capability.ITextAlignmentCapability;
import org.rcfaces.core.component.capability.IVisibilityCapability;
import org.rcfaces.core.internal.renderkit.WriterException;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:01 $
 */
public interface ICssWriter {

    String ABSOLUTE = "absolute";

    String AUTO = "auto";

    String BOLD = "bold";

    String HIDDEN = "hidden";

    String INHERIT = "inherit";

    String INLINE = "inline";

    String ITALIC = "italic";

    String NO_REPEAT = "no-repeat";

    String NONE = "none";

    String NORMAL = "normal";

    String RELATIVE = "relative";

    String REPEAT_X = "repeat-x";

    String REPEAT_Y = "repeat-Y";

    String UNDERLINE = "underline";

    void done() throws WriterException;

    ICssWriter writeProperty(String name, String value);

    ICssWriter writeProperty(String name, String value, String unit);

    ICssWriter writeFont(IFontCapability capability);

    ICssWriter writeFont(String font);

    ICssWriter writeFontFamily(String fontFamily);

    ICssWriter writeFontSize(String fontSize);

    ICssWriter writeFontStyle(String fontStyle);

    ICssWriter writeFontWeight(String fontWeight);

    ICssWriter writeTextAlignment(ITextAlignmentCapability capability);

    ICssWriter writeForeground(IForegroundBackgroundColorCapability capability);

    ICssWriter writeColor(String color);

    ICssWriter writeVisibility(String visibility);

    ICssWriter writePosition(IPositionCapability capability);

    ICssWriter writePosition(String position);

    ICssWriter writeTextDecoration(String textDecoration);

    ICssWriter writeTopPx(int top);

    ICssWriter writeLeftPx(int left);

    ICssWriter writeTop(String top);

    ICssWriter writeLeft(String left);

    ICssWriter writeBottomPx(int bottom);

    ICssWriter writeRightPx(int right);

    ICssWriter writeSize(ISizeCapability capability);

    ICssWriter writeMargin(IMarginCapability capability);

    ICssWriter writeVisibility(IVisibilityCapability capability);

    ICssWriter writeBackground(
            IForegroundBackgroundColorCapability foregroundBackgroundColorCapability,
            IBackgroundImageCapability backgroundImageCapability);

    ICssWriter writeBackground(String backgroundColor,
            String backgroundImageURL, Boolean repeatX, Boolean repeatY,
            String positionX, String positionY);

    ICssWriter writeOverflow(String overflowValue);

    ICssWriter writeDisplay(String displayValue);

    ICssWriter writeWidth(String widthValue);

    ICssWriter writeWidthPx(int widthPx);

    ICssWriter writeHeight(String heightValue);

    ICssWriter writeHeightPx(int heightPx);

    ICssWriter writeTextAlign(String textAlignement);

    ICssWriter writeVerticalAlign(String verticalAlignement);

    ICssWriter writeBorderStyle(String borderStyle);

    ICssWriter writeBorderLeftWidth(int borderSizePx);
    
    ICssWriter writeBorderRightWidth(int borderSizePx);

    ICssWriter writeMargin(String margin);

    ICssWriter writeBackgroundColor(String backgroundColor);

    ICssWriter writePadding(String padding);

    ICssWriter writePaddingTop(String valueOf);
}