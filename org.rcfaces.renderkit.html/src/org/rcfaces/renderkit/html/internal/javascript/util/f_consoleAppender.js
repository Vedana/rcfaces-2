/* 
 * $Id: f_consoleAppender.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 * f_consoleAppender
 *
 * @class hidden f_consoleAppender extends f_object
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */

var __statics={
	/**
	 * @method protected static
	 */
	Initializer: function() {	
 		// this est la classe !
		this.f_newInstance();
	},
	/**
	 * @method private static
	 * @context object:console
	 */
	_OpenCloseButtonClick: function() {
		var console=this._console;
		
		console._openClose();
	},
	/**
	 * @method private static
	 * @context object:console
	 */
	_ClearButtonClick: function() {
		var console=this._console;
		
		var list=console._list;
		while (list.hasChildNodes()) {
			list.removeChild(list.lastChild);
		}
	},
	/**
	 * @method private static
	 * @context object:console
	 */
	_ErrorButtonClick: function() {
		var console=this._console;
		
		console._showError=!console._showError;
		f_core.SetCookieValue("consoleError", (console._showError)?"true":"false");
		
		console._filterLIs();
	},
	/**
	 * @method private static
	 * @context object:console
	 */
	_WarningButtonClick: function() {
		var console=this._console;
		
		console._showWarning=!console._showWarning;
		f_core.SetCookieValue("consoleWarning", (console._showWarning)?"true":"false");
		
		console._filterLIs();
	},
	/**
	 * @method private static
	 * @context object:console
	 */
	_InfoButtonClick: function() {
		var console=this._console;
		
		console._showInfo=!console._showInfo;
		f_core.SetCookieValue("consoleInfo", (console._showInfo)?"true":"false");
		
		console._filterLIs();
	},
	/**
	 * @method private static
	 * @context object:console
	 */
	_DebugButtonClick: function() {
		var console=this._console;
		
		console._showDebug=!console._showDebug;
		f_core.SetCookieValue("consoleDebug", (console._showDebug)?"true":"false");
		
		console._filterLIs();
	},
	/**
	 * @method private static
	 * @context object:console
	 */
	_ResizeClick: function(evt) {
		if (!evt) {
			evt = f_core.GetJsEvent(this);
		}
			
  		var console=this._console;
		if (console._closed) {
		   	return f_core.CancelJsEvent(evt);
		}

		f_core.AddEventListener(document, "mousemove", f_consoleAppender._ResizeCursorDragMove, this);
		f_core.AddEventListener(document, "mouseup",   f_consoleAppender._ResizeCursorDragStop, this);

		f_core.CancelJsEvent(evt);
  		
		var eventPos=f_core.GetJsEventPosition(evt, document);
		var cursorPos=f_core.GetAbsolutePosition(console._console);
		console._dragDeltaX=eventPos.x-cursorPos.x;
		console._dragDeltaY=eventPos.y-(cursorPos.y+console._console.clientHeight);
		
//		document.title="dx="+console._dragDeltaX+" dy="+console._dragDeltaY;
		
		window._dragOldCursor=document.body.style.cursor;
		document.body.style.cursor="e-resize";
		
		window._dragConsole=console;
		window._draggedComponent=this;
	
		return false;	
	},
	/**
	 * @method private static
	 * @context event:evt	 
	 */
	_ResizeCursorDragStop: function(evt) {
		f_core.RemoveEventListener(document, "mousemove", f_consoleAppender._ResizeCursorDragMove, window._draggedComponent);
		f_core.RemoveEventListener(document, "mouseup",   f_consoleAppender._ResizeCursorDragStop, window._draggedComponent);

		document.body.style.cursor=window._dragOldCursor;
		window._dragOldCursor=undefined;
	
 	 	var console=window._dragConsole;
	 	f_core.SetCookieValue("consoleWidth", console._console.clientWidth);
		f_core.SetCookieValue("consoleHeight", console._console.clientHeight);
 	
		window._dragConsole=undefined;
		window._draggedComponent=undefined;
	},
	/**
	 * @method private static
	 * @context event:evt	 
	 */
	_ResizeCursorDragMove: function(evt) {
 		if (!evt) {
 			evt = f_core.GetJsEvent(this);
 		}
 
 	 	var console=window._dragConsole;
 		
		var eventPos=f_core.GetJsEventPosition(evt, document);
		var cursorPos=f_core.GetAbsolutePosition(console._console);	

		var dx=eventPos.x-cursorPos.x-console._dragDeltaX;
		var dy=eventPos.y-(cursorPos.y+console._console.clientHeight)-console._dragDeltaY;

		console._userWidth=console._console.clientWidth-dx;
		console._userHeight=console._console.clientHeight+dy;
	
		var curs;
		if (Math.abs(dx)<Math.abs(dy)) {
			curs="n-resize";
			
		} else if (Math.abs(dx)>Math.abs(dy)) {
			curs="e-resize";
		}
	
		if (curs) {
			document.body.style.cursor=curs;
		}
		
		console._updateSizes();
						
		return f_core.CancelJsEvent(evt);	
	}
}
var __members = {
	f_consoleAppender: function() {
		this.f_super(arguments);
		
		this._cnt=0;
	
		f_log.AddAppenders(this);
		
		var w=f_core.GetCookieValue("consoleWidth");
		if (w) {
			this._userWidth=parseInt(w, 10);
		}
		var h=f_core.GetCookieValue("consoleHeight");
		if (h) {
			this._userHeight=parseInt(h, 10);
		}
		var closed=f_core.GetCookieValue("consoleClosed");
		if (closed=="true") {
			this._closed=true;
		}
		var att=f_core.GetCookieValue("consoleError");
		this._showError=(!att)?true:(att=="true");

		var att=f_core.GetCookieValue("consoleWarning");
		this._showWarning=(!att)?true:(att=="true");

		var att=f_core.GetCookieValue("consoleInfo");
		this._showInfo=(!att)?true:(att=="true");

		var att=f_core.GetCookieValue("consoleDebug");
		this._showDebug=(!att)?true:(att=="true");

		var self=this;
		this._performDocumentComplete=function() {
			f_core.RemoveEventListener(window, "load", self._performDocumentComplete);
			self._performDocumentComplete=undefined;
			self._documentCompleted=true;
			
			var pipe=self._pipe;
			if (!pipe) {
				return;
			}
			self._pipe=undefined;
			
			for(var i=0;i<pipe.length;i++) {
				self.f_doAppend(pipe[i]);
			}			
		};
		
		f_core.AddEventListener(window, "load", this._performDocumentComplete);
	},
	f_finalize: function() {
	
		this._console=undefined;
		this._list=undefined;
		this._body=undefined;
		this._documentCompleted=undefined; // boolean  - mais on le laisse -
		this._pipe=undefined;
//		this._cnt=undefined; // number
		this._buttons=undefined;

		var documentComplete=this._performDocumentComplete;
		if (documentComplete) {
			this._performDocumentComplete=undefined

			f_core.RemoveEventListener(window, "load", documentComplete);
		}
		
		var button=this._button;
		if (button) {
			this._button=undefined;
			button.onclick=null;
			button._console=undefined;
			
			f_core.VerifyProperties(button);
		}
		
		var button=this._clearButton;
		if (button) {
			this._clearButton=undefined;
			button.onclick=null;
			button._console=undefined;
			
			f_core.VerifyProperties(button);
		}
		
		var button=this._clearButton;
		if (button) {
			this._clearButton=undefined;
			button.onclick=null;
			button._console=undefined;
			
			f_core.VerifyProperties(button);
		}
		
		var button=this._errorButton;
		if (button) {
			this._errorButton=undefined;
			button.onclick=null;
			button._console=undefined;
			
			f_core.VerifyProperties(button);
		}
		
		var button=this._warningButton;
		if (button) {
			this._warningButton=undefined;
			button.onclick=null;
			button._console=undefined;
			
			f_core.VerifyProperties(button);
		}
		
		var button=this._infoButton;
		if (button) {
			this._infoButton=undefined;
			button.onclick=null;
			button._console=undefined;
			
			f_core.VerifyProperties(button);
		}
		
		var button=this._debugButton;
		if (button) {
			this._debugButton=undefined;
			button.onclick=null;
			button._console=undefined;
			
			f_core.VerifyProperties(button);
		}
	
		this.f_super(arguments);
	},
	/**
	 * @method public
	 */
	f_doAppend: function(event) {
		if (!this._documentCompleted) {
			var pipe=this._pipe;
			if (!pipe) {
				pipe=new Array;
				this._pipe=pipe;
			}
			
			pipe.push(event);
			return;
		}
		var console=this._console;
		if (!console) {
			f_core.Assert(event.window, "No window defined !");
			
			console=this._makeConsole(event.window.document);
		}
	
		var list=this._list;
		
		var doc=list.ownerDocument;
		var li=doc.createElement("li");
		li.className="f_consoleAppender_item";
		
		if ((this._cnt++) % 2) {
			li.style.backgroundColor="#eeeeee";
			
		} else {
			li.style.backgroundColor="#dedede";
		}
		
		var date=event.date;
		var h=date.getHours();
		if (h<10) h="0"+h;
		var m=date.getMinutes();
		if (m<10) m="0"+m;
		var s=date.getSeconds();
		if (s<10) s="0"+s;
		var ms=date.getMilliseconds();
		if (ms<10) ms="0"+ms;
		if (ms<100) ms="0"+ms;
	
		var msg=event.message;
		var title="["+h+":"+m+":"+s+"."+ms+"]";

		if (event.window!=window) {
			title+=" {window: "+window.name+"}";
		}
	
		switch(event.level) {
		case f_log.FATAL:
			title+=" FATAL";
			break;
		case f_log.ERROR:
			title+=" ERROR";
			break;
		case f_log.WARN:
			title+=" WARN";
			break;
		case f_log.INFO:
			title+=" INFO";
			break;
		case f_log.DEBUG:
			title+=" DEBUG";
			break;
		}
		li._level=event.level;
		
		if (msg) {
			title+=" "+event.name;
			
		} else {
			msg=event.name;
		}

		var span=doc.createElement("span");
		span.style.fontSize="small";
		span.style.fontWeight="bold";
		span.style.display="block";
		span.appendChild(doc.createTextNode(title+" ")); // L'espace est pour le copier/coller
		
		li.appendChild(span);
		
		if (typeof(msg)=="string") {
			var sp=msg.split("\n");
			for(var i=0;i<sp.length;i++) {
				if (i) {
					li.appendChild(doc.createElement("br"));
				}
				
				li.appendChild(doc.createTextNode(sp[i]));
			}
		}
				
		var ex=event.exception;
		if (ex) {
			if (typeof(ex)!="string") {
				li.appendChild(doc.createElement("hr"));
				
				var span=doc.createElement("span");
				span.style.fontSize="small";
				span.style.color="#666";
				span.appendChild(doc.createTextNode("Exception: "));
				li.appendChild(span);
			}
							
			if (ex.fileName && ex.lineNumber) {
				var span=doc.createElement("span");
				span.style.fontSize="small";
				span.appendChild(doc.createTextNode("    ("));

				var link=doc.createElement("a");
				link.target="_blank";
				link.href="view-source:"+ex.fileName+"#"+ex.lineNumber;
				span.appendChild(link);
				link.appendChild(doc.createTextNode(ex.fileName+":"+ex.lineNumber));

				span.appendChild(doc.createTextNode(")"));
				li.appendChild(span);
			}	
					
			
			li.appendChild(doc.createElement("br"));
					
			if (ex.number) {
				var span=doc.createElement("span");
				span.style.fontSize="small";
				span.style.color="#888";
				span.appendChild(doc.createTextNode("Number: "));

				var span=doc.createElement("span");
				span.style.fontSize="small";
				span.appendChild(doc.createTextNode((ex.number & 0xffff).toString(16)));
			}
					
			if (ex.name) {
				var span=doc.createElement("span");
				span.style.fontSize="small";
				span.style.color="#888";
				span.appendChild(doc.createTextNode("Name: "));

				var span=doc.createElement("span");
				span.style.fontSize="small";
				span.appendChild(doc.createTextNode(ex.name));
			}

			var m=ex.message;
			if (!m && typeof(ex)=="string") {
				m=ex;
			}
			if (typeof(m)=="string") {
				var span=doc.createElement("span");
				span.style.fontSize="small";
				span.style.display="block";
				
				var span2=doc.createElement("span");
				span2.style.color="#666";
				span2.appendChild(doc.createTextNode("Message: "));
				span.appendChild(span2);
				
				var sp=m.split('\n');
				for(var i=0;i<sp.length;i++) {
					if (i) {
						span.appendChild(doc.createElement("br"));
					}
					
					//var s=sp[i];
					span.appendChild(doc.createTextNode(sp[i]));
				}
				li.appendChild(span);
			}

			var m2=ex.description;
			if (m2 && m!=m2) {
				var span=doc.createElement("span");
				span.style.fontSize="small";
				span.style.display="block";
				
				var span2=doc.createElement("span");
				span2.style.color="#666";
				span2.appendChild(doc.createTextNode("Description: "));
				span.appendChild(span2);
				
				var sp=m2.split('\n');
				for(var i=0;i<sp.length;i++) {
					if (i) {
						span.appendChild(doc.createElement("br"));
					}
					
					//var s=sp[i];
					span.appendChild(doc.createTextNode(sp[i]));
				}
				li.appendChild(span);
			}
						
			var m=ex.stack;
			if (typeof(m)=="string") {
				var span=doc.createElement("span");
				span.style.fontSize="small";
				span.style.display="block";
				
				var span2=doc.createElement("span");
				span2.style.color="#666";
				span2.appendChild(doc.createTextNode("Stack: "));
				span.appendChild(span2);

				var sp=m.split('\n');
				for(var i=0;i<sp.length;i++) {
					if (i) {
						span.appendChild(doc.createElement("br"));
					}
					
					//var s=sp[i];
					span.appendChild(doc.createTextNode(sp[i]));
				}
				li.appendChild(span);
			}
		}
		
		this._filterLI(li);
		
		list.appendChild(li);
		
		li.scrollIntoView(false);
	},
	_makeConsole: function(doc) {
		var table=doc.createElement("div");
		table.className="f_consoleAppender_table";

		var tds=doc.createElement("div");
		tds.className="f_consoleAppender_command";
		table.appendChild(tds);
		
		var div=doc.createElement("div");
		div.className="f_consoleAppender_body";
		table.appendChild(div);
		this._body=div;
		if (this._closed) {
			div.style.display="none";
		}
		
		if (f_core.IsGecko()) {
			div.style.overflow="-moz-scrollbars-vertical";
		}
			
		var ul=doc.createElement("ul");
		ul.className="f_consoleAppender_list";
		div.appendChild(ul);
		this._list=ul;
		
		var TABLE=true;

		if (TABLE) {
			var buttons=doc.createElement("div");
			tds.appendChild(buttons);
			buttons.className="f_consoleAppender_buttons";
			if (this._closed) {
				buttons.style.display="none";
			}
			this._buttons=buttons;
			
			var img=doc.createElement("img");
			img.className="f_consoleAppender_button";
			buttons.appendChild(img);
			img.onclick=f_consoleAppender._ClearButtonClick;
			img._console=this;
			img.width=16;
			img.height=16;
			img.title="Permanently delete all entries";
			img.src=f_env.GetStyleSheetBase()+"/consoleAppender/delete.gif";
			this._clearButton=img;
			
			var img=doc.createElement("img");
			img.className="f_consoleAppender_button";
			buttons.appendChild(img);
			img.onclick=f_consoleAppender._ErrorButtonClick;
			img._console=this;
			img.width=16;
			img.height=16;
			img.title="Shows/hides all Errors";
			img.src=f_env.GetStyleSheetBase()+"/consoleAppender/error.gif";
			this._errorButton=img;
			
			var img=doc.createElement("img");
			img.className="f_consoleAppender_button";
			buttons.appendChild(img);
			img.onclick=f_consoleAppender._WarningButtonClick;
			img._console=this;
			img.width=16;
			img.height=16;
			img.title="Shows/hides all Warnings";
			img.src=f_env.GetStyleSheetBase()+"/consoleAppender/warning.gif";
			this._warningButton=img;
			
			var img=doc.createElement("img");
			img.className="f_consoleAppender_button";
			buttons.appendChild(img);
			img.onclick=f_consoleAppender._InfoButtonClick;
			img._console=this;
			img.width=16;
			img.height=16;
			img.title="Shows/hides all Infos";
			img.src=f_env.GetStyleSheetBase()+"/consoleAppender/info.gif";
			this._infoButton=img;
			
			var img=doc.createElement("img");
			img.className="f_consoleAppender_button";
			buttons.appendChild(img);
			img.onclick=f_consoleAppender._DebugButtonClick;
			img._console=this;
			img.width=16;
			img.height=16;
			img.title="Shows/hides all Debugs";
			img.src=f_env.GetStyleSheetBase()+"/consoleAppender/debug.gif";
			this._debugButton=img;
		}
		
		var img=doc.createElement("img");
		img.className="f_consoleAppender_image";
		tds.appendChild(img);
		
		img.onclick=f_consoleAppender._OpenCloseButtonClick;
		img._console=this;
		
		this._button=img;
		this._updateButton();
		
		tds.onmousedown=f_consoleAppender._ResizeClick;
		tds._console=this;
		
		doc.body.appendChild(table);
		
		this._console=table;
		this._updateConsole();
		
		this._updateSizes();
	},
	_openClose: function() {
		//var console=this._console;
		
		var display="block";

		this._closed=!this._closed;
		if (this._closed) {
			// On ferme !
			
			display="none";
		}
		
		f_core.SetCookieValue("consoleClosed", (this._closed)?"true":"false");
		
		this._updateConsole();
		this._body.style.display=display;
		if (this._buttons) {
			this._buttons.style.display=display;
		}
		this._updateSizes();

		if (!this._closed) {
			// On se place sur le dernier LI
			var dh=this._body.scrollHeight-this._list.scrollHeight;
			if (dh>0) {
				this._body.scrollTop=this._list.scrollHeight+dh;
			}
		}
		
		this._updateButton();
	},
	_updateConsole: function() {
		var borderStyle=(this._closed)?"none":"solid";
		
		var style=this._console.style;
		
		style.borderLeftStyle=borderStyle;
		style.borderTopStyle=borderStyle;
		style.borderBottomStyle=borderStyle;
	},
	_updateButton: function() {
		var url="buttonLeft.gif";
		if (!this._closed) {
			url="button.gif";
		}
		
		this._button.src=f_env.GetStyleSheetBase()+"/consoleAppender/"+url;
	},
	_updateSizes: function() {
		var consoleStyle=this._console.style;
		
		if (this._closed) {
			consoleStyle.width="16px";
			consoleStyle.height="41px";

			this._button.style.top="0px";
			this._body.style.width="auto";
			this._body.style.height="auto";
			
			return;
		}

		var s=f_core.GetViewSize();
		
		var w=this._userWidth;
		var h=this._userHeight;
		if (!w) {
			w=Math.floor(s.width/3);
			h=Math.floor(s.height/3);
			
		} else {
			if (w>s.width) {
				w=s.width-32;
			}
			if (h>s.height) {
				h=s.height-32;
			}
		}
		
		consoleStyle.width=w+"px";
		consoleStyle.height=h+"px";
		
		w-=16;
		if (f_core.IsInternetExplorer()) {
			w-=2;
			h-=2;
		}
		
		this._body.style.width=w+"px";
		this._body.style.height=h+"px";
		this._button.style.top="50%";
	},
	_filterLIs: function() {
		var nodes=this._list.childNodes;
		
		for(var i=0;i<nodes.length;i++) {
			var li=nodes[i];
			
			this._filterLI(li);
		}
	},
	_filterLI: function(li) {
		var visible=true;
		
		switch(li._level) {
		case f_log.FATAL:
			visible=true;
			break;
			
		case f_log.ERROR:
			visible=this._showError;
			break;
			
		case f_log.WARN:
			visible=this._showWarning;
			break;
			
		case f_log.INFO:
			visible=this._showInfo;
			break;
			
		case f_log.DEBUG:
			visible=this._showDebug;
			break;
		}
		
		
		if (!visible) {
			if (li.style.display!="none") {
				li.style.display="none";
			}
			return;
		}
		
		if (li.style.display=="none") {
			li.style.display="block";
		}
		return;
	}
}

new f_class("f_consoleAppender", {
	extend: f_object,
	statics: __statics,
	members: __members
});
