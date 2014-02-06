/*
 * $Id: f_criteriaPopupManager.js,v 1.2 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * f_criteriaPopupManager class
 * 
 * @class public f_criteriaPopupManager extends Object
 * @author jb.meslin@vedana.com
 * @version $Revision: 1.2 $
 */
var __statics = {

	/**
	 * @method public static
	 * @param Event evt
	 * @return Boolean
	 */
	OnPreSelectedCriteriaChange: function(evt) {
		var menu = evt.f_getComponent();
		menu.f_setSelectionAutoClose(false);
	},
		
	/**
	 * @method public static
	 * @param Event
	 *            evt
	 * @return Boolean
	 */
	OnSelectedCriteriaChange: function(evt) {

		var menu = evt.f_getComponent();
		var grid = menu.f_getOwnerComponent();
		var selectedCriteria = undefined;

		if (grid && grid.fa_evaluateCriteria) {

			var value = evt.f_getItem()._value;

			// colonne en cours
			var columnId = null;
			var columns = grid.f_getColumns();

			for ( var i = 0; i < columns.length; i++) {
				var col = columns[i];
				if (col._menuPopupId == menu._menuId) {
					columnId = col.f_getId();
				}
			}

			selectedCriteria = grid.fa_getSelectedCriteria();

			if (!selectedCriteria) {
				selectedCriteria = [];
			}

			// est elle déjà selectionnée
			var index = -1;
			for ( var i = 0; i < selectedCriteria.length; i++) {
				var crit = selectedCriteria[i];
				if (crit.id != columnId) {
					continue;
				}
				index = i;
				break;
			}

			var valuesObject = new Object;
			if (value === null) {
				value = fa_criteriaManager.DEFAULT_NULL_VALUE;
			}
			valuesObject.value = value;
			var oldValues = undefined;

			if (index != -1) {
				// si index alors on récupère les anciennes valeurs
				oldValues = selectedCriteria[index].values;
				// on enlève temporairement de la liste
				selectedCriteria.splice(index, 1);
			}

			var criteriaCardinality = grid
					.fa_getColumnCriteriaCardinality(columnId);

			if (oldValues
					&& criteriaCardinality != fa_cardinality.ONE_CARDINALITY) {

				var found = false;
				for ( var i = 0; i < oldValues.length; i++) {

					if (oldValues[i].value !== valuesObject.value) {
						if (oldValues.length == 1
								&& criteriaCardinality == fa_cardinality.OPTIONAL_CARDINALITY) {
							// optional : on peut déselectionner
							oldValues.splice(0, 1);
						}
						continue;
					}
					// valeur déjà selectionnée
					found = true;
					if (oldValues.length == 1
							&& criteriaCardinality == fa_cardinality.ONEMANY_CARDINALITY) {
						// si c'est le dernier en one many on le garde
						break;
					}
					// cas générale on supprime la valeur
					oldValues.splice(i, 1);
					break;
				}

				if (!found) {
					oldValues.push(valuesObject);
				}
			} else {
				// si nouveau critère ou one cardinality on ajoute directement
				oldValues = [ valuesObject ];
			}
			if (oldValues.length > 0 ) {
				// on met le critère en fin de liste
				selectedCriteria.push( {
					id : columnId,
					values : oldValues
				});
			}
		}
		// appel ajax pour mettre à jour les popups
		grid.fa_evaluateCriteria(selectedCriteria, function(resultCount,
				availableCriteria) {
			f_criteriaPopupManager.UpdateCriteriaPopups(resultCount,
					availableCriteria, selectedCriteria, grid);
		}, null);

		return f_core.CancelJsEvent(evt);
	},

	/**
	 * @method public
	 * @param Number
	 *            resultCount
	 * @param Array
	 *            availableCriteria
	 * @param Array selectedCriteria
	 * @param f_grid grid
	 * @return void 
	 */
	UpdateCriteriaPopups : function(resultCount, availableCriteria,
			selectedCriteria, grid) {

		if (availableCriteria === undefined) {
			// pas de critère possible? à gérer ? //TODO
			return;
		}

		var columns = grid.f_getColumns();
		// boucle sur chaque colonne
		for ( var i = 0; i < columns.length; i++) {
			var col = columns[i];
			var columnId = col.f_getId();
		
			
			if (columnId && grid.fa_getColumnCriteriaCardinality(columnId)) {
				var cardinality = grid.fa_getColumnCriteriaCardinality(columnId);
				var items = undefined;

				// recherche les criteres disponibles pour la colonne
				for ( var j = 0; j < availableCriteria.length; j++) {
					var criteria = availableCriteria[j];
					if (criteria.id == columnId) {
						var popupId = col._menuPopupId;
						var menuItem = undefined;
						if (popupId) {
							menuItem = grid.f_getSubMenuById(col._menuPopupId);
						}

						if (!menuItem) {
							break;
						}
						menuItem.f_removeAllItems(menuItem);
						menuItem._criteriaPopup = true;
						var selectedValues = [];
						for ( var k = 0; k < selectedCriteria.length; k++) {
							var criteriaSelected = selectedCriteria[k];

							if (criteriaSelected.id != columnId) { // critères
								// selectionnés
								// de la
								// colonne
								continue;
							}
							selectedValues = criteriaSelected.values;
							break;
						}

						for ( var k = 0; k < criteria.values.length; k++) {
							var item = criteria.values[k];
							var itemValue = item.value;
							var itemLabel = item.label;
							if (!itemLabel) {
								if (itemValue === null) { 
									var resourceBundle = f_resourceBundle.Get(fa_criteriaManager);
									itemLabel = "("+resourceBundle.f_get("EMPTY_LABEL") +")";
									itemValue = fa_criteriaManager.DEFAULT_NULL_VALUE;
								} else {
									itemLabel = itemValue;
								}
							}
							var checked = false;
							for ( var l = 0; l < selectedValues.length; l++) {
								var selectedValue = selectedValues[l].value;
								if (selectedValue === itemValue) {
									checked = true; // item selectionné
								}
							}
							
							
							switch (cardinality) {

							
							case fa_cardinality.ONE_CARDINALITY:
								menuItem.f_appendRadioItem(menuItem,
										col._menuPopupId + "::" + k,
										"criteriaGroup", itemLabel, itemValue,
										checked, true);
								break;
							case fa_cardinality.OPTIONAL_CARDINALITY:
							case fa_cardinality.ZEROMANY_CARDINALITY:
							case fa_cardinality.ONEMANY_CARDINALITY:
								
								menuItem.f_appendCheckItem(menuItem,
										col._menuPopupId + "::" + k, itemLabel,
										itemValue, checked, true);
								break;
							}

						}
					}
				}
			}
		}
		// refresh de l'arbre
		grid.fa_setSelectedCriteria(selectedCriteria);
	}

};

var __members = {

	f_criteriaPopupManager : function() {
		this.f_super(arguments);
	},

	f_finalize : function() {
		this.f_super(arguments);
	}

};

new f_class("f_criteriaPopupManager", {
	statics : __statics,
	members : __members
});
