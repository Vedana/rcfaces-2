
/**
 * f_indexedDbEngine class
 *
 * @class public f_indexedDbEngine extends f_object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:32 $
 */


var __statics = {
	
	/**
	 * @field private static final String
	 */
	_DATABASE_NAME: "rcfacesDb", 
	
	/**
	 * @field private static final Number
	 */
	_TRANSACTION_TIMEOUT_MS: 2000,
	
	/**
	 * @field private static final String
	 */
	_RCFACES_OBJECTSTORE: "rcfaces.directory",
	
	/**
	 * @field private static final String
	 */
	_RCFACES_INDEX_NAME: "rcfaces_index",
	
	
	/**
	 * @field hidden static final String
	 */
	INDEX_FIELD: "__index",
	
	_Singleton: undefined,	
	
	
	/**
	 * @method public static
	 * @return f_indexedDbEngine
	 */
	Get: function() {
		var s=f_indexedDbEngine._Singleton;
		
		if (s===undefined) {
			var indexedDb = window.indexedDB || window.webkitIndexedDB || window.mozIndexedDB || window.msIndexedDB;
			// Now we can open our database
			
			f_core.Info(f_indexedDbEngine, "Get: IndexedDb engine detection="+!!indexedDb);
			
			if (indexedDb) {
				s = f_indexedDbEngine.f_newInstance(indexedDb);

			} else {			
				s=null;
			}
			
			f_indexedDbEngine._Singleton=s;
			
			if (!window.IDBTransaction) {
				if (window.webkitIDBTransaction) {
					// Chrome s'amuse ... 
					window.IDBTransaction = window.webkitIDBTransaction;
					window.IDBKeyRange = window.webkitIDBKeyRange;
				}
			}
		}
		
		return s; 
	},
		
	/**
	 * @method public static
	 * @return f_indexedData
	 */
	FromComponent: function(component) {
		var engine=f_indexedDbEngine.Get();
		if (!engine) {
			return;
		}
		
		return engine.f_fromComponent(component);
	}
};

var __members = {
	
	/**
	 * @field private IDBFactory
	 */	
	_indexedDb: undefined,
	
	/**
	 * @field private IDBDatabase
	 */	
	_database: undefined,
	
	/**
	 * @field private Boolean
	 */
	_ready: undefined,
	
	/**
	 * @field private Array
	 */
	_funcsList: undefined,
	
	f_indexedDbEngine: function(indexedDb) {
		this.f_super(arguments);
		
		this._indexedDb=indexedDb;
		
		this._funcsList=new Array();
	
		var request=indexedDb.open(f_indexedDbEngine._DATABASE_NAME);
	
		var self=this;
		request.onerror=function(event) {
			try {
				return self.f_performRequestError(event);
				
			} catch (x) {
				f_core.Error(f_indexedDbEngine, "f_indexedDbEngine: Call f_performRequestError throws exception", x);
			}				
		};
		request.onsuccess=function(event) {
			try {
				return self.f_performRequestSuccess(event);
				
			} catch (x) {
				f_core.Error(f_indexedDbEngine, "f_indexedDbEngine: Call f_performRequestSuccess throws exception", x);
			}				
			
		};
	},
	f_performRequestError: function(event) {
		f_core.Error(f_indexedDbEngine, "f_performRequestError: Can not open database "+event);
		
		this._ready=false;
		
		this._processFuncs(null, event);
	},
	f_performRequestSuccess: function(event) {
		f_core.Info(f_indexedDbEngine, "f_performRequestError: Database opening successed ("+event+")");
		
		this._ready=true;
		
		this._database = event.target.result;
		
		this._processFuncs(this, event);
	},
	f_fromComponent: function(component) {
		if (this._ready===false) {
			return null;
		}
		
		var contentName=f_core.GetAttributeNS(component, "idbName");
		// On prend idbName pour economiser !
		if (!contentName) {
			return null;
		}
	
		var contentPK=f_core.GetAttributeNS(component, "idbPK");
		var contentKey=f_core.GetAttributeNS(component, "idbKey");
		var contentRowCount=f_core.GetNumberAttributeNS(component, "idbCount");
		var contentIndex=f_core.GetAttributeNS(component, "idbIndex");
			
		var ret = f_indexedData.f_newInstance(this, contentName, contentKey, contentRowCount, contentPK, contentIndex);
		
		return ret;
	},
	
	/**
	 * @method hidden
	 * @param func
	 * @return Boolean
	 */
	f_async: function(func) {
		if (this._ready===false) {
			func(null);
			return false;
		}
		if (this._ready) {
			func(this);
			return true;
		}
		
		this._funcsList.push(func);
		
		return undefined;
	},
	
	/**
	 * @method private 
	 * @param Object param
	 * @return void
	 */
	_processFuncs: function(param, event) {
		var funcsList=this._funcsList;
		
		for(;funcsList.length;) {
			var func=funcsList.shift();
			
			try {
				func(param, event);
				
			} catch (ex) {
				f_core.Error(f_indexedDbEngine, "_processFuncs: func throws exception (func="+func.toString+")", ex);
			}
		}
	},
	/**
	 * @method public
	 * @param Array names of ObjectStore (or a String for a single name)
	 * @param optional Boolean readWrite
	 * @return IDBTransaction Transaction object
	 */
	f_startTransaction: function(names, readWrite) {
		
		f_core.Debug(f_indexedDbEngine, "f_startTransaction: Starting transaction for '"+names+"' readWrite="+readWrite);

		if (!this._ready) {
			throw new Error("Database not ready !");
		}
		
		if (typeof(names)=="string") {
			names=[names];
		}
		
		// @TODO Il faut passer en version "readonly" ou "readwrite"
		var transaction=this._database.transaction(names, (readWrite)?"readwrite":"readonly"); //(readOnly)?0:1); //, f_indexedDbEngine._TRANSACTION_TIMEOUT_MS);
		
		f_core.Debug(f_indexedDbEngine, "f_startTransaction: Transaction started for '"+name+"'. (transaction="+transaction+")");
		
		return transaction;
	},
	
	/**
	 * @metod public
	 * @param String contentName 
	 * @param String contentKey
	 * @param Number contentRowCount
	 * @param Function func
	 * @return void
	 */
	f_asyncGetObjectStore: function(contentName, contentKey, contentRowCount, contentIndex, contentPrimaryKey, func) {
	
		var self=this;

		var objectStoreNames=this._database.objectStoreNames;
		if (!objectStoreNames.contains(f_indexedDbEngine._RCFACES_OBJECTSTORE)) {
			this._changeObjectStore(function(state, event) {
				if (!state) {
					func(null, event);
					return;
				}
								
				self._database.createObjectStore(f_indexedDbEngine._RCFACES_OBJECTSTORE, null);
			
				self.f_asyncGetObjectStore(contentName, contentKey, contentRowCount, contentIndex, contentPrimaryKey, func);
			});
			
			return;
		}
		
		var store=undefined;
		try {
			var transaction=this.f_startTransaction(f_indexedDbEngine._RCFACES_OBJECTSTORE, true);
		
			store=transaction.objectStore(f_indexedDbEngine._RCFACES_OBJECTSTORE);
		
		} catch (x) {
			f_core.Debug(f_indexedDbEngine, "f_asyncGetObjectStore: get objectStore '"+f_indexedDbEngine._RCFACES_OBJECTSTORE+"' throws exception", x);
		}
		
		if (!store) {
			func(null);
			return;
		}
		
		var request=store.get(contentName);
		request.onerror=function(event) {
			f_core.Error(f_indexedDbEngine, "f_asyncGetObjectStore: get '"+contentName+"' from objectStore '"+store+"' throws exception "+event);
			try {
				func(null, event);
				
			} catch (x) {
				f_core.Error(f_indexedDbEngine, "f_asyncGetObjectStore: Call func throws exception", x);
			}				
		};
		request.onsuccess=function(event) {
			f_core.Debug(f_indexedDbEngine, "f_asyncGetObjectStore: get '"+contentName+"' from objectStore '"+store+"' returns "+event);
			var result=event.target.result;
			
			try {			
				self._syncContent(store, result, contentName, contentKey, contentRowCount, contentIndex, contentPrimaryKey, event, function(state, event) {
					if (!state) {
						func(null, event);
						return;
					}
					
					func(self, event);
				});
				
			} catch (x) {
				f_core.Error(f_indexedDbEngine, "f_asyncGetObjectStore: _syncContent function throws exception", x);
			}				
		};
		
	},

	_syncContent: function(store, result, contentName, contentKey, contentRowCount, contentIndex, contentPrimaryKey, event, func) {
		var self=this;

		f_core.Debug(f_indexedDbEngine, "_syncContent: Synchronize content '"+contentName+"' contentKey='"+contentKey+"' contentRowCount='"+contentRowCount+"' contentIndex='"+contentIndex+"' contentPrimaryKey='"+ contentPrimaryKey+"'.");

		var clearAllData=false;
		if (result) {
			if (result.contentIndex!=contentIndex || result.contentKey!=contentKey || result.contentRowCount!=contentRowCount || result.contentPrimaryKey!=contentPrimaryKey) {
				clearAllData=true;
				result=null;
				
				f_core.Debug(f_indexedDbEngine, "_syncContent: Not same data, reset content !");
			}
		}

		if (!result) {
			result={
				contentKey: contentKey,
				contentRowCount: contentRowCount,
				contentName: contentName,
				contentIndex: contentIndex,
				contentPrimaryKey: contentPrimaryKey
			};

			f_core.Debug(f_indexedDbEngine, "_syncContent: Store new content '"+result+"' !");

			store.put(result, contentName);
		}
		
		var objectStoreNames=this._database.objectStoreNames;
		if (!objectStoreNames.contains(contentName)) {

			f_core.Debug(f_indexedDbEngine, "_syncContent: ObjectStore '"+contentName+"' not found.");
			
			this._changeObjectStore(function(state, event) {

				f_core.Debug(f_indexedDbEngine, "_syncContent: Change object store '"+state+"'.");

				if (!state) {
					func(null, event);
					return;
				}
				
				var newStore=self._database.createObjectStore(contentName, null);

				f_core.Debug(f_indexedDbEngine, "_syncContent: Object store '"+newStore+"' created.");
		
				if (contentIndex) {
					newStore.createIndex(f_indexedDbEngine._RCFACES_INDEX_NAME, f_indexedDbEngine.INDEX_FIELD, {
						multiEntry: true
					});
				}
				
				func(self, event);
			});
			
			return;
		}
		
		if (clearAllData) {

			f_core.Debug(f_indexedDbEngine, "_syncContent: Clearing all data");

			var newTransaction=this.f_startTransaction(contentName, true);
			
			var contentStore=newTransaction.objectStore(contentName);
			
			var request=contentStore.clear();

			request.onerror=function(event) {

				f_core.Error(f_indexedDbEngine, "_syncContent: clear all content off '"+contentName+"' from objectStore '"+store+"' throws error "+event);

				try {
					func(null, event);
				
				} catch (x) {
					f_core.Error(f_indexedDbEngine, "_syncContent: Call func throws exception", x);
				}				
			};
			request.onsuccess=function(event) {

				f_core.Debug(f_indexedDbEngine, "_syncContent: clear all content off '"+contentName+"' from objectStore '"+store+"' SUCCESS event="+event);

				try {
					func(self, event);
					
				} catch (x) {
					f_core.Error(f_indexedDbEngine, "_syncContent: Call func throws exception", x);
				}				
			};
			
			func(this, event);
			return;
		}

		f_core.Debug(f_indexedDbEngine, "_syncContent: Content '"+contentName+"' synchronized.");

		func(this, event);
	},
	
	_changeObjectStore: function(func) {
		var database=this._database;
		var newVersion=database.version+1;
		
		database.close();
		this._database=undefined;

		var request=this._indexedDb.open(f_indexedDbEngine._DATABASE_NAME, newVersion);
		
		var self=this;
		request.onerror=function(event) {
			f_core.Error(f_indexedDbEngine, "_changeObjectStore: Can not open database "+event);
		
			try {
				func(null, event);
			
			} catch (x) {
				f_core.Error(f_indexedDbEngine, "_changeObjectStore: Call func throws exception", x);
			}
		};
		request.onblocked=function(event) {
			f_core.Error(f_indexedDbEngine, "_changeObjectStore: Can not open database "+event);
			
			try {
				func(null, event);
			
			} catch (x) {
				f_core.Error(f_indexedDbEngine, "_changeObjectStore: Call func throws exception", x);
			}
		};
		request.onsuccess=function(event) {
			var newDatabase = event.target.result;
			self._database=newDatabase;
			
			f_core.Debug(f_indexedDbEngine, "_changeObjectStore: Database opened "+newDatabase);
		};
		request.onupgradeneeded=function(event) {
			var newDatabase = event.target.result;
			self._database=newDatabase;

			f_core.Debug(f_indexedDbEngine, "_changeObjectStore: Database opened "+newDatabase+" version="+newDatabase.version);

			try {
				func(self, event);
				
			} catch (x) {
				f_core.Error(f_indexedDbEngine, "_changeObjectStore: Call func throws exception", x);
			}
		};
		
	}
	
};

new f_class("f_indexedDbEngine", {
	extend: f_object,
	members: __members,
	statics: __statics
});
