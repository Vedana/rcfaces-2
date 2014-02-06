/*
 * $Id: f_dialog.js,v 1.3 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * <p><strong>f_dialog</strong> represents popup modal window.
 *
 * @class public final f_dialog extends f_shell
 * @author Fred Lefevere-Laoide Lefevere-Laoide (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:27 $
 */

var __statics = {
	
	/**
	 * @field private static final
	 */
	_DEFAULT_FEATURES: {
		priority: 0,
		styleClass: "f_dialog"
	}

};

var __members = {

	/**
	 * <p>Construct a new <code>f_dialog</code> with the specified
     * initial values.</p>
	 *
	 * @method public
	 * @param Number style the style of control to construct
	 */
	f_dialog: function(style) {
		this.f_super(arguments, style | f_shell.COPY_STYLESHEET);

		var defaultFeatures=this.f_getDefaultFeatures();

		var title=null;
		var h=-1;
		var w=-1;
		var p=0;
		var styleClass=null;
		var backgroundMode=null;
		
		if (this.nodeType==f_core.ELEMENT_NODE) {
			title = f_core.GetAttributeNS(this,"title", null);
			h=f_core.GetNumberAttributeNS(this,"height", -1);
			w=f_core.GetNumberAttributeNS(this,"width", -1);
			p=f_core.GetNumberAttributeNS(this,"dialogPriority", 0);
			styleClass = f_core.GetAttributeNS(this,"styleClass", null);
			backgroundMode = f_core.GetAttributeNS(this,"backgroundMode", null);
		}
		
		var shellDecorator=defaultFeatures.shellDecorator;
		if (shellDecorator) {
			this.f_setShellDecorator(shellDecorator);
		}
		
		if (title===null) {
			title=defaultFeatures.title;
		}
		if (title) {
			this.f_setTitle(title);
		}

		if (h<0 && defaultFeatures.height>0) {
			h=defaultFeatures.height;
		}
		if (h>0) {
			this.f_setHeight(h);
		}

		if (w<0 && defaultFeatures.width>0) {
			w=defaultFeatures.width;
		}
		if (w>0) {
			this.f_setWidth(w);
		}

		if (p<0 && defaultFeatures.dialogPriority>0) {
			p=defaultFeatures.dialogPriority;			
		}
		if (p>0) {
			this.f_setPriority(p);
		}
 			
		if (styleClass===null) {
			styleClass=defaultFeatures.styleClass;
		}
		if (styleClass) {
			this.f_setStyleClass(styleClass);
		}
			
		if (backgroundMode===null && defaultFeatures.backgroundMode) {
			backgroundMode=defaultFeatures.backgroundMode;
		}
		if (backgroundMode) {
			this.f_setBackgroundMode(backgroundMode);
		}
	},

	/*
	 * <p>Destruct a new <code>f_dialog</code>.</p>
	 *
	 * @method public
	 *
	f_finalize: function() {
		this.f_super(arguments);
	},
	*/

	f_getDefaultFeatures: function() {
		return f_dialog._DEFAULT_FEATURES;
	}
};

new f_class("f_dialog", {
	extend: f_shell,
	members: __members,
	statics: __statics
});

