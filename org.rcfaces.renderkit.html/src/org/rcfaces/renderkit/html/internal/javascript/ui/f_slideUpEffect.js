/*
 * $Id: f_slideUpEffect.js,v 1.3 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * 
 *
 * @class hidden f_slideUpEffect extends f_timerEffect
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:27 $
 */
var __statics = {
	/** 
	 * @field private static final Number
	 */
	_STEP_MS: 50,

	/** 
	 * @field private static final Number
	 */
	_STEP: 0.3,

	/**
	 * @method protected static
	 * @return void
	 */
	Initializer: function() {
		f_effect.Declare("slideUp", this);
	}
};

var __members = {
	f_slideUpEffect: function(component, callback) {
		this.f_super(arguments, component, callback);
		
		if (!component.offsetHeight) {
			this._current=0;
			this._componentHeightSetted=component.style.height;
			
		} else {
			this._current=1;
		}
	},
	f_performEffect: function(set) {
		this._nextValue=(set)?0:1;
		
		var component=this._component;
		if (this._current!=this._nextValue && this._current==0) {
			component.style.display="block";
			component.style.height="1px";
		}
		
		if (this._callback) {
			this._callback(this._current);
		}
		
		this.f_wakeUpTimer();
	},
	f_getStepMs: function() {
		return f_slideUpEffect._STEP_MS;
	},
	f_getStep: function() {
		return f_slideUpEffect._STEP;
	},
	f_performTick: function(component, cur, next) {
		
		if (this._callback) {
			this._callback(cur);
		}
		
		if (cur<0.0001) {
			component.style.display="none";
			return;
		}
		
		if (cur>0.9999) {
			if (this._componentHeightSetted) {
				component.style.height=this._componentHeightSetted;
			} else {
				component.style.height="auto";
			}
			component.scrollTop=0;
			return;
		}
		
		var h=component.scrollHeight;
		var ch=Math.floor(h*cur);
		
		component.style.height=ch+"px";
		component.scrollTop=h-ch;
	}
};

new f_class("f_slideUpEffect", null, __statics, __members, f_timerEffect);

