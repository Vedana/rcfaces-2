/*
 * $Id: f_card.js,v 1.3 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * class Card
 *
 * @class f_card extends f_component, fa_asyncRender
 * @author olivier Oeuillot
 * @version $REVISION: $
 */
var __members = {
	f_card: function() {
		this.f_super(arguments);

		this.f_setHiddenMode(f_component.HIDDEN_MODE_IGNORE);
	},
	f_finalize: function() {
		var cardBox=this._cardBox;
		this._cardBox=undefined; // f_cardBox

//		this._value=undefined; // String
		
		var vcard=this._vcard;
		if (vcard) {
			// On efface la trace de cette carte .. 
			// Pour que le VerifyComponent soit correcte
			if (cardBox.f_destroyCard) {
				cardBox.f_destroyCard(vcard);
			}

			// On verifie tout de meme ....
			this._vcard=undefined;
		}
		
		this.f_super(arguments);
	},
	/**
	 * @method hidden
	 * @return void
	 */
	f_declareCard: function(cardBox, value) {
		this._cardBox=cardBox;
		this._value=value;
	},
	/**
	 * @method public
	 * @param optional Event evt Optional javascript event.
	 * @return void
	 */
	f_setFocus: function(evt) {
		if (this.f_isDisabled()) {
			return;
		}

		var cardBox=this._cardBox;
		if (cardBox) {
			cardBox.f_setCardFocus(this, evt);
		}
	},
	/**
	 * @method hidden
	 * @return f_component
	 */
	f_getFocusHandler: function() {
		return this._cardBox;
	},
	/**
	 * @method hidden
	 * @return HTMLElement
	 */
	fa_getInteractiveParent: function() {
		return this; //._tabbedPane;
	},
	/**
	 * @method public
	 * @return String value of the card.
	 */
	f_getValue: function() {
		return this._value;
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_serialize: function() {
		if (!this.f_isVisible()) {
			// Pour eviter que l'info soit transmis au serveur !
		
			this.f_setProperty(f_prop.VISIBLE, undefined);
		}	
		
		this.f_super(arguments);
	},
	/**
	 * @method public
	 * @return f_cardBox
	 */
	f_getCardBox: function() {
		return this._cardBox;
	}
};

new f_class("f_card", {
	extend: f_component,
	aspects: [ fa_asyncRender ],
	members: __members
});
