/*
 * $Id: f_dragAndDropEngine.js,v 1.4 2013/12/16 13:07:19 jbmeslin Exp $
 */

/**
 * f_dragAndDropEngine class
 *
 * @class public f_dragAndDropEngine extends f_object, fa_screenAutoScroll
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/12/16 13:07:19 $
 */
 
var __statics = {
		
	/**
	 * @field private static final f_dragAndDropEngine
	 */	
	_Current: undefined,
	
	
	/**
	 * @field private static final String[]
	 */	
	_DefaultDragAndDropInfos: ["popup" ],
	
	/**
	 * 
	 * @method hidden static
	 * @param f_component component 
	 * @return f_dragAndDropEngine
	 */
	 Create: function(component) {
	
		var engine = f_dragAndDropEngine.f_newInstance(component);
	
		return engine;
	 },
	 
	/**
	 * 
	 * @method public static
	 * @return boolean
	 */
	 IsDragInProgress: function() {
		 return !!f_dragAndDropEngine._Current;
	 },
	 
	 /**
	 * 
	 * @method public static
	 * @param  Array sourceTypes
	 * @param  Array targetTypes
	 * @return Array selectedTypes
	 */
	 ComputeTypes: function(sourceTypes , targetTypes) {
		 var selectedTypes=new Array();		
	
		if (targetTypes && targetTypes.length && sourceTypes && sourceTypes.length) {
			var ts=new Array();
			for (var j=0;j<targetTypes.length;j++) {
				var tt=targetTypes[j];
				var rt=f_dragAndDropEngine._SplitTypes(tt);
				
				ts.push(rt);
			}
						
			for(var i=0;i<sourceTypes.length;i++) {
				var st=sourceTypes[i];
				var st2=f_dragAndDropEngine._SplitTypes(st);
				if (!st2) {
					continue;
				}
				
				for (var j=0;j<targetTypes.length;j++) {
					var tt2=ts[j];
					
					if ((st2[0]=="*" || tt2[0]=="*" || st2[0]==tt2[0]) && (st2[1]=="*" || tt2[1]=="*" || st2[1]==tt2[1])) {
						selectedTypes.f_addElement(targetTypes[j]); // Il faut conserver le parametre éventuel !
					}
				}
			}
		}
		return selectedTypes;		
	 },
	 /**
	  * @method private static
	  * @param Event evt
	  * @return Boolean
	  * @context event:evt
	  */
	 _DragMove: function(evt) {
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
		 
		try {
			var current=f_dragAndDropEngine._Current;
	
			f_core.Debug(f_dragAndDropEngine, "_DragMove: drag move ! current="+current);
	
			if (!current) {
				return;
			}
			
			return current._dragMove(evt);
			
		} catch (x) {
			f_core.Error(f_dragAndDropEngine, "_DragMove: exception", x);
		}
	 },
	 /**
	  * @method private static
	  * @param Event evt
	  * @return Boolean
	  * @context event:evt
	  */
	 _DragStop: function(evt) {
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		try {
			var current=f_dragAndDropEngine._Current;
	
			f_core.Debug(f_dragAndDropEngine, "_DragStop: stop drag ! current="+current);
	
			if (!current) {
				return;
			}	
			
			current._dragStop(evt, false);
			
			f_dragAndDropEngine._Exit();
			
			if (f_core.IsInternetExplorer()) {
//				f_core.Debug(f_dragAndDropEngine, "_DragStop: from="+evt.srcElement+" to="+evt.toElement+" related="+evt.relatedElement);
				
				try {
					evt.srcElement.fireEvent("onmouseup");

				} catch (x2) {
					// Dans certains cas, ca peut arriver !
				}
			}
			
			return;
			
		} catch (x) {
			f_core.Error(f_dragAndDropEngine, "_DragStop: exception", x);
		}
	 },
	 /**
	  * @method private static
	  * @param Event evt
	  * @return Boolean
	  * @context event:evt
	  */
	 _KeyDownUp: function(evt) {
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		try {
			var current=f_dragAndDropEngine._Current;
	
			f_core.Debug(f_dragAndDropEngine, "_KeyDown/_KeyUp: stop drag ! current="+current+" keyCode="+evt.keyCode);
	
			if (!current) {
				return;
			}	
			
			if (evt.keyCode==f_key.VK_ESCAPE) {
				
				current._dragStop(evt, true);
				
				return f_dragAndDropEngine._Exit();
			}
			
			switch(evt.keyCode) {
			case f_key.VK_SHIFT:
			case f_key.VK_CONTROL:
			case f_key.VK_ALT:
				current._modifyEffect(evt);
				return true;
			}
			
		} catch (x) {
			f_core.Error(f_dragAndDropEngine, "_KeyDown/_KeyUp: exception", x);
		}
	 },
	 /**
	  * @method private static
	  * @param Event evt
	  * @return Boolean
	  * @context event:evt
	  */
	 _FocusExit: function(evt) {
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		try {
			var current=f_dragAndDropEngine._Current;
	
			f_core.Info(f_dragAndDropEngine, "_FocusExit: focus ! current="+current);
	
			if (!current) {
				return;
			}	
			
			current._dragStop(evt, true);
			
			return f_dragAndDropEngine._Exit();
			
		} catch (x) {
			f_core.Error(f_dragAndDropEngine, "_FocusExit: exception", x);
		}
	 },
	 /**
	  * @method private static
	  * @return Boolean
	  */
	 _Exit: function() {
		var current=f_dragAndDropEngine._Current;
		
		if (!current) {

			f_core.Debug(f_dragAndDropEngine, "_Exit: nothing to clear");
			return;
		}	
		
		f_core.Debug(f_dragAndDropEngine, "_Exit: exit dnd engine ! current="+current);
		
		f_dragAndDropEngine._Current=undefined;

		return current._exit();
	 },
	/**
	 * @method private static
	 * @param String
	 * @return String[]
	 */
	_SplitTypes: function(type) {
		
		var idx=type.indexOf(';');
		if (idx>=0) {
			type=type.substring(0, idx);
		}

		var cache=f_dragAndDropEngine._Cache;
		if (!cache) {
			cache=new Object();
			f_dragAndDropEngine._Cache=cache;
		}
		
		var types=cache[type];
		if (types!==undefined) {
			return types;
		}
		
		types=type.split("/");
		if (types.length!=2) {
			types=null;
		} else {		
			types = [ f_core.Trim(types[0]), f_core.Trim(types[1]) ];
		}
		
		cache[type]=types;
		
		return types;		
	},
	
	/**
	 * @method public static
	 * @param String name
	 * @param Function constructor
	 * @return void
	 */
	RegisterDragAndDropPopup: function(name, constructor) {
		var infos=f_dragAndDropEngine._DragAndDropInfos;
		if (!infos) {
			infos=new Object;
			f_dragAndDropEngine._DragAndDropInfos = infos;
		}
		infos[name]=constructor;
	},
	/**
	 * @method public static
	 * @param String... name
	 * @return void
	 */
	SetDefaultDragAndDropPopup: function(name) {
		
		f_dragAndDropEngine._DefaultDragAndDropInfos=f_core.PushArguments(null, name);
	},
	/**
	 * @method public static
	 */
	Finalizer: function() {
		 f_dragAndDropEngine._Current = undefined;
		 f_dragAndDropEngine._DragAndDropInfos=undefined;
		 f_dragAndDropEngine._DefaultDragAndDropInfos = undefined; // String[]
	}
};

var __members = {
		
	/**
	 * @field private f_component
	 */
	_sourceComponent: undefined,
	
	/**
	 * @field private f_component
	 */
	_targetComponent: undefined,
	
	/**
	 * @field private Object
	 */
	_targetItem: undefined,
	
	/**
	 * @field private any
	 */
	_targetItemValue: undefined,
	
	/**
	 * @field private any
	 */
	_targetItemComponent: undefined,

	/**
	 * @field private Boolean
	 */
	_initialized: undefined,
		
	/**
	 * @method 
	 * @param f_component sourceComponent
	 * @param String[] dragAndDropPopupNames
	 */
	f_dragAndDropEngine: function(sourceComponent, names) {
		this.f_super(arguments);

		this._sourceComponent=sourceComponent;
		this._dragAndDropInfoNames=names;
	},
	
	/** 
	 * @method public
	 */
	f_finalize: function() {
		this._elementOver=undefined;
		var timerId=this._elementOverTimerId;
		if (timerId) {
			this._elementOverTimerId=undefined;
			
			f_core.GetWindow(this._sourceComponent).clearTimeout(timerId);
		}

		this._sourceComponent=undefined;
		this._clearFields();
		// this._lastEffectsInfo=undefined; // Number
		// this._lastTypesInfo=undefined; // String[]
		// this._dragAndDropInfoNames=undefined; // String[]

		
		this.f_super(arguments);
	},
	/**
	 * @method public
	 * @param String... name
	 * @return f_dragAndDropInfo Las drag and drop info popup instance.
	 */
	f_addDragAndDropInfoByName: function(name) {
		var infos=f_dragAndDropEngine._DragAndDropInfos;
		if (!infos) {
			return null;
		}
		
		for(var i=0;i<arguments.length;i++) {
			var name=arguments[i];
			
			var cb=infos[name];
			if (!cb) {
				continue;
			}
			
			var dndInfo=cb(this);
			if (!dndInfo) {
				continue;
			}
			
			var old=this._dragAndDropInfo;
			if (old) {
				dndInfo.f_setParent(old);
			}
			
			this._dragAndDropInfo=dndInfo;
		}
		
		return this._dragAndDropInfo;
	},
	/**
	 * @method public
	 * @param Event jsEvent
	 * @param Object selection
	 * @return Boolean
	 */
	f_start: function(jsEvent, selection) {	
//		f_core.Debug(f_dragAndDropEngine, "f_dragAndDropEngine: sourceComponent='"+this._sourceComponent+"' sourceItem='"+sourceItem+"' sourceItemValue='"+sourceItemValue+"' sourceItemElement='"+sourceItemElement+"' sourceDragEffects='"+sourceDragEffects+"' sourceDragTypes='"+sourceDragTypes+"'");

		if (this._install(jsEvent)===false) {

			f_core.Debug(f_dragAndDropEngine, "f_start: install returns FALSE");

			return false;
		}	
		
		f_dragAndDropEngine._Current = this;		

		f_core.Debug(f_dragAndDropEngine, "f_start: installed returns TRUE");
		
		var srcComponent = this._sourceComponent;
		
		this._sourceItems= srcComponent.f_getDragItems(selection);
		this._sourceItemsValue= srcComponent.f_getDragItemsValue(selection);
		this._sourceItemsElement = srcComponent.f_getDragItemsValue(selection);
		this._sourceDragEffects=  srcComponent.f_getDragEffects(selection);
		this._sourceDragTypes= srcComponent.f_getDragTypes(selection);		
		
		return true;
	},
	
	
	/** 
	 * @method protected
	 * @return void
	 */
	_clearFields: function() {
		this._sourceItems=undefined;
		this._sourceItemsValue=undefined;
		this._sourceItemsElement = undefined;
		this._sourceDragEffects=undefined;
		this._sourceDragTypes=undefined;			

		this._targetComponent=undefined;
		this._targetItem=undefined;
		this._targetItemValue=undefined;
		this._targetItemElement = undefined;
		this._targetDragEffects=undefined;
		this._targetDragTypes=undefined;			
		
		this._additionalTargetInformations=undefined;
		this._additionalSourceInformations=undefined;
		
		this._lastEffectInfo=undefined;
		this._lastTypesInfo=undefined;
		
		this._currentDropEffect=undefined;
		this._currentDropTypes=undefined;
		
		this._lastMousePosition=undefined;
		this._lastShiftKey=undefined;
		this._lastAltKey=undefined;
		this._lastCtrlKey=undefined;
		this._lastMetaKey=undefined;
		
		
		this._releaseDropInfos(true);
	},
	
	/**
	 * @method private
	 * @return Boolean
	 */
	_install: function(jsEvent) {	
		f_dragAndDropEngine._Exit();

		var ret=this.f_fireEventToSource(f_dndEvent.DRAG_INIT_STAGE, jsEvent);
		
//		f_core.Debug(f_dragAndDropEngine, "_install: DRAG_INIT_STAGE event returns '"+ret+"'.");
		
		if (ret===false) {
			return false;
		}
		
		this._initialized = true;
		this._started = false;

		var doc=this._sourceComponent.ownerDocument;
		
		var eventPos = f_core.GetJsEventPosition(jsEvent, doc);
		this._initialMousePosition=eventPos;

		f_core.Debug(f_dragAndDropEngine, "_install: Initial mouse position "+eventPos.x+"/"+eventPos.y);

		f_core.AddEventListener(doc, "mousemove", f_dragAndDropEngine._DragMove, doc);
		f_core.AddEventListener(doc, "mouseup", f_dragAndDropEngine._DragStop, doc);
		f_core.AddEventListener(doc, "keydown", f_dragAndDropEngine._KeyDownUp, doc);
		f_core.AddEventListener(doc, "keyup", f_dragAndDropEngine._KeyDownUp, doc);
		f_core.AddEventListener(doc, "focus", f_dragAndDropEngine._FocusExit, doc);
		f_core.AddEventListener(doc, "blur", f_dragAndDropEngine._FocusExit, doc);
	
		this.fa_installAutoScroll();
		
		return true;
	},
	/**
	 * @method private
	 * @return void
	 */
	_exit: function() {
		f_core.Debug(f_dragAndDropEngine, "_exit: Exit drag/drop engine.");

		this._releaseDropInfos(true);
		
		this.fa_uninstallAutoScroll();
		
		var doc=this._sourceComponent.ownerDocument;
	
		f_core.RemoveEventListener(doc, "mousemove", f_dragAndDropEngine._DragMove, doc);
		f_core.RemoveEventListener(doc, "mouseup", f_dragAndDropEngine._DragStop, doc);
		f_core.RemoveEventListener(doc, "keydown", f_dragAndDropEngine._KeyDownUp, doc);
		f_core.RemoveEventListener(doc, "keyup", f_dragAndDropEngine._KeyDownUp, doc);
		f_core.RemoveEventListener(doc, "focus", f_dragAndDropEngine._FocusExit, doc);
		f_core.RemoveEventListener(doc, "blur", f_dragAndDropEngine._FocusExit, doc);
		
		if (this._dragLock) {
			this._dragLock=false;
			
			f_event.ExitEventLock(f_event.DND_LOCK);
		}

		var dndInfo=this._dragAndDropInfo;
		if (dndInfo) {
			try {
				dndInfo.f_end();
				
			} catch (x) {
				f_core.Error(f_dragAndDropEngine, "_exit: call f_end of dndInfo throws exception", x); 
			}
		}

		this._clearFields();
	},	
	
	/**
	 * @method hidden
	 * @param String stage
	 * @param Event jsEvent
	 * @param String[] types
	 * @param Number effect
	 * @param Object modifiedDetail
	 * @return Boolean
	 */
	f_fireEventToSource: function(stage, jsEvent, types, effect, modifiedDetail) {
		//f_core.Debug(f_dragAndDropEngine, "f_fireEventToSource: prepare event source ("+this._sourceComponent+") '"+stage+"' jsEvent='"+jsEvent+"' effect='"+effect+"' returns "+ret);

		var ret;
		
		try {
			ret=f_dndEvent.FireEvent(this._sourceComponent, f_event.DRAG, jsEvent, this, stage, this._targetItem, this._targetItemValue, this._targetComponent, effect, types, modifiedDetail);
			
		} catch (x) {
			f_core.Error(f_dragAndDropEngine, "f_fireEventToSource: fire to source ("+this._sourceComponent+") '"+stage+"' jsEvent='"+jsEvent+"' effect='"+effect+"' throws exception", x);
			
			throw x;
		}
		
		//f_core.Debug(f_dragAndDropEngine, "f_fireEventToSource: fire to source ("+this._sourceComponent+") '"+stage+"' jsEvent='"+jsEvent+"' effect='"+effect+"' returns '"+ret+"'");
		
		return ret;
	},
	
	
	/**
	 * @method hidden
	 * @param String stage
	 * @param Event jsEvent
	 * @param any itemValue
	 * @param String[] types
	 * @param Number effect
	 * @param Object modifiedDetail
	 * @return Boolean
	 */
	f_fireEventToTarget: function(stage, jsEvent, types, effect, modifiedDetail) {
		//f_core.Debug(f_dragAndDropEngine, "f_fireEventToTarget: prepare event source ("+this._targetComponent+") '"+stage+"' jsEvent='"+jsEvent+"' effect='"+effect+"' returns "+ret);
		
		var ret;
		try {
			ret=f_dndEvent.FireEvent(this._targetComponent, f_event.DROP, jsEvent, this, stage, this._targetItem, this._targetItemValue, this._targetComponent, effect, types, modifiedDetail);
		
		} catch (x) {
			f_core.Error(f_dragAndDropEngine, "f_fireEventToTarget: fire to target ("+this._targetComponent+") '"+stage+"' jsEvent='"+jsEvent+"' effect='"+effect+"' throws exception", x);
			
			throw x;
		}

		//f_core.Debug(f_dragAndDropEngine, "f_fireEventToTarget: fire to target ("+this._targetComponent+") '"+stage+"' jsEvent='"+jsEvent+"' effect='"+effect+"' returns '"+ret+"'");
		
		return ret;
	},
	
	/**
	 * @method public
	 * @param Event jsEvent
	 * @return Boolean
	 */
	_dragStart: function(jsEvent) {
//		f_core.Debug(f_dragAndDropEngine, "_startDrag: started="+this._started);

		if (this._started) {
			return false;
		}

		var ret=this.f_fireEventToSource(f_dndEvent.DRAG_START_STAGE, jsEvent);
		
		f_core.Debug(f_dragAndDropEngine, "_startDrag: DRAG_START_STAGE event returns '"+ret+"'.");
		
		if (ret===false) {
			return false;
		}
		
		this._started=true;
		
		if (!this._dragLock) {
			this._dragLock=true;
			
			f_event.EnterEventLock(f_event.DND_LOCK);
		}
		
		var dndInfo=this._dragAndDropInfo;
		if (!dndInfo) {
			var names=this._dragAndDropInfoNames;
			if (names===undefined) {
				names=f_dragAndDropEngine._DefaultDragAndDropInfos;
			}
			if (names) {
				this.f_addDragAndDropInfoByName.apply(this, names);
			}
		}
		
		var dndInfo=this._dragAndDropInfo;
		if (dndInfo) {
			try {
				dndInfo.f_start();
				
			} catch (x) {
				f_core.Error(f_dragAndDropEngine, "_dragStart: call f_start of dndInfo throws exception", x); 
			}
		}
	
		// Affichage du phantom !		
		
		return true;
	},
	/**
	 * @method private
	 * @return Boolean
	 */
	_dragStop: function(jsEvent, cancel) {
//		f_core.Debug(f_dragAndDropEngine, "_dragStop: Drag stop started='"+this._started+"'.")
		
		if (!this._started) {
			return;
		}
		
		this._started=undefined;
		
		var targetComponent=this._targetComponent;
		if (!targetComponent || !this._currentDropEffect || cancel) {
			
			this.f_fireEventToSource(f_dndEvent.DRAG_ABORTED_STAGE, jsEvent);
			
			return false;
		}
		
//		var targetItemValue=this._targetItemValue;
		var types=this._currentDropTypes;
		var effect=this._currentDropEffect;
		
		var canceled=false;
				
		var ret=this.f_fireEventToSource(f_dndEvent.DRAG_REQUEST_STAGE, jsEvent, types, effect);
		if (ret===false) {
			canceled=true;
			
		} else {
			var ret=this.f_fireEventToTarget(f_dndEvent.DROP_REQUEST_STAGE, jsEvent, types, effect);
			if (ret===false) {
				canceled=true;
			}
		}
				
		if (canceled) {		
			this.f_fireEventToSource(f_dndEvent.DRAG_CANCELED_STAGE, jsEvent);
			this.f_fireEventToTarget(f_dndEvent.DROP_CANCELED_STAGE, jsEvent);
			return false;
		}
				
		this.f_fireEventToSource(f_dndEvent.DRAG_COMPLETE_STAGE, jsEvent, types, effect);
		
		this.f_fireEventToTarget(f_dndEvent.DROP_COMPLETE_STAGE, jsEvent, types, effect);
		
		try {
			f_dndEvent.FireEvent(this._targetComponent, f_event.DROP_COMPLETE, jsEvent, this, null, this._targetItem, this._targetItemValue, this._targetComponent, effect, types, true);
		
		} catch (x) {
			f_core.Error(f_dragAndDropEngine, "_dragStop: fire DROP_COMPLETE throws exception: jsEvent='"+jsEvent+"'.", x);
		}

		return f_core.CancelJsEvent(jsEvent);
	},
	/**
	 * @method private
	 * @param Event jsEvent
	 * @return Boolean
	 */
	_dragMove: function(jsEvent) {

		var eventPos = f_core.GetJsEventPosition(jsEvent);
		this._lastMousePosition=eventPos;
		
		this._lastShiftKey=jsEvent.shiftKey;
		this._lastAltKey=jsEvent.altKey;
		this._lastCtrlKey=jsEvent.ctrlKey;
		this._lastMetaKey=jsEvent.metaKey;
		this._lastClientX=jsEvent.clientX;
		this._lastClientY=jsEvent.clientY;

		f_core.Debug(f_dragAndDropEngine, "_dragMove: started="+this._started+" dndInfo="+this._dragAndDropInfo);
		
		if (!this._started) {

//			f_core.Debug(f_dragAndDropEngine, "_dragMove: Test start drag");
			
			var ipos=this._initialMousePosition;
			
			var l=Math.sqrt(Math.pow(ipos.x-eventPos.x, 2)+Math.pow(ipos.y-eventPos.y, 2));

//			f_core.Debug(f_dragAndDropEngine, "_dragMove: move '"+l+"' pixels.");

			if (l<5) {
				return false;
			}
	
//			f_core.Debug(f_dragAndDropEngine, "_dragMove: Start drag");

			if (this._dragStart()===false) {
				return false;
			}
		}
		
		var dndInfo=this._dragAndDropInfo;
		if (dndInfo) {
			try {
				dndInfo.f_move(eventPos.x, eventPos.y);
				
			} catch (x) {
				f_core.Error(f_dragAndDropEngine, "_dragMove: call f_move of dndInfo throws exception", x); 
			}
		}
		
		var dropElement=jsEvent.srcElement;
		if (!dropElement) {
			dropElement=jsEvent.target;
		}
		
		var de=f_core.SearchComponentByAbsolutePosition(eventPos.x, eventPos.y);
		dropElement=(de.length)?de[de.length-1]:null; 
	
		//f_core.Debug(f_dragAndDropEngine, "_dragMove: DropElement="+dropElement);
		
		if (!dropElement || dropElement.nodeType!=f_core.ELEMENT_NODE) {

//			f_core.Debug(f_dragAndDropEngine, "_dragMove: invalid dropElement '"+dropElement+"'");
			return this._cancelTarget(jsEvent);
		}
		
		var dropComponent=f_core.GetParentByClass(dropElement);
		//f_core.Debug(f_dragAndDropEngine, "_dragMove: DropComponent="+dropComponent);
		
		var queryDropInfosCall=this._queryDropInfosCall;
		if (this._targetComponent && !this._verifyParent(this._targetComponent, dropComponent)) {
			this._releaseDropInfos(true);
		}
		
		if (!dropComponent) {
			// Target definie ... on abandonne target

//			f_core.Debug(f_dragAndDropEngine, "_dragMove: invalid dropComponent '"+dropComponent+"' (dropElement='"+dropElement+"')");
			return this._cancelTarget(jsEvent);
		}
			
		var startTimer=false;
		var endTimer=false;
		var overTimerId=this._elementOverTimerId;
		if (dropComponent.fa_findAutoOpenElement) {
			var elementOver=dropComponent.fa_findAutoOpenElement(dropElement);
			var old=this._elementOver;
			this._elementOver=elementOver;
		
			if (elementOver && old && dropComponent.fa_isSameAutoOpenElement(elementOver, old)) {
				// On continue
			
			} else if (elementOver && old) {
				// Un autre, on relance le timer
				endTimer=true;
				startTimer=true;
			
			} else if (elementOver) {
				startTimer=true;
				
			} else if (old) {
				endTimer=true;
			}

//			f_core.Debug(f_dragAndDropEngine, "_dragMove: elementOver="+(elementOver?elementOver._value:null)+" / "+(old?old._value:null)+" start="+startTimer+" end="+endTimer);
					
		} else if (overTimerId) {
			endTimer=true;
		}
		
		if (endTimer) {
			this._elementOverTimerId=undefined;
			f_core.GetWindow(this._sourceComponent).clearTimeout(overTimerId);
		}
		
		if (startTimer) {
			var self=this;
			var _dropComponent=dropComponent;
			
			overTimerId=f_core.GetWindow(this._sourceComponent).setTimeout(function() {
				
				if (window._rcfacesExiting) {
					return false;
				}
				try {
					var elementOver=self._elementOver;
					
					self._elementOverTimerId=undefined;
					self._elementOver=undefined;
					
					_dropComponent.fa_performAutoOpenElement(elementOver);
					
				} catch (x) {
					f_core.Error(f_dragAndDropEngine, "_dragMove: auto open timeout throws exception", x); 
				}
				
			}, 500);
			
			this._elementOverTimerId=overTimerId;
		}
		
		
		if (!dropComponent.f_isDroppable || !dropComponent.f_isDroppable()) {
			// Target definie ... on abandonne target

//			f_core.Debug(f_dragAndDropEngine, "_dragMove: component is not droppable '"+dropComponent+"' (dropElement='"+dropElement+"')");
			return this._cancelTarget(jsEvent, false);
		}
		
		this._queryDropInfosCall=dropComponent;
		
		var dropInfos=dropComponent.f_queryDropInfos(this, jsEvent, dropElement);
		if (!dropInfos) {
			// La target ne trouve rien ...

//			f_core.Debug(f_dragAndDropEngine, "_dragMove: no dropInfos for component '"+dropComponent+"', dropElement='"+dropElement+"'");
			return this._cancelTarget(jsEvent, false);
		}
		
		if (this._sourceComponent==dropComponent && this._sourceItemValue==dropInfos.itemValue) {
			// On peut pas copier sur soi-même !
//			f_core.Debug(f_dragAndDropEngine, "_dragMove: same item as source !");
			return;
		}
	
		if (this._targetComponent==dropComponent && this._targetItemValue==dropInfos.itemValue) {
			// Pas de changement !
//			f_core.Debug(f_dragAndDropEngine, "_dragMove: same item as target !");
			return;
		}
		
		this._cancelTarget(jsEvent, false);
		
		this._targetComponent=dropComponent;
		this._targetItem=dropInfos.item;
		this._targetItemValue=dropInfos.itemValue;
		this._targetItemElement = dropInfos.itemElement;
		this._targetDropTypes = dropInfos.dropTypes;
		this._targetDropEffects = dropInfos.dropEffects;		
		this._additionalTargetInformations=undefined;
		this._dropInfos=dropInfos;
	
//		f_core.Debug(f_dragAndDropEngine, "_dragMove: new target targetComponent='"+this._targetComponent+"' targetItem='"+this._targetItem+"' targetItemValue='"+this._targetItemValue+"' targetItemElement='"+this._targetItemElement+"' targetDropEffects='"+this._targetDropEffects+"' this._targetDropTypes='"+this._targetDropTypes+"'");

		try {
			dropComponent.f_overDropInfos(this, dropInfos);
					
		} catch (x) {
			f_core.Error(f_dragAndDropEngine, "_dragMove: call f_overDropInfos throws exception", x); 
		}				
		
		return this._computeDragAndDrop(jsEvent);
	},
	/**
	 * @method private
	 * @return Boolean
	 */
	_verifyParent: function(parent, child) {
		if (parent==child) {
			return true;
		}
		
		for(;child && child!=parent;child=child.parentNode);
		
		return !!child;
	},
	/**
	 * @method private
	 * @param Event jsEvent
	 * @return Boolean
	 */
	_cancelTarget: function(jsEvent, clearTimer) {
		var target=this._targetComponent;
//		f_core.Debug(f_dragAndDropEngine, "_cancelTarget: target='"+target+"'.");

		if (!target) {
			this._computeDragAndDrop(jsEvent);
			return false;
		}		
		
		this.f_fireEventToSource(f_dndEvent.DRAG_OVER_CANCELED_STAGE, jsEvent);			
		this.f_fireEventToTarget(f_dndEvent.DROP_OVER_CANCELED_STAGE, jsEvent);

		this._additionalTargetInformations=undefined;
		this._targetComponent=undefined;
		this._targetItem=undefined;
		this._targetItemValue=undefined;
		this._targetItemElement = undefined;
		this._targetDropTypes = undefined;
		this._targetDropEffects = undefined;		
		
		this._releaseDropInfos(false);
		
		if (clearTimer!==false) {
			var timerId=this._elementOverTimerId;
			if (timerId) {
				this._elementOverTimerId=undefined;
				
//				f_core.Debug(f_dragAndDropEngine, "_cancelTarget: clear timeout #"+timerId);
				
				f_core.GetWindow(this._sourceComponent).clearTimeout(timerId);
			}
		}
		
		this._computeDragAndDrop(jsEvent);
		return false;
	},
	/**
	 * @method private
	 * @param Boolean
	 * @return void
	 */
	_releaseDropInfos: function(releaseDrop) {
		var queryDropInfosCall=this._queryDropInfosCall;
		if (!queryDropInfosCall) {
			return;
		}
		
		var dropInfos=this._dropInfos;
		if (dropInfos) {
			this._dropInfos=undefined;
			
			try {
				queryDropInfosCall.f_outDropInfos(this, dropInfos);
						
			} catch (x) {
				f_core.Error(f_dragAndDropEngine, "_releaseDropInfos: call f_outDropInfos throws exception", x); 
			}				
		}
		
		if (releaseDrop===true) {
			this._queryDropInfosCall=undefined;

			try {
				queryDropInfosCall.f_releaseDropInfos(this);
						
			} catch (x) {
				f_core.Error(f_dragAndDropEngine, "_releaseDropInfos: call f_releaseDropInfos throws exception", x); 
			}
		}
	},
	/**
	 * @method private
	 * @param Event jsEvent
	 * @return Boolean
	 */
	_modifyEffect: function(jsEvent) {

		this._lastShiftKey=jsEvent.shiftKey;
		this._lastAltKey=jsEvent.altKey;
		this._lastCtrlKey=jsEvent.ctrlKey;
		this._lastMetaKey=jsEvent.metaKey;

		if (!this._targetDropEffects) {
			return true;
		}
				
		var newEffect=this._computeEffect(jsEvent);
		if (newEffect==this._currentDropEffect) {
			return true;
		}
		
		this._computeDragAndDrop(jsEvent);
		return true;
	},
	/**
	 * @method private
	 * @param Event jsEvent
	 * @return Number Effect
	 */
	_computeEffect: function(jsEvent) {
		var targetEffects=this._targetDropEffects;
		var sourceEffects=this._sourceDragEffects;

		var effects=targetEffects & sourceEffects;
	
		var effect=f_dndEvent.NONE_DND_EFFECT;
		if (effects) {
			if (jsEvent) {
				if ((jsEvent.shiftKey || jsEvent.ctrlKey) && jsEvent.altKey && (effects & f_dndEvent.MOVE_DND_EFFECT)) {
					// ALT + (SHIFT || CTRL) => MOVE
					effect=f_dndEvent.MOVE_DND_EFFECT;
	
				} else if (jsEvent.shiftKey && jsEvent.ctrlKey && (effects & f_dndEvent.LINK_DND_EFFECT)) {
					// CTRL + SHIFT => LINK
					effect=f_dndEvent.LINK_DND_EFFECT;
	
				} else if (jsEvent.altKey && (effects & f_dndEvent.LINK_DND_EFFECT)) {
					effect=f_dndEvent.LINK_DND_EFFECT;
					
				} else if (jsEvent.ctrlKey && (effects & f_dndEvent.COPY_DND_EFFECT)) {
					effect=f_dndEvent.COPY_DND_EFFECT;
					
				} else if (jsEvent.shiftKey && (effects & f_dndEvent.MOVE_DND_EFFECT)) {
					effect=f_dndEvent.MOVE_DND_EFFECT;
				}
			}
			
			if (!effect) {
				if (effects & f_dndEvent.DEFAULT_DND_EFFECT) {
					effect=f_dndEvent.DEFAULT_DND_EFFECT;
				
				} else if (effects & f_dndEvent.LINK_DND_EFFECT) {
					effect=f_dndEvent.LINK_DND_EFFECT;
				
				} else if (effects & f_dndEvent.MOVE_DND_EFFECT) {
					effect=f_dndEvent.MOVE_DND_EFFECT;
					
				} else if (effects & f_dndEvent.COPY_DND_EFFECT) {
					effect=f_dndEvent.COPY_DND_EFFECT;
				}				
			}
		}
		
	//	f_core.Debug(f_dragAndDropEngine, "_computeEffect: targetEffects=0x"+targetEffects.toString(16)+" sourceEffects=0x"+sourceEffects.toString(16)+" effects=0x"+effects.toString(16)+" computedEffect=0x"+effect.toString(16)+" ");
		
		return effect;
	},
	/**
	 * @method private
	 * @param Event jsEvent
	 * @return Boolean
	 */
	_computeDragAndDrop: function(jsEvent) {
		this._currentDropEffect=0;			
		this._currentDropTypes=null;			

		if (!this._targetComponent) {
//			f_core.Debug(f_dragAndDropEngine, "_computeDragAndDrop: target='"+this._targetComponent+"'.");

			this._updateDnDMask();			
			return;
		}
		
		var targetTypes=this._targetDropTypes;
		var sourceTypes=this._sourceDragTypes;

	//	f_core.Debug(f_dragAndDropEngine, "_computeDragAndDrop: match types target='"+targetTypes+"', source='"+sourceTypes+"'.");
		
		var selectedTypes=f_dragAndDropEngine.ComputeTypes(sourceTypes, targetTypes);
	
	//	f_core.Debug(f_dragAndDropEngine, "_computeDragAndDrop: match result = "+selectedTypes);
	
		if (!selectedTypes.length) {
			this._updateDnDMask();			
			return;
		}
	
		var effect=this._computeEffect(jsEvent);

		if (!effect) {
			this._updateDnDMask();	
			
			return;
		}
		
		this._additionalTargetInformations=null;
		var modifiedDetail = new Object();
		var ret=this.f_fireEventToSource(f_dndEvent.DRAG_OVER_STAGE, jsEvent, selectedTypes, effect, modifiedDetail);
		if (ret===false) {
			this._updateDnDMask();			
			return;
		}
		
		if(modifiedDetail._effect !== undefined){
			effect = modifiedDetail._effect;
		}
		if(modifiedDetail._types !== undefined){
			selectedTypes = modifiedDetail._types;
		}
		
		modifiedDetail = new Object();
		ret=this.f_fireEventToTarget(f_dndEvent.DROP_OVER_STAGE, jsEvent, selectedTypes, effect, modifiedDetail);
		if (ret===false) {			
			this.f_fireEventToSource(f_dndEvent.DRAG_OVER_CANCELED_STAGE, jsEvent);

			this._updateDnDMask();
			return;
		}
		
		if(modifiedDetail._effect !== undefined){
			effect = modifiedDetail._effect;
		}
		if(modifiedDetail._types !== undefined){
			selectedTypes = modifiedDetail._types;
		}
		
		this._currentDropEffect=effect;
		this._currentDropTypes=selectedTypes;
		this._lastEffectInfo=undefined;
		this._lastTypesInfo=undefined;
    
		this._updateDnDMask();			
		
		return true;
	},
	/**
	 * @method private
	 * @return void
	 */
	_updateDnDMask: function() {
		var effect=this._currentDropEffect;
		var types=this._currentDropTypes;

		var dndInfo=this._dragAndDropInfo;
		if (dndInfo) {
			this._lastEffectInfo=effect;
			this._lastTypesInfo=types;
			
			try {
				dndInfo.f_updateTarget(types, effect, this._targetComponent, this._targetItem, this._targetItemValue, this._additionalTargetInformations);
				
			} catch (x) {
				f_core.Error(f_dragAndDropEngine, "_updateDnDMask: call f_updateTarget of dndInfo throws exception", x); 
			}
		}
		
	},
	/**
	 * @method public
	 * @param Map infos
	 * @return void
	 */
	f_setTargetAdditionnalInformations: function(infos) {
		this._additionalTargetInformations=infos;
	},
	/**
	 * @method public
	 * @param Map infos
	 * @return void
	 */
	f_setSourceAdditionnalInformations: function(infos) {
		this._additionalSourceInformations=infos;
	},
	/**
	 * @method public
	 * @return Map infos
	 */
	f_getSourceAdditionnalInformations: function() {
		return this._additionalSourceInformations;
	},
	/**
	 * @method public
	 * @return any first sourceItem
	 */
	f_getSourceItem: function() {
		if (this._sourceItems && this._sourceItems[0]) {
			return this._sourceItems[0];
		}
		return null;
	},
	/**
	 * @method public
	 * @return any first sourceItemValue
	 */
	f_getSourceItemValue: function() {
		if (this._sourceItemsValue && this._sourceItemsValue[0]) {
		return this._sourceItemsValue[0];
		}
		return null;
	},
	/**
	 * @method public
	 * @return Array array of sourceItems
	 */
	f_getSourceItems: function() {
		return this._sourceItems;
	},
	/**
	 * @method public
	 * @return Array array of sourceItemValues
	 */
	f_getSourceItemsValue: function() {
		return this._sourceItemsValue;
	},
	/**
	 * @method public
	 * @return any source Component
	 */
	f_getSourceComponent: function() {
		return this._sourceComponent;
	},
	/**
	 * @method public
	 * @return Number[] last mouse position
	 */
	fa_getLastMousePosition: function() {
		return this._lastMousePosition;
	},
	/**
	 * @method public
	 * @return HtmlElement the scrollable container
	 */
	fa_getScrollableContainer: function() {
		return document.body;
	},
	/**
	 * @method public
	 * @return void
	 */
	f_updateMousePosition: function() {
		
		var event;
		
		if (f_core.IsInternetExplorer()) {
			event = document.createEventObject();
			event.detail = 0;
			event.screenX = this._lastClientX;
			event.screenY = this._lastClientY;
			event.clientX = this._lastClientX;
			event.clientY = this._lastClientY;
			event.ctrlKey = this._lastCtrlKey;
			event.altKey = this._lastAltKey;
			event.shiftKey = this._lastShiftKey;
			event.metaKey = this._lastMetaKey;
			event.button = 0;
			event.relatedTarget = document.body;

		} else {
			event=document.createEvent("MouseEvents");		
			
			event.initMouseEvent("move", 
					true, 
					true, 
					window, 
					0, 
					this._lastClientX, 
					this._lastClientY, 
					this._lastClientX, 
					this._lastClientY, 
					this._lastCtrlKey, 
					this._lastAltKey, 
					this._lastShiftKey, 
					this._lastMetaKey,
					0, 
					document.body);		
		}
		
		this._dragMove(event);
	},
	/**
	 * @method public
	 * @return void
	 */
	fa_autoScrollPerformed: function() {
		this.f_updateMousePosition();
	}
};

new f_class("f_dragAndDropEngine", {
	extend: f_object,
	aspects: [ fa_screenAutoScroll ],
	statics: __statics,
	members: __members
});