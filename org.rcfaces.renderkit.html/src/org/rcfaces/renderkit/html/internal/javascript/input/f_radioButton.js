/*
 * $Id: f_radioButton.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * Class f_radioButton
 * 
 * @class public f_radioButton extends f_checkButton, fa_groupName, fa_required,
 *        fa_clientValidatorParameters
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
var __statics = {
	_InstallRequired : function(radioButton) {
		var checkCallbacks = {
			f_performCheckValue : function(event) {
				if (radioButton.f_getSelectedInGroup()) {
					return;
				}

				// Rien de sélectionné !
				//

				var summary = radioButton
						.f_getClientValidatorParameter("REQUIRED_ERROR_SUMMARY");
				var detail = radioButton
						.f_getClientValidatorParameter("REQUIRED_ERROR_DETAIL");

				if (!summary) {
					var resourceBundle = f_resourceBundle.Get(f_radioButton);
					summary = resourceBundle
							.f_formatParams("REQUIRED_ERROR_SUMMARY");
					// detail=resourceBundle.f_formatParams("REQUIRED_ERROR_DETAIL");

					if (!summary) {
						summary = f_locale.Get().f_formatMessageParams(
								"javax_faces_component_UIInput_REQUIRED", null,
								"Radio button selection is required.");
					}
				}

				var messageContext = f_messageContext.Get(radioButton);
				messageContext.f_addMessage(radioButton,
						f_messageObject.SEVERITY_ERROR, summary, detail);

				/*
				 * On met l'erreur sur le composant qui contient Required var
				 * components=radioButton.f_listAllInGroup(); for(var i=0;i<components.length;i++) {
				 * var component=components[i];
				 * 
				 * var messageContext=f_messageContext.Get(component);
				 * messageContext.f_addMessage(component,
				 * f_messageObject.SEVERITY_ERROR, message, null); }
				 */

				return false;
			}
		};

		radioButton._checkCallbacks = checkCallbacks;
		f_core.AddCheckListener(radioButton, checkCallbacks);
	},
	/**
	 * @method public static
	 * @param HTMLElement parent
	 * @param String localId  (<code>null</code> is accepted)
	 * @param String groupName
	 * @param optional String text
	 * @param optional String styleClass
	 * @param optional Object[] args
	 * @return f_radioButton
	 */
	Create : function(parent, localId, groupName, text, styleClass, args) {
		
		if (!localId) {
			localId=f_core.AllocateAnoIdentifier();
		}
		
		var id=fa_namingContainer.ComputeComponentId(parent, localId);
		
		var props={
			id: id,
			className: "f_radioButton"
		};		
		if (styleClass) {
			props["v:styleClass"]=styleClass;
			props.className+=" "+styleClass;
		}
		
		var label = f_core.CreateElement(parent, "LABEL", props);
		
		f_core.CreateElement(label, "input", {
			id: id+"::input",
			className: "f_radioButton_input",
			name: groupName,
			type: "radio"
		});
		
		f_core.CreateElement(label, "span", {
			id: id+"::text",
			className: "f_radioButton_text",
			textnode: (text)?text:''
		});
		
		return f_class.Init(label, f_radioButton, args);
	}
};

var __members = {

	f_radioButton : function() {
		this.f_super(arguments);

		if (this.f_isRequired()) { // Installe le handler si nécessaire !
			f_radioButton._InstallRequired(this);
		}
	},

	f_finalize : function() {
		var checkCallbacks = this._checkCallbacks;
		if (checkCallbacks) {
			this._checkCallbacks = undefined;

			f_core.RemoveCheckListener(this, checkCallbacks);
		}

		this.f_super(arguments);
	},

	/**
	 * @method public
	 * @return String
	 */
	f_getGroupName : function() {
		var input = this.f_getInput();
		if (!input) {
			return null;
		}
		return input.name;
	},
	/*
	 * @method public @param String group @return void
	 * 
	 * f_setGroupName: function(group) { var input=this._input; if (!input ||
	 * !group) { return; }
	 * 
	 * this.f_changeGroup(this.f_getGroupName(), group, this); input.name =
	 * group;
	 * 
	 * this.f_setProperty(f_prop.GROUPNAME,group); },
	 */

	/**
	 * @method public
	 * @return f_radioButton
	 */
	f_getSelectedInGroup : function() {
		return this.f_mapIntoGroupOfComponents(this.f_getGroupName(), function(item) {
			if (item.f_isSelected()) {
				return item;
			}
		});
	},
	/**
	 * Returns the value of the selected button of the group.
	 * 
	 * @method public
	 * @return String
	 */
	f_getValue : function() {
		var button = this.f_getSelectedInGroup();
		if (!button) {
			return undefined;
		}
		if (!button.f_getRadioValue) {
			return null;
		}
		return button.f_getRadioValue();
	},
	/**
	 * Select the radio button by its RadioValue.
	 * 
	 * @method public
	 * @param String
	 *            value radioValue of the button
	 * @return Boolean <code>true</code> if the button has been found.
	 */
	f_setValue : function(value) {
		if (this.f_mapIntoGroupOfComponents(this.f_getGroupName(), function(item) {
			if (!item.f_getRadioValue) {
				return undefined;
			}

			if (value != item.f_getRadioValue()) {
				return undefined;
			}

			item.f_setSelected(true);
			return true;

		})) {
			return true;
		}

		// On déselectionne tout alors !
		this.f_mapIntoGroupOfComponents(this.f_getGroupName(), function(item) {
			var itemInput = item.f_getInput();

			if (itemInput && itemInput.checked) {
				itemInput.checked = false;
			}
		});

		return false;
	},
	/**
	 * @method public
	 * @return f_radioButton[]
	 */
	f_listAllInGroup : function() {
		return this.f_listGroupOfComponents(this.f_getGroupName());
	},
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isSelected : function() {
		var input = this.f_getInput();
		if (!input) {
			return false;
		}

		return (input.checked == true);
	},
	/**
	 * @method public
	 * @param Boolean
	 *            set
	 * @return void
	 */
	f_setSelected : function(set) {
		var input = this.f_getInput();

		if (!input || input.checked == set) {
			return;
		}

		if (!set) {
			input.checked = false;
			return;
		}

		if (f_core.IsGecko()) {
			// Pour Gecko, il faut le faire à la main !

			this.f_mapIntoGroupOfComponents(this.f_getGroupName(), function(item) {
				var itemInput = item.f_getInput();

				if (itemInput && itemInput.checked) {
					itemInput.checked = false;
				}
			});
		}

		input.checked = true;
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getRadioValue : function() {
		var input = this.f_getInput();

		if (!input) {
			return undefined;
		}

		return input.value;
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_serializeValue : function() {
		var selectedButton = this.f_getSelectedInGroup();

		var value = (selectedButton) ? selectedButton.f_getInput().value : null;

		this.f_setProperty(f_prop.SELECTED, value);
	},
	/**
	 * @method protected
	 * @return Boolean
	 */
	fa_isNativeRadioElement : function() {
		return true;
	},
	/**
	 * @method protected
	 * @return void
	 */
	fa_updateRequired : function() {
		this.f_updateStyleClass();
	}
};

new f_class("f_radioButton", {
	extend : f_checkButton,
	aspects : [ fa_groupName, fa_required, fa_clientValidatorParameters ],
	members : __members,
	statics : __statics
});
