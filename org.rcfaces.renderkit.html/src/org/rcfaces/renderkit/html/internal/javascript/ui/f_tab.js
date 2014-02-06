/*
 * $Id: f_tab.js,v 1.3 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * class Tab.
 *
 * @class f_tab extends f_card
 * @author olivier Oeuillot
 * @version $REVISION: $
 */

var __members = {
	
	f_tab: function() {
		this.f_super(arguments);
		
		var tabbedPaneClientId=f_core.GetAttributeNS(this,"tabbedPaneId");
		if (tabbedPaneClientId) {
			var properties= {
				_id:				this.id,
				_titleGenerated:	true,
				_value: 			f_core.GetAttributeNS(this,"value"),
				_selected: 			f_core.GetBooleanAttributeNS(this,"selected", false),
				_disabled: 			f_core.GetBooleanAttributeNS(this,"disabled", false),
				_text: 				f_core.GetAttributeNS(this,"text"),
				_accessKey: 		f_core.GetAttributeNS(this,"accessKey"),
				_imageURL: 			f_core.GetAttributeNS(this,"imageURL"),
				_selectedImageURL: 	f_core.GetAttributeNS(this,"selectedImageURL"),
				_hoverImageURL: 	f_core.GetAttributeNS(this,"hoverImageURL"),
				_disabledImageURL: 	f_core.GetAttributeNS(this,"disabledImageURL")
			};
			
			var tabbedPane=f_core.GetElementByClientId(tabbedPaneClientId, this.ownerDocument, true);
			
			tabbedPane.f_declareCard(properties);			
		}
	},
	
	f_finalize: function() {		
		this._mask=undefined;  // HTMLElement
		// this._text=undefined;  // String
		// this._accessKey=undefined; // String
		// this._value=undefined; // String
		
		// this._disabled=undefined; // boolean
		// this._imageURL=undefined; // string
		// this._hoverImageURL=undefined; // string
		// this._disabledImageURL=undefined; // string
		// this._selectedImageURL=undefined; // string
		
		this.f_super(arguments);
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getText: function() {
		return this._text;
	},
	/**
	 * @method public
	 * @param String text The text.
	 * @return void
	 */
	f_setText: function(text) {
		if (text == this._text) {
			return;
		}
		this._text=text;
		this.f_setProperty(f_prop.TEXT,text);
		
		var cardBox=this._cardBox;
		if (cardBox) {
			cardBox.f_setTabText(this, text);
		}
		
		var heading=document.getElementById(this.id+"::heading");
		if (heading) {
			f_core.SetTextNode(heading, text);
		}
		
	},
	/**
	 * @method public
	 * @param String imageURL
	 * @return void
	 */
	f_setImageURL: function(imageURL) {
		this._imageURL=imageURL;

		var cardBox=this._cardBox;
		if (cardBox) {
			cardBox.f_setTabImageURL(this, imageURL);
		}
	},
	/**
	 * @method public
	 * @param String imageURL
	 * @return void
	 */
	f_setDisabledImageURL: function(imageURL) {
		this._disabledImageURL=imageURL;

		var cardBox=this._cardBox;
		if (cardBox) {
			cardBox.f_setTabDisabledImageURL(this, imageURL);
		}
	},
	/**
	 * @method public
	 * @param String imageURL
	 * @return void
	 */
	f_setHoverImageURL: function(imageURL) {
		this._hoverImageURL=imageURL;

		var cardBox=this._cardBox;
		if (cardBox) {
			cardBox.f_setTabHoverImageURL(this, imageURL);
		}
	},
	/**
	 * @method public
	 * @param String imageURL
	 * @return void
	 */
	f_setSelectedImageURL: function(imageURL) {
		this._selectedImageURL=imageURL;

		var cardBox=this._cardBox;
		if (cardBox) {
			cardBox.f_setTabSelectedImageURL(this, imageURL);
		}
	},
	/**
	 * @method public
	 * @param Boolean disabled
	 * @return void
	 */
	f_setDisabled: function(disabled) {
		if (disabled!==false) {
			disabled=true;
		}
		
		if (this._disabled==disabled) {
			return;
		}
		
		this._disabled=disabled;
		
		var cardBox=this._cardBox;
		if (cardBox) {
			cardBox.f_setTabDisabled(this, disabled);
		}
		
		this.f_setProperty(f_prop.DISABLED, disabled);
	},
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isDisabled: function() {
		return this._disabled;
	},
	/**
	 * @method hidden
	 */
	f_declareTab: function(tabbedPane, value, text, accessKey, disabled, imageURL, disabledImageURL, selectedImageURL, hoverImageURL) {
		this.f_declareCard(tabbedPane, value);

		this._text=text;
		this._accessKey=accessKey;
		this._disabled=disabled;
		this._imageURL=imageURL;
		this._disabledImageURL=disabledImageURL;
		this._hoverImageURL=hoverImageURL;
		this._selectedImageURL=selectedImageURL;
		
		var mask=this.ownerDocument.createElement("div");
		mask.className="f_tabbedPane_tab_mask";
		this._mask=mask;
		
		f_core.InsertBefore(this, mask, this.firstChild);
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getAccessKey: function() {
		return this._accessKey;
	},
	f_performAccessKey: function(evt) {
		var cardBox=this._cardBox;
		if (cardBox) {
			cardBox.f_performTabAccessKey(this, evt);
		}
	},
	/**
	 * @method protected
	 * @return Boolean
	 */	 
	f_parentShow: function() {
		if (this.f_isDisabled()) {
			return false;
		}
		
		this._cardBox.f_selectCard(this, false);
		
		return this.f_super(arguments);		
	},
	/**
	 * @method public
	 * @return Boolean
	 */
	f_forceChildVisibility: function(component) {
		return false;
	}
};

new f_class("f_tab", {
	extend: f_card,
	members: __members
});
