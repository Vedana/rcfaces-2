/*
 * $Id: f_cardBox.js,v 1.2 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * @class public f_cardBox extends f_component, fa_immediate
 *
 * @author olivier Oeuillot
 * @version $REVISION: $
 */

var __members={

	f_cardBox: function() {
		this.f_super(arguments);

		this._cards=new Array;
	},
	f_finalize: function() {
		this._selectedCard=undefined;

		var cards=this._cards;
		if (cards) {
			this._cards=undefined;

			for(var i=0;i<cards.length;i++) {
				var card=cards[i];
				
				// On peut le faire de ce destructeur, ou celui de la card !
				this.f_destroyCard(card);
			}
		}

		this.f_super(arguments);
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_updateCards: function() {
		var cards=this._cards;
		for(var i=0;i<cards.length;i++) {
			var card=cards[i];
		
			var ccard=f_core.GetElementByClientId(card._id);
			f_core.Assert(ccard, "f_cardBox.f_updateCards: Can not find card component of card '"+card._id+"'.");

			f_core.Debug(f_cardBox, "f_updateCards: Update card#"+i+" card="+card+" ccard="+ccard);
			card._ccard=ccard;
			ccard._vcard=card;			
			ccard.f_declareCard(this, card._value);	
		}
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_destroyCard: function(card) {
		var cards=this._cards;
		if (cards) {
			cards.f_removeElement(card);
		}

		if (!card._cardBox) {
			return;
		}		
		
		// card._id=undefined; // string
		card._cardBox=undefined;

		var ccard=card._ccard;
		f_core.Debug(f_cardBox, "Destroy card: "+card+"  comp="+ccard);
		if (ccard) {
			card._ccard=undefined;
		
			ccard._vcard=undefined;
		}
				
		// Pas forcement les cartes peuvent etre effacées aprés !
		//f_core.VerifyProperties(card);		
	},

	/**
	 * @method hidden
	 * @return void
	 */
	f_setCardFocus: function(card) {
	},

	/**
	 * @method hidden
	 * @return void
	 */
	f_updateCardStyle: function(card) {
	},

	/**
	 * @method hidden
	 * @return f_card
	 */
	f_declareCard: function(card) {
		f_core.Assert(typeof(card._id)=="string", "f_cardBox.f_declareCard: Invalid card id parameter ("+card._id+")");

		//var card=new Object;

		var cards=this._cards;
		if (cards.length) {
			var prev=cards[cards.length-1];
			prev._next=card;
			card._prev=prev;
		}
		cards.push(card);	
			
		card._cardBox=this;
		//card._value=cardValue;
		//card._id=cardBodyId;
			
		if (card._selected) {
			this._selectedCard=card;
		}
	
		if (f_core.IsDebugEnabled(f_cardBox)) {		
			card.toString=function() {
				return "[card id="+this._id+" value="+this._value+" selected="+this._selected+"]";
			};
		}
				
		return card;
	},

	/**
	 * @method public
	 * @param f_card cardComponent Card to select. (or a string as the identifier of the card)
	 * @param Boolean setFocus Set focus if possible !
	 * @return Boolean
	 */
	f_selectCard: function(cardComponent, setFocus) {
		if (typeof(cardComponent)=="string") {
			var id=cardComponent;
			cardComponent=f_core.GetElementByClientId(id);
	
			f_core.Assert(cardComponent, "f_cardBox.f_selectCard: Can not find card '"+id+"'.");
		}

		var card=cardComponent._vcard;
		
		if (!card) {
			return false;
		}
		
		return this._selectCard(card);
	},
	
	/**
	 * @method public
	 * @return f_card
	 */
	f_getSelectedCard: function() {
		var sc=this._selectedCard;
		if (!sc) {
			return null;
		}
		
		return sc._ccard;
	},
	/**
	 * Returns the value of the selected card.
	 * 
	 * @method public
	 * @return String
	 */
	f_getValue: function() {
		var selectedCard=this.f_getSelectedCard();
		
		if (!selectedCard) {
			return null;
		}
		
		return selectedCard.f_getValue();
	},
	/**
	 * Select a card by its value.
	 * 
	 * @method public
	 * @param String value
	 * @return Boolean <code>true</code> if success.
	 */
	f_setValue: function(value) {
		var card=this.f_getCardByValue(value);
		if (!card) {
			return false;
		}
			
		return this._selectCard(card);			
	},
	/**
	 * Search a card by its value.
	 * 
	 * @method public
	 * @param String value
	 * @return f_card
	 */
	f_getCardByValue: function(value) {
		var cards=this._cards;
		if (!cards) {
			f_core.Debug(f_cardBox, "f_getCardByValue: No card defined, searched value='"+value+"'.");
			return null;
		}
		
		for(var i=0;i<cards.length;i++) {
			var card=cards[i];
						
			if (card._value!=value) {
				continue;
			}
			
			return card;
		}	
		
		f_core.Debug(f_cardBox, "f_getCardByValue: Can not find card associated to value='"+value+"'.");
		return null;
	},
	/**
	 * @method private
	 * @return Boolean
	 */
	_selectCard: function(card) {		
		f_core.Assert(card && card._cardBox==this , "f_cardBox._selectCard: Invalid card object ("+card+")");

		if (this._selectedCard==card) {
			return false;
		}
		
		var old=this._selectedCard;
		this._selectedCard=null;
		if (old) {
			this.f_updateCardStyle(old);
		}
		
		if (old) {
			old._ccard.f_setVisible(false);
		}
		card._ccard.f_setVisible(true);
		
		this._selectedCard=card;
		this.f_updateCardStyle(card);
		
		this.f_setProperty(f_prop.SELECTED, card._id);
		
		return true;
	},
	f_setDomEvent: function(type, target) {
		switch(type) {
		case f_event.SELECTION:
			return;
		}

		this.f_super(arguments, type, target);
	},
	f_clearDomEvent: function(type, target) {
		switch(type) {
		case f_event.SELECTION:
			return;
		}

		this.f_super(arguments, type, target);
	},
	/**
	 * @method public 
	 * @return f_card[] Card array
	 */
	f_listCards: function() {
		var ret=new Array;
		
		var cards=this._cards;
		if (!cards) {
			return ret;
		}
		
		for(var i=0;i<cards.length;i++) {
			ret.push(cards[i]._ccard);	
		}
		
		return ret;
	}
};
 
new f_class("f_cardBox", {
	extend: f_component,
	aspects: [fa_immediate],
	members: __members
});
