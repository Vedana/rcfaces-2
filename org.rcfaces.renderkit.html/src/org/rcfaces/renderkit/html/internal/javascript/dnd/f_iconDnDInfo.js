/*
 * $Id: f_iconDnDInfo.js,v 1.2 2013/11/13 12:53:31 jbmeslin Exp $
 */

/**
 * f_iconDnDInfo class
 *
 * @class public f_iconDnDInfo extends f_abstractElementDnDInfo
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:31 $
 */

var __statics = {

	/**
	 * @field static final Number
	 */
	_DEFAULT_IMAGE_WIDTH: 32,
		
	/**
	 * @field static final Number
	 */
	_DEFAULT_IMAGE_HEIGHT: 32,
	
	/**
	 * @field static final String
	 */
	DND_SOURCE_IMAGE: "dnd.source.image",
	
	/**
	 * @field static final Number
	 */
	DND_SOURCE_IMAGE_WIDTH: "dnd.source.imageWidth",
	
	/**
	 * @field static final Number
	 */
	DND_SOURCE_IMAGE_HEIGHT: "dnd.source.imageHeight",
	
	/**
	 * @field static final String
	 */
	_NAME: "icon",
	
	/**
	 * @field static final String
	 */
	_CLASSNAME: "f_iconDnDInfo",
		
	Initializer: function() {
		f_dragAndDropEngine.RegisterDragAndDropPopup(f_iconDnDInfo._NAME, function(dragAndDropEngine) {
			return f_iconDnDInfo.f_newInstance(dragAndDropEngine);
		});
	}
};

var __members = {
		
	/**
	 * @field private Number
	 */
	_imageWidth: undefined,
	
	/**
	 * @field private Number
	 */
	_imageHeight: undefined,
				
	f_iconDnDInfo: function(dragAndDropEngine) {
		this.f_super(arguments, dragAndDropEngine);
		
		this._imageWidth=f_iconDnDInfo._DEFAULT_IMAGE_WIDTH;
		this._imageHeight=f_iconDnDInfo._DEFAULT_IMAGE_HEIGHT;
	},
		
	f_finalize: function() {
			
		this.f_super(arguments);
	},
	
	f_start: function() {
		this.f_super(arguments);
				
		var imgElement=this.f_getCursorElement();

		var engine=this.f_getDragAndDropEngine();
		
		var sourceAdditionalInfos=engine.f_getSourceAdditionnalInformations();
		var sourceItem=engine.f_getSourceItem();
		var sourceItemValue=engine.f_getSourceItemValue();
		var sourceComponent=engine.f_getSourceComponent();
		
		var imageURL=this.f_getItemProperty(f_iconDnDInfo.DND_SOURCE_IMAGE, false, sourceComponent, sourceItem, sourceItemValue, sourceAdditionalInfos);
		if (!imageURL) {
			if (sourceComponent.f_getItemImage) {
				imageURL=sourceComponent.f_getItemImage(sourceItemValue);
			}
		}
		
		if (!imageURL) {
			imgElement.style.display="none";			
			return;
		}
		
		if (imgElement.src!=imageURL) {
			imgElement.src=imageURL;
		}
		
		var updatePos=false;
		
		var imageWidth=this.f_getItemProperty(f_iconDnDInfo.DND_SOURCE_IMAGE_WIDTH, false, sourceComponent, sourceItem, sourceItemValue, sourceAdditionalInfos);		
		if (!imageWidth) {
			imageWidth="";
		}
		
		if (imageWidth && this._imageWidth!=imageWidth) {
			this._imageWidth=parseInt(imageWidth);
			imgElement.width=this._imageWidth;
				
			updatePos=true;
		}
		
		var imageHeight=this.f_getItemProperty(f_iconDnDInfo.DND_SOURCE_IMAGE_HEIGHT, false, sourceComponent, sourceItem, sourceItemValue, sourceAdditionalInfos);		
		if (!imageHeight) {
			imageHeight="";
		}
		
		if (imageHeight && this._imageHeight!=imageHeight) {
			this._imageHeight=parseInt(imageHeight);
			imgElement.height=this._imageHeight;
			updatePos=true;
		}
		
		if (updatePos) {
			this.f_setOffsetPosition(-this._imageWidth/2, -this._imageHeight/2);
		}
		
		
		imgElement.style.display="block";					
	},
	/**
	 * @method protected
	 * @return String
	 */
	f_getMainElementType: function() {
		return "img";
	},	
	f_fillElement: function(imgElement) {
		imgElement.style.display="none";
		imgElement.width=this._imageWidth;
		imgElement.height=this._imageHeight;

		this.f_setOffsetPosition(-this._imageWidth/2, -this._imageHeight/2);
	},
	f_getCursorClassName: function() {
		return f_iconDnDInfo._CLASSNAME;
	}
};
		
new f_class("f_iconDnDInfo", {
	extend: f_abstractElementDnDInfo,
	statics: __statics,
	members: __members
});