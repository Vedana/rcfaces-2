/*
 * $Id: f_actionList.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */

/**
 *
 * @class hidden f_actionList extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @author Joel Merlin
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */

var __statics= {
	/**
	 * @method private static 
	 * @return void
	 */
	_ShowEventException: function(index, type, link, evt, fct, ex) {
		var s="*** Action Error: type="+type+" (action #"+index+")\n";
	
		s+="-- Target Object --------------------------\n";
		
		if (!link) {
			s+="link=NULL\n";
			
		} else {
			if (link.tagName) {
				s+="id="+link.id+" tagName="+link.tagName+" cssClass="+link.className+"\n";
			}
			if (link._kclass) {
				s+="f_class="+link._kclass._name+"\n";
			}
	
			if (link.toString) {
				s+=link.toString();
				
			} else {
				s+=link;
			}
		}
		s+="\n-- Event Object --------------------------\n";
		
		var cmp=evt.f_getComponent();
		if (cmp) {
			if (cmp==link) {
				s+="evt.component = *** target ***\n";
			} else {
				if (cmp.tagName) {
					s+="evt.component: id="+cmp.id+" tagName="+cmp.tagName+" cssClass="+cmp.className+"\n";
				}
				if (cmp._kclass) {
					s+="evt.component: f_class="+cmp._kclass._name+"\n";
				}
						
				if (cmp.toString) {
					s+="evt.component="+cmp.toString()+"\n";
				} else {
					s+="evt.component="+cmp+"\n";
				}
			}
		}
		if (evt.f_getItem()) {
			s+="evt.component="+evt.f_getItem()+"\n";
		}
		if (evt.f_getValue()) {
			s+="evt.component="+evt.f_getValue()+"\n";
		}
		s+="-- Exception Object -----------------------\n";
		s+=ex;
		
		var code=fct.toString().split('\n');
		if (code.length>15) {
			s+="\n-- Function source - (first 15 lines) ---\n";
	
		} else {
			s+="\n-- Function source ----------------------\n";
		}		
		
		for(var i=0;i<code.length && i<15;i++) {
			s+=code[i]+"\n";
		}
		
		//alert(s);
		f_core.Error(f_actionList, s, ex);
	}
};

var __members= {
	/**
	 * @method hidden
	 * @param HTMLElement component
	 * @param String type
	 */
	f_actionList: function(component,type) {
		f_core.Assert(typeof(type)=="string", "f_actionList.f_actionList: Type of actionList is invalid '"+type+"'.");
		
		this._link = component;
		this._type = type;
	},

	f_finalize: function() {
		this._link=undefined;  // object
	//	this._type=undefined; // string
		this._actions=undefined;  // function[]
		this._firsts=undefined;  // function[]
	},
	
	/**
	 * 
	 * @method public
	 * @return f_component 
	 */
	f_getElement: function() {
		return this._link;
	},
	
	/**
	 * 
	 * @method public
	 * @return String 
	 */
	f_getType: function() {
		return this._type;
	},
	
	/**
	 * 
	 * @method hidden
	 */
	f_addAction: function(action) {
		var as=this._actions;
		if (!as) {
			as = new Array;
			this._actions = as;
		}
		
		f_core.PushArguments(as, arguments);
		
		if (f_core.IsDebugEnabled(f_actionList)) {
			for(var i=0;i<as.length;i++) {
				var a=as[i];
				
				f_core.Assert(typeof(a)=="string" || typeof(a)=="function", "f_actionList.f_addAction: Bad listener for action type '"+this._type+"'. (listener="+a+").");
			}
		}
	},
	
	/**
	 * 
	 * @method hidden
	 */
	f_addActions: function(actions) {	
		this.f_addAction.apply(this, actions);
	},
	
	/**
	 * 
	 * @method hidden
	 */
	f_removeAction: function(listeners) {
		var actions=this._actions;
		var firsts=this._firsts;
		if (!actions && !firsts) {
			return;
		}
		
		for(var i=0;i<arguments.length;i++) {
			var action=arguments[i];
			
			f_core.Assert(typeof(action)=="string" || typeof(action)=="function", "f_actionList.f_removeAction: Bad listener for action type '"+this._type+"'. (listener="+action+").");
			
			if (actions && actions.f_removeElement(action)) {
				continue;
			}
			if (firsts) {
				firsts.f_removeElement(action);
			}
		}
	},
	
	/**
	 * 
	 * @method hidden
	 */
	f_removeActions: function(actions) {
		this.f_removeAction.apply(this, actions);
	},
	
	/**
	 * 
	 * @method hidden
	 */
	f_clearActions: function() {
		this._actions=undefined;
	},
	
	/**
	 * 
	 * @method hidden
	 */
	f_clearActionFirsts: function() {
		this._firsts=undefined;
	},
	
	/**
	 * 
	 * @method hidden
	 */
	f_isEmpty: function() {
		var actions=this._actions;
		if (actions && actions.length) {
			return false;
		}
		
		var firsts=this._firsts;
		if (firsts && firsts.length) {
			return false;
		}
		
		return true;
	},
	
	/**
	 * 
	 * @method hidden
	 */
	f_addActionFirst: function(actions) {
		var as=this._firsts;
		if (!as) {
			as = new Array;
			this._firsts = as;
		}
		
		if (f_core.IsDebugEnabled(f_actionList)) {
			for(var i=0;i<arguments.length;i++) {
				var a=arguments[i];
				
				f_core.Assert(typeof(a)=="string" || typeof(a)=="function", "f_actionList.f_addActionFirst: Bad listener for action type '"+this._type+"'. (listener="+a+").");
				
			//	f_core.Debug(f_actionList, "f_actionList.f_addActionFirst: type="+this._type+" "+a);
			}
		}
		
		as.unshift.apply(as, arguments);
	},
	
	/**
	 * 
	 * @method hidden
	 */
	f_callActions: function(evt) {
		f_core.Assert(this._link, "f_callActions: No linked component for this actionList !");
		var win=window; // Il faut garder une référence ...
		
		var ret = true;

		var firsts = this._firsts;
		var actions = this._actions;
		if (!actions || !actions.length) {
			if (!firsts || !firsts.length) {
				f_core.Debug(f_actionList, "f_callActions: No actions '"+this._type+"' returns true.");
				return ret;
			}
			actions=firsts;
			
		} else if (firsts && firsts.length) {
			actions=firsts.concat(actions);
		}
		
	//	f_core.Debug(f_actionList, "f_callActions: call "+actions.length+" listeners: "+actions);
	
		var link=this._link;
		
		for(var i=0;i<actions.length;i++) {		
			var fct = actions[i];
			if (!fct) {
				continue;
			}
	
			var oldEvent=undefined;
			try {
				oldEvent=f_event.SetEvent(evt);
				
				if (typeof(fct) == "string") {
					// Le commande est transformée en fonction !
					fct=new win.Function("event", fct);
					actions[i]=fct;
				}
	
				ret = fct.call(link, evt);
				
			} catch (ex) {
				if (win._rcfacesExiting) {
					return false;
				}

				f_actionList._ShowEventException(i, this._type, link, evt, fct, ex);
				ret=false;
				
			} finally {
				if (!win._rcfacesExiting && win.f_event && f_event.SetEvent) {
					f_event.SetEvent(oldEvent);
				}
			}
			
			if (win._rcfacesExiting) {
				return false;
			}
	
			if (f_core.IsDebugEnabled(f_actionList)) {
				var fn=String(fct._kclass?(fct._kclass._name+"."+fct._kname):(fct.name?fct.name:fct));
				if (fn.length>64) {
					var idx=fn.indexOf('\n', 64);
					if (idx>0) {
						fn=fn.substring(0, idx)+"   ...";
					}
				}
				
				f_core.Debug(f_actionList, "f_callActions: ("+this._type+":"+link.id+") "+fn+"\n=> returns "+ret);
			}
			
			if (ret===false) {
				break;
			}
		}
		
		// Attention un submit a pu survenir, et nous sommes plus dans un contexte camelia sain !
		// => Plus d'appels aux méthodes !
		
		f_core.Debug(f_actionList, "f_callActions: returns "+ret);
		return ret;
	},
	/**
	 * @method public
	 * @return String
	 */
	toString: function() {
		return "[f_actionList type="+this._type+"]";
	}
};

new f_class("f_actionList", {
	statics: __statics, 
	members: __members
});
