/*
 * $Id: f_popupDnDInfo.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */

/**
 * f_popupDnDInfo class
 *
 * @class public f_popupDnDInfo extends f_abstractElementDnDInfo
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */

var __statics = {
			
	/**
	 * @field static final String
	 */
	DND_TARGET_INFO: "dnd.target.info",
	
	/**
	 * @field static final String
	 */
	DND_TARGET_ACTION: "dnd.target.action",

	/**
	 * @field static final String
	 */
	_NAME: "popup",
	
	/**
	 * @field static final String
	 */
	_CLASSNAME: "f_popupDnDInfo",
		
	Initializer: function() {
		f_dragAndDropEngine.RegisterDragAndDropPopup(f_popupDnDInfo._NAME, function(dragAndDropEngine) {
			return f_popupDnDInfo.f_newInstance(dragAndDropEngine);
		});
	}
};

var __members = {
	f_popupDnDInfo: function(dragAndDropEngine) {
		this.f_super(arguments, dragAndDropEngine);
		
		this.f_setOffsetPosition(16, 16);
	},
		
	f_finalize: function() {
		this._effectElement=undefined;
		this._actionElement=undefined;
		this._infoElement=undefined;
			
		this.f_super(arguments);
	},
	
	f_start: function() {
		this._showBorder=false;
		
		this.f_super(arguments);
	},
	
	f_fillElement: function(element) {
		this._effectElement=f_core.CreateElement(element, "img", {
			className: "f_popupDnDInfo_effect",
			src: f_env.GetBlankImageURL(),
			width: 16,
			height: 16
		});
		this._actionElement=f_core.CreateElement(element, "LABEL", {
			className: "f_popupDnDInfo_action"
		});
		this._infoElement=f_core.CreateElement(element, "LABEL", {
			className: "f_popupDnDInfo_info"
		});		
		
	},
	f_updateTarget: function(types, effect, targetComponent, targetItem, targetItemValue, targetAdditionalInformations) {
		this.f_super(arguments, types, effect, targetComponent, targetItem, targetItemValue, targetAdditionalInformations);
	
		var actionElement=this._actionElement;		
		var infoElement=this._infoElement;		

		if (!targetComponent) {
			actionElement.style.display="none";			
			infoElement.style.display="none";
			
			this.f_showBorder(false, effect);
			return;
		}		

		var show=false;

		var txt=this.f_getEffectActionLabel(types, effect, targetComponent, targetItem, targetItemValue, targetAdditionalInformations);		
		if (!txt) {
			actionElement.style.display="none";
		} else {		
			actionElement.style.display="inline";
			f_core.SetTextNode(actionElement, txt);
			show=true;
		}
		
		var txt=this.f_getEffectInfoLabel(types, effect, targetComponent, targetItem, targetItemValue, targetAdditionalInformations);		
		if (!txt) {
			infoElement.style.display="none";
		} else {		
			infoElement.style.display="inline";
			f_core.SetTextNode(infoElement, txt);
			show=true;
		}
		
		this.f_showBorder(show, effect);
	},
	/**
	 * @method protected
	 * @param Boolean show
	 * @param Number effect
	 * @return void
	 */
	f_showBorder: function(show, effect) {
		if (this._showBorder===show) {
			return;
		}
		
		this._showBorder=show;
		
		this.f_updateCursorStyle(effect);
	},
	f_computeCursorStyle: function(effect) {
		var cl=this.f_super(arguments, effect);
		
		if (this._showBorder) {
			cl+=" f_popupDnDInfo_border";
		}
		
		return cl;
	},
	/**
	 * @method protected
	 * @param Number effect
	 * @return String
	 */
	f_getEffectActionLabel: function(types, effect, targetComponent, targetItem, targetItemValue, targetAdditionalInformations) {
	
		var txt=this.f_getItemProperty(f_popupDnDInfo.DND_TARGET_ACTION, false, targetComponent, targetItem, targetItemValue, targetAdditionalInformations);
		if (txt) {
			return txt;
		}
		
		switch(effect) {
		case f_dndEvent.COPY_DND_EFFECT:
			return "Copier sur";
			
		case f_dndEvent.MOVE_DND_EFFECT:
			return "Déplacer vers";
			
		case f_dndEvent.LINK_DND_EFFECT:
			return "Créer le lien dans";
		}
		
		return null;
	},
	/**
	 * @method protected
	 * @param Number effect
	 * @return String
	 */
	f_getEffectInfoLabel: function(types, effect, targetComponent, targetItem, targetItemValue, targetAdditionalInformations) {
		return this.f_getItemProperty(f_popupDnDInfo.DND_TARGET_INFO, true, targetComponent, targetItem, targetItemValue, targetAdditionalInformations);
	},
	
	f_getCursorClassName: function() {
		return f_popupDnDInfo._CLASSNAME;
	}
};
		
new f_class("f_popupDnDInfo", {
	extend: f_abstractElementDnDInfo,
	statics: __statics,
	members: __members
});