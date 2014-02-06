
/**
 * f_indexedDatabases class
 *
 * @class public f_indexedData extends f_object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:32 $
 */

var __statics = {
	/**
	 * @method hidden static
	 * @param String contentIndex
	 * @return Object
	 */
	ParseIndexesDescription: function(contentIndex) {
		if (!contentIndex) {
			return null;
		}
		
		var obj={};
		
		var ss=contentIndex.split(",");
		for(var i=0;i<ss.length;i++) {
			var s=ss[i];
			
			var ts=s.split(':');
			var pm= {};
			var fieldName=ts[0];
			obj[fieldName]=pm;
			
			for(var j=1;j<ts.length;j++) {
				var t=ts[j];
				switch(t) {
				case "ic":
					pm._ignoreCase=true;
					obj._ignoreCase=true;					
					break;
				case "ia":
					pm._ignoreAccent=true;
					obj._ignoreAccent=true;					
					break;
				case "sw":
					pm._startsWith=true;
					obj._startsWith=true;					
					break;
				case "ft":
					pm._fullText=true;
					obj._fullText=true;					
					break;
				case "ew":
					pm._eachWord=true;
					obj._eachWord=true;					
					break;
				}
			}
		}
		
		return obj;
	}
};

var __members = {
		
	/**
	 * @field private f_indexedDbEngine
	 */
	_indexedDbEngine: undefined,
		
	/**
	 * @field private String
	 */
	_contentName: undefined,
	
	/**
	 * @field private String
	 */
	_contentKey: undefined,
	
	/**
	 * @field private Number
	 */
	_contentRowCount: undefined,
	
	/**
	 * @field private String
	 */
	_contentPrimaryKey: undefined,
	
	/**
	 * @field private String
	 */
	_contentIndex: undefined,
	
	/**
	 * @field private Array
	 */
	_funcsList: undefined,
	
	/**
	 * @field private Boolean
	 */
	_ready: undefined,
	
	/**
	 * @field private Array
	 */
	_pendingRequests: undefined,
	
	/**
	 * @field private Boolean
	 */
	_completed: undefined,
	
	
	/**
	 * @field private Object
	 */
	_indexesDesc: undefined,
		
	f_indexedData: function(indexedDbEngine, contentName, contentKey, contentRowCount, contentPrimaryKey, contentIndex) {
		this.f_super(arguments);
		
		f_core.Assert(typeof(indexedDbEngine)=="object" && indexedDbEngine, "Invalid indexedDbEngine parameter '"+indexedDbEngine+"'");
		f_core.Assert(typeof(contentName)=="string" && contentName.length, "Invalid contentName parameter '"+contentName+"'");
		f_core.Assert(typeof(contentKey)=="string" && contentKey.length, "Invalid contentKey parameter '"+contentKey+"'");
		f_core.Assert(typeof(contentRowCount)=="number", "Invalid contentRowCount parameter '"+contentRowCount+"'");
		f_core.Assert(typeof(contentPrimaryKey)=="string" && contentPrimaryKey.length, "Invalid contentPrimaryKey parameter '"+contentPrimaryKey+"'");
		
		this._indexedDbEngine=indexedDbEngine;
				
		this._contentName=contentName;
		this._contentKey=contentKey;
		this._contentRowCount=contentRowCount;
		this._contentPrimaryKey=contentPrimaryKey;
		
		this._contentIndex=contentIndex;
		if (contentIndex) {
			this._indexesDesc=f_indexedData.ParseIndexesDescription(contentIndex);
		}

		var self=this;
		window.setTimeout(function() {
			if (window._rcfacesExiting) {
 				return false;
 			}

			self._verifyCompleted();
		}, 100);
	},
	
	f_finally: function() {
		// this._contentName=undefined; // String
		// this._contentKey=undefined; // String
		// this._contentRowCount=undefined; // Number
		// this._contentIndex=undefined; // String
		// this._contentPrimaryKey=undefined; // String
		// this._indexesDesc=undefined; // Object
		this._indexedDbEngine=undefined; // Object
		this._ready=false; // Boolean
		this._pendingRequests= undefined;

		this._funcsList = undefined; // Array<Function>
	
		this.f_super(arguments);
	},
	/**
	 * @method public
	 * @param Array rows
	 * @param Function datasFunction
	 * @return void
	 */
	f_asyncFillRows: function(rows, datasFunction) {	
		if (this._completed) {
			return;
		}
		
		var self=this;
		this.f_async(function(state) {
			if (!state) {
				return;
			}
			self._asyncFillRows(rows, datasFunction);
		});
	},
	
	/**
	 * @method private
	 * @param Array rows
	 * @param Function datasFunction
	 * @return void
	 */
	_asyncFillRows: function(rows, datasFunction) {
		if (this._completed) {
			return;
		}

		var indexedDbEngine = this._indexedDbEngine;
		
		var pendingRequests=this._pendingRequests;
		if (!pendingRequests) {
			pendingRequests=new Array;
			this._pendingRequests=pendingRequests;
		}
		
		var self=this;
		var onerror=function(event) {
			self._onRequestError(event, this);
		};
		var onsuccess=function(event) {
			self._onRequestSuccess(event, this);
		};
		
		var contentPrimaryKey=this._contentPrimaryKey;
		var keyFunction=this._keyAccessor(contentPrimaryKey);
		
		this._completed=undefined;
		
		var indexesDesc=this._indexesDesc;
		try {
			var transaction = indexedDbEngine.f_startTransaction(this._contentName, true);
		
			var objectStore = transaction.objectStore(this._contentName);
			
			f_core.Debug(f_indexedData, "_asyncFillRows: objectStore="+objectStore);
			
			for(var i=0;i<rows.length;i++) {
				var row=rows[i];
				
				var datas=(datasFunction)?datasFunction(row):row;
				var key=keyFunction(datas);
				
				if (indexesDesc) {
					this._fillIndexes(datas, indexesDesc);
				}

				f_core.Debug(f_indexedData, "_asyncFillRows:   add '"+key+"' datas='"+datas+"'");

				var request=objectStore.put(datas, key);
				request.onerror=onerror;
				request.onsuccess=onsuccess;
				pendingRequests.push(request);
			}
		
		} catch (x) {
			f_core.Error(f_indexedData, "_asyncFillRows: fill rows send exception ",x);
		}
	},
	_keyAccessor: function(key) {
		if (key.indexOf('+')>0) {
			var keys=key.split('+');
			var fs=new Array;
			for(var i=0;i<keys.length;i++) {
				fs.push(this._keyAccessor(keys[i]));
			}
			
			return function(row) {
				var k="";
				for(var i=0;i<fs.length;i++) {
					if (k) {
						k+="$$";
					}
					k+=fs[i](row);
				}
				
				return k;
			};
		}
		
		var idx=key.indexOf('.');
		if (idx>0) {
			var fs=key.split(".");
			
			return function(row) {
				for(var i=0;i<fs.length;i++) {
					var key=fs[i];
					row=row[key];
					
					if (row===undefined) {
						break;
					}
				}
				
				return row;
			};
		}
		
		return function(row) {
			return row[key];
		};
	},
	/**
	 * @method private
	 * @param Object datas
	 * @param String 
	 */
	_fillIndexes: function(datas, indexesDesc) {
		var indexes=[];
		datas[f_indexedDbEngine.INDEX_FIELD] = indexes;
		
		for(var fieldName in indexesDesc) {
			var desc=indexesDesc[fieldName];
			
			var f=this._keyAccessor(fieldName);
			
			var value=f(datas);
			if (value===null || value===undefined) {
				continue;
			}
			
			if (typeof(value)!="string") {
				value=String(value);
				// On ne traite que des chaines !
			}
			
			if (!value) {
				// Chaine vide !
				continue;
			}
		
			if (desc._ignoreCase) {
				value=value.toLowerCase();
			}

			if (desc._ignoreAccent) {
				value=this._removeAccent(value);
			}
			
			if (desc._eachWord) {
				var words = value.split(/\W+/);
				
				for(var i=0;i<words.length;i++) {
					var word=words[i];
					
					if (!word) {
						continue;
					}
					
					indexes.push(word);
				}
				
				continue;
			}
			
			indexes.push(value);
		}
	},
	_removeAccent: function(text) {
		if (window.f_vb) {
			var ret = f_vb.RemoveAccents(text);
			
			return ret;
		}
		
		f_core.Assert(true, "f_indexedData._removeAccent: Not implemented include f_vb class !");
		
		return text;
	},
	/**
	 * @method private
	 * @param DOMEvent event
	 * @param IDBRequest request
	 * @return void
	 */
	_onRequestError: function(event, request) {
		if (window._rcfacesExiting) {
			return false;
		}

		f_core.Debug(f_indexedData, "_onRequestError: Request errored '"+request+"' event="+event);
		
		var pendingRequests=this._pendingRequests;
		if (!pendingRequests) {
			return;
		}
		pendingRequests.f_removeElement(request);
	},
	/**
	 * @method private
	 * @param DOMEvent event
	 * @param IDBRequest request
	 * @return void
	 */
	_onRequestSuccess: function(event, request) {
		if (window._rcfacesExiting) {
			return false;
		}

		f_core.Debug(f_indexedData, "_onRequestSuccess: Request successed '"+request+"' event="+event);
		
		var pendingRequests=this._pendingRequests;
		if (!pendingRequests) {
			return;
		}
		pendingRequests.f_removeElement(request);
	},
	/**
	 * @method private
	 * @param Function func
	 * @return Boolean
	 */
	f_async: function(func) {
		if (this._ready===false) {
			this._callFunc(func, null);
			return false;
		}
		if (this._ready) {
			this._callFunc(func, this);
			return true;
		}
		
		var funcsList=this._funcsList;
		
		if (!funcsList) {
			
			funcsList=new Array();
			this._funcsList=funcsList;

			var self=this;
			if (this._indexedDbEngine.f_async(function(state, event) {
					if (!state) {
						self._ready=false;
						self._processFuncs(null);
						return;
					}
					
					self._verifyDatabase(event);
				})===false) {
				
				this._ready=false;
				this._callFunc(func, null);
				return false;
			}
		}
		
		funcsList.push(func);
		
		return undefined;
	},
	
	/**
	 * @method private
	 * @return void
	 */
	_verifyDatabase: function(event) {
		var indexedDbEngine = this._indexedDbEngine;

		var self=this;
		indexedDbEngine.f_asyncGetObjectStore(this._contentName, this._contentKey, this._contentRowCount, this._contentIndex, this._contentPrimaryKey, function(state, event) {
			if (!state) {

				f_core.Debug(f_indexedData, "_verifyDatabase: Verification FAILED");

				self._ready=false;
				window.setTimeout(function() {
					if (window._rcfacesExiting) {
		 				return false;
		 			}

					try {
						self._processFuncs(null, event);
					} catch (x) {
						f_core.Error(f_indexedData, "_verifyDatabase: _processFuncs function (FAILED) throws exception", x);
					}
					
				}, 10);
				return;
			}
			
			self._ready=true;

			f_core.Debug(f_indexedData, "_verifyDatabase: Verification successed (state="+state+")");

			window.setTimeout(function() {
				if (window._rcfacesExiting) {
	 				return false;
	 			}

				try {
					self._processFuncs(self, event);
					
				} catch (x) {
					f_core.Error(f_indexedData, "_verifyDatabase: _processFuncs (SUCCESS) throws exception", x);
				}
			}, 10);
		});
	},
	
	/**
	 * @method private 
	 * @param Object param
	 * @return void
	 */
	_processFuncs: function(param, event) {
		var funcsList=this._funcsList;
		
		f_core.Assert(funcsList instanceof Array, "_processFuncs: Invalid funcsList ("+funcsList+")");

		f_core.Debug(f_indexedData, "_processFuncs: process "+funcsList.length+" function(s).");

		for(;funcsList.length;) {
			var func=funcsList.shift();

			f_core.Assert(func instanceof Function, "_processFuncs: Invalid func object ("+func+")");

			f_core.Debug(f_indexedData, "_processFuncs: call function '"+func+"'.");
			
			this._callFunc(func, param, event);
		}
	},
	/**
	 * @method private
	 * @param Function func
	 * @param Object param
	 * @param Event event
	 * @return void
	 */
	_callFunc: function(func, param, event) {
		try {
			func.call(this, param, event);
			
		} catch (ex) {
			f_core.Error(f_indexedData, "_callFunc: call of function throws exception (func="+func.toString+")", ex);
		}
		
	},
	/**
	 * @method public
	 * @param String text
	 * @param Number first
	 * @param Number maxNumber
	 * @param Function func
	 */
	f_asyncSearch: function(text, first, maxNumber, func) {
		var self=this;
		this.f_async(function(state, event) {
			if (!state) {
				self._callFunc(func, null, event);
				return;
			}
			self._asyncSearch(text, first, maxNumber, func);
		});
	},
	/**
	 * @method private
	 * @param String text
	 * @param Number first
	 * @param Number maxNumber
	 * @param Function func
	 */
	_asyncSearch: function(text, first, maxNumber, func) {
		f_core.Debug(f_indexedData, "_asyncSearch: text='"+text+"' first='"+first+"' maxNumber="+maxNumber+"  databaseCompleted="+this._completed);

		if (this._completed===false) {
			this._callFunc(func, null);
			return;
		}

		var indexedDbEngine = this._indexedDbEngine;

		var transaction = indexedDbEngine.f_startTransaction(this._contentName, false);
		
		var objectStore = transaction.objectStore(this._contentName);

		var self=this;
		if (!this._completed) {
			var request=objectStore.count();
			request.onsuccess=function(event) {
				if (window._rcfacesExiting) {
	 				return false;
	 			}

				var c=event.target.result;

				f_core.Debug(f_indexedData, "_asyncSearch: count='"+c+"' contentRowCount="+self._contentRowCount);

				if (c!=self._contentRowCount) {
					self._completed=false;
					self._callFunc(func, null, event);
					return;
				}
				self._completed=true;
				
				self._asyncSearchCursor(text, first, maxNumber, func, objectStore);
			};

			request.onerror=function(event) {
				f_core.Error(f_indexedData, "_asyncSearch: count error '"+event+"'");
				
				self._callFunc(func, null, event);
			};

			return;
		}
		
		if (text===null) {
			this._asyncListCursor(text, first, maxNumber, func, objectStore);
			return;
		}
		
		this._asyncSearchCursor(text, first, maxNumber, func, objectStore);
	},
	/**
	 * @method private
	 * @param String text
	 * @param Number first
	 * @param Number maxNumber
	 * @param Function func
	 * @param IDBObjectStore
	 * @return void
	 */
	_asyncListCursor: function(text, first, maxNumber, func, objectStore) {

		f_core.Debug(f_indexedData, "_asyncListCursor: text='"+text+"' first='"+first+"' maxNumber='"+maxNumber+"'.");

		var cursorRequest=objectStore.openCursor(keyRange, IDBCursor.next);
		cursorRequest.onsuccess = function(event) {
			if (window._rcfacesExiting) {
 				return false;
 			}

			var result = event.target.result;

			f_core.Debug(f_indexedData, "_asyncListCursor: cursor success result='"+result+"'");

		    if (!result) {
		    	ret._resultNumber=ret.length;
				self._callFunc(func, ret, event);
		    	return;
		    }
	
		    ret.push(result.value);
		    
		    if (ret.length==maxNumber) {
		    	ret._resultNumber=self._contentRowCount;
		    	self._callFunc(func, ret, event);
		    	return;
		    }
		    
		    result['continue']();
		};
		
		cursorRequest.onerror = function(event) {
			f_core.Debug(f_indexedData, "_asyncListCursor: cursor error event='"+event+"'");

			self._callFunc(func, null, event);
		};

	},
	/**
	 * @method private
	 * @param String text
	 * @param Number first
	 * @param Number maxNumber
	 * @param Function func
	 * @param IDBObjectStore
	 * @return void
	 */
	_asyncSearchCursor: function(text, first, maxNumber, func, objectStore) {

		f_core.Debug(f_indexedData, "_asyncSearchCursor: text='"+text+"' first='"+first+"' maxNumber='"+maxNumber+"'.");
		
		var search=[];
		var desc=this._indexesDesc;
		if (desc) {
			// recherche par index
			
			// format  keyPath:function  (function= '' ':ignorecase' )
			
			if (desc._ignoreCase) {
				text=text.toLowerCase();
			}

			if (desc._ignoreAccent) {
				text=this._removeAccent(text);
			}
			
			if (desc._eachWord) {
				var words = text.split(/\W+/);
				
				for(var i=0;i<words.length;i++) {
					var word=words[i];
					
					if (!word) {
						continue;
					}
					
					search.push(word);
				}
				
			} else {
				search.push(text);
			}
		} else {
			search.push(text);
		}
		
		var index=objectStore.index(f_indexedDbEngine._RCFACES_INDEX_NAME);
		if (!index) {
			f_core.Error(f_indexedData, "_asyncSearchCursor: No index '"+f_indexedDbEngine._RCFACES_INDEX_NAME+"'");

			this._callFunc(func, null);
			return;
		}
		
		var ret=[];
	
		f_core.Debug(f_indexedData, "_asyncSearchCursor: search='"+search+"'");

		var self=this;
//		for(var i=0;i<search.length;i++) {
		// La multi recherche pose des problemes pour la fusion des resultats et la recherche de doublons
			var s=search[0];
			
			var keyRange;
			if (desc && desc._startsWith) {
				var s2=s.substring(0, s.length-1)+String.fromCharCode(s.charCodeAt(s.length-1)+1);
				
				keyRange=IDBKeyRange.bound(s, s2, false, true);
			} else {
				keyRange=IDBKeyRange.only(s);
			}
			
			var cursorRequest=index.openCursor(keyRange, IDBCursor.next);
			cursorRequest.onsuccess = function(event) {
				if (window._rcfacesExiting) {
	 				return false;
	 			}

				var result = event.target.result;

				f_core.Debug(f_indexedData, "_asyncSearchCursor: cursor success result='"+result+"'");

			    if (!result) {
			    	ret._resultNumber=ret.length;
					self._callFunc(func, ret, event);
			    	return;
			    }
		
			    ret.push(result.value);
			    
			    if (ret.length==maxNumber) {
			    	self._countIndexResult(ret, index, keyRange, func);
			    	return;
			    }
			    
			    result['continue']();
			};
			
			cursorRequest.onerror = function(event) {
				f_core.Debug(f_indexedData, "_asyncSearchCursor: cursor error event='"+event+"'");

				self._callFunc(func, null, event);
			};
//		}
	},
	_countIndexResult: function(ret, index, keyRange, func) {
		var self=this;
		
		var countRequest = index.count(keyRange);
		countRequest.onsuccess = function(event) {
			if (window._rcfacesExiting) {
 				return false;
 			}

			var result = event.target.result;

			f_core.Debug(f_indexedData, "_countIndexResult: index count result='"+result+"'");
			
			ret._resultNumber=result;
			
			self._callFunc(func, ret, event);
		};
		
		countRequest.onerror = function(event) {
			f_core.Error(f_indexedData, "_countIndexResult: index count failed event='"+event+"'");

			ret._resultNumber=undefined;
			
			self._callFunc(func, ret, event);
		};
		
	},
	/**
	 * @method public
	 * @param String key
	 * @param Function func
	 * @return void
	 */
	f_asyncSearchKey: function(key, func) {
		var self=this;
		this.f_async(function(state, event) {
			if (!state) {
				self._callFunc(func, null, event);
				return;
			}
			self._asyncSearchKey(key, func);
		});
	},
	/**
	 * @method private
	 * @param String key
	 * @param Function func
	 * @return void
	 */
	_asyncSearchKey: function(key, func) {
		f_core.Debug(f_indexedData, "_asyncSearchKey: key='"+key+"' databaseCompleted="+this._completed);

		if (this._completed===false) {
			this._callFunc(func, null);
			return;
		}

		var indexedDbEngine = this._indexedDbEngine;

		var transaction = indexedDbEngine.f_startTransaction(this._contentName, false);
		
		var objectStore = transaction.objectStore(this._contentName);
		
		var self=this;
		var keyRequest = objectStore.get(key);
		keyRequest.onsuccess = function(event) {
			if (window._rcfacesExiting) {
 				return false;
 			}

			var result = event.target.result;

			f_core.Debug(f_indexedData, "_asyncSearchKey: key search result='"+result+"'");
						
			self._callFunc(func, result, event);
		};
		
		keyRequest.onerror = function(event) {
			f_core.Error(f_indexedData, "_asyncSearchKey: key search failed event='"+event+"'");
	
			self._callFunc(func, null, event);
		};
	},
	
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isCompleted: function() {
		return this._completed;
	},
	/**
	 * @method private
	 * @return void
	 */
	_verifyCompleted: function() {
		if (this._completed) {
			return;
		}

		var indexedDbEngine = this._indexedDbEngine;

		var transaction = indexedDbEngine.f_startTransaction(this._contentName, false);
		
		var objectStore = transaction.objectStore(this._contentName);

		var self=this;
		var request=objectStore.count();
		request.onsuccess=function(event) {
			if (window._rcfacesExiting) {
 				return false;
 			}

			var c=event.target.result;

			f_core.Debug(f_indexedData, "_verifyCompleted: count='"+c+"' contentRowCount="+self._contentRowCount);

			if (c!=self._contentRowCount) {
				self._completed=false;
				return;
			}
			self._completed=true;
		};	

		request.onerror=function(event) {
			f_core.Error(f_indexedData, "_verifyCompleted: count error '"+event+"'");
		};
	}
};

new f_class("f_indexedData", {
	extend: f_object,
	members: __members,
	statics: __statics
});
