/*
 * $Id: f_toolBar.js,v 1.3 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * Classe ToolBar.
 *
 * @class f_toolBar extends f_component
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:28 $
 */
 
var __statics = {
}

var __members = {
	/**
	 * @field private HTMLElement[]
	 */
	_separators: undefined,
	
	f_finalize: function() {
		this._separators=undefined;  // HtmlLiElement[]
		
		this.f_super(arguments);
	},
	f_update: function() {
		this.f_super(arguments);

		this._hideSeparators();
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_documentComplete: function() {
		this.f_super(arguments);
		
		this._hideSeparators();
	},
	/**
	 * @method private
	 * @return HTMLElement[]
	 */
	_listSeparators: function() {
		var separators=this._separators;
		if (separators) {
			return separators;			
		}
		
		separators=new Array;
		this._separators=separators;
		
		var lis=this.getElementsByTagName("li");
		for(var i=0;i<lis.length;i++) {
			var li=lis[i];
			
			if (!f_core.GetAttributeNS(li,"separator")) {
				continue;
			}
			
			separators.push(li);
		}
		
		return separators;
	},
	/**
	 * @method private
	 * @return void
	 */
	_hideSeparators: function() {
		var separators=this._listSeparators();
		
		for(var i=0;i<separators.length;i++) {
			var separator=separators[i];
			var style=separator.style;
			
			var next=separator.nextSibling;
			if (!next) {
				// Dernier item, on le cache
				
				style.display="none";
				continue;
			}
			var prev=separator.previousSibling;
			if (!prev) {
				// Premier item
				//style.visibilty="hidden";
				style.display="none";
				continue;
			}
			if (separators.f_contains(prev)) {
				// Deux séparateurs d'affilé !
				style.display="none";
				continue;			
			}
			
			var stepHeight=(separator.offsetHeight+prev.offsetHeight+next.offsetHeight)/3;
			
			var offsetTop=separator.offsetTop;
			var prevOffsetTop=prev.offsetTop-offsetTop;
			if (prevOffsetTop>0) {
				prevOffsetTop=0;
			} else {
				prevOffsetTop-=(prevOffsetTop % (stepHeight/2));
			}
			
			var nextOffsetTop=next.offsetTop-offsetTop;
			if (nextOffsetTop<0) {
				nextOffsetTop=0;
			} else {
				nextOffsetTop-=(nextOffsetTop % (stepHeight/2));				
			}
			
			f_core.Debug(f_toolBar, "_hideSeparators: prev="+prev+"["+prevOffsetTop+"] next="+next+"["+nextOffsetTop+"] separator["+offsetTop+"] step="+stepHeight);
			
			if (prevOffsetTop<0) {
				// On est passé à la ligne !
				//style.visibility="hidden";
				style.display="none";
				continue;				
			}
			
			if (nextOffsetTop>0) {
				// C'est le dernier de la ligne
				style.display="none";
				continue;				
			}
			
			//style.visibility="visible";
			style.display="block";
		}
	}}
 
new f_class("f_toolBar", {
	extend: f_component,
	statics: __statics,
	members: __members
});