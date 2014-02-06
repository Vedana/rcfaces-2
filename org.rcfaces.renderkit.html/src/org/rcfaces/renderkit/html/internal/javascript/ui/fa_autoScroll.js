/*
 * $Id: fa_autoScroll.js,v 1.2 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * Aspect
 *
 * @aspect public abstract fa_autoScroll
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:27 $
 */

var __statics = {
	/**
	 * @field protected static final Number
	 */
	AUTO_SCROLL_SIZE: 20,
	
	/**
	 * @field private static final Number
	 */
	_AUTO_SCROLL_DELAY: 200

};

var __members = {

	f_finalize: function() {
		this.fa_uninstallAutoScroll();
	},
	
	/**
	 * @method protected
	 * @return void 
	 */
	fa_installAutoScroll: function() {
	
		// Scrollable ... il faut surveiller le haut et le bas !
		
		var autoScrollIntervalId=this._autoScrollIntervalId;
		if (autoScrollIntervalId) {
			return;
		}

		var scrollableComponent=this.fa_getScrollableContainer();
		if (!scrollableComponent) {
			return;
		}
		
		if (!this.fa_isContainerAutoScroll(scrollableComponent)) {
			return;
		}

		var componentPosition=f_core.GetAbsolutePosition(scrollableComponent);
	
		var self=this;

		this._autoScrollIntervalId=f_core.GetWindow(scrollableComponent).setInterval(function() {
			if (window._rcfacesExiting) {
				return false;
			}
			
			var mousePosition=self.fa_getLastMousePosition();
			if (!mousePosition) {
//				document.title="Mouse position=null";
				return;
			}

			try {
				self.fa_updateAutoScroll(scrollableComponent, componentPosition, mousePosition);

			} catch (x) {
				f_core.Error(fa_autoScroll, "fa_installAutoScroll.interval: throws exception", x);
			}
			
		}, fa_autoScroll._AUTO_SCROLL_DELAY);	
	},
	
	/**
	 * @method protected
	 * @return void 
	 */
	fa_uninstallAutoScroll: function() {
		
		var scrollIntervalId=this._autoScrollIntervalId;
		if (!scrollIntervalId) {
			return;
		}
		
		this._autoScrollIntervalId=undefined;

//			document.title="Clear auto scroll";

		var scrollableComponent=this.fa_getScrollableContainer();
		
		f_core.GetWindow(scrollableComponent).clearInterval(scrollIntervalId);
	},
	

	/**
	 * @method protected
	 * @return Boolean
	 */
	fa_isContainerAutoScroll: function(scrollableComponent) {
		
		if (scrollableComponent.offsetWidth-scrollableComponent.clientWidth<2) { // @TODO Retire les BORDS
			return false;
		}
		
		return true;
	},
	
	/**
	 * @method protected
	 * @param HTMLElement scrollableComponent
	 * @param Object componentPosition Component position
	 * @param Object mousePosition Mouse position
	 * @return void
	 */
	fa_updateAutoScroll: function(scrollableComponent, componentPosition, mousePosition) {
	
		var dy=mousePosition.y-componentPosition.y;
		var dy2=componentPosition.y+scrollableComponent.offsetHeight-mousePosition.y;
		
		if (scrollableComponent.offsetWidth-scrollableComponent.clientWidth>2) {
			// Ajout du scroll horizontal en bas ...
			
			dy2-=(scrollableComponent.offsetHeight-scrollableComponent.clientHeight);
		}
		
//		document.title="dy2="+dy2+"  dd="+(scrollableComponent.offsetHeight-scrollableComponent.clientHeight+" td="+new Date().getTime());
		
		if (dy>=0 && dy<=fa_autoScroll.AUTO_SCROLL_SIZE) {
			var st=scrollableComponent.scrollTop;
			if (st>0) {
				st-=fa_autoScroll.AUTO_SCROLL_SIZE;
				if (st<0) {
					st=0;
				}
				
				scrollableComponent.scrollTop=st;
							
				if (this.fa_autoScrollPerformed) {
					this.fa_autoScrollPerformed();
				}
			}
			
		} else if (dy2>=0 && dy2<=fa_autoScroll.AUTO_SCROLL_SIZE) {
			var st=scrollableComponent.scrollTop;
			st+=fa_autoScroll.AUTO_SCROLL_SIZE;
			if (st<0) {
				st=0;
			}
			
			scrollableComponent.scrollTop=st;
			
			if (this.fa_autoScrollPerformed) {
				this.fa_autoScrollPerformed();
			}
		}
	},
	
	/**
	 * @method protected
	 * @return Object 
	 */
	fa_getLastMousePosition: f_class.ABSTRACT,
	
	/**
	 * @method protected
	 * @return HTMLElement 
	 */
	fa_getScrollableContainer: f_class.ABSTRACT,
	
	/**
	 * @method protected
	 * @return void 
	 */
	fa_autoScrollPerformed: f_class.OPTIONAL_ABSTRACT
};

new f_aspect("fa_autoScroll", {
	members: __members,
	statics: __statics
});
