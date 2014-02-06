/**
 * Aspect Criteria Manager
 *
 * @aspect public abstract fa_criteriaManager   
 * @author jbmeslin@vedana.com
 * @version $Revision: 1.2 $   
 */
 
var __statics = {
		
	/**
	 * @field public static final String
	 */
	DEFAULT_NULL_VALUE: '\x01'
			
};

var __members = {

	f_finalize: function() {
		this._selectedCriteria = undefined; //ISelectedCriteria[] 
	},

	/**
	 * @method public
	 * @param Object selectedCriteria
	 * @param Object callback 
	 * @return void
	 */
	fa_evaluateCriteria: f_class.ABSTRACT,
	
	/**
	 * @method protected abstract
	 * @param Array criteriaSelected  Sous la forme  [{ id: "idColonne", values: [ val1, val2 ] }, {...} ] 
	 * @return void
	 */
	fa_setSelectedCriteria: f_class.ABSTRACT,
	
	/**
	 * @method protected 
	 * @return Array selectedDatagrid
	 */
	fa_getSelectedCriteria: function () {
		return this._selectedCriteria;
	},
	
	/**
	 * @method private
	 * @param Array selectedCriteria
	 * @return Array 
	 */
	_computeSelectedCriteria : function(selectedCriteria) {
		
		if (selectedCriteria === undefined) {
			return undefined;
		}
		
		var result = new Array();
		for (var i = 0; i < selectedCriteria.length; i++) {
			var crit=selectedCriteria[i];
			
			result.push(encodeURIComponent(crit.id));
			
			var array= crit.values;
			var arrayString=new Array;
			for(var j=0;j<array.length;j++) {			
				arrayString.push(encodeURIComponent(array[j].value));
			}
			
			result.push(encodeURIComponent(arrayString.join(',')));
		}
		
		return result.join(',');
	},
	
	f_serialize :  {
		after : function() {
			if (this._selectedCriteria !== undefined) {
				
				var selectedCriteria = this._selectedCriteria;
				var selectedCriteriaColumns = "";
				for ( var i = 0; i < selectedCriteria.length; i++) {
					var criteria = selectedCriteria[i];
					if (i<1) {
						selectedCriteriaColumns += criteria.id;
					}else {
						selectedCriteriaColumns += ","+criteria.id;
					}
				}
				this.f_setProperty("criteriaValues", this._computeSelectedCriteria(this._selectedCriteria));
				this.f_setProperty("selectedCriteriaColumns", selectedCriteriaColumns);	
			}
		}
	},
	
	/**
	 * @method protected abstract
	 * @param String columnId
	 * @param Array values
	 * @return Object criteriaSelected
	 */
	fa_addSelectedCriteria : f_class.ABSTRACT,
	
	/**
	 * @method public 
	 * @return Integer cardinality
	 */
	fa_getColumnCriteriaCardinality: f_class.ABSTRACT,
	
	/**
	 * @method public abstract
	 * @param String columnId Identifier of Column or column object  
	 */
	fa_getCriteriaLabelByColumn: f_class.ABSTRACT,
	
	/**
	 * Returns the cardinality of a column criteria
	 * 
	 * @method public abstract 
	 * @param String columnId Identifier of column
	 * @return Number  criteria cardinality constant or "undefined" if the column is not known 
	 */
	fa_getColumnCriteriaCardinality: f_class.ABSTRACT

};

new f_aspect("fa_criteriaManager", {
	statics: __statics,
	members: __members
});
