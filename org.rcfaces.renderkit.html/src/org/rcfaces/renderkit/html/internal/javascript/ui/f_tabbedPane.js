/*
 * $Id: f_tabbedPane.js,v 1.4 2013/11/13 12:53:27 jbmeslin Exp $
 */

/**
 *
 * @class public f_tabbedPane extends f_cardBox
 *
 * @author olivier Oeuillot
 * @version $REVISION: $
 */

var __statics = {
	
	/**
	 * @field private static final String
	 */
	_TITLE_ID_SUFFIX: "::title",
	
	/**
	 * @field private static 
	 */
	_PreparedImages: undefined,

	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:tabbedPane
	 */
	_TabbedPane_onresize: function(evt) {
		var tabbedPane=this._tabbedPane;
		
		if (!evt) {
			evt=f_core.GetJsEvent(this);
		}

		if (tabbedPane.f_getEventLocked(evt, false)) {
			return false;
		}
		
		tabbedPane._resize();
		
		return true;
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:tabbedPane
	 */
	_TabbedPane_click: function(evt) {
		var tab=this._tab;
		var tabbedPane=tab._cardBox;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (tabbedPane.f_getEventLocked(evt)) {
			return false;
		}

		//var old=tabbedPane._selectedCard;

		tabbedPane._selectTab(this._tab, true, evt);
				
		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:tabbedPane
	 */
	_TabbedPane_focus: function(evt) {
		var tab=this._tab;
		var tabbedPane=tab._cardBox;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (tabbedPane.f_getEventLocked(evt, false)) {
			return false;
		}
				
		return true;
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:tabbedPane
	 */
	_TabbedPane_keyPress: function(evt) {
		var tab=this._tab;
		var tabbedPane=tab._cardBox;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (tabbedPane.f_getEventLocked(evt, false)) {
			return false;
		}

		switch(evt.keyCode) {
		case f_key.VK_RIGHT: // FLECHE VERS LA DROITE
		case f_key.VK_LEFT: // FLECHE VERS LA GAUCHE
		case f_key.VK_HOME:
		case f_key.VK_END:
		case f_key.VK_SPACE:
			return f_core.CancelJsEvent(evt);
		}
		
		return true;	
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:tabbedPane
	 */
	_TabbedPane_keyDown: function(evt) {
		var tab=this._tab;
		var tabbedPane=tab._cardBox;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (tabbedPane.f_getEventLocked(evt)) {
			return false;
		}
			
		switch(evt.keyCode) {
		case f_key.VK_RIGHT: // FLECHE VERS LA DROITE
			var next=tab._next;
			for(;next && next._disabled;next=next._next);
			if (next) {
				tabbedPane._selectTab(next, true, evt);
			}
			break;
			
		case f_key.VK_LEFT: // FLECHE VERS LA GAUCHE
			var prev=tab._prev;
			for(;prev && prev._disabled;prev=prev._prev);		
			if (prev) {
				tabbedPane._selectTab(prev, true, evt);
			}
			break;
			
		case f_key.VK_HOME:
			var next=tabbedPane._cards[0];
			for(;next && next._disabled;next=next._next);
			if (next && next!=tabbedPane._selectedCard) {
				tabbedPane._selectTab(next, true, evt);
			}
			break;

		case f_key.VK_END:
			var prev=tabbedPane._cards[tabbedPane._cards.length-1];
			for(;prev && prev._disabled;prev=prev._prev);
			if (prev && prev!=tabbedPane._selectedCard) {
				tabbedPane._selectTab(prev, true, evt);
			}
			break;
			
		case f_key.VK_SPACE:
			if (tab!=tabbedPane._selectedCard) {
				tabbedPane._selectTab(tab, false, evt);
			}
			break;
			
		default:
			return true;
		}

		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:tabbedPane
	 */
	_TabbedPane_mouseover: function(evt) {
		var tab=this._tab;
		var tabbedPane=tab._cardBox;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (tabbedPane.f_getEventLocked(evt, false)) {
			return false;
		}

		tabbedPane._tabMouseOver(this._tab, evt);
		
		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 * @param Event evt
	 * @return Boolean
	 * @context object:tabbedPane
	 */
	_TabbedPane_mouseout: function(evt) {
		var tab=this._tab;
		var tabbedPane=tab._cardBox;

		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}

		if (tabbedPane.f_getEventLocked(evt, false)) {
			return false;
		}

		tabbedPane._tabMouseOut(this._tab, evt);
		
		return f_core.CancelJsEvent(evt);
	},
	/**
	 * @method private static
	 */
	_PrepareImages: function() {
		var styleSheetBase=f_env.GetStyleSheetBase();
		if (!styleSheetBase) {
			return;
		}

		var args= [
			"/tabbedPane/xpMid2.gif", 
			"/tabbedPane/xpT2.gif" ];

		f_tabbedPane._PreparedImages=new Object;

		for(var i=0;i<args.length;i++) {
			var filename=args[i];
			
			var url=styleSheetBase+filename;
			f_imageRepository.PrepareImage(url);
			
			f_tabbedPane._PreparedImages[filename]=url;
		}
	},
	/**
	 * @method private static
	 */
	_GetImageURL: function(filename) {
		var url=f_tabbedPane._PreparedImages[filename];
		
		f_core.Assert(url, "Unknown filename '"+filename+"'.");
		
		return url;
	},
	/**
	 * @method protected static
	 * @return void
	 */
	Initializer: function() {
		f_tabbedPane._PrepareImages();
	}
};

var __members = {

	f_tabbedPane: function() {
		this.f_super(arguments);
		
		// On laisse tomber, les images sont maintenant directement réferencées dans le HTML ...
		// f_tabbedPane._PrepareImages();
		
		this._tabIndex=f_core.GetAttributeNS(this,"tabIndex");
	},
	f_finalize: function() {
		var title=this._title;
		if (title) {
			this._title=undefined;
			
			f_core.RemoveResizeEventListener(title, f_tabbedPane._TabbedPane_onresize);

			title._tabbedPane=undefined; // f_tabbedPane
			f_core.VerifyProperties(title);
		}

		// this._tabIndex=undefined; // number
		this._overTab=undefined; // f_tab
		// this._imageURL=undefined; // string
		// this.onresize=null; // ????
		// this._resizeHeight=undefined; // boolean
		// this._resizeWidth=undefined; //boolean
		
		this.f_super(arguments);
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_updateCards: function() {
		var cards=this._cards;
		for(var i=0;i<cards.length;i++) {
			var tab=cards[i];

			var ccard=f_core.GetElementByClientId(tab._id, this.ownerDocument);
			f_core.Assert(ccard, "f_tabbedPane.f_updateCards: Can not find card component of tab '"+tab._id+"'.");

			f_core.Debug(f_tabbedPane, "f_updateCards: Update tab#"+i+" tab="+tab+" ccard="+ccard);
			tab._ccard=ccard;
			ccard._vcard=tab;			
			ccard.f_declareTab(this, tab._value, tab._text, tab._accessKey, tab._disabled, tab._imageURL, tab._disabledImageURL, tab._selectedImageURL, tab._hoverImageURL);	
		}

		
		if (!this._selectedCard && cards.length) {
			this._selectTab(cards[0], false, null, false);
		}
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_documentComplete: function() {
		this.f_super(arguments);
		
		f_core.Assert(this.fa_componentUpdated, "f_tabbedPane.f_documentComplete: Component is not updated !");
		if (!this.fa_componentUpdated) {
			// Lors d'une erreur le documentComplete peut etre appelé !
			return;
		}
		
		if (!this._title) {
			return;
		}
		
		if (!this.style.width) {
			for(var i=0;i<this._cards.length;i++) {
				var tab=this._cards[i];
	
				var _tab=tab._ccard;
				if (!_tab) {
					continue;
				}
		
				if (_tab.style.width) {
					continue;
				}
	
				this._resizeWidth=true;
				break;
			}
		}
				
//		if (!this.style.height) {
			this._resizeHeight=true;
//		}
		
		/*
		if (this._resizeWidth || this._resizeHeight) {
			this._resize();
			
			f_core.AddResizeEventListener(this._title, f_tabbedPane._TabbedPane_onresize);
		
			if (f_core.IsInternetExplorer()) {
				// Il faut le faire reafficher la barre, sinon il y a un probleme de position des titres
				this.f_updateCardStyle(this._selectedCard);
			}
		}
		*/
	},
	/**
	 * @method private
	 */
	_resize:function() {
		
		var width=undefined;
		if (this._resizeWidth) {		
			width=this.offsetWidth+"px";
		}
		
		var maxHeight=0;
		
		var cards=this._cards;
		for(var i=0;i<cards.length;i++) {
			var tab=cards[i];

			var _tab=tab._ccard;
		
			f_core.Assert(_tab, "f_tabbedPane._resize: Invalid ccard for tab#"+i+" component=#"+tab.id+"."+tab.className+" .");
		
		/*	
			if (this._resizeHeight) {
				_tab.style.height="";
	
				if (maxHeight<_tab.offsetHeight) {
					maxHeight=_tab.offsetHeight;
				}
			}
			*/
			
			if (width) {
				_tab.style.width=width;
			}

			var mask=_tab._mask;
			var textTitle=tab._textTitle;
			
			var mleft=textTitle.offsetLeft;
			var mright=textTitle.offsetLeft+textTitle.offsetWidth-1;
			
			if (true) {
				//mleft-=f_core.ComputeBorderLength(textTitle, "left");
				//mright-=f_core.ComputeBorderLength(textTitle, "right");
				
				var l=f_core.ComputeBorderLength(mask.parentNode, "left");
				mleft-=l;
				mright-=l;
			}
			
			var w=tab._leftTitle.offsetWidth;
			if (!tab._prev) {
				mleft-=w-1;
			} else {
				mleft-=w-3;
			}
			
			w=tab._rightTitle.offsetWidth;			
			if (!tab._next) {
				mright+=w-1;
			} else {
				mright+=w-1;
			}
					
			f_core.Debug(f_tabbedPane, "_resize: Set mask position "+mleft+" to "+mright);
			
			mask.style.left=(mleft)+"px";
			mask.style.width=(mright-mleft+1)+"px";
		}
	
		if (this.style.height) {
			// Hauteur fixée !
			var p1=f_core.GetAbsolutePosition(this);
			var div0=f_core.GetFirstElementByTagName(this, "div", true);
			var p2=f_core.GetAbsolutePosition(div0);
	
			var ythis=p1.y;
			var ybody=p2.y;
			
			maxHeight=this.offsetHeight-ybody+ythis;
		}				


		if (maxHeight>0) {
			var height=maxHeight+"px";
			
			for(var i=0;i<this._cards.length;i++) {
				var tab=this._cards[i];
	
				var _tab=tab._ccard;
				if (!_tab) {
					continue;
				}
				
				_tab.style.height=height;
			}

			height=(maxHeight+2)+"px";
			var bodies=this.getElementsByTagName("div");
			for(var i=0;i<bodies.length;i++) {
				var body=bodies[i];
				
				if (body.className!="f_tabbedPane_content") {
					continue;
				}
				
				body.style.height=height;
				break;
			}
		}	
	},
	
	/**
	 * @method public
	 * @param f_tab tab Tab to select
	 * @return optional Boolean setFocus Set focus if possible !
	 * @return Boolean
	 */
	f_selectCard: function(tab, setFocus) {
		f_core.Assert(typeof(tab)=="object" && tab, "f_tabbedPane.f_selectCard: Invalid parameter 'tab' ("+tab+")");
		f_core.Assert(typeof(setFocus)=="boolean" || setFocus===undefined, "f_tabbedPane.f_selectCard: Invalid parameter 'setFocus' ("+setFocus+" / typeof='"+typeof(setFocus)+"')");

		var _tab=tab._vcard;
		f_core.Assert(_tab, "f_tabbedPane.f_selectCard: L'objet n'est pas un onglet ! ("+tab+")");
		
		return this._selectTab(_tab, setFocus, null);
	},
	/**
	 * Select a tab by its value.
	 * 
	 * @method public
	 * @param String value
	 * @return optional Boolean setFocus  Set focus if possible !
	 * @return Boolean <code>true</code> if success.
	 */
	f_setValue: function(value, setFocus) {
		f_core.Assert(typeof(value)=="string" || value===null, "f_tabbedPane.f_setValue: Invalid parameter 'value' ("+value+")");
		f_core.Assert(typeof(setFocus)=="boolean" || setFocus===undefined, "f_tabbedPane.f_setValue: Invalid parameter 'setFocus' ("+setFocus+")");

		var tab=this.f_getCardByValue(value);
		if (!tab) {
			return false;
		}
			
		return this._selectTab(tab, setFocus, null);			
	},
	/**
	 * @method private
	 */
	_selectTab: function(tab, setFocus, evt, sendEvent) {
		var ccard=tab._ccard;
		
		if (ccard.f_isDisabled()) {
			return false;
		}
	
		// ON verifie la selection par l'appel d'un evenement !
		if (setFocus) {
			f_core.SetFocus(tab._textLink);
		}
		
		if (tab==this._selectedCard) {		
			return true;
		}
		
		if (sendEvent!==false) {
			if (this.f_fireEvent(f_event.PRE_SELECTION, evt, ccard, ccard.f_getValue())===false) {
				return false;
			}
		}
		
		var old=this._selectedCard;
		this._selectedCard=null;
		if (old) {
			this.f_updateCardStyle(old);
		}
			
		if (old) {
			old._ccard.f_setVisible(false);
		}
		ccard.f_setVisible(true);
		
		this._selectedCard=tab;
		this.f_updateCardStyle(tab);
		
		this.f_setProperty(f_prop.SELECTED, tab._id);
		
		if (sendEvent!==false) {
			this.f_fireEvent(f_event.SELECTION, evt, ccard, ccard.f_getValue());
		}
		
		return true;
	},
	/**
	 * @method private
	 */
	_tabMouseOver: function(tab, evt) {
		if (tab._disabled) {
			return;
		}
		
		var old=null;
		
		if (this._overTab) {
			if (tab==this._overTab) {
				return;
			}

			old=this._overTab;			
		}

		this._overTab=tab;
		if (old) {		
			this.f_updateCardStyle(old);		
		}
		
		this.f_updateCardStyle(tab);
	},
	/**
	 * @method private
	 * @return void
	 */
	_tabMouseOut: function(tab, evt) {
		if (this._overTab!=tab) {
			return;
		}
		this._overTab=undefined;
		
		this.f_updateCardStyle(tab);
	},
	/**
	 * @method protected
	 * @param f_tab tab
	 * @return void
	 */
	f_updateCardStyle: function(tab) {
		var rightTTitleImage;
		var leftTTitleImage;
		var rightTitle;
		var leftTitle;
		var textTTitle;
		var textTitle;

		if (this._selectedCard==tab) {
			if (!tab._prev) {
				// Le plus à gauche est sélectionné !
				leftTTitleImage="_ttitleLeftA";
				leftTitle="_titleLeft_selected";

			} else if (tab._prev==this._overTab) {
				leftTTitleImage="_ttitleNextRH";
				leftTitle="_titleNext_sright";

			} else {
				leftTTitleImage="_ttitleNextR";
				leftTitle="_titleNext_sright";
			}
						
			if (!tab._next) {
				// Le plus à droite est sélectionné
				rightTTitleImage="_ttitleRightA";
				rightTitle="_titleRight_selected";

			} else if (tab._next==this._overTab) {
				rightTTitleImage="_ttitleNextLH";
				rightTitle="_titleNext_sleft";

			} else {
				rightTTitleImage="_ttitleNextL";
				rightTitle="_titleNext_sleft";
			}
			
			textTTitle="_ttitleText_selected";
			textTitle="_titleText_selected";
		} else {
			if (!tab._prev) {
				if (tab==this._overTab) {
					leftTTitleImage="_ttitleLeftH";
				} else {
					leftTTitleImage="_ttitleLeft";
				}
				leftTitle="_titleLeft";

			} else if (tab._prev==this._selectedCard) {
				// L'onglet de gauche est selectionné !
				if (tab==this._overTab) {
					leftTTitleImage="_ttitleNextLH";
					
				} else {
					leftTTitleImage="_ttitleNextL";
				}
				leftTitle="_titleNext_sleft";
							
			} else {
				if (tab==this._overTab) {
					leftTTitleImage="_ttitleNextHR";

				} else if (tab._prev && tab._prev==this._overTab) {
					leftTTitleImage="_ttitleNextHL";

				} else {
					leftTTitleImage="_ttitleNext";
				}
				leftTitle="_titleNext";
			}

			if (!tab._next) {
				if (tab==this._overTab) {
					rightTTitleImage="_ttitleRightH";
					
				} else  {
					rightTTitleImage="_ttitleRight";
				}
				rightTitle="_titleRight";

			} else if (tab._next==this._selectedCard) {
				// L'onglet de droite est selectionné !
				
				if (tab==this._overTab) {
					rightTTitleImage="_ttitleNextRH";
					
				} else {
					rightTTitleImage="_ttitleNextR";
				}
				rightTitle="_titleNext_sright";
							
			} else {
				if (tab==this._overTab) {
					rightTTitleImage="_ttitleNextHL";

				} else if (tab._next && tab._next==this._overTab) {
					rightTTitleImage="_ttitleNextHR";
					
				} else {
					rightTTitleImage="_ttitleNext";
				}
				rightTitle="_titleNext";
			}
		
			if (tab==this._overTab) {
				textTTitle="_ttitleText_over";
				
			} else {
				textTTitle="_ttitleText";
			}
			
			if (tab._disabled) {
				textTitle="_titleText_disabled";
				
			} else {
				textTitle="_titleText";
			}
		}
		
		var className="f_tabbedPane";
		tab._rightTTitleImage.className=className+rightTTitleImage;
		tab._leftTTitleImage.className=className+leftTTitleImage;
		tab._rightTitle.className=className+rightTitle;
		tab._leftTitle.className=className+leftTitle;
		tab._textTitle.className=className+textTitle;
		tab._textTTitle.className=className+textTTitle;
		
		var icon=tab._icon;
		if (icon) {
			var imageURL=null;

			if (tab._disabled) {
				imageURL=tab._disabledImageURL;
			}

			if (!imageURL && this._selectedCard==tab) {
				imageURL=tab._selectedImageURL;
			}
			
			if (!imageURL && tab==this._overTab) {
				imageURL=tab._hoverImageURL;
			}
			
			if (!imageURL) {
				imageURL=tab._imageURL;
			}
			
			if (imageURL) {
				icon.src=imageURL;
	//			icon.style.display="inherit";
				
			} else {
	//			icon.style.display="none";
			}
		}
	},
	/**
	 * @method hidden
	 */
	f_declareCard: function(tab) {
		f_core.Assert(tab._value===undefined || typeof(tab._value)=="string", "f_tabbedPane.f_declareTab: Invalid tabValue parameter ("+tab._value+")");
		f_core.Assert(tab._selected===undefined || typeof(tab._selected)=="boolean", "f_tabbedPane.f_declareTab: Invalid selected parameter ("+tab._selected+")");
		f_core.Assert(tab._text===undefined || typeof(tab._text)=="string", "f_tabbedPane.f_declareTab: Invalid text parameter ("+tab._text+")");
		
		tab=this.f_super(arguments, tab);

		f_core.Debug(f_tabbedPane, "f_declareCard: Declare tab : "+tab);

		var blankImage=f_env.GetBlankImageURL();
		
		/*
		if (tab._imageURL) {
			f_imageRepository.PrepareImage(tab._imageURL);
		}
		if (tab._disabledImageURL) {
			f_imageRepository.PrepareImage(tab._disabledImageURL);
		}
		if (tab._hoverImageURL) {
			f_imageRepository.PrepareImage(tab._hoverImageURL);
		}
		if (tab._selectedImageURL) {
			f_imageRepository.PrepareImage(tab._selectedImageURL);
		}
		*/
		
		var doc=this.ownerDocument;
		
		var table=this._title;
		if (!table) {
			table=doc.getElementById(this.id+f_tabbedPane._TITLE_ID_SUFFIX);
			//table=f_core.GetChildByCssClass(this,"f_tabbedPane_title");
			this._title=table;
			table._tabbedPane=this;
		}
		
		var rows=table.rows;
		
		var trTitle=rows[0];
		var trText=rows[1];
		
		var textTitle;
		var textLink;
		var rightTitle;
		var rightTTitleImage;
		var leftTitle;
		var leftTTitleImage;
		var textTTitle;
		var icon;
		
		if (tab._titleGenerated) {
			var cards=this._cards;
			var indexGenerated=(cards.length-1)*2;
			
			var nodes=trTitle.childNodes;

			leftTTitleImage=nodes[indexGenerated].firstChild;
			textTTitle=nodes[indexGenerated+1];
			rightTTitleImage=nodes[indexGenerated+2].firstChild;
			
			nodes=trText.childNodes;
			
			leftTitle=nodes[indexGenerated];
			textTitle=nodes[indexGenerated+1];
			textLink=textTitle.firstChild;
			rightTitle=nodes[indexGenerated+2];
			if (tab._imageURL) {
				icon=textLink.firstChild;
			}
			
		} else {		
			//var cellsTitle=trTitle.cells;
			var cellsText=trText.cells;
			
			if (!tab._prev) {
				// Premier !
				
				var tdTitleLeft=doc.createElement("td");
				f_core.AppendChild(trTitle, tdTitleLeft);
				
				leftTTitleImage=doc.createElement("img");
				leftTTitleImage.src=blankImage;
				leftTTitleImage.width=5;
				leftTTitleImage.height=5;
				f_core.AppendChild(tdTitleLeft, leftTTitleImage);
				
				leftTitle=doc.createElement("td");
				f_core.AppendChild(trText, leftTitle);
				
			} else {
				//var tdTitleLeft=cellsTitle[cellsTitle.length-1];
				leftTTitleImage=tab._prev._rightTTitleImage;
				leftTTitleImage.width=7;
				leftTitle=cellsText[cellsText.length-1];
			}
			
			textTTitle=doc.createElement("td");		
			f_core.AppendChild(trTitle, textTTitle);
		
			textTitle=doc.createElement("td");
			f_core.AppendChild(trText, textTitle);
			
			textLink=f_core.CreateElement(textTitle, "a", {
				href: f_core.CreateJavaScriptVoid0()
	//			tabIndex: (this._tabIndex)?(this._tabIndex):0  // ????
			});
			
			if (tab._accessKey && f_core.IsInternetExplorer()) {
				// Il faut positionner l'accessKey !
				textLink.accessKey=tab._accessKey;
			}
	
			var imageURL=tab._imageURL;
			if (imageURL) {
				icon=f_core.CreateElement(textLink, "img", {
					src: imageURL,
					align: "center", 
					border: 0,
					className: "f_tabbedPane_titleIcon"
				});
			}
			
			if (tab._text) {
				f_component.AddLabelWithAccessKey(textLink, tab._text, tab._accessKey);
			}		
		
			var tdTitleRight=doc.createElement("td");
			f_core.AppendChild(trTitle, tdTitleRight);
	
			rightTTitleImage=doc.createElement("img");
			rightTTitleImage.src=blankImage;
			rightTTitleImage.width=5;
			rightTTitleImage.height=5;
			f_core.AppendChild(tdTitleRight, rightTTitleImage);		
			
			rightTitle=doc.createElement("td");
			f_core.AppendChild(trText, rightTitle);
		}

		f_core.Assert(textTitle && textTitle.nodeType==f_core.ELEMENT_NODE, "f_tabbedPane.f_declareCard: Invalid textTitle component ("+textTitle+")");
		textTitle.onclick=f_tabbedPane._TabbedPane_click;
		textTitle.onmouseover=f_tabbedPane._TabbedPane_mouseover;
		textTitle.onmouseout=f_tabbedPane._TabbedPane_mouseout;
		textTitle._tab=tab;
		tab._textTitle=textTitle;

		f_core.Assert(textLink && textLink.nodeType==f_core.ELEMENT_NODE, "f_tabbedPane.f_declareCard: Invalid textLink component ("+textLink+")");
		textLink.onclick=f_tabbedPane._TabbedPane_click;
		textLink.onfocus=f_tabbedPane._TabbedPane_focus;
		textLink.onkeydown=f_tabbedPane._TabbedPane_keyDown;
		textLink.onkeypress=f_tabbedPane._TabbedPane_keyPress;
		textLink._tab=tab;		
		tab._textLink=textLink;

		f_core.Assert(rightTTitleImage && rightTTitleImage.nodeType==f_core.ELEMENT_NODE, "f_tabbedPane.f_declareCard: Invalid rightTTitleImage component ("+rightTTitleImage+")");
		tab._rightTTitleImage=rightTTitleImage;
		
		f_core.Assert(textTTitle && textTTitle.nodeType==f_core.ELEMENT_NODE, "f_tabbedPane.f_declareCard: Invalid textTTitle component ("+textTTitle+")");
		tab._textTTitle=textTTitle;

		f_core.Assert(leftTTitleImage && leftTTitleImage.nodeType==f_core.ELEMENT_NODE, "f_tabbedPane.f_declareCard: Invalid leftTTitleImage component ("+tab.leftTTitleImage+")");
		tab._leftTTitleImage=leftTTitleImage;

		f_core.Assert(rightTitle && rightTitle.nodeType==f_core.ELEMENT_NODE, "f_tabbedPane.f_declareCard: Invalid rightTitle component ("+rightTitle+")");
		tab._rightTitle=rightTitle;

		f_core.Assert(leftTitle && leftTitle.nodeType==f_core.ELEMENT_NODE, "f_tabbedPane.f_declareCard: Invalid leftTitle component ("+leftTitle+")");
		tab._leftTitle=leftTitle;
		
		tab._icon=icon;
		
		if (!tab._titleGenerated) {
			this.f_updateCardStyle(tab);
		}		
	},
	/**
	 * @method protected
	 * @param f_tab tab
	 * @return void
	 */
	f_destroyCard: function(tab) {
		f_core.Assert(tab._cardBox, "f_tabbedPane.f_destroyCard: Invalid tab object ("+tab+")");
		
		var ccard=tab._ccard;		
		f_core.Debug(f_tabbedPane, "f_destroyCard: Destroy tab: "+tab+"  component="+ccard);
		
		tab._next=undefined; // f_tab
		tab._prev=undefined; // f_tab
		
		f_core.VerifyProperties(tab._leftTitle);	
		tab._leftTitle=undefined; // HTMLTDElement
		
		f_core.VerifyProperties(tab._rightTitle);	
		tab._rightTitle=undefined; // HTMLTDElement
		
		f_core.VerifyProperties(tab._leftTTitleImage);	
		tab._leftTTitleImage=undefined;
		
		f_core.VerifyProperties(tab._textTTitle);	
		tab._textTTitle=undefined;
		
		f_core.VerifyProperties(tab._rightTTitleImage);	
		tab._rightTTitleImage=undefined;
		
		f_core.VerifyProperties(tab._icon);	
		tab._icon=undefined; // HTMLImageElement
		
		var textTitle=tab._textTitle;
		if (textTitle) {
			tab._textTitle=undefined;
			
			textTitle.onclick=null;
			textTitle.onmouseover=null;
			textTitle.onmouseout=null;
			textTitle._tab=undefined;

			f_core.VerifyProperties(textTitle);		
		}

		var textLink=tab._textLink;
		if (textLink) {
			tab._textLink=undefined;
			
			textLink.onclick=null;
			textLink.onfocus=null;
			textLink.onkeydown=null;
			textLink.onkeypress=null;
			textLink._tab=undefined;

			f_core.VerifyProperties(textLink);		
		}

		// tab._id=undefined; // string
		// tab._imageURL=undefined; // string
		// tab._disabledImageURL=undefined; // string
		// tab._hoverImageURL=undefined; // string
		// tab._selectedImageURL=undefined; // string
		// tab._disabled=undefined; // boolean
				
		this.f_super(arguments, tab);
	},
	/**
	 * @method hidden
	 */
	f_setTabImageURL: function(_tab, imageURL) {
		var tab=_tab._vcard;
		f_core.Assert(tab, "f_tabbedPane.f_setTabImageURL: L'objet n'est pas un onglet ! ("+_tab+")");
		tab._imageURL=imageURL;

		if (!this.fa_componentUpdated) {
			return;
		}

		this.f_updateCardStyle(tab);
	},
	/**
	 * @method hidden
	 */
	f_setTabDisabledImageURL: function(_tab, imageURL) {
		var tab=_tab._vcard;
		f_core.Assert(tab, "f_tabbedPane.f_setTabDisabledImageURL: L'objet n'est pas un onglet ! ("+ _tab +")");
		tab._disabledImageURL=imageURL;

		if (!this.fa_componentUpdated) {
			return;
		}

		this.f_updateCardStyle(tab);
	},
	/**
	 * @method hidden
	 */
	f_setTabHoverImageURL: function(_tab, imageURL) {
		var tab=_tab._vcard;
		f_core.Assert(tab, "f_tabbedPane.f_setTabHoverImageURL: L'objet n'est pas un onglet ! ("+ _tab+")");
		tab._hoverImageURL=imageURL;

		if (!this.fa_componentUpdated) {
			return;
		}

		this.f_updateCardStyle(tab);
	},
	/**
	 * @method hidden
	 */
	f_setTabSelectedImageURL: function(_tab, imageURL) {
		var tab=_tab._vcard;
		f_core.Assert(tab, "f_tabbedPane.f_setTabSelectedImageURL: L'objet n'est pas un onglet ! ("+ _tab+")");
		tab._selectedImageURL=imageURL;

		if (!this.fa_componentUpdated) {
			return;
		}

		this.f_updateCardStyle(tab);
	},
	/**
	 * @method hidden
	 */
	f_setTabText: function(_tab, text) {
		var tab=_tab._vcard;
		f_core.Assert(tab, "f_tabbedPane.f_setTabText: L'objet n'est pas un onglet ! ("+ _tab+")");
		// @TODO !
	},
	/**
	 * @method hidden
	 */
	f_setTabDisabled: function(_tab, disabled) {
		var tab=_tab._vcard;
		f_core.Assert(tab, "f_tabbedPane.f_setTabDisabled: L'objet n'est pas un onglet ! ("+ _tab+")");
		
		tab._disabled=disabled;
		tab._textLink.disabled=disabled;

		if (!this.fa_componentUpdated) {
			return;
		}
		
		var update=true;

		if (disabled) {
			if (this._overTab==tab) {
				this._overTab=null;
			}
			
			if (this._selectedCard==tab) {
				var found=false;
				for(var i=0;i<this._cards.length;i++) {
					var t=this._cards[i];
						
					if (this._selectTab(t, false, null)==false) {
						continue;
					}
					
					update=false;
					found=true;
					break;
				}
				
				if (!found) {
					this._selectedCard=null;
				}
			}
			
		} else if (!this._selectedCard) {
			if (this._selectTab(tab, false, null)) {
				update=false;
			}
		}
		
		if (update) {
			this.f_updateCardStyle(tab);
		}
	},
	/**
	 * @method hidden
	 */
	f_setCardFocus: function(_tab, evt) {
		var tab=_tab._vcard;
		f_core.Assert(tab, "f_tabbedPane.f_setCardFocus: L'objet n'est pas un onglet ! ("+ _tab+")");
		
		f_core.SetFocus(tab._textLink);
	},
	/**
	 * @method hidden
	 */
	f_performTabAccessKey: function(_tab, evt) {
		var tab=_tab._vcard;
		f_core.Assert(tab, "f_tabbedPane.f_setCardFocus: L'objet n'est pas un onglet ! ("+ _tab+")");
	
		this._selectTab(tab, true, evt);
	},
	/**
	 * @method public
	 * @return void
	 */
	f_setFocus: function() {
		if (!f_core.ForceComponentVisibility(this)) {
			return;
		}

		if (!this._selectedCard) {
			return;
		}
		
		var component=this._selectedCard._textLink;
		if (!component) {
			return;
		}
		try {
			component.focus();
			
		} catch (x) {
			f_core.Error(f_tabbedPane, "f_setFocus: Error while setting focus to '"+component.id+"'.", x);
		}
	},
	/**
	 * @method public 
	 * @return f_tab[] Card array
	 */
	f_listTabs: function() {
		return this.f_listCards();
	}
};
 
new f_class("f_tabbedPane", {
	extend: f_cardBox,
	members: __members,
	statics: __statics
});

