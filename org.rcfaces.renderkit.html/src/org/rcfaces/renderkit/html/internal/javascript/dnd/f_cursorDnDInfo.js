/*
 * $Id: f_cursorDnDInfo.js,v 1.2 2013/11/13 12:53:31 jbmeslin Exp $
 */

/**
 * f_cursorDnDInfo class
 *
 * @class public f_cursorDnDInfo extends f_dragAndDropInfo
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:31 $
 */

var __statics = {
		
	/**
	 * @field static final String
	 */
	_NAME: "cursor",
		
	Initializer: function() {
		f_dragAndDropEngine.RegisterDragAndDropPopup(f_cursorDnDInfo._NAME, function(dragAndDropEngine) {
			return f_cursorDnDInfo.f_newInstance(dragAndDropEngine);
		});
	}
};

var __members = {	
		
	/**
	 * @field private Number
	 */
	_lastEffect: undefined, 
	
	/**
	 * @field private String
	 */
	_oldCursor: undefined, 
	
	f_cursorDnDInfo: function(dragAndDropEngine) {
		this.f_super(arguments, dragAndDropEngine);
		
	},
	f_finalize: function() {
		this.f_super(arguments);
		
		this.f_updateMouseCursor(-1);
	},
	/**
	 * @method protected
	 * @param Number effect
	 * @return void
	 */
	f_updateMouseCursor: function(effect) {
		if (effect==this._lastEffect) {
			return;
		}
		this._lastEffect=effect;
		
		var body=document.body;

		if (effect<0) {
			body.style.cursor=(this._oldCursor)?(this._oldCursor):"auto";
			return;
		}

		if (this._oldCursor===undefined) {
			this._oldCursor=body.style.cursor;
		}
		
		if (effect==f_dndEvent.NONE_DND_EFFECT) {
			body.style.cursor="no-drop";
			
		} else {
			body.style.cursor="default";
		}
	},
	/**
	 * @method public
	 * @param String[] types
	 * @param Number effect
	 * @param f_component targetComponent
	 * @param any targetItem
	 * @param Object targetItemValue
	 * @return void
	 */
	f_updateTarget: function(types, effect, targetComponent, targetItem, targetItemValue, infos) {
		f_core.Debug(f_cursorDnDInfo, "f_updateTarget: types="+types+" effect="+effect+" targetComponent='"+targetComponent+"' targetItem='"+targetItem+"' targetItemValue='"+targetItemValue+"'");

		this.f_updateMouseCursor(effect);
		
		this.f_super(arguments, types, effect, targetComponent, targetItem, targetItemValue, infos);
	},

	/**
	 * @method public
	 * @return void
	 */
	f_end: function() {
		f_core.Debug(f_cursorDnDInfo, "f_end: calling ...");

		this.f_updateMouseCursor(-1);
		
		this.f_super(arguments);
	}
};
		
new f_class("f_cursorDnDInfo", {
	extend: f_dragAndDropInfo,
	statics: __statics,
	members: __members
});