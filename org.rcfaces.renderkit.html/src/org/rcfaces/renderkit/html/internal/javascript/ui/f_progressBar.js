/*
 * $Id: f_progressBar.js,v 1.3 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * Classe ProgressBar
 *
 * @class public f_progressBar extends f_component
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $) 
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:27 $
 */

var __statics = {
	/**
	 * @field private static final Number
	 */
	_BORDER_SIZE: 20,
	
	/**
	 * @field private static final Number
	 */
	_ANIM_SIZE: 75,
	
	/**
	 * @field private static final Number
	 */
	_OVER_DELAY: 40,
	
	/**
	 * @field private static final Number
	 */
	_ANIMATION_DELAY: 2000,
	
	/**
	 * @field private static final Number
	 */
	_NEXT_ANIMATION_DELAY: 50,
	
	
	/**
	 * @method public static
	 * @param HTMLElement parent
	 * @param Number width
	 * @param Number height
	 * @param optional String styleClass
	 * @return f_progressBar
	 */
	Create: function(parent, width, height, styleClass) {
		
		if (!styleClass) {
			styleClass="";
		}
		
		styleClass="f_progressBar "+styleClass;
		
		var div=f_core.CreateElement(parent, "div", {
			className: styleClass,
			width: width,
			height: height
		});
		
		div.style.width=width+"px";		
		div.style.height=height+"px";
		
		var progressBar=f_progressBar.f_decorateInstance(div);
			
		progressBar.f_completeComponent();
		
		return progressBar;
	}
}

var __members = {
	f_progressBar: function() {
		this.f_super(arguments);

		if (this.nodeType==f_core.ELEMENT_NODE) {
			var smin=f_core.GetAttributeNS(this,"min");
			this._min=(smin)?parseFloat(smin):0;
			
			var smax=f_core.GetAttributeNS(this,"max");
			this._max=(smax)?parseFloat(smax):0;
	
			var svalue=f_core.GetAttributeNS(this,"value");
			this._value=(svalue)?parseFloat(svalue):0;
		}
				
		if (!this.childNodes.length) {
			this.f_fillBar();
		}
		
		this._updateBar();			
	},
	f_finalize: function() {
		// this._min=undefined; // number
		// this._max=undefined; // number
		// this._value=undefined; // number
		// this._step=undefined; // number
		this._images=undefined; // HtmlImageElement[]
		
		var timerId=this._overTimerId;
		if (timerId) {
			this._overTimerId=undefined;
			
			f_core.GetWindow(this).clearTimeout(timerId);
		}
		
		var timerId=this._overAnimTimerId;
		if (timerId) {
			this._overAnimTimerId=undefined;
			
			f_core.GetWindow(this).clearInterval(timerId);
		}
	
		this.f_super(arguments);
	},
	f_fillBar: function() {
		var width=parseInt(f_core.GetCurrentStyleProperty(this, "width"));
		var height=parseInt(f_core.GetCurrentStyleProperty(this, "height"));
		
		var borderSize=f_progressBar._BORDER_SIZE;
	
		var blankImageURL=f_env.GetBlankImageURL();
		
		var sts = [ "f_progressBar_bg", "f_progressBar_fg" ];
		
		var images=new Array;
		this._images=images;
		
		for(var i=0;i<sts.length;i++) {
			var st=sts[i];
		
			var bg=f_core.CreateElement(this, "div", {
				className: st,
				cssWidth: width,
				cssHeight: height
			});		
			
			var w1=borderSize;
			var w2=width-borderSize*2;
			var w3=borderSize;

			if (width<borderSize*2) {
				w1=Math.floor(width/2);
				w2=0;
				w3=width-w1;
			}
			
			images.push(
				f_core.CreateElement(bg, "img", {
					className: st+"Left",
					width: w1,
					cssWidth: w1,
					height: height,
					src: blankImageURL
				}),
			
				f_core.CreateElement(bg, "img", {
					className: st+"Center",
					width: w2,
					cssWidth: w2,
					height: height,
					src: blankImageURL
				}),
			
				f_core.CreateElement(bg, "img", {
					className: st+"Right",
					width: w3,
					cssWidth: w3,
					height: height,
					src: blankImageURL
				})
			);				
		}
		
					
		images.push(f_core.CreateElement(images[3].parentNode, "img", {
				className: "f_progressBar_anim",
				width: f_progressBar._ANIM_SIZE,
				cssWidth: f_progressBar._ANIM_SIZE,
				height: height,
				src: blankImageURL
			})
		);
	},
	/** 
	 * Returns the minimum value.
	 *
	 * @method public
	 * @return Number Minimum value
	 */
	f_getMin: function() {
		return this._min;
	},
	/** 
	 * Set the minimum value.
	 *
	 * @method public
	 * @param Number min The minimum value to set.
	 * @return void
	 */
	f_setMin: function(min) {
		f_core.Assert(typeof(min)=="number", "f_progressBar.f_setMin: Min parameter must be a number. ("+min+")");
		
		this._min=min;
		
		this.f_setProperty(f_prop.MIN, min);
		
		this._updateBar();
	},
	/** 
	 * Returns the maximum value.
	 *
	 * @method public
	 * @return Number Maximum value
	 */
	f_getMax: function() {
		return this._max;
	},
	/** 
	 * Set the maximum value.
	 *
	 * @method public
	 * @param Number max The maximum value to set.
	 * @return void
	 */
	f_setMax: function(max) {
		f_core.Assert(typeof(max)=="number", "f_progressBar.f_setMax: Max parameter must be a number. ("+max+")");
		
		this._max=max;
		this.f_setProperty(f_prop.MAX, max);
		
		this._updateBar();
	},
	/** 
	 * Returns the value of the progression.
	 *
	 * @method public
	 * @return Number Value of the progression
	 */
	f_getValue: function() {
		return this._value;
	},
	/** 
	 * Set the value of the progression.
	 *
	 * @method public
	 * @param Number value The value of the progression.
	 * @return void
	 */
	f_setValue: function(value) {
		f_core.Assert(typeof(value)=="number", "f_progressBar.f_setValue: Value parameter must be a number. ("+value+")");
		this._value=value;

		this.f_setProperty(f_prop.VALUE, value);
		
		this._updateBar();
	},
	/** 
	 * Set the indeterminate state.
	 *
	 * @method public
	 * @param Boolean indeterminate State of indeterminate.
	 * @return void
	 */
	f_setIndeterminate: function(indeterminate) {
		f_core.Assert(typeof(indeterminate)=="boolean", "f_progressBar.f_setIndeterminate: Invalid indeterminate parameter '"+indeterminate+"'.");  

		this._indeterminate=indeterminate;
	},
	/** 
	 * Returns the indeterminate state.
	 *
	 * @method public
	 * @return Boolean State of indeterminate.
	 */
	f_isIndeterminate: function() {
		return this._indeterminate;
	},
	/**
	 * @method private
	 * @return void
	 */
	_updateBar: function() {
		var min=this._min;
		var max=this._max;
		var images=this._images;
	
		if (!images) {
			return;
		}
	
		var parentNode=images[3].parentNode;
		var parentWidth=parentNode.parentNode.offsetWidth-2; // Pour les bords
		
		f_core.Debug(f_progressBar, "_updateBar: min="+min+" max="+max+" value="+this._value+" parentWidth="+parentWidth);
		if (parentWidth<1) {
			return;
		}
		
		var cursorStyle=parentNode.style;
		var value=this._value;

		if (min>=max || value<=min) {
			if (cursorStyle.visibility!="hidden") {
				cursorStyle.visibility="hidden";
			}
			var timerId=this._overTimerId;
			if (timerId) {
				this._overTimerId=undefined;
				
				f_core.GetWindow(this).clearTimeout(timerId);
			}
			var timerId=this._overAnimTimerId;
			if (timerId) {
				this._overAnimTimerId=undefined;
				
				f_core.GetWindow(this).clearInterval(timerId);
			}
			return;
		}
		
		if (value>max) {
			value=max;
		}
		
		var width=Math.floor((value-min)/(max-min)*parentWidth);
		
		var step=this._step;
		if (step && value<max) {
			width-=(width % step);
		}
	
		images[3].parentNode.style.width=width+"px";
	
		var borderSize=f_progressBar._BORDER_SIZE;
		if (width<borderSize*2) {
			var w=Math.floor(width/2);
			width-=w;
			
			images[3].style.width=w+"px";
			images[4].style.width="0";
			images[5].style.width=width+"px";
		} else {
			width-=borderSize*2;
			
			images[3].style.width=borderSize+"px";
			images[4].style.width=width+"px";
			images[5].style.width=borderSize+"px";	
			
			if (f_core.IsGecko()) {
				// Pour une fois, un gros hack pour un BUG de maj d'affichage de Gecko
				images[5].style.position="absolute";
				images[5].style.left=(borderSize+width)+"px";	
			}
		}
		
		if (cursorStyle.visibility!="inherit") {
			cursorStyle.visibility="inherit";
		}
	
		var timerId=this._overTimerId;
		if (this.fa_componentUpdated && !timerId && !this._overAnimTimerId) {
			var self=this;
			
			this._overTimerId=f_core.GetWindow(this).setTimeout(function() {
				self._startAnimation();
				
			}, f_progressBar._ANIMATION_DELAY);
		}	
	},
	_startAnimation: function() {
		this._overTimerId=undefined;
		
		if (!this._images) {
			return;
		}
		var animImg=this._images[6];
		animImg.style.left=(-f_progressBar._ANIM_SIZE)+"px";
		animImg.style.visibility="inherit";
	
		if (this._overAnimTimerId) {
			f_core.GetWindow(this).clearInterval(this._overAnimTimerId);
		}
	
		var self=this;
		this._overAnimTimerId=f_core.GetWindow(this).setInterval(function() {			
			self._nextAnimation();
		}, f_progressBar._NEXT_ANIMATION_DELAY);
	},
	_nextAnimation: function() {
		if (!this._images) {
			var timerId=this._overAnimTimerId;
			if (timerId) {
				this._overAnimTimerId=undefined;

				f_core.GetWindow(this).clearInterval(timerId);
			}
			return;
		}

		var animImg=this._images[6];
		var pos=parseInt(animImg.style.left)+14; //Math.floor(f_progressBar._ANIM_SIZE/2);

		var parentNode=animImg.parentNode;
		var parentWidth=parentNode.offsetWidth; // Pour les bords
		if (f_core.IsInternetExplorer()) {
			parentWidth-=f_core.ComputeBorderLength(parentNode, "left", "right");	
		}		
		
		if (pos>parentWidth) {
			var timerId=this._overAnimTimerId;
			if (timerId) {
				this._overAnimTimerId=undefined;

				f_core.GetWindow(this).clearInterval(timerId);
			}
			animImg.style.visibility="hidden";
		
			if (false) {	
				var self=this;
				this._overTimerId=f_core.GetWindow(this).setTimeout(function() {
					
					self._startAnimation();
					
				}, f_progressBar._ANIMATION_DELAY);
			}			
			return;
		}	

		animImg.style.left=pos+"px";
	}
}
 
new f_class("f_progressBar", {
	extend: f_component,
	members: __members,
	statics: __statics
});