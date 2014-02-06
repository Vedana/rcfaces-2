/*
 * $Id: fa_treeDnd.js,v 1.2 2013/12/16 13:07:19 jbmeslin Exp $
 */

/**
 * Aspect fa_treeDnd
 *
 * @aspect public abstract fa_treeDnd extends fa_draggable, fa_droppable, fa_autoScroll, fa_autoOpen
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/12/16 13:07:19 $
 */
var __statics = {
};

var __members = {
		
	fa_treeDnd: function() {
		
		if (this.f_isDraggable()) {
			this._dragAndDropEngine= f_dragAndDropEngine.Create(this);
		}
	
		if (this.f_isDroppable()) {
			this.f_addEventListener(f_event.DRAG, this._treeDropListener);

			this._bodyDroppable=f_core.GetBooleanAttributeNS(this, "bodyDroppable", false);
		}
		
	},
	f_finalize: function() {
		this._dragAndDropEngine=undefined;
		this._targetDragAndDropEngine=undefined;	
		
		// this._bodyDroppable=undefined; // Boolean
	},
	
	/**
	 * @method private
	 * @param f_event event drag event
	 * @return void
	 */
	_treeDropListener: function(event) {
		var dndEvent = f_dndEvent.As(event);
		var targetComponent = dndEvent.f_getTargetComponent();
		if(!targetComponent) {
			return;
		}
		if (dndEvent.f_getSourceComponent() != targetComponent) {
			return;
		}
		
		switch(dndEvent.f_getStage()) {
		case f_dndEvent.DRAG_OVER_STAGE: 
			// interdit le drop dans un noeud fils
			var itemSource = dndEvent.f_getSourceItem();
			var itemTarget = dndEvent.f_getTargetItem();
			
			while(itemTarget._depth > 0){
				itemTarget = this._getParentNode(itemTarget);
				if(itemTarget == itemSource){
					dndEvent.f_setEffect(f_dndEvent.NONE_DND_EFFECT);
					break;
				}
			}	
			break;
		}
	},
	
	/**
	 * @method protected
	 * @param Event jsEvent
	 * @return Boolean
	 */
	fa_dragNode: function(jsEvent) {
		var dnd=this._dragAndDropEngine;
		if (!dnd) {
			return false;
		}
		
		var selection=new Object;
		selection._items = new Array;
		selection._itemsValue = new Array;
		var itemsDragTypes = new Array;
		var currentSelection = this._currentSelection;
		var lastEffects = undefined;
		for ( var i = 0; i < currentSelection.length; i++) {
					
			var dragTypes=undefined;
			var dragEffects=undefined;
			var node = currentSelection[i];
					
			for(var n=node;n && (dragTypes===undefined || dragEffects===undefined);n=n._parentTreeNode) {
				if (dragTypes===undefined) {
					dragTypes=n._dragTypes;
				}
				if (dragEffects===undefined) {
					dragEffects=n._dragEffects;
				}
			}
			f_core.Debug(fa_treeDnd, "_dragNode: dragEffects=0x"+dragEffects+" dragTypes='"+dragTypes+"'");
			
			if (!dragEffects || !dragTypes) {
				return false;
			}
			
			if (lastEffects){
				lastEffects = dragEffects & lastEffects;
			} else {
				lastEffects = dragEffects;
			}
			
			if (itemsDragTypes.length){
				itemsDragTypes = f_dragAndDropEngine.ComputeTypes(dragTypes, itemsDragTypes);
			} else {
				itemsDragTypes = dragTypes;
			}
			
			selection._items[i] = node;
			selection._itemsValue[i] = node._value; 
		}
		
		if (!lastEffects) {
			return false;
		}
		if (!itemsDragTypes.length) {
			return false;
		}
		
		selection._dragEffects = lastEffects;
		selection._dragTypes = itemsDragTypes;
		selection._itemsElement = currentSelection;
		var ret=dnd.f_start(jsEvent, selection);

		f_core.Debug(fa_treeDnd, "_dragNode: start returns '"+ret+"'");
		
		return ret;
	},
	
	/**
	 * @method public 
	 * @return Array
	 */
	f_getDragItems : function(selection) {
		return selection._items;
	},
	
	/**
	 * @method public 
	 * @return Array
	 */
	f_getDragItemsValue : function(selection) {
		return selection._itemsValue;
	},
	
	/**
	 * @method public 
	 * @return Array
	 */
	f_getDragItemsElement : function(selection) {
		return selection._itemsElement;
	},
	
	/**
	 * @method public 
	 * @return Array
	 */
	f_getDragTypes : function(selection) {
		return selection._dragTypes;
	},
	
	/**
	 * @method public 
	 * @return Number
	 */
	f_getDragEffects : function(selection) {
		return selection._dragEffects;
	},
	f_queryDropInfos: function(dragAndDropEngine, jsEvent, element) {
		this._targetDragAndDropEngine=dragAndDropEngine;

		this.fa_installAutoScroll();

		var node=null;
		var found=this._findNodeFromElement(element);
		if (!found) {
			return null;
							}
							
		node=found._node;
		
		if (this._bodyDroppable!==true && node==this) {
			return null;
						}
						
		if (node._disabled) {
			return null;
		}
						
		var dropTypes=undefined;
		var dropEffects=undefined;
					
		for(var n=node;n && (dropTypes===undefined || dropEffects===undefined);n=n._parentTreeNode) {
			if (dropTypes===undefined) {
				dropTypes=n._dropTypes;
			}
			if (dropEffects===undefined) {
				dropEffects=n._dropEffects;
			}
		}
		
		if (!dropTypes || !dropEffects) {
			return null;
		}		

		return {
			item: node,
			itemValue: found._value,
			targetItemElement: found._nodeElement,
			dropTypes: dropTypes,
			dropEffects: dropEffects		
		};
	},
	f_overDropInfos: function(dragAndDropEngine, infos) {
		var node=infos.item;
		var element=infos.targetItemElement;

		if (node._tooltip) {
			this._lastRemovedTitleElement=element;
			element._divNode.removeAttribute("title");
		}
				
		element._dndOver=true;		
		this.fa_updateElementStyle(element);			
	},
	f_outDropInfos: function(dragAndDropEngine, infos) {
		this._lastRemovedTitleElement=undefined;
			
		var node=infos.item;
		var element=infos.targetItemElement;
		
		if (node._tooltip) {
			element._divNode.title=node._tooltip;
		}
		
		element._dndOver=false;		
		this.fa_updateElementStyle(element);			
	},
	f_releaseDropInfos: function() {
		this._targetDragAndDropEngine=undefined;
		
		this.fa_uninstallAutoScroll();
	},
	fa_getLastMousePosition: function() {
		return this._targetDragAndDropEngine.fa_getLastMousePosition();
	},

	fa_autoScrollPerformed: function() {
		if (this._targetDragAndDropEngine) {
			this._targetDragAndDropEngine.f_updateMousePosition();
		}
	},
	fa_findAutoOpenElement: function(htmlElement) {
		if (!this._userExpandable) {
			return null;
		}

		var found= this._findNodeFromElement(htmlElement);
		if (!found) {
			f_core.Debug(fa_treeDnd, "fa_findAutoOpenElement: can not found any component");

			return null;
		}
		
		var node=found._node;

		f_core.Debug(fa_treeDnd, "fa_findAutoOpenElement: node="+node+" value='"+node._value+"' container='"+node._container+"' open='"+node._opened+"'.");

		if (node._container && !node._opened && found._value!==undefined) {
			return found;
		}
		
		return null;
	},
	fa_performAutoOpenElement: function(element) {
		this.f_openNode(element._value);		
	},
	fa_isSameAutoOpenElement: function(elt1, elt2) {
		return elt1._value===elt2._value;
	}
};

new f_aspect("fa_treeDnd", {
	statics: __statics,
	members: __members,
	extend: [  fa_droppable, fa_draggable, fa_autoScroll, fa_autoOpen ]	
});
