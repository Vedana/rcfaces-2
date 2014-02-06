/*
 * $Id: f_imageRadioButton.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */
 
/**
 * class f_imageRadioButton
 *
 * @class f_imageRadioButton extends f_imageCheckButton, fa_groupName, fa_required
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
var __members = {	
	
	f_imageRadioButton: function() {
		this.f_super(arguments);
		
		var groupName=f_core.GetAttributeNS(this,"groupName");
		if (groupName ) {	
			this._groupName=groupName;
		
			this.f_addToGroup(groupName, this.id);
		}
			
	},

/*
	f_finalize: function() {
		this._groupName=undefined; // string
			
		this.f_super(arguments);
	},
	*/
	
	/**
	 * @method protected
	 * @param f_event event
	 * @return Boolean
	 */
	f_performImageSelection: function(event) {
		if (this.f_isSelected()) {
			return false;
		}
		
		return this.f_super(arguments, event);
	},
	/**
	 * Set selected state.
	 * 
	 * @method public
	 * @param Boolean set
	 * @return void
	 */
	f_setSelected: function(set) {
		if (set!==false) {
			set=true;
		}
		
		if (set) {
			var selected=this.f_getSelectedInGroup();
			if (selected) {
				selected.f_setSelected(false);
			}		
		}

		this.f_super(arguments, set);
	},	
	/**
	 * Returns the group name of the button
	 * 
	 * @method public
	 * @return String
	 */
	f_getGroupName: function() {
		return this._groupName;
	},
	/*
	 * Set the group name of the button.
	 *
	 * @method protected
	 * @param String group
	 * @return void
	 *
	f_setGroupName: function(group) {
		if (group==this._groupName) {
			return;
		}
		
		this.f_changeGroup(this._groupName, group, this);
		this._groupName = group;
		this.f_setProperty(f_prop.GROUPNAME, group);
	},
	*/
	/**
	 * Returns the selected button of the same group of this button.
	 *
	 * @method public
	 * @return f_imageRadioButton
	 */
	f_getSelectedInGroup: function() {
		return this.f_mapIntoGroupOfComponents(this.f_getGroupName(), function(item) {
			if (item.f_isSelected()) {
				return item;
			}
		});
	},
	/**
	 * List all buttons of same group.
	 * 
	 * @method public
	 * @return f_imageRadioButton[]
	 */
	f_listAllInGroup: function() {
		return this.f_listGroupOfComponents(this.f_getGroupName());
	},
	fa_updateRequired: function() {
	},
	/**
	 * Returns the value associated to the input component.
	 *
	 * @method public
	 * @return Object The value associated.
	 */
	f_getValue: function() {
		var selected=this.f_getSelectedInGroup();
		if (!selected) {
			return null;
		}
		
		return selected.f_getInternalValue();
	},
	/**
	 * Returns the value associated to the input component.
	 *
	 * @method public
	 * @param Object value
	 * @return Boolean If value is recognized.
	 */
	f_setValue: function(value) {
		this.f_setSelected(value!==false);
	}
};

new f_class("f_imageRadioButton", {
	extend: f_imageCheckButton, 
	aspects: [ fa_groupName, fa_required ],
	members: __members
});
