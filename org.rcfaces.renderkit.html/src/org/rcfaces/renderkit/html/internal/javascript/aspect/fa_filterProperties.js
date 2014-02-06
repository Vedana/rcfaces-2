/*
 * $Id: fa_filterProperties.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect FilterProperties
 *
 * @aspect abstract fa_filterProperties
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
 
var __members = {
	/**
	 * @method public
	 */
	fa_filterProperties: function() {
		// Au mieux on prend l'ancien !
		this._filtred=f_core.GetBooleanAttributeNS(this, "filtred", this._filtred); 		
	},
	/*
	f_finalize:  function() {
		// this._filtred=undefined; // boolean
		// this._filterExpression=undefined; // String
		// this._filterProperties=undefined; // Map<string, string>
	},
	*/
	
	/**
	 * Returns filter properties.
	 * 
	 * @method public
	 * @return Object
	 */
	f_getFilterProperties: function() {
		// On copie les propriétés !
		var ret=new Object;
		var properties=this.fa_getFilterPropertiesObject();
		if (!properties) {
			return ret;
		}
		
		for(var name in properties) {
			ret[name]=properties[name];
		}
		
		return ret;
	},
	/**
	 * Specify some properties of a filter expression.
	 * 
	 * @method public
	 * @param String name1
	 * @param String value1
	 * @param String... name2
	 * @return void
	 */
	f_setFilterProperty: function(name1, value1, name2) {
		var properties=this.fa_getFilterPropertiesObject();
		if (!properties) {
			properties=new Object;
		}
		
		for(var i=0;i<arguments.length;) {
			var name=arguments[i++];
			var value=arguments[i++];
			
			properties[name]=value;
		}
		
		this.f_setFilterProperties(properties);
	},
	/**
	 * Specify the filter expression.
	 * 
	 * @method public
	 * @param Object properties
	 * @return void
	 */
	f_setFilterProperties: function(properties) {
		f_core.Assert(this._filtred, "fa_filterProperties.f_setFilterProperties: This component does not support filter properties !");

		f_core.Assert(typeof(properties)=="object", "fa_filterProperties.f_setFilterProperties: Filter properties must be an Object or null !");
	
		var filterExpression="";
		if (properties) {
			filterExpression=f_core.EncodeObject(properties);
		}
	
		f_core.Debug(fa_filterProperties, "f_setFilterProperties: Expression of properties='"+filterExpression+"'.");
	
		//if (this._filterExpression==expression) {
			// return;
			// NON: Car il peut y avoir une mise à jour !
		//}
		
		var myProps=new Object;
		if (properties) {
			for(var name in properties) {
				myProps[name]=properties[name];
			}
		}
		
		this._filterProperties=myProps;
		this._filterExpression=filterExpression;
		this.f_setProperty(f_prop.FILTER_EXPRESSION, filterExpression);
		
		this.fa_updateFilterProperties(myProps);
	},
	/**
	 * @method hidden
	 * @return Object
	 */
	fa_getFilterPropertiesObject: function() {
		var filterProperties=this._filterProperties;
		if (filterProperties!==undefined) {
			return filterProperties;
		}
				
		var filterExpression=f_core.GetAttributeNS(this, "filterExpression");
		filterProperties=null;
	
		if (filterExpression) {
			filterProperties=f_core.DecodeObject(filterExpression);
		}
		
		this._filterExpression=filterExpression;
		this._filterProperties=filterProperties;
		return filterProperties;		
	},
	/**
	 * @method hidden
	 * @return String
	 */
	fa_getSerializedPropertiesExpression: function() {
		this.fa_getFilterPropertiesObject();
		
		return this._filterExpression;
	},
	/**
	 * @method hidden abstract
	 * @param String message
	 * @return void
	 */
	fa_cancelFilterRequest: f_class.ABSTRACT,
	
	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_updateFilterProperties: f_class.ABSTRACT
			
};

new f_aspect("fa_filterProperties", {
	members: __members
});

