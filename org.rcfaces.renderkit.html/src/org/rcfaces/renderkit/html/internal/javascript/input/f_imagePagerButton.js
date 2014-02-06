/*
 * $Id: f_imagePagerButton.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * class f_imagePagerButton
 *
 * @class f_imagePagerButton extends f_imageButton, fa_pager
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
var __members = {
	f_imagePagerButton: function() {
		this.f_super(arguments);
		
		this._type=f_core.GetAttributeNS(this,"type");
		
		var forComponent=f_core.GetAttributeNS(this,"for");
		this._hideIfDisabled=f_core.GetBooleanAttributeNS(this,"hideIfDisabled", false);
		
		// this.f_setDisabled(true); // D'office !  on attend la synchro !
		// C'est fait sur le serveur !

		if (forComponent) {
			this._for=forComponent;
			
		} else  {
			f_core.Error(f_imagePagerButton, "f_imagePagerButton: 'for' attribute is not defined !");
		}
	},
	f_finalize: function() {
		this._pagedComponent=undefined;
		
		// this._for=undefined; // string
		// this._type=undefined; // string
		// this._hideIfDisabled=undefined; // boolean
		
		this.f_super(arguments);
	},
	fa_pagedComponentInitialized: function(pagedComponent) {
		this._pagedComponent=pagedComponent;
		
		var type=this._type;
		
		var disabled=this._isDisabled(type, pagedComponent);
		
		this.f_setDisabled(disabled);	
		
		if (!this.fa_componentUpdated && !disabled) {
			// Et oui, le bouton est généré DISABLED et il peut passer en ENABLED lors de la construction ! 
			this._updateImage();
		}
	},
	/**
	 * @method protected
	 * 
	 * @param String type
	 * @param Element pagedComponent
	 * @return Boolean 
	 */
	_isDisabled: function(type, pagedComponent) {

		var first=pagedComponent.f_getFirst();		
		var rows=pagedComponent.f_getRows();
		// rows = nombre de ligne affichée
		
		var rowCount=pagedComponent.f_getRowCount(); 
		// rowCount peut etre negatif, si on ne connait pas le nombre
		
		f_core.Debug(f_imagePagerButton, "fa_pagedComponentInitialized: Update image: id="+this.id+" type="+type+" first="+first+" rows="+rows+" rowCount="+rowCount);
		
		if (!pagedComponent || !type || !rowCount || !rows) {
			return true;
		}
		
		type=type.toLowerCase();
			
		switch(type) {
		case "first":
		case "prev":
			return (first<1);
				
		case "next":
		case "last":
			return (rowCount>0 && first+rows>=rowCount);

		default:
			var pageN=parseInt(type, 10);
			if (!isNaN(pageN) && (first/rows)!=pageN) {
				return (rowCount>=0 && pageN*rows>rowCount);
			}
		}
			
		return true;
	},
	
	fa_updateDisabled: function(set) {
		this.f_super(arguments, set);
		
		if (this._hideIfDisabled) {
			this.f_setVisible(!set);
		}
	},

	f_imageButtonSelect: function() {
		if (!this._focus)  {
			this.f_setFocus();
		}

		if (this.f_isReadOnly() || this.f_isDisabled()) {
			return false;
		}

		var type=this._type;
		if (!type) {
			return false;
		}

		var pagedComponent=this._pagedComponent;
		if (!pagedComponent) {
			return false;
		}

		this._processSelection(pagedComponent, type);
		
		return false;
	},
	/**
	 * 
	 * @method protected
	 * @param pagedComponent
	 * @param type
	 * @return void
	 */
	_processSelection: function(pagedComponent, type) {

		var first=pagedComponent.f_getFirst();
		var rows=pagedComponent.f_getRows();

		var newFirst=-1;
				
		switch(type.toLowerCase()) {
		case "first":
			newFirst=0;
			break;
		
		case "prev":
			newFirst=first-rows;
			if (newFirst<0) {
				newFirst=0;
			}
			break;
		
		case "next":
			newFirst=first+rows;
			break;
		
		case "last":
			var maxRows=pagedComponent.f_getMaxRows();
			var rowCount=pagedComponent.f_getRowCount();

			if (rowCount>0) {
				newFirst=rowCount - ((rowCount+rows-1) % rows)-1;
	
			} else if (first+rows==maxRows) {
				newFirst=maxRows;
			
			} else {
				newFirst=maxRows - ((maxRows+rows-1) % rows)-1;
			}
			break;
		}

		if (newFirst>=0) {
			pagedComponent.f_setFirst(newFirst);
		}
	},
	
	f_update: function() {
		this.f_super(arguments);

		var forComponent=this._for;
		
		if (forComponent) {
			fa_pagedComponent.RegisterPager(forComponent, this);
		}
	}
};

new f_class("f_imagePagerButton", {
	extend: f_imageButton,
	aspects: [ fa_pager ],
	members: __members
});
