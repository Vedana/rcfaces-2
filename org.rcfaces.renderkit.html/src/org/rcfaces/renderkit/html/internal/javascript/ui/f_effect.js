/*
 * $Id: f_effect.js,v 1.2 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * 
 *
 * @class hidden f_effect extends f_object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:28 $
 */
var __statics = {
	/**
	 * @field private static
	 */
	 _EffectClasses: undefined, 
	 
	/**
	 * Declare an effect.
	 *
	 * @method hidden static final
	 * @param String name Name of effect.
	 * @param f_class clazz Class of effect to instanciate.
	 * @return void
	 */
	Declare: function(name, clazz) {
		f_core.Assert(typeof(name)=="string", "Name of effect is not a string. ("+name+")");
		f_core.Assert(clazz instanceof f_class, "Effect parameter must be a class. ("+clazz+")");

		var effects=f_effect._EffectClasses;
		if (!effects) {
			effects=new Object;
			f_effect._EffectClasses=effects;
		}
		
		f_core.Assert(effects[name]===undefined, "Effect '"+name+"' is already declared !");		

		effects[name]=clazz;
		
		f_core.Info(f_effect, "Declare effect '"+name+"'.");		
	},
	/**
	 * Create an effect found by its name !
	 *
	 * @method public static final
	 * @param String name Name of the effect.
	 * @param HTMLElement component Component which be affected by the effect.
	 * @param optional Function callback Callback which be called when the effect changes properties.
	 * @return f_effect An instance of f_effect class.
	 */
	Create: function(name, component, callback) {
		var effectClasses=f_effect._EffectClasses;
		if (!effectClasses) {
			f_core.Info(f_effect, "No declared effects (Asked effect: "+name+").");
			return null;
		}
		
		var claz=effectClasses[name];
		if (!claz) {
			f_core.Info(f_effect, "Effect '"+name+"' not found.");
			return null;
		}
		
		f_core.Info(f_effect, "Create an effect '"+name+"' for component '"+component.id+"'.");
		return claz.f_newInstance(component, callback);
	},
	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer: function() {
		f_effect._EffectClasses=undefined;
	}
}
var __members = {
	f_effect: function(component, callback) {
		this.f_super(arguments);

		this._component=component;
		this._callback=callback;
	},
	f_finalize: function() {
		this._component=undefined;
		this._callback=undefined;

		this.f_super(arguments);
	},
	/**
	 * @method public Run the effect animation.
	 * @param Object value A parameter ...
	 * @return void
 	 */
	f_performEffect: function(value) {
	},
	/**
	 * @method public
	 * @return HTMLElement Component associated to this effect.
	 */
	f_getComponent: function() {
		return this._component;
	}
}
new f_class("f_effect", {
	extend: f_object, 
	statics: __statics,
	members: __members
});