/*
 * $Id: f_json.js,v 1.2 2013/11/13 12:53:29 jbmeslin Exp $
 */

/**
 *
 * @class f_json extends Object
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:29 $
 */
 
var __statics = {
	/**
	 * @field private static final Object
	 */
    _ENCODED: {
        '\b': '\\b',
        '\t': '\\t',
        '\n': '\\n',
        '\f': '\\f',
        '\r': '\\r',
        '"' : '\\"',
        '\\': '\\\\'
	},

	/**
	 * @method private static
	 * @param String
	 * @param Array
	 * @return void
	 */
	_ToJSONString: function(object, sb, filter) {
		switch(typeof(object)) {
		case "string":
            if (/["\\\x00-\x1f]/.test(object)) {
               sb.push('"', object.replace(/([\x00-\x1f\\"])/g, function (a, b) {
                    var c = f_json._ENCODED[b];
                    if (c) {
                        return c;
                    }
                    c = b.charCodeAt();
                    return '\\u00' +
                        Math.floor(c / 16).toString(16) +
                        (c % 16).toString(16);
                }), '"');
                return;
            }
            
            sb.append('"', object, '"');			
            return;
		
		case "boolean":
			sb.push(object);
			return;
		
		case "number":
		    sb.push(isFinite(object) ? object : null);
		    return;

		case "object":
			if (object===null) {
				sb.push(object);
				return;
			}
			
			if (object instanceof Array) {
				sb.push("[");
				for(var i=0;i<object.length;i++) {
					if (i>0) {
						sb.push(",");						
					}
					
					f_json._ToJSONString(object[i], sb);
				}
				sb.push("]");
				return;
			}
			
			if (object instanceof Date) {
				function f(n) {
		            return n < 10 ? "0" + n : n;
		        }
		
				sb.push('"', object.getFullYear(), "-",
		                f(object.getMonth() + 1), "-",
		                f(object.getDate()), "T",
		                f(object.getHours()), ":",
		                f(object.getMinutes()), ":",
		                f(object.getSeconds()), '"');
				return;
			}
			
			sb.push("{");
			var first=true;
	        for (var k in obj2) {
	            if (!obj2.hasOwnProperty(k)) {
	            	continue;
	            }
	         
	         	if (first) {
	         		first=false;
	         	} else {
	         		sb.push(",");
	         	}
	         
	         	sb.push(k, ":");       
				f_json._ToJSONString(obj2[k], sb);
	        }
	        sb.push("}");
	        return;
		}
		
		f_core.Debug(f_json, "_ToJSONString: Unsupported type: "+typeof(object)+" = "+object);
		return null;
	},

	/**
	 * @method public static
	 * @param String formattedObject A formatted format of a JSON object(s)
	 * @param optional function filter
	 * @return any
	 */
	Parse: function(formattedObject, filter) {
		f_core.Assert(typeof(formattedObject), "f_json.Parse: Invalid formattedObject parameter ("+formattedObject+")");

        function walk(k, v) {
            if (v && typeof(v)=== 'object') {
                for (var i in v) {
                    if (v.hasOwnProperty(i)) {
                        v[i] = walk(i, v[i]);
                    }
                }
            }
            return filter(k, v);
        }

        if (/^[,:{}\[\]0-9.\-+Eaeflnr-u \n\r\t]*$/.test(formattedObject.
                replace(/\\./g, '@').
                replace(/"[^"\\\n\r]*"/g, ''))) {

            var j = f_core.WindowScopeEval('(' + formattedObject + ')');

            if (typeof(filter)=== 'function') {
                j = walk('', j);
            }
            
            return j;
        }

		throw new SyntaxError('parseJSON');
	},
	/**
	 * @method public static
	 * @param Object objectToFormatToString The object which will be formatted
	 * @param optional function filter Filter function
	 * @return String
	 */
	Format: function(objectToFormatToString, filter) {
		f_core.Assert(filter && typeof(filter)!="function", "f_json.Format: Invalid filter parameter ("+filter+")");

		var sb=new Array;
		
		f_json._ToJSONString(objectToFormatToString, sb, filter);
		
		return sb.join("");
	}
}

new f_class("f_json", {
	statics: __statics
});
