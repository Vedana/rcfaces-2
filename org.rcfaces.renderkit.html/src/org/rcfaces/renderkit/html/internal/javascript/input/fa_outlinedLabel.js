/*
 * $Id: fa_outlinedLabel.js,v 1.1 2013/11/13 12:53:28 jbmeslin Exp $
 */
 
/**
 *
 *
 * @aspect fa_outlinedLabel
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/11/13 12:53:28 $
 */
var __statics = {
	/**
	 * @method private static
	 * @param String contentIndex
	 * @return Object
	 */
	ParseOutlinedLabelMethod: function(method) {
		var obj={};
		if (!method) {
			return obj;
		}
		
		var ss=method.split(",");
		for(var i=0;i<ss.length;i++) {
			var t=ss[i];
			switch(t) {
			case "ic":
				obj._ignoreCase=true;					
				break;
			case "ia":
				obj._ignoreAccent=true;					
				break;
			case "sw":
				obj._startsWith=true;					
				break;
			case "ft":
				obj._fullText=true;					
				break;
			case "mt":
				obj._multiple=true;					
				break;
			case "ew":
				obj._eachWord=true;					
				break;
			case "se":
				obj._server=true;					
				break;
			}
		}
		
		return obj;
	},
	/**
	 * @method private static
	 * @param String text
	 * @return String
	 */
	RemoveAccents: function(text) {
		if (window.f_vb) {
			var ret = f_vb.RemoveAccents(text);
			
			return ret;
		}
		
		f_core.Assert(true, "f_indexedData._removeAccent: Not implemented include f_vb class !");
		
		return text;
	},
	/**
	 * @field private static final
	 */
	_SearchRegexp: /[\w\-áãàâäåéèêëíìîïóõòôöúùûüµýÿçñÀÁÂÃÄÅÈÉÊËÌÍÎÏÓÔÕÖÒÙÚÛÜÝÇÑ]+/g
};


var __members={
	
	/**
	 * @field protected String
	 */
	_outlinedLabel: undefined,
	
	/**
	 * @field protected String
	 */
	_normalizedOutlinedLabel: undefined,
	
	/**
	 * @field protected String
	 */
	_outlinedLabelMethod: undefined,
		
	fa_outlinedLabel: function() {
		var method=f_core.GetAttributeNS(this, "outlinedLabelMethod");
		if (method) {
			this._outlinedLabelMethod=fa_outlinedLabel.ParseOutlinedLabelMethod(method);
		}
		var outlinedLabel=f_core.GetAttributeNS(this, "outlinedLabel");
		this.f_setOutlinedLabel(outlinedLabel);
	},
	/**
	 * @method public
	 * @return String
	 */
	fa_getOutlinedLabel: function() {
		return this._outlinedLabel;
	},
	/**
	 * @method public
	 * @param String newOutlinedLabel
	 * @return void
	 */
	f_setOutlinedLabel: function(newOutlinedLabel) {
		
		if (this._outlinedLabel==newOutlinedLabel) {
			return;
		}
		
		this._normalizedOutlinedLabel=this._normalizeOutlinedLabel(newOutlinedLabel);
		
		this._outlinedLabel=newOutlinedLabel;
		
		this.fa_updateOutlinedLabels(newOutlinedLabel);
		
		this.f_setProperty(f_prop.OUTLINED_LABEL, newOutlinedLabel);
	},
	/**
	 * @method protected
	 * @param String m
	 * @return String
	 */
	_normalizeOutlinedLabel: function(m) {
		var method=this._outlinedLabelMethod;
		
		if (!m || !method) {
			return m;
		}
		if (method._ignoreCase) {
			m=m.toLowerCase();
		}
		if (method._ignoreAccent) {
			m=fa_outlinedLabel.RemoveAccents(m);
		}
		
		return m;
	},
	/**
	 * @method private
	 * @param String labelText
	 * @param String outlinedLabel
	 * @return int[] 
	 */
	_searchOutlinedLabelIndexes: function(labelText, outlinedLabel) {
		var indexes=new Array;
		var method=this._outlinedLabelMethod;
		labelText=this._normalizeOutlinedLabel(labelText);
		
		if (method && (method._eachWord || method._startsWith)) {
			var regexp=fa_outlinedLabel._SearchRegexp;
			regexp.index=0;
			
			for(;;) {
				var ret = regexp.exec(labelText);
				if (!ret) {
					break;
				}
				
				if (method._eachWord && ret[0]!=label) {
					continue;
				}
				if (method._startsWith && ret[0].indexOf(outlinedLabel)!=0) {
					continue;
				}
				
				indexes.push(ret.index);
			}
		} else {
			var start=0;
			for(;;) {
				var idx=labelText.indexOf(outlinedLabel, start);
				if (idx<0) {
					break;
				}
				
				indexes.push(idx);
				
				start=idx+outlinedLabel.length;
				
				if (!method || !method._multiple) {
					break;
				}
			}
		}
			
		return indexes;
	},
	/**
	 * @method protected
	 * @param String labelText
	 * @param HtmlElement labelComponent
	 * @return Boolean Returns <code>TRUE</code> if the token is found in the label.
	 */
	fa_setOutlinedSpan: function(labelText, labelComponent) {
		var outlinedLabel=this._normalizedOutlinedLabel;

		var indexes=undefined;
		if (outlinedLabel) {
			indexes=this._searchOutlinedLabelIndexes(labelText, outlinedLabel);
		}
		
		if (!indexes || !indexes.length) {
			if (!labelComponent.firstChild || labelComponent._outlined) {
				labelComponent._outlined=undefined;
				
				f_core.SetTextNode(labelComponent, labelText);
			}
			return false;
		}
		
		labelComponent._outlined=true;
		
		for(;labelComponent.firstChild;) {
			labelComponent.removeChild(labelComponent.firstChild);
		}
		
		var doc=labelComponent.ownerDocument;
		
		var cur=0;
		for(var i=0;i<indexes.length;i++) {
			var idx=indexes[i];
			
			if (idx>cur) {
				f_core.CreateTextNode(labelComponent, labelText.substring(cur, idx));
			}
			cur=idx+outlinedLabel.length;
			
			f_core.CreateElement(labelComponent, "strong", {
				textNode: labelText.substring(idx, cur),
				className: "f_outlinedLabel"
			});
		}
		
		if (cur<labelText.length) {
			var textNode=doc.createTextNode(labelText.substring(cur));
			f_core.AppendChild(labelComponent, textNode);
		}		
		
		return true;
	},

	/**
	 * @method protected abstract
	 * @return void
	 */
	f_setProperty: f_class.ABSTRACT,
	
	/**
	 * @method protected abstract
	 * @return void
	 */
	fa_updateOutlinedLabels: f_class.ABSTRACT
};

new f_aspect("fa_outlinedLabel", __statics, __members);
