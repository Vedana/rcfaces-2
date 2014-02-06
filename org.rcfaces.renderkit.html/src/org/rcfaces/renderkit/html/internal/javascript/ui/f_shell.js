/*
 * $Id: f_shell.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * <p><strong>f_shell</strong> represents popup window.
 *
 * @class public final f_shell extends f_object, fa_eventTarget
 * @author Fred Lefevere-Laoide (latest modification by $Author: jbmeslin $)
 * @author Olivier Oeuillot
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
var __statics = {

	/**
	 * @field private static final
	 */
	_EVENTS: {
			init: f_event.INIT,
			close: f_event.CLOSE,
			user: f_event.USER
	},

	/**
	 * @field public static final String
	 */
	CLOSE_BUTTON_EVENT: "closeButton",
	
	/**
	 * @field public static final String
	 */
	GREYED_BACKGROUND_MODE: "greyed",
	
	/**
	 * @field public static final String
	 */
	LIGHT_GREYED_BACKGROUND_MODE: "light",
	
	/**
	 * @field public static final String
	 */
	TRANSPARENT_BACKGROUND_MODE: "transparent",
	
	/**
	 * @field public static final String
	 */
	OPAQUE_BACKGROUND_MODE: "opaque",

	/**
	 * Style constant for resize area trim
	 * 
	 * @field public static final Number
	 */
	MOVE_STYLE: 1<<3,
	
	/**
	 * Style constant for resize area trim
	 * 
	 * @field public static final Number
	 */
	RESIZE_STYLE: 1<<4,
	
	/**
	 * Style constant for title area trim
	 * 
	 * @field public static final Number
	 */
	TITLE_STYLE: 1<<5,
	
	/**
	 * Style constant for close box trim
	 * 
	 * @field public static final Number
	 */
	CLOSE_STYLE: 1<<6,
	
	/**
	 * Style constant for minimize box trim
	 * 
	 * @field public static final Number
	 */
	MIN_STYLE: 1<<7,
	
	/**
	 * Style constant for horizontal scrollbar behavior
	 * 
	 * @field public static final Number
	 */
	H_SCROLL_STYLE: 1<<8,
	
	/**
	 * Style constant for vertical scrollbar behavior
	 * 
	 * @field public static final Number
	 */
	V_SCROLL_STYLE: 1<<9,
	
	/**
	 * Style constant for maximize box trim
	 * 
	 * @field public static final Number
	 */
	MAX_STYLE: 1<<10,
	
	/**
	 * Style constant for hide background
	 * 
	 * @field public static final Number
	 */
	HIDE_SCREEN_STYLE: 1<<11,
	
	/**
	 * Copy styleSheet from the main frame
	 * 
	 * @field public static final Number
	 */
	COPY_STYLESHEET: 1<<12,
	
	/**
	 * Force the body of the shell to be an IFrame
	 * 
	 * @field public static final Number
	 */
	FRAME_ELEMENT: 1<<13,
	
	/**
	 * The frame can be transparent
	 * 
	 * @field public static final Number
	 */
	TRANSPARENT: 1<<14,
	
	/**
	 * Style constant for modeless behavior
	 * 
	 * @field public static final Number
	 */
	MODELESS_STYLE: 0,
	
	/**
	 * Style constant for primary modal behavior
	 * 
	 * @field public static final Number
	 */
	PRIMARY_MODAL_STYLE: 1<<15,
	
	/**
	 * Style constant for application modal behavior
	 * 
	 * @field public static final Number
	 */
	APPLICATION_MODAL_STYLE: 1<<16,
	
	/**
	 * Style constant 
	 * 
	 * @field public static final Number
	 */
	LIGHT_CONTAINER_STYLE: 1<<30,
	
	/**
	 * @field private static final Number
	 */	
	_DEFAULT_HEIGHT: 100,
	
	/**
	 * @field private static final Number
	 */	
	_DEFAULT_WIDTH: 300,
	
	/**
	 * @field private static Number
	 */
	_ID: 0,
	
	/**
	 * @field public static final Number
	 */
	CREATED_STATUS: 0x00,
	
	/**
	 * @field public static final Number
	 */
	OPENING_STATUS: 0x10,
	
	/**
	 * @field public static final Number
	 */
	OPENED_STATUS: 0x12,
	
	/**
	 * @field public static final Number
	 */	
	CLOSING_STATUS: 0x20,
	
	/**
	 * @field public static final Number
	 */	
	ABOUT_TO_CLOSE_STATUS: 0x21,
	
	
	/**
	 * @field public static final Number
	 */
	CLOSED_STATUS: 0x24,
	
	/**
	 * @field public static final Number
	 */
	DESTROYING_STATUS: 0x30,
	
	/**
	 * @field public static final Number
	 */
	DESTROYED_STATUS: 0x31
};

var __members = {

	/**
	 * @field private String
	 */
	_id: undefined,

	/**
	 * @field private Number
	 */
	_style: undefined,
	
	/**
	 * @field private String
	 */
	_backgroundMode: undefined,

	/**
	 * @field private Number
	 */
	_height: undefined,
	
	/**
	 * @field private Number
	 */
	_width: undefined,
	
	/**
	 * @field private Boolean
	 */
	_closable: undefined,
	
	/**
	 * @field private Number
	 */
	_priority: 0,
	
	
	/**
	 * @field private Number
	 */
	_shellStatus: 0,
	
	/**
	 * @field hidden Boolean
	 */
	_showNextShell: true,

	/**
	 * @field private any
	 */
	_returnValue: undefined,
	
	/**
	 * @field protected String
	 */
	_title: undefined,
	
	/**
	 * @field protected String
	 */
	_shellDecoratorName: undefined,
	
	/**
	 * @field protected String
	 */
	_returnFocusClientId: undefined,
	
	/**
	 * <p>Construct a new <code>f_shell</code> with the specified
     * initial values.</p>
	 *
	 * @method public
	 * @param optional Number style the style of control to construct
	 * @param optional Function drawingFunction
	 * @param optional Function returnValueFunction
	 */
	f_shell: function(style, drawingFunction, returnValueFunction) {
		this.f_super(arguments);

		f_core.Assert(style===undefined || typeof(style)=="number", "f_shell.f_shell: Invalid style parameter ("+style+")");
		f_core.Assert(drawingFunction===undefined || typeof(drawingFunction) == "function", "f_shell.f_shell: bad parameter type: drawingFunction is not a function "+drawingFunction);
		
		this._id="shell_"+(f_shell._ID++);
		
		this._style=(style)?style:0;
		this._backgroundMode=(this._style & f_shell.HIDE_BACKGROUND_STYLE)?f_shell.GREYED_BACKGROUND_MODE:null;
		this._drawingFunction=drawingFunction;
		this._returnValueFunction=returnValueFunction;
		this._width=f_shell._DEFAULT_WIDTH;
		this._height=f_shell._DEFAULT_HEIGHT;
		
		this._shellManager=f_shellManager.Get();
		
		if (this.nodeType==f_core.ELEMENT_NODE) {			
			var events=f_core.GetAttributeNS(this,"events");
			if (events) {
				this.f_initEventAtts(f_shell._EVENTS, events);
			}

			var shellDecoratorName = f_core.GetAttributeNS(this,"shellDecorator");
			if (shellDecoratorName) {
				this._shellDecoratorName = shellDecoratorName;
			}
			
			var closable = f_core.GetBooleanAttributeNS(this,"closable");
			if (closable === true) {
				this._style |= f_shell.CLOSE_STYLE;
			}
			
			this._returnFocusClientId = f_core.GetAttributeNS(this,"returnFocusClientId");
		}
		
	},


	/**
	 * <p>Destruct a <code>f_shell</code>.</p>
	 *
	 * @method public
	 * @return void
	 */
	f_finalize: function() {
		// this._title=undefined; // String
		// this._shellDecoratorName=undefined; // String

		var shellStatus=this._shellStatus;
		if (shellStatus!=f_shell.CREATED_STATUS &&
			shellStatus!=f_shell.DESTROYED_STATUS) {

			if (shellStatus<f_shell.CLOSED_STATUS) {
				this.f_preDestruction();
			}

			this._shellStatus=f_shell.DESTROYING_STATUS;

			this.f_postDestruction();
			
			this._shellStatus=f_shell.DESTROYED_STATUS;			
		}
		
		// this._backgroundMode=undefined; //string
		// this._imageURL=undefined; //string
		// this._style=undefined; //number
		//this._height=undefined; // number
		//this._width=undefined; // number
		// this._styleClass=undefined; // string
		
		this._drawingFunction=undefined; // function
		this._returnValueFunction=undefined; // function
		this._returnValue=undefined; // any

		this._shellManager=undefined; // f_shellManager

		this._shellBody=undefined; // HtmlElement

		this.f_super(arguments);
	},

	/**
	 * @method public
	 * @return String
	 */
	f_getId: function() {
		return this._id;
	},

	/**
	 *  <p>Return the priority.</p>
	 *
	 * @method public 
	 * @return Number priority
	 */
	f_getPriority: function() {
		return this._priority;
	},
	
	/**
	 *  <p>Sets the priority.</p>
	 *
	 * @method public 
	 * @param Number priority
	 * @return void
	 */
	f_setPriority: function(priority) {
    	f_core.Assert(typeof(priority)=="number", "f_shell.f_setPriority: Invalid priority parameter '"+priority+"'."+typeof(priority));

		this._priority = priority;
	},

	/**
	 *  <p>Return the style of the shell.</p>
	 *
	 * @method public 
	 * @return Number The style
	 */
	f_getStyle: function() {
		return this._style;
	},
	
	/**
	 *  <p>Return the background mode.</p>
	 *
	 * @method public 
	 * @return String background mode : transparent, greyed, opaque
	 */
	f_getBackgroundMode: function() {
		return this._backgroundMode;
	},
	
	/**
	 *  <p>Sets the background mode.</p>
	 *
	 * @method public 
	 * @param String backgroundMode background mode : transparent, greyed, opaque
	 * @return void
	 */
	f_setBackgroundMode: function(backgroundMode) {
    	f_core.Assert(typeof(backgroundMode)=="string", "f_shell.f_setBackgroundMode: Invalid parameter '"+backgroundMode+"'.");

		this._backgroundMode = backgroundMode;
	},
	

	/**
	 *  <p>Return the height.</p>
	 *
	 * @method public 
	 * @return String height
	 */
	f_getHeight: function() {
		return this._height;
	},
	/**
	 *  <p>Sets Height.</p>
	 *
	 * @method public 
	 * @param Number height
	 * @return void
	 */
	f_setHeight: function(height) {
    	f_core.Assert(typeof(height)=="number", "f_shell.f_setHeight: Invalid height parameter '"+height+"'.");

		this._height = height;
	},
	
	/**
	 *  <p>Return the width.</p>
	 *
	 * @method public 
	 * @return Number width
	 */
	f_getWidth: function() {
		return this._width;
	},
	/**
	 *  <p>Sets width.</p>
	 *
	 * @method public 
	 * @param Number width
	 * @return void
	 */
	f_setWidth: function(width) {
    	f_core.Assert(typeof(width)=="number", "f_shell.f_setWidth: Invalid width parameter '"+width+"'.");

		this._width = width;
	},

	/**
	 *  <p>decorate the iframe
	 *  </p>
	 *
	 * @method protected
	 * @param HTMLElement iframe
	 * @return void
	 */
	f_prepareOpening: function() {
     	f_core.Debug(f_shell, "f_prepareOpening: entering");
		
		var width=this.f_getWidth();
		if (!width || width<1) {  // Il faut traiter undefined
			width=f_shell._DEFAULT_WIDTH;
			this.f_setWidth(width);
		}

		var height=this.f_getHeight();
		if (!height || height<1) {
			height=f_shell._DEFAULT_HEIGHT;
			this.f_setHeight(height);
		}
		
		var shellDecorator=this._shellManager.f_getShellDecorator(this);
		var mySize=shellDecorator.f_computeTrim(width, height);
		
		// calculate iframe size and position
		var viewSize=f_core.GetViewSize();
	
		var x=0;
		if (viewSize.width > mySize.width) {
			x = Math.round((viewSize.width - mySize.width)/2);
			
		} else {
			mySize.width = viewSize.width;
		}
		
		var y=0;
		if (viewSize.height > mySize.height) {
			y = Math.round((viewSize.height - mySize.height)/2);
			
		} else {
			mySize.height = viewSize.height;
		}
		
		var scrolls=f_core.GetScrollOffsets();		
		x+=scrolls.x;
		y+=scrolls.y;
			
		this._shellManager.f_setShellBounds(this, x, y, mySize.width, mySize.height);
	},
	
	/**
	 *  <p>Set the body of the shell
	 *  </p>
	 *
	 * @method protected
	 * @param HTMLElement shellBody
	 * @return void
	 */
	f_setBody: function(shellBody) {
     	f_core.Debug(f_shell, "f_setBody: fill body in "+shellBody);		
     	
     	this._shellBody=shellBody;
	},
	/**
	 *  <p>construct the content of the shell 
	 *  </p>
	 *
	 * @method protected
	 * @param HTMLElement shellBody
	 * @return void
	 */
	f_fillBody: function(shellBody) {
     	f_core.Debug(f_shell, "f_fillBody: fill body in "+shellBody);		
     	
     	this.f_setBody(shellBody);
	},

	/**
	 *  <p>delete the content of the shell. </p>
	 *
	 * @method protected
	 * @return void
	 */
	f_deleteBody: function() {
     	f_core.Debug(f_shell, "f_deleteBody: entering");
     	
     	this._shellBody=null;
	},	
	
	/**
	 * @method protected
	 * @param Boolean firstTime
	 * @return void
	 */
	f_setFocus: function(firstTime) {
		f_core.Debug(f_shell, "f_setFocus: entering with firstTime = "+firstTime);
		if (!this._shellBody) {
			f_core.Debug(f_shell, "f_setFocus: Doc is not complete yet");
			return;
		}
		
		var nextFocusable=null;
		if (firstTime) {
			var inputs=f_core.GetElementsByTagName(this._shellBody, "input");
			
			for(var i=0;i<inputs.length;i++) {
				var input=inputs[i];
			    		
				if (!input.type) {
					continue;
				}
				
				if (input.type.toLowerCase()!="submit") {
					continue;
				}
				
				nextFocusable=input;
				break;
			}
		}

  		if (!nextFocusable) {
   			nextFocusable=f_core.GetNextFocusableComponent(this._shellBody);
  		}
  		
   		if (nextFocusable) {
     		f_core.Debug(f_shell, "f_setFocus: Set focus on "+nextFocusable.outerHTML);

	     	f_core.SetFocus(nextFocusable, false);
   		}
	},

	/**
	 * @method public
	 * @param optional Function returnValueFunction
	 * @param optional String returnFocusClientId
	 * @return void
	 */
	f_open: function(returnValueFunction, returnFocusClientId) {
		f_core.Assert(
			this.f_getStatus()!=f_shell.OPENING_STATUS &&
			this.f_getStatus()!=f_shell.OPENED_STATUS &&
			this.f_getStatus()!=f_shell.CLOSING_STATUS, "f_open: Invalid shell state ! ("+this._status+")");
		
		if (returnValueFunction!==undefined) {
			this._returnValueFunction=returnValueFunction;
		}
		
		if (returnFocusClientId!==undefined) {
			this._returnFocusClientId=returnFocusClientId;
		}
		
		this._shellManager.f_openShell(this);
	},	

	/**
	 * @method public
	 * @param optional any returnValue
	 * @return void
	 */
	f_close: function(returnValue) {
		f_core.Debug(f_shell, "f_close: Request shell close '"+this+"'  returnValue='"+returnValue+"'");
		
		if (this.f_getStatus()!=f_shell.OPENED_STATUS) {
			return;
		}
		
		this._returnValue=returnValue;

		this.f_setStatus(f_shell.CLOSING_STATUS);
		
		var self=this;
		
		var shellManager=this._shellManager;
		
		// On découple la destruction ... pour éviter des problèmes de sécurité !
		window.setTimeout(function() {
	
			if (window._rcfacesExiting) {
				return false;
			}
		
			try {
				shellManager.f_closeShell(self);
				
			} catch (ex) {
				f_core.Error(f_shell, "f_close.timer: Can not close '"+self+"'", ex);
				
			} finally {
				shellManager=null;
			}
		}, 0);
	},
	f_preConstruct: function() {
	},
	f_postConstruct: function() {		
	},
	f_preDestruction: function() {
		this.f_setStatus(f_shell.ABOUT_TO_CLOSE_STATUS);
	},
	f_postDestruction: function() {
		this.f_setStatus(f_shell.DESTROYED_STATUS);
		
		var returnValue=this._returnValue;
			
		//function(type, jsEvt, item, value, selectionProvider, detail) 
		this.f_fireEvent(f_event.CLOSE, null, null, returnValue, null, null);

		var returnValueFunction=this._returnValueFunction;
		if (typeof(returnValueFunction)=="function") {
			try {
				if (returnValueFunction.call(this, returnValue)==false) {
					this.f_cancelNextShell();
				}
	
			} catch (x) {
				f_core.Error(f_shell, "f_shell.f_close: Exception when calling return value '"+returnValue+"'.", x);			
			}
			
		} else if (returnValue===false) {
			this.f_cancelNextShell();
		}
		
		var returnFocusClientId=this._returnFocusClientId;
		if (returnFocusClientId && this._showNextShell!==false) {
			var elt=f_core.GetElementByClientId(returnFocusClientId);
			
			if (elt) {
				f_core.SetFocus(elt, true);
			}
		}
	},
	
	/**
	 * @method public
	 * @return void
	 */
	f_cancelNextShell: function() {
		this._showNextShell=false;
	},
	
	/**
	 * @method public
	 * @return Boolean
	 */
	f_isNextShellCanceled: function() {
		return this._showNextShell;
	},
	
	/**
	 * @method public
	 * @return String
	 */
	f_getTitle: function(title) {
		return this._title ;
	},
	
	/**
	 * @method public
	 * @param String title
	 * @return void
	 */
	f_setTitle: function(title) {
		f_core.Assert(title===null || typeof(title)=="string", "f_shell.f_setTitle: Invalid title parameter ('"+title+"')");

		if (title==this._title) {
			return;
		}
		
		var styleChanged=false;
		if (title && !(this._style & f_shell.TITLE_STYLE)) {
			this._style|=f_shell.TITLE_STYLE;
			styleChanged=true;
		}
		
		this._title = title;
		
		var shellManager = this.f_getShellManager();
		
		if (shellManager.f_hasShellDecorator(this)==false) {
			return;
		}
		
		if (styleChanged) {
			shellManager.f_setShellDecoration(this, f_shellDecorator.STYLE_DECORATOR, this._style);
		}
		shellManager.f_setShellDecoration(this, f_shellDecorator.TITLE_DECORATOR, title);
	},
	/**
	 * @method hidden
	 * @param f_shellDecorator shellDecorator
	 * @return void 
	 */
	f_updateDecoration: function(shellDecorator){
		var title = this.f_getTitle();
		if (title) {
			shellDecorator.f_setDecorationValue(f_shellDecorator.TITLE_DECORATOR, title);
		}
	},
	/**
	 * @method protected
	 * @return f_shellDecorator shellDecorator
	 */
	f_getShellDecorator: function() {
		
		var shellDecorator = this._shellManager.f_getShellDecorator(this);
		
		return shellDecorator;
	},
	/**
	 * @method public
	 * @param f_shellDecorator shellDecorator
	 * @return void
	 */
	f_setShellDecorator: function(shellDecorator) {
		f_core.Assert(typeof(shellDecorator)=="object", "f_shell.f_setShellDecorator: Invalid shellDecorator parameter ('"+shellDecorator+"')");
		
		this._shellManager.f_setShellDecorator(this, shellDecorator);
	},
	/**
	 * @method protected
	 * @return f_shellManager 
	 */
	f_getShellManager: function() {
		return this._shellManager;
	},
	/**
	 * @method public
	 * @return String
	 */
	f_getStyleClass: function() {
		return this._styleClass;
	},
	/**
	 * @method public
	 * @param String styleClass
	 * @return void
	 */
	f_setStyleClass: function(styleClass) {
		f_core.Assert(styleClass===null || typeof(styleClass)=="string", "f_shell.f_setStyleClass: Invalid styleClass parameter ("+styleClass+")");

		this._styleClass=styleClass;
	},
	/**
	 * @method public
	 * @return Number
	 */
	f_getStatus: function() {
		return this._shellStatus;
	},
	/**
	 * @method hidden
	 * @param Number status
	 * @return void
	 */
	f_setStatus: function(status) {
		f_core.Assert(typeof(status)=="number", "f_shell.f_setStatus: Invalid status parameter ("+status+")");

		this._shellStatus=status;
	},
	
	/**
	 *  <p>Return the shellDecoratorName String.</p>
	 *
	 * @method public 
	 * @return String shellDecorateurName
	 */
	f_getShellDecoratorName: function() {
		return this._shellDecoratorName;
	},
	
	/**
	 *  <p>Sets the shellDecoratorName String.</p>
	 *
	 * @method public 
	 * @param String shellDecoratorName
	 * @return void
	 */
	f_setShellDecoratorName: function(shellDecoratorName) {
    	f_core.Assert((typeof(shellDecoratorName)=="string"), "f_shell.f_setShellDecoratorName: Invalid parameter '"+shellDecoratorName+"'.");
    	
		this._shellDecoratorName = shellDecoratorName;
		
	},
	/**
	 * @method public
	 * @param String id Identifier of component.
	 * @return HTMLElement
	 */
	f_findComponent: function(id) {
		return fa_namingContainer.FindComponents(this, arguments);
	},
	/**
	 * @method public
	 * @param String id Identifier of component.
	 * @return HTMLElement
	 */
	f_findSiblingComponent: function(id) {
		return fa_namingContainer.FindSiblingComponents(this, arguments);
	},
	/**
	 * Specify the clientId of a component which will get focus when the shell will be closed
	 * 
	 * @method public
	 * @param String clientId Identifier of component.
	 * @return void
	 */
	f_setReturnFocusClientId: function(clientId) {
    	f_core.Assert(clientId===null || clientId===undefined || typeof(clientId)=="string", "f_shell.f_setReturnFocusClientId: Invalid clientId parameter '"+backgroundMode+"'.");
    	
    	this._returnFocusClientId=clientId;
	},
	/**
	 * @method public
	 * @return String Id Identifier of component which will get focus when the shell will be closed
	 */
	f_getReturnFocusClientId: function() {
     	return this._returnFocusClientId;
	},
	/**
	 * @method public
	 * @return String
	 */
	_toString: function() {
		return "[f_shell id="+this._id+" styleClass='"+this._styleClass+"']";
	}
};

new f_class("f_shell", {
	extend: f_object,
	aspects: [ fa_eventTarget ],
	statics: __statics,
	members: __members
});
