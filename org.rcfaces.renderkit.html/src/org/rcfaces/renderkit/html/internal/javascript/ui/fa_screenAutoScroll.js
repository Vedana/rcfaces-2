/*
 * $Id: fa_screenAutoScroll.js,v 1.2 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 * Aspect
 *
 * @aspect public abstract fa_screenAutoScroll extends fa_autoScroll
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:27 $
 */

var __statics = {
	/**
	 * @field protected static final Number
	 */
	SCREEN_AUTO_SCROLL_SIZE: 32

};

var __members = {
	/**
	 * @method protected
	 * @return Boolean
	 */
	fa_isContainerAutoScroll: function(scrollableComponent) {
		
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
		var scrollOffsets=f_core.GetScrollOffsets(null, scrollableComponent.ownerDocument);
		
		var viewSize=f_core.GetViewSize(null, scrollableComponent.ownerDocument);
		
		var documentSize=f_core.GetDocumentSize(null, scrollableComponent.ownerDocument);

		//document.title="scrollOffset="+scrollOffsets.x+"/"+scrollOffsets.y+"  viewSize="+viewSize.width+"/"+viewSize.height+"  documentSize="+documentSize.width+"/"+documentSize.height+" mousePosition="+mousePosition.x+"/"+mousePosition.y+"  dt="+new Date().getTime();

		var nx=scrollOffsets.x;
		var ny=scrollOffsets.y;

		var dt=fa_screenAutoScroll.SCREEN_AUTO_SCROLL_SIZE;
		
		if (scrollOffsets.y && mousePosition.y-scrollOffsets.y<dt) {
			ny=mousePosition.y-dt;
			if (ny<0) {
				ny=0;
			}		
			
			mousePosition.y+=ny-scrollOffsets.y;
		
		} else if (documentSize.height>viewSize.height && mousePosition.y>viewSize.height+scrollOffsets.y-dt) {
			ny=mousePosition.y+dt-viewSize.height;
			
			if (ny>documentSize.height-viewSize.height) {
				ny=documentSize.height-viewSize.height;
			}
						
			mousePosition.y+=ny-scrollOffsets.y;
		}
		
		
		if (scrollOffsets.x && mousePosition.x-scrollOffsets.x<dt) {
			nx=mousePosition.x-dt;
			if (nx<0) {
				nx=0;
			}		
			
			mousePosition.x-=scrollOffsets.x-nx;
			
		} else if (documentSize.width>viewSize.width && mousePosition.x>viewSize.width+scrollOffsets.x-dt) {
			nx=mousePosition.x+dt-viewSize.width;
			
			if (nx>documentSize.width-viewSize.width) {
				nx=documentSize.width-viewSize.width;
			}
						
			mousePosition.x+=nx-scrollOffsets.x;
		}
		
		if (nx!=scrollOffsets.x || ny!=scrollOffsets.y) {
			window.scrollTo(nx, ny);
			
			if (this.fa_autoScrollPerformed) {
				this.fa_autoScrollPerformed();
			}
		}
	}

};

new f_aspect("fa_screenAutoScroll", {
	extend: [ fa_autoScroll ],
	members: __members,
	statics: __statics
});
