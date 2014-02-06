/*
 * $Id: fa_clientData.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect ClientData
 *
 * @aspect fa_clientData
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __statics = {
	/**
	 * @field private static final String
	 */
	_REMOVED_PROPERTY: "removed",
	
	/**
	 * @field private static final String
	 */
	_CHANGED_PROPERTY: "changed"
};

var __members = {
/*
	f_finalize: function() {
		this._clientDatas=undefined;  // Map<string, string>
		this._modifiedDatas=undefined;  // Map<string, string>			 
		this._newDatas=undefined; // Map<string, string>
	},
	*/
	
	/**
	 * 
	 * @method public
	 * @param String name Name of property
	 * @return String
	 */
	f_getClientData: function(name) {
		f_core.Assert(typeof(name)=="string", "fa_clientData.f_getClientData: Name of clientData must be a string !");

		var clientDatas=this._clientDatas;
		if (clientDatas===undefined) {
			clientDatas=f_core.ParseDataAttribute(this);
			this._clientDatas=clientDatas;
		}
		
		return clientDatas[name];
	},
	/**
	 * 
	 * @method public
	 * @return Object
	 * @see #f_getClientDataSet()
	 */
	f_getClientDatas: function() {
		return this.f_getClientDataSet();
	},
	/**
	 * 
	 * @method public
	 * @return Object 
	 */
	f_getClientDataSet: function() {
		if (this._clientDatas===undefined) {
			this.f_getClientData("");
		}
		
		var clientData=this._clientDatas;

		var obj=new Object();
		for(var name in clientData) {
			obj[name]=clientData[name];
		}		
		
		return obj;
	},
	/**
	 * 
	 * @method public
	 * @param String name1
	 * @param optional String value1
	 * @param optional String... name2
	 * @return void
	 */
	f_setClientData: function(name1, value1, name2) {
		if (this._clientDatas===undefined) {
			this.f_getClientData("");
		}
		
		var data=this._clientDatas;
		
		if (!this.fa_componentUpdated) {
			for (var i=0;i<arguments.length;) {
				var name=arguments[i++];
				var value=arguments[i++];

				data[name]=value;
			}
			return;
		}		
		
		var modifiedData=this._modifiedDatas;
		var newData=this._newDatas;
		
		if (!modifiedData) {
			modifiedData=new Object;
			this._modifiedDatas=modifiedData;

			newData=new Object;
			this._newDatas=newData;
		}
		
		for (var i=0;i<arguments.length;) {
			var name=arguments[i++];
			f_core.Assert(typeof(name)=="string", "fa_clientData.f_setClientData: Name of clientData must be a string !");

			var value=undefined;
			
			if (i<arguments.length) {
				value=arguments[i++];
				
				f_core.Assert(typeof(value)=="string" || !value, "fa_clientData.f_setClientData: Value of clientData must be a string or null !");
			}
			
			if (!data[name] && !modifiedData[name] && !newData[name]) {
				// C'est un nouveau !
				newData[name]=true;
			}
			
			if (!value) {
				delete data[name];
				
				if (newData[name]) {
					// C'est un nouveau ... on efface seulement la propriété "modifié" !
					delete modifiedData[name];
					continue;
				}
					
				modifiedData[name]=fa_clientData._REMOVED_PROPERTY;
				continue;
			}
			
			modifiedData[name]=fa_clientData._CHANGED_PROPERTY;
			data[name] = value;
		}
	},
	/**
	 * Clear all client datas associated to the component.
	 * 
	 * @method public
	 * @return void
	 */
	f_clearClientDatas: function() {
		var data=this._clientDatas;
		
		if (data===undefined) {
			return;
		}
		
		this._clientDatas=new Object();
		if (!this.fa_componentUpdated) {
			return;
		}		
		
		var modifiedData=this._modifiedDatas;
		var newData=this._newDatas;
		
		if (!modifiedData) {
			modifiedData=new Object;
			this._modifiedDatas=modifiedData;

			newData=new Object;
			this._newDatas=newData;
		}
		
		for(var name in data) {							
			if (newData[name]) {
				// C'est un nouveau ... on efface seulement la propriété "modifié" !
				delete modifiedData[name];
				continue;
			}
			modifiedData[name]=fa_clientData._REMOVED_PROPERTY;
		}
	},
	f_serialize: {
		after: function() {
			var modifiedData=this._modifiedDatas;
			if (!modifiedData) {
				return;
			}
			
			var v=new Array;
			
			var data=this._clientDatas;
			for(var name in modifiedData) {				
				var type=modifiedData[name];
				if (type==fa_clientData._REMOVED_PROPERTY) {
					v.push("R", name);
					continue;
				}

				var value=data[name];
				v.push("S", name, value);
			}
			
			this.f_setProperty(f_prop.DATA, v, true);
		}
	}
};

new f_aspect("fa_clientData", {
	statics: __statics,
	members: __members
});
