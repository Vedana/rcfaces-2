/*
 * $Id: TextEditorToolFolderRenderer.java,v 1.3 2013/07/03 12:25:10 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.TextEditorComboComponent;
import org.rcfaces.core.component.TextEditorImageButtonComponent;
import org.rcfaces.core.component.TextEditorToolFolderComponent;
import org.rcfaces.core.component.ToolFolderComponent;
import org.rcfaces.core.component.ToolItemSeparatorComponent;
import org.rcfaces.core.component.capability.ITextEditorButtonType;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.item.IStyleClassItem;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:10 $
 */
public class TextEditorToolFolderRenderer extends ToolFolderRenderer {

    public static final String SEPARATOR = "separator";

    private static final Log LOG = LogFactory
            .getLog(TextEditorToolFolderRenderer.class);

    private static final String DEFAULT_ITEM_TYPES = 
    		//ITextEditorButtonType.CUT
            //+ ", " + ITextEditorButtonType.COPY + ", "
            //+ ITextEditorButtonType.PASTE + ", " + SEPARATOR + ","
    ITextEditorButtonType.UNDO + ", " + ITextEditorButtonType.REDO + ", "
            + SEPARATOR + "," + ITextEditorButtonType.FONT_NAME + ", "
            + ITextEditorButtonType.FONT_SIZE + ", " + SEPARATOR + ", "
            + ITextEditorButtonType.BOLD + ", " + ITextEditorButtonType.ITALIC
            + ", " + ITextEditorButtonType.UNDERLINE + ", "
            + ITextEditorButtonType.STRIKE + ", " + SEPARATOR + ", "
            + ITextEditorButtonType.JUSTIFY_LEFT + ", "
            + ITextEditorButtonType.JUSTIFY_CENTER + ", "
            + ITextEditorButtonType.JUSTIFY_RIGHT + ", "
            + ITextEditorButtonType.JUSTIFY_FULL + ", " + SEPARATOR + ", "
            + ITextEditorButtonType.ORDEREDLIST + ", "
            + ITextEditorButtonType.UNORDEREDLIST + ", "
            + ITextEditorButtonType.OUTDENT + ", "
            + ITextEditorButtonType.INDENT + ", " + SEPARATOR + ", "
            + ITextEditorButtonType.INCREASE_FONT_SIZE + ", "
            + ITextEditorButtonType.DECREASE_FONT_SIZE + ", "
            + ITextEditorButtonType.SUPER_SCRIPT + ", "
            + ITextEditorButtonType.SUB_SCRIPT;

    private static final SelectItem[] FONT_SIZE_SELECT_ITEMS = new SelectItem[] {
            new SelectItem("", ""), new SelectItem("1", "8"),
            /* new SelectItem("9pt", "9"), */
            new SelectItem("2", "10"), /* new SelectItem("11pt", "11"), */
            new SelectItem("3", "12"), new SelectItem("4", "14"),
            /* new SelectItem("16pt", "16"), */new SelectItem("5", "18"),
            /* new SelectItem("20pt", "20"), */new SelectItem("6", "24"),
            new SelectItem("7", "36"), /* new SelectItem("48pt", "48") */};

    private static final SelectItem[] FONT_SELECT_ITEMS = new SelectItem[] {
            new SelectItem("", "Par défaut"),
            new FontNameItem("arial,helvetica,sans-serif", "Arial",
                    "f_textEditor_arial"),
            new FontNameItem("arial black,avant garde", "Arial Black",
                    "f_textEditor_arialB"),
            new FontNameItem("andale mono,times", "Andale Mono",
                    "f_textEditor_andaleM"),
            new FontNameItem("book antiqua,palatino", "Book Antiqua",
                    "f_textEditor_bookA"),
            new FontNameItem("comic sans ms,sand", "Comic Sans MS",
                    "f_textEditor_comisSMS"),
            new FontNameItem("courier new,courier", "Courier New",
                    "f_textEditor_courierN"),
            new FontNameItem("georgia,palatino", "Georgia",
                    "f_textEditor_georgia"),
            new FontNameItem("helvetica", "Helvetica", "f_textEditor_helvetica"),
            new FontNameItem("impact,chicago", "Impact", "f_textEditor_impact"),
            new FontNameItem("symbol", "Symbol", "f_textEditor_fsymbol"),
            new FontNameItem("tahoma,arial,helvetica,sans-serif", "Tahoma",
                    "f_textEditor_tahoma"),
            new FontNameItem("terminal,monaco", "Terminal",
                    "f_textEditor_fterminal"),
            new FontNameItem("times new roman,times", "Times New Roman",
                    "f_textEditor_timesNR"),
            new FontNameItem("trebuchet ms,geneva", "Trebuchet MS",
                    "f_textEditor_trebuchetMS"),
            new FontNameItem("verdana,geneva", "Verdana",
                    "f_textEditor_verdana"),
            new FontNameItem("webdings", "Webdings", "f_textEditor_webdings"),
            new FontNameItem("wingdings,zapf dingbats", "Wingdings",
                    "f_textEditor_wingdings") };

    private static final String DEFAULT_TEXT_EDITOR_TOOL_FOLDER_STYLE_CLASS = "f_textEditorToolFolder";

    private static final Map<String, IToolFolderItemRenderer> TOOL_ITEMS_RENDERER = new HashMap<String, IToolFolderItemRenderer>(
            32);
    static {
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.BOLD,
                new TextEditorButtonItemRenderer(ITextEditorButtonType.BOLD));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.ITALIC,
                new TextEditorButtonItemRenderer(ITextEditorButtonType.ITALIC));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.UNDERLINE,
                new TextEditorButtonItemRenderer(
                        ITextEditorButtonType.UNDERLINE));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.STRIKE,
                new TextEditorButtonItemRenderer(ITextEditorButtonType.STRIKE));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.SUB_SCRIPT,
                new TextEditorButtonItemRenderer(
                        ITextEditorButtonType.SUB_SCRIPT));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.SUPER_SCRIPT,
                new TextEditorButtonItemRenderer(
                        ITextEditorButtonType.SUPER_SCRIPT));

        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.JUSTIFY_LEFT,
                new TextEditorButtonItemRenderer(
                        ITextEditorButtonType.JUSTIFY_LEFT, "alignment"));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.JUSTIFY_RIGHT,
                new TextEditorButtonItemRenderer(
                        ITextEditorButtonType.JUSTIFY_RIGHT, "alignment"));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.JUSTIFY_CENTER,
                new TextEditorButtonItemRenderer(
                        ITextEditorButtonType.JUSTIFY_CENTER, "alignment"));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.JUSTIFY_FULL,
                new TextEditorButtonItemRenderer(
                        ITextEditorButtonType.JUSTIFY_FULL, "alignment"));

        TOOL_ITEMS_RENDERER.put(SEPARATOR, new IToolFolderItemRenderer() {
        

            public void appendChildren(IHtmlWriter htmlWriter, List children) {
                children.add(new ToolItemSeparatorComponent());
            }
        });

        TOOL_ITEMS_RENDERER
                .put(ITextEditorButtonType.OUTDENT,
                        new TextEditorButtonItemRenderer(
                                ITextEditorButtonType.OUTDENT));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.INDENT,
                new TextEditorButtonItemRenderer(ITextEditorButtonType.INDENT));

        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.UNDO,
                new TextEditorButtonItemRenderer(ITextEditorButtonType.UNDO));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.REDO,
                new TextEditorButtonItemRenderer(ITextEditorButtonType.REDO));

        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.CUT,
                new TextEditorButtonItemRenderer(ITextEditorButtonType.CUT));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.COPY,
                new TextEditorButtonItemRenderer(ITextEditorButtonType.COPY));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.PASTE,
                new TextEditorButtonItemRenderer(ITextEditorButtonType.PASTE));

        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.UNORDEREDLIST,
                new TextEditorButtonItemRenderer(
                        ITextEditorButtonType.UNORDEREDLIST));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.ORDEREDLIST,
                new TextEditorButtonItemRenderer(
                        ITextEditorButtonType.ORDEREDLIST));

        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.DECREASE_FONT_SIZE,
                new TextEditorButtonItemRenderer(
                        ITextEditorButtonType.DECREASE_FONT_SIZE));
        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.INCREASE_FONT_SIZE,
                new TextEditorButtonItemRenderer(
                        ITextEditorButtonType.INCREASE_FONT_SIZE));

        TOOL_ITEMS_RENDERER.put(ITextEditorButtonType.FONT_NAME,
                new TextEditorComboItemRenderer(
                        ITextEditorButtonType.FONT_NAME, FONT_SELECT_ITEMS) {
                  

                    protected String listItems(IHtmlWriter htmlWriter) {
                        TextEditorToolFolderComponent toolFolderComponent = (TextEditorToolFolderComponent) htmlWriter
                                .getComponentRenderContext().getComponent();

                        return toolFolderComponent.getFontNames(htmlWriter
                                .getComponentRenderContext().getFacesContext());
                    }

                });

        TOOL_ITEMS_RENDERER
                .put(ITextEditorButtonType.FONT_SIZE,
                        new TextEditorComboItemRenderer(
                                ITextEditorButtonType.FONT_SIZE,
                                FONT_SIZE_SELECT_ITEMS) {
                            

                            protected String listItems(IHtmlWriter htmlWriter) {
                                TextEditorToolFolderComponent toolFolderComponent = (TextEditorToolFolderComponent) htmlWriter
                                        .getComponentRenderContext()
                                        .getComponent();

                                return toolFolderComponent
                                        .getFontSizes(htmlWriter
                                                .getComponentRenderContext()
                                                .getFacesContext());
                            }
                        });
    }

    protected List<UIComponent> getChildren(IHtmlWriter htmlWriter) {
        List<UIComponent> children = new ArrayList<UIComponent>();

        String itemTypes = ((TextEditorToolFolderComponent) htmlWriter
                .getComponentRenderContext().getComponent())
                .getItemTypes(htmlWriter.getComponentRenderContext()
                        .getFacesContext());

        if (itemTypes == null) {
            itemTypes = getDefaultItemTypes(htmlWriter);
        }

        if (itemTypes != null) {
            for (StringTokenizer st = new StringTokenizer(itemTypes, ",; "); st
                    .hasMoreTokens();) {

                String type = st.nextToken().toLowerCase();

                IToolFolderItemRenderer renderer = TOOL_ITEMS_RENDERER
                        .get(type);

                if (renderer == null) {
                    throw new FacesException("Unknown text editor item type '"
                            + type + "'");
                }

                renderer.appendChildren(htmlWriter, children);
            }
        }

        List<UIComponent> l = super.getChildren(htmlWriter);
        l.addAll(children);

        return l;
    }

    protected String getDefaultItemTypes(IHtmlWriter htmlWriter) {
        return DEFAULT_ITEM_TYPES;
    }

    protected void encodeBeginToolFolder(IHtmlWriter writer,
            ToolFolderComponent toolFolderComponent) throws WriterException {

        if (toolFolderComponent.isStyleClassSetted() == false) {
            toolFolderComponent
                    .setStyleClass(getDefaultTextEditorToolFolderStyleClass(writer));
        }

        super.encodeBeginToolFolder(writer, toolFolderComponent);
    }

    protected String getDefaultTextEditorToolFolderStyleClass(IHtmlWriter writer) {
        return DEFAULT_TEXT_EDITOR_TOOL_FOLDER_STYLE_CLASS;
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:10 $
     */
    protected interface IToolFolderItemRenderer {
        void appendChildren(IHtmlWriter htmlWriter, List<UIComponent> children);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:10 $
     */
    protected static class TextEditorButtonItemRenderer implements
            IToolFolderItemRenderer {

  

        private final String type;

        private final String groupName;

        public TextEditorButtonItemRenderer(String type) {
            this(type, null);
        }

        public TextEditorButtonItemRenderer(String type, String groupName) {
            this.type = type;
            this.groupName = groupName;
        }

        public void appendChildren(IHtmlWriter htmlWriter,
                List<UIComponent> children) {

            TextEditorImageButtonComponent button = new TextEditorImageButtonComponent(
                    type);

            button.setType(type);

            TextEditorToolFolderComponent toolFolderComponent = (TextEditorToolFolderComponent) htmlWriter
                    .getComponentRenderContext().getComponent();

            String forId = toolFolderComponent.getFor();

            String forClientId = htmlWriter
                    .getHtmlComponentRenderContext()
                    .getHtmlRenderContext()
                    .computeBrotherComponentClientId(toolFolderComponent, forId);

            button.setFor(forClientId);

            if (groupName != null) {
                button.setGroupName(groupName);
            }

            children.add(button);
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:10 $
     */
    protected static abstract class TextEditorComboItemRenderer implements
            IToolFolderItemRenderer {

        

        private final String type;

        private final SelectItem selectItems[];

        public TextEditorComboItemRenderer(String type,
                SelectItem selectItems[]) {

            this.type = type;
            this.selectItems = selectItems;
        }

        protected abstract String listItems(IHtmlWriter htmlWriter);

        public void appendChildren(IHtmlWriter htmlWriter,
                List<UIComponent> children) {

            TextEditorComboComponent combo = new TextEditorComboComponent(type);

            combo.setType(type);

            UISelectItems selectItemsComponent = new UISelectItems();

            SelectItem selectItems[] = null;

            String sItems = listItems(htmlWriter);
            if (sItems != null) {
                StringTokenizer st = new StringTokenizer(sItems, ", ");

                selectItems = new SelectItem[st.countTokens()];
                for (int i = 0; st.hasMoreTokens(); i++) {
                    String token = st.nextToken();

                    selectItems[i] = new SelectItem(token, token);
                }
            }

            if (selectItems == null) {
                selectItems = this.selectItems;
            }

            if (selectItems == null || selectItems.length < 1) {
                return;
            }

            selectItemsComponent.setValue(selectItems);

            TextEditorToolFolderComponent toolFolderComponent = (TextEditorToolFolderComponent) htmlWriter
                    .getComponentRenderContext().getComponent();

            String forId = toolFolderComponent.getFor();

            String forClientId = htmlWriter
                    .getHtmlComponentRenderContext()
                    .getHtmlRenderContext()
                    .computeBrotherComponentClientId(toolFolderComponent, forId);

            combo.setFor(forClientId);

            combo.getChildren().add(selectItemsComponent);

            children.add(combo);
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:10 $
     */
    public static class FontNameItem extends SelectItem implements
            IStyleClassItem {

        private static final long serialVersionUID = -6713360676011803876L;

        private final String styleClass;

        public FontNameItem(String fontNameValue, String fontNameLabel,
                String styleClass) {
            super(fontNameValue, fontNameLabel);

            this.styleClass = styleClass;
        }

        public String getStyleClass() {
            return styleClass;
        }

    }
}
