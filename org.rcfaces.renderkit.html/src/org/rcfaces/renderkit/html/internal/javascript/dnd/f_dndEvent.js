/*
 * $Id: f_dndEvent.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */

/**
 * f_dndEvent class
 *
 * @class public f_dndEvent extends Object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
 
var __statics = {


	/**
	 * @field public static Number
	 */
    NONE_DND_EFFECT: 0x00,

	/**
	 * @field public static Number
	 */
    DEFAULT_DND_EFFECT: 0x01,

	/**
	 * @field public static Number
	 */
    COPY_DND_EFFECT: 0x02,

	/**
	 * @field public static Number
	 */
	LINK_DND_EFFECT: 0x04,

	/**
	 * @field public static Number
	 */
    MOVE_DND_EFFECT: 0x08,

	/**
	 * @field public static String
	 */
	DRAG_INIT_STAGE: "dragInit",
	
	/**
	 * @field public static String
	 */
	DRAG_START_STAGE: "dragStart",

	/**
	 * @field public static String
	 */
	DRAG_ABORTED_STAGE: "dragAborted",
	
	/**
	 * @field public static String
	 */
	DROP_REQUEST_STAGE: "dropRequest",
	

	/**
	 * @field public static String
	 */
	DRAG_REQUEST_STAGE: "dragRequest",

	/**
	 * @field public static String
	 */
	DROP_CANCELED_STAGE: "dropCanceled",
	

	/**
	 * @field public static String
	 */
	DRAG_CANCELED_STAGE: "dragCanceled",
	

	/**
	 * @field public static String
	 */
	DROP_COMPLETE_STAGE: "dropComplete",
	

	/**
	 * @field public static String
	 */
	DRAG_COMPLETE_STAGE: "dragComplete",
	
	/**
	 * @field public static String
	 */
	DRAG_OVER_STAGE: "dragOver",

	/**
	 * @field public static String
	 */
	DROP_OVER_STAGE: "dropOver",

	/**
	 * @field public static String
	 */
	DRAG_OVER_CANCELED_STAGE: "dragOverCanceled",

	/**
	 * @field public static String
	 */
	DROP_OVER_CANCELED_STAGE: "dropOverCanceled",
	
	/**
	 * Convert a f_event event to a f_dndEvent event to get more informations.
	 * 
	 * @method public static
	 * @param f_event event
	 * @return f_dndEvent
	 */
	As: function(event) {
		return new f_dndEvent(event);
	},
	/**
	 * @method hidden static
	 * @param f_component component
	 * @param String eventType
	 * @param Event jsEvent
	 * @param f_dragAndDropEngine engine
	 * @param Object value
	 * @param optional String stage
	 * @param optional Boolean serialize
	 * @return Boolean
	 */
	FireEvent: function(component, eventType, jsEvent, engine, stage, targetItem, targetItemValue, targetComponent, effect, types, modifiedDetail, serialize) {

		var serializedValue;
		if (serialize) {
			serializedValue=f_core.EncodeObject({
				targetItemValue: targetItemValue,

				sourceItemValue: engine.f_getSourceItemValue(),
				sourceComponent: engine.f_getSourceComponent(),

				effect: effect,
				types: types
			});
		}

		var value = {
				_targetItem: targetItem,
				_targetItemValue: targetItemValue,
				_targetComponent: targetComponent,

				_effect: effect,
				_types: types,
				
				_engine: engine,
				
				_stage: stage,
				
				_modifiedDetail: modifiedDetail  
		};

		var event=new f_event(component, eventType, jsEvent, null, value, null, null, serializedValue);
		
		try {			
			return component.f_fireEvent(event);
			
		} finally {
			f_classLoader.Destroy(event);
		}
	}
};

var __members = {
		
	/**
	 * @field private Object
	 */
	_detail: undefined,
	
	/**
	 * @field private f_dragAndDropEngine
	 */
	_engine: undefined,
	
	/**
	 * @field private String
	 */
	_stage: undefined,
	
	/**
	 * @method private
	 * @param f_event event
	 */
	f_dndEvent: function(event) {
		var detail=event.f_getValue();
		
		this._detail=detail;
		this._engine=detail._engine;
		this._stage=detail._stage;
		this._modifiedDetail=detail._modifiedDetail;
	},

	/**
	 * @method public
	 * @return f_component
	 */
	f_getTargetComponent: function() {
		return this._detail._targetComponent;
	},

	/**
	 * @method public
	 * @return any
	 */
	f_getTargetItem: function() {
		return this._detail._targetItem;
	},

	/**
	 * @method public
	 * @return String
	 */
	f_getTargetItemValue: function() {
		return this._detail._targetItemValue;
	},

	/**
	 * @method public
	 * @return f_component
	 */
	f_getSourceComponent: function() {
		return this.f_getDragAndDropEngine().f_getSourceComponent();
	},

	/**
	 * @method public
	 * @return any
	 */
	f_getSourceItem: function() {
		return this.f_getDragAndDropEngine().f_getSourceItem();
	},

	/**
	 * @method public
	 * @return String
	 */
	f_getSourceItemValue: function() {
		return this.f_getDragAndDropEngine().f_getSourceItemValue();
	},
	
	/**
	 * @method public
	 * @return Array
	 */
	f_getSourceItems: function() {
		return this.f_getDragAndDropEngine().f_getSourceItems();
	},

	/**
	 * @method public
	 * @return Array
	 */
	f_getSourceItemsValue: function() {
		return this.f_getDragAndDropEngine().f_getSourceItemsValue();
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getStage: function() {
		return this._stage;
	},
	/**
	 * @method public
	 * @return f_dragAndDropEngine
	 */
	f_getDragAndDropEngine: function() {
		return this._engine;
	},
	/**
	 * @method public
	 * @return Number
	 */
	f_getEffect: function() {
		return this._detail._effect;
	},
	/**
	 * @method public
	 * @param Number effect
	 * @return void
	 */
	f_setEffect :function(effect){
		f_core.Assert(typeof(effect)=="number", "f_dndEvent.f_setEffect:" +
				" Invalid effect parameter ("+effect+" (typeof=" + typeof(effect) +")");

		if (!this._modifiedDetail){
			throw new Error("Impossible to change effect : "+effect);
		}
		
		this._modifiedDetail._effect = effect;
		
		f_core.Debug(f_dndEvent, "f_setEffect: change effect="+effect+ "during stage : " +
				this.f_getStage() +" targetComponent="+this.f_getTargetComponent());
	},
	
	/**
	 * @method public
	 * @return String[]
	 */
	f_getTypes: function() {
		return this._detail._types;
	},
	/**
	 * @method public
	 * @param String[] types
	 * @return void
	 */
	f_setTypes :function(types){
		f_core.Assert(typeof(types)=="object", "f_dndEvent.f_setTypes:" +
				" Invalid types parameter ("+types+" (typeof=" + typeof(types) +")");
		
		if (!this._modifiedDetail) {
			throw new Error("Impossible to change types : "+types);
		}
		
		this._modifiedDetail._types = types;
		f_core.Debug(f_dndEvent, "f_setTypes: change types="+types+ "during stage : " +
				this.f_getStage() +" targetComponent="+this.f_getTargetComponent());
	}
	
	
};

new f_class("f_dndEvent", {
	statics: __statics,
	members: __members
});
