/*
 * $Id: TabbedPaneRenderer.java,v 1.4 2013/12/11 10:19:48 jbmeslin Exp $
 */

package org.rcfaces.renderkit.html.internal.renderer;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.rcfaces.core.component.TabComponent;
import org.rcfaces.core.component.TabbedPaneComponent;
import org.rcfaces.core.component.capability.IHiddenModeCapability;
import org.rcfaces.core.component.familly.IContentAccessors;
import org.rcfaces.core.component.iterator.ITabIterator;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.component.IImageAccessors;
import org.rcfaces.core.internal.component.IStatesImageAccessors;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.util.ParamUtils;
import org.rcfaces.renderkit.html.internal.HtmlTools;
import org.rcfaces.renderkit.html.internal.IAccessibilityRoles;
import org.rcfaces.renderkit.html.internal.ICssWriter;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptClasses;


/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/12/11 10:19:48 $
 */
public class TabbedPaneRenderer extends CardBoxRenderer {
	

	protected static final String TITLE_CLASSNAME_SUFFIX = "_title";

	protected static final String TITLE_ID_SUFFIX = ""
			+ UINamingContainer.SEPARATOR_CHAR
			+ UINamingContainer.SEPARATOR_CHAR + "title";

	protected static final String CONTENT_CLASSNAME = "_content";

	protected String getJavaScriptClassName() {
		return JavaScriptClasses.TABBED_PANE;
	}

	protected void renderTabHeader(IHtmlWriter htmlWriter)
			throws WriterException {

		IComponentRenderContext componentRenderContext = htmlWriter
				.getComponentRenderContext();

		TabbedPaneComponent tabbedPaneComponent = (TabbedPaneComponent) componentRenderContext
				.getComponent();

		FacesContext facesContext = htmlWriter.getComponentRenderContext()
				.getFacesContext();

		htmlWriter.startElement(IHtmlWriter.TABLE);

		htmlWriter.writeRole(IAccessibilityRoles.TAB);

		htmlWriter.writeId(getTitleId(htmlWriter));
		htmlWriter.writeClass(getTitleClassName(htmlWriter));
		htmlWriter.writeCellPadding(0);
		htmlWriter.writeCellSpacing(0);
		htmlWriter.writeln();

		TabComponent tabs[] = null;

		if (RcfacesContext.isJSF1_2() || RcfacesContext.isJSF2_0()) {
			ITabIterator tabsIterator = tabbedPaneComponent.listTabs();
            List<TabComponent> l = new ArrayList<TabComponent>(
                    tabsIterator.count());

			for (; tabsIterator.hasNext();) {
				TabComponent tabComponent = tabsIterator.next();

				if (tabComponent.isRendered() == false) {
					continue;
				}

				if (Boolean.FALSE.equals(tabComponent
						.getVisibleState(facesContext))) {
					tabComponent
							.setHiddenMode(IHiddenModeCapability.SERVER_HIDDEN_MODE);
					continue;
				}

				l.add(tabComponent);
			}

            tabs = l.toArray(new TabComponent[l.size()]);

			setCardBoxJSF12_Generation(htmlWriter.getComponentRenderContext()
					.getRenderContext());
		}

		htmlWriter.startElement(IHtmlWriter.TR);
		if (tabs != null) {
			renderTabbedPaneHeaderTitleTop(htmlWriter, tabs);
		}
		htmlWriter.endElement(IHtmlWriter.TR);

		htmlWriter.startElement(IHtmlWriter.TR);
		if (tabs != null) {
			renderTabbedPaneHeaderTitleBotom(htmlWriter, tabs);
		}
		htmlWriter.endElement(IHtmlWriter.TR);

		htmlWriter.endElement(IHtmlWriter.TABLE);

		htmlWriter.startElement(IHtmlWriter.DIV);
		/*
		 * String w = tabbedPaneComponent.getWidth(); if (w != null) {
		 * htmlWriter.writeAttribute("width", w); }
		 */
		htmlWriter.writeClass(getContentClassName(htmlWriter));

		String width = tabbedPaneComponent.getWidth(facesContext);
		String height = tabbedPaneComponent.getHeight(facesContext);

		/*
		 * if (tabbedPaneComponent.getAsyncRenderMode(facesContext) !=
		 * IAsyncRenderModeCapability.NONE_ASYNC_RENDER_MODE) { if (width ==
		 * null || height == null) { throw new FacesException( "TabbedPane '" +
		 * tabbedPaneComponent.getId() + "' can not have interactiveRender
		 * enable without settings of attributes width and height !", null); } }
		 */

		ICssWriter cssWriter = htmlWriter.writeStyle(16);
		if (width != null) {
			cssWriter.writeWidth("100%");
		}
		if (height != null) {
			// style += getPixelSize(height, 23) + "px;";
		}
	}

	protected String getTitleClassName(IHtmlWriter writer) {
		return getMainStyleClassName() + TITLE_CLASSNAME_SUFFIX;
	}

	protected String getTitleId(IHtmlWriter writer) {
		return writer.getComponentRenderContext().getComponentClientId()
				+ TITLE_ID_SUFFIX;
	}

	protected String getContentClassName(IHtmlWriter writer) {
		return getMainStyleClassName() + CONTENT_CLASSNAME;
	}

	protected void encodeEnd(IComponentWriter writer) throws WriterException {

		IHtmlWriter htmlWriter = (IHtmlWriter) writer;

		htmlWriter.endElement(IHtmlWriter.DIV);

		super.encodeEnd(htmlWriter);
	}

	protected boolean useComponentIdVarAllocation() {
		return true;
	}

	protected void renderTabbedPaneHeaderTitleTop(IHtmlWriter htmlWriter,
			TabComponent tabs[]) throws WriterException {

		for (int i = 0; i < tabs.length; i++) {
			writeTabHeaderTitleTop(htmlWriter, tabs[i], i == 0,
					(i + 1 < tabs.length) ? tabs[i + 1] : null);
		}
	}

	protected void writeTabHeaderTitleTop(IHtmlWriter htmlWriter,
			TabComponent tabComponent, boolean first,
			TabComponent nextTabComponent) throws WriterException {

		IHtmlRenderContext htmlRenderContext = htmlWriter
				.getHtmlComponentRenderContext().getHtmlRenderContext();
		FacesContext facesContext = htmlRenderContext.getFacesContext();

		TabbedPaneComponent tabbedPaneComponent = tabComponent.getTabbedPane();

		boolean selected = tabbedPaneComponent.getSelectedTab(facesContext) == tabComponent;

		String blankImageURL = htmlRenderContext.getHtmlProcessContext()
				.getStyleSheetURI(BLANK_IMAGE_URL, true);

		if (first) {
			htmlWriter.startElement(IHtmlWriter.TD);

			htmlWriter.startElement(IHtmlWriter.IMG);

			htmlWriter.writeClass((selected) ? "f_tabbedPane_ttitleLeftA"
					: "f_tabbedPane_ttitleLeft");

			htmlWriter.writeSrc(blankImageURL);
			htmlWriter.writeWidth(5);
			htmlWriter.writeHeight(5);

			htmlWriter.endElement(IHtmlWriter.IMG);

			htmlWriter.endElement(IHtmlWriter.TD);
		}

		htmlWriter.startElement(IHtmlWriter.TD);

		htmlWriter.writeClass((selected) ? "f_tabbedPane_ttitleText_selected"
				: "f_tabbedPane_ttitleText");
		htmlWriter.endElement(IHtmlWriter.TD);

		htmlWriter.startElement(IHtmlWriter.TD);

		htmlWriter.startElement(IHtmlWriter.IMG);

		if (nextTabComponent == null) {
			htmlWriter.writeClass((selected) ? "f_tabbedPane_ttitleRightA"
					: "f_tabbedPane_ttitleRight");

		} else if (tabbedPaneComponent.getSelectedTab(facesContext) == nextTabComponent) {
			htmlWriter.writeClass("f_tabbedPane_ttitleNextR");

		} else {
			htmlWriter.writeClass((selected) ? "f_tabbedPane_ttitleNextL"
					: "f_tabbedPane_ttitleNext");
		}
		htmlWriter.writeSrc(blankImageURL);
		htmlWriter.writeWidth(5);
		htmlWriter.writeHeight(5);

		htmlWriter.endElement(IHtmlWriter.IMG);

		htmlWriter.endElement(IHtmlWriter.TD);
	}

	protected void renderTabbedPaneHeaderTitleBotom(IHtmlWriter htmlWriter,
			TabComponent tabs[]) throws WriterException {

		for (int i = 0; i < tabs.length; i++) {
			writeTabHeaderTitleBottom(htmlWriter, tabs[i], i == 0,
					(i + 1 < tabs.length) ? tabs[i + 1] : null);
		}
	}

	protected void writeTabHeaderTitleBottom(IHtmlWriter htmlWriter,
			TabComponent tabComponent, boolean first,
			TabComponent nextTabComponent) throws WriterException {

		IHtmlRenderContext htmlRenderContext = htmlWriter
				.getHtmlComponentRenderContext().getHtmlRenderContext();
		FacesContext facesContext = htmlRenderContext.getFacesContext();

		TabbedPaneComponent tabbedPaneComponent = tabComponent.getTabbedPane();

		boolean selected = tabbedPaneComponent.getSelectedTab() == tabComponent;
		boolean disabled = tabComponent.isDisabled(facesContext)
				| tabbedPaneComponent.isDisabled(facesContext);

		if (first) {
			htmlWriter.startElement(IHtmlWriter.TD);
			htmlWriter
					.writeClass((selected) ? "f_tabbedPane_titleLeft_selected"
							: "f_tabbedPane_titleLeft");
			htmlWriter.endElement(IHtmlWriter.TD);
		}

		htmlWriter.startElement(IHtmlWriter.TD);

		if (disabled) {
			htmlWriter.writeClass("f_tabbedPane_titleText_disabled");

		} else if (selected) {
			htmlWriter.writeClass("f_tabbedPane_titleText_selected");

		} else {
			htmlWriter.writeClass("f_tabbedPane_titleText");
		}

		htmlWriter.startElement(IHtmlWriter.A);

		String tabId = computeTabInputId(htmlWriter, tabComponent);
		if (tabId != null) {
			htmlWriter.writeId(tabId);
		}

        htmlWriter.writeHRef_JavascriptVoid0();

		// AccessKey
		String accessKey = tabComponent.getAccessKey(facesContext);
		if (accessKey != null) {
			htmlWriter.writeAccessKey(accessKey);
		}

		Integer tabIndex = tabComponent.getTabbedPane().getTabIndex(
				facesContext);
		if (tabIndex != null) {
			htmlWriter.writeTabIndex(tabIndex.intValue());
		}

		IContentAccessors contentAccessors = tabComponent
				.getImageAccessors(facesContext);

		if (contentAccessors instanceof IImageAccessors) {
			IImageAccessors imageAccessors = (IImageAccessors) contentAccessors;

			String imageURL = null;

			if (disabled || selected) {
				if (imageAccessors instanceof IStatesImageAccessors) {
					IStatesImageAccessors statesImageAccessors = (IStatesImageAccessors) imageAccessors;

					if (disabled) {
						IContentAccessor contentAccessor = statesImageAccessors
								.getDisabledImageAccessor();
						if (contentAccessor != null) {
							imageURL = contentAccessor.resolveURL(facesContext,
									null, null);
						}
					} else if (selected) {
						IContentAccessor contentAccessor = statesImageAccessors
								.getSelectedImageAccessor();
						if (contentAccessor != null) {
							imageURL = contentAccessor.resolveURL(facesContext,
									null, null);
						}
					}
				}
			}

			if (imageURL == null) {
				IContentAccessor contentAccessor = imageAccessors
						.getImageAccessor();

				if (contentAccessor != null) {
					imageURL = contentAccessor.resolveURL(facesContext, null,
							null);
				}
			}

			if (imageURL != null) {
				htmlWriter.startElement(IHtmlWriter.IMG);

				htmlWriter.writeSrc(imageURL);
				htmlWriter.writeAlign("center");
				htmlWriter.writeBorder(0);
				htmlWriter.writeClass("f_tabbedPane_titleIcon");

				htmlWriter.endElement(IHtmlWriter.IMG);
			}
		}

		String text = tabComponent.getText(facesContext);
		if (text != null) {
			text = ParamUtils.formatMessage(tabComponent, text);
			HtmlTools.writeSpanAccessKey(htmlWriter, tabComponent, text, true);
		}

		htmlWriter.endElement(IHtmlWriter.A);

		htmlWriter.endElement(IHtmlWriter.TD);

		htmlWriter.startElement(IHtmlWriter.TD);

		if (nextTabComponent == null) {
			htmlWriter
					.writeClass((selected) ? "f_tabbedPane_titleRight_selected"
							: "f_tabbedPane_titleRight");

		} else if (tabbedPaneComponent.getSelectedTab(facesContext) == nextTabComponent) {
			htmlWriter.writeClass("f_tabbedPane_titleNext_sright");

		} else {
			htmlWriter.writeClass((selected) ? "f_tabbedPane_titleNext_sleft"
					: "f_tabbedPane_titleNext");
		}

		htmlWriter.endElement(IHtmlWriter.TD);
	}

	protected String computeTabInputId(IHtmlWriter htmlWriter,
			TabComponent tabComponent) {
		return tabComponent.getClientId(htmlWriter.getComponentRenderContext()
                .getFacesContext()) + TabRenderer.INPUT_ID_SUFFIX;
	}
}