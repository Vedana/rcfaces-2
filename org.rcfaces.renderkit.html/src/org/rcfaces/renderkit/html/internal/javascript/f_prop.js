/*
 * $Id: f_prop.js,v 1.3 2013/11/13 12:53:32 jbmeslin Exp $
 */

/**
 * f_prop class
 *
 * @class final hidden f_prop
 * @author Joel Merlin
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:32 $
 */
var __statics = {

	/**
	 * @field hidden static final String
	 */
	AUTO_COMPLETION:		"autoCompletion",

	/**
	 * @field hidden static final String
	 */
	BACKGROUND:				"background",

	/**
	 * @field hidden static final String
	 */
	CHECKABLE:				"checkable",

	/**
	 * @field hidden static final String
	 */
	CHECKED:				"checked",

	/**
	 * @field hidden static final String
	 */
	CHECKED_ITEMS:			"checkedItems",

	/**
	 * @field hidden static final String
	 */
	COLLAPSED:				"collapsed",

	/**
	 * @field hidden static final String
	 */
	COLLAPSED_ITEMS:		"collapsedItems",

	/**
	 * @field hidden static final String
	 */
	COLLAPSED_TEXT:			"collapsedText",

	/**
	 * @field hidden static final String
	 */
	COLUMN_WIDTHS:			"columnWidths",
	/**
	 * @field hidden static final String
	 */
	CONTENT_URL:			"contentURL",
	
	/**
	 * @field hidden static final String
	 */
	CURSOR:					"cursor",

	/**
	 * @field hidden static final String
	 */
	DATA:					"data",

	/**
	 * @field hidden static final String
	 */
	DELAY_MS:				"delayMs",

	/**
	 * @field hidden static final String
	 */
	DESELECTED_ITEMS:		"deselectedItems",

	/**
	 * @field hidden static final String
	 */
	DISABLE_PROPOSALS:		"disableProposals",

	/**
	 * @field hidden static final String
	 */
	DISABLED:				"disabled",

	/**
	 * @field hidden static final String
	 */
	DISABLED_ITEMS:			"disabledItems",

	/**
	 * @field hidden static final String
	 */
	DISABLED_IMAGE_URL:		"disabledImageURL",

	/**
	 * @field hidden static final String
	 */
	EDITABLE:				"editable",

	/**
	 * @field hidden static final String
	 */
	ENABLED_ITEMS:			"enabledItems",

	/**
	 * @field hidden static final String
	 */
	EXPANDED_ITEMS:			"expandedItems",

	/**
	 * @field hidden static final String
	 */
	FILTER_EXPRESSION:		"filterExpression",

	/**
	 * @field hidden static final String
	 */
	FIRST:					"first",

	/**
	 * @field hidden static final String
	 */
	FOCUS_ID:			"focusId",

	/**
	 * @field hidden static final String
	 */
	FOREGROUND:				"foreground",

	/**
	 * @field hidden static final String
	 */
	GROUPNAME:				"groupName",

	/**
	 * @field hidden static final String
	 */
	HEIGHT:					"height",

	/**
	 * @field hidden static final String
	 */
	HIDE_ADDITIONAL:		"hideAdditional",

	/**
	 * @field hidden static final String
	 */
	HIDEROOTNODE:			"hideRootNode",

	/**
	 * @field hidden static final String
	 */
	HORZSCROLLPOS:			"horizontalScrollPosition",

	/**
	 * @field hidden static final String
	 */
	HOVER_IMAGE_URL:		"hoverImageURL",

	/**
	 * @field hidden static final String
	 */
	ID:						"id",

	/**
	 * @field hidden static final String
	 */
	IMAGE_URL:				"imageURL",

	/**
	 * @field hidden static final String
	 */
	IMMEDIATE:				"immediate",

	/**
	 * @field hidden static final String
	 */
	INTERNAL:				"internal",

	/**
	 * @field hidden static final String
	 */
	LISTITEMSELECTED:		"listItemSelected",

	/**
	 * @field hidden static final String
	 */
	MAX:					"max",

	/**
	 * @field hidden static final String
	 */
	MIN:					"min",

	/**
	 * @field hidden static final String
	 */
	MULTIPLE:				"multiple",
	
	/**
	 * @field hidden static final String
	 */
	OUTLINED_LABEL:			"outlinedLabel",

	/**
	 * @field hidden static final String
	 */
	READONLY:				"readOnly",

	/**
	 * @field hidden static final String
	 */
	REQUIRED:				"required",

	/**
	 * @field hidden static final String
	 */
	ROWS:					"rows",

	/**
	 * @field hidden static final String
	 */
	SELECT:					"select",

	/**
	 * @field hidden static final String
	 */
	SELECTABLE:				"selectable",

	/**
	 * @field hidden static final String
	 */
	SELECTED:				"selected",

	/**
	 * @field hidden static final String
	 */
	SELECTED_IMAGE_URL:		"selectedImageURL",

	/**
	 * @field hidden static final String
	 */
	SELECTED_ITEMS:			"selectedItems",

	/**
	 * @field hidden static final String
	 */
	SERIALIZED_INDEXES:		"serializedIndexes",

	/**
	 * @field hidden static final String
	 */
	SHOW_ADDITIONAL:		"showAdditional",

	/**
	 * @field hidden static final String
	 */
	SORT_INDEX:				"sortIndex",

	/**
	 * @field hidden static final String
	 */
	SORT_ORDER:				"sortOrder",

	/**
	 * @field hidden static final String
	 */
	STEP:				"step",

	/**
	 * @field hidden static final String
	 */
	TABIDSELECTED:			"tabIdSelected",

	/**
	 * @field hidden static final String
	 */
	TEXT:					"text",

	/**
	 * @field hidden static final String
	 */
	TEXTALIGNMENT:			"textAlignment",

	/**
	 * @field hidden static final String
	 */
	TOOLTIP:				"toolTip",

	/**
	 * @field hidden static final String
	 */
	TREENODE_DEFEXPANDEDIMAGEURL:"defaultExpandedTreeNodeImageURL",

	/**
	 * @field hidden static final String
	 */
	TREENODE_DEFIMAGEURL:	"defaultTreeNodeImageURL",

	/**
	 * @field hidden static final String
	 */
	TREENODE_DEFLEAFIMAGEURL:"defaultLeafTreeNodeImageURL",

	/**
	 * @field hidden static final String
	 */
	UNCHECKED_ITEMS:		"uncheckedItems",

	/**
	 * @field hidden static final String
	 */
	VALUE:					"value",

	/**
	 * @field hidden static final String
	 */
	VERTSCROLLPOS:			"verticalScrollPosition",

	/**
	 * @field hidden static final String
	 */
	VISIBLE:				"visible",

	/**
	 * @field hidden static final String
	 */
	WIDTH:					"width",

	/**
	 * @field hidden static final String
	 */
	X:						"x",

	/**
	 * @field hidden static final String
	 */
	Y:						"y",
	
	/**
	 * @field hidden static final String
	 */
	ALL_VALUE:				"\x07all"
};

new f_class("f_prop", {
	statics: __statics
});

