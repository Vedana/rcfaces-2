/**
 *
 * A JavaScript implementation of the RSA Data Security, Inc. MD5 Message
 * Digest Algorithm, as defined in RFC 1321.
 * Version 2.1 Copyright (C) Paul Johnston 1999 - 2002.
 * Other contributors: Greg Holt, Andrew Kepert, Ydnar, Lostinet
 * Distributed under the BSD License
 * See http://pajhome.org.uk/crypt/md5 for more info
 * 
 * @class f_md5 extends f_object
 * @author Paul Johnston and other contributors
 */


var __statics = {
	/**
	 * bits per input character. 8 - ASCII; 16 - Unicode
	 *
	 * @field private static final Number
	 */
	_CHRSZ: 8,
	
	/**
	 * Convert a string to an array of little-endian words
	 * If f_md5.CHRSZ is ASCII, characters >255 have their hi-byte silently ignored.
	 * 
	 * @method private static 
	 */
	_Str2binl: function(str) {
	  var bin = Array();
	  var mask = (1 << f_md5._CHRSZ) - 1;
	  for(var i = 0; i < str.length * f_md5._CHRSZ; i += f_md5._CHRSZ) {
	    bin[i>>5] |= (str.charCodeAt(i / f_md5._CHRSZ) & mask) << (i%32);
	  }
	  return bin;
	},
	
	/**
	 * Convert an array of little-endian words to a string
	 * 
	 * @method private static 
	 */
	_Binl2str: function(bin) {
	  var str = "";
	  var mask = (1 << f_md5._CHRSZ) - 1;
	  for(var i = 0; i < bin.length * 32; i += f_md5._CHRSZ) {
		str += String.fromCharCode((bin[i>>5] >>> (i % 32)) & mask);
	  }
	  return str;
	},
	
	/**
	 * Convert an array of little-endian words to a hex string.
	 * 
	 * @method private static 
	 */
	_Binl2hex: function(binarray, hexcase) {
		var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
		var str = "";
		for(var i = 0; i < binarray.length * 4; i++) {
			str += hex_tab.charAt((binarray[i>>2] >> ((i%4)*8+4)) & 0xF) +
				hex_tab.charAt((binarray[i>>2] >> ((i%4)*8  )) & 0xF);
		}
		return str;
	},
	
	/**
	 * Convert an array of little-endian words to a base-64 string
	 * 
	 * @method private static 
	 */
	_Binl2b64: function(binarray, b64pad) {
		var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
		var str = "";
		for(var i = 0; i < binarray.length * 4; i += 3) {
			var triplet = (((binarray[i   >> 2] >> 8 * ( i   %4)) & 0xFF) << 16)
	                | (((binarray[i+1 >> 2] >> 8 * ((i+1)%4)) & 0xFF) << 8 )
	                |  ((binarray[i+2 >> 2] >> 8 * ((i+2)%4)) & 0xFF);
			
			for(var j = 0; j < 4; j++) {
				if(i * 8 + j * 6 > binarray.length * 32) {
					if (b64pad) {
						str += b64pad;
					}
					continue;
				}

				str += tab.charAt((triplet >> 6*(3-j)) & 0x3F);
			}
		}
		return str;
	},
	/**
	 * Calculate the MD5 of an array of little-endian words, and a bit length
	 *
	 * @method private static
	 */
	_ComputeMd5: function (x, len) {
		/* append padding */
		x[len >> 5] |= 0x80 << ((len) % 32);
		x[(((len + 64) >>> 9) << 4) + 14] = len;
	
		var md5_ff=f_md5._FF;
		var md5_gg=f_md5._GG;
		var md5_hh=f_md5._HH;
		var md5_ii=f_md5._II;
		var md5_safe_add=f_md5._Safe_add;
	
		var a =  1732584193;
		var b = -271733879;
		var c = -1732584194;
		var d =  271733878;
		
		for(var i = 0; i < x.length; i += 16) {
		    var olda = a;
		    var oldb = b;
		    var oldc = c;
		    var oldd = d;
		
		    a = md5_ff(a, b, c, d, x[i+ 0], 7 , -680876936);
		    d = md5_ff(d, a, b, c, x[i+ 1], 12, -389564586);
		    c = md5_ff(c, d, a, b, x[i+ 2], 17,  606105819);
		    b = md5_ff(b, c, d, a, x[i+ 3], 22, -1044525330);
		    a = md5_ff(a, b, c, d, x[i+ 4], 7 , -176418897);
		    d = md5_ff(d, a, b, c, x[i+ 5], 12,  1200080426);
		    c = md5_ff(c, d, a, b, x[i+ 6], 17, -1473231341);
		    b = md5_ff(b, c, d, a, x[i+ 7], 22, -45705983);
		    a = md5_ff(a, b, c, d, x[i+ 8], 7 ,  1770035416);
		    d = md5_ff(d, a, b, c, x[i+ 9], 12, -1958414417);
		    c = md5_ff(c, d, a, b, x[i+10], 17, -42063);
		    b = md5_ff(b, c, d, a, x[i+11], 22, -1990404162);
		    a = md5_ff(a, b, c, d, x[i+12], 7 ,  1804603682);
		    d = md5_ff(d, a, b, c, x[i+13], 12, -40341101);
		    c = md5_ff(c, d, a, b, x[i+14], 17, -1502002290);
		    b = md5_ff(b, c, d, a, x[i+15], 22,  1236535329);
		
		    a = md5_gg(a, b, c, d, x[i+ 1], 5 , -165796510);
		    d = md5_gg(d, a, b, c, x[i+ 6], 9 , -1069501632);
		    c = md5_gg(c, d, a, b, x[i+11], 14,  643717713);
		    b = md5_gg(b, c, d, a, x[i+ 0], 20, -373897302);
		    a = md5_gg(a, b, c, d, x[i+ 5], 5 , -701558691);
		    d = md5_gg(d, a, b, c, x[i+10], 9 ,  38016083);
		    c = md5_gg(c, d, a, b, x[i+15], 14, -660478335);
		    b = md5_gg(b, c, d, a, x[i+ 4], 20, -405537848);
		    a = md5_gg(a, b, c, d, x[i+ 9], 5 ,  568446438);
		    d = md5_gg(d, a, b, c, x[i+14], 9 , -1019803690);
		    c = md5_gg(c, d, a, b, x[i+ 3], 14, -187363961);
		    b = md5_gg(b, c, d, a, x[i+ 8], 20,  1163531501);
		    a = md5_gg(a, b, c, d, x[i+13], 5 , -1444681467);
		    d = md5_gg(d, a, b, c, x[i+ 2], 9 , -51403784);
		    c = md5_gg(c, d, a, b, x[i+ 7], 14,  1735328473);
		    b = md5_gg(b, c, d, a, x[i+12], 20, -1926607734);
		
		    a = md5_hh(a, b, c, d, x[i+ 5], 4 , -378558);
		    d = md5_hh(d, a, b, c, x[i+ 8], 11, -2022574463);
		    c = md5_hh(c, d, a, b, x[i+11], 16,  1839030562);
		    b = md5_hh(b, c, d, a, x[i+14], 23, -35309556);
		    a = md5_hh(a, b, c, d, x[i+ 1], 4 , -1530992060);
		    d = md5_hh(d, a, b, c, x[i+ 4], 11,  1272893353);
		    c = md5_hh(c, d, a, b, x[i+ 7], 16, -155497632);
		    b = md5_hh(b, c, d, a, x[i+10], 23, -1094730640);
		    a = md5_hh(a, b, c, d, x[i+13], 4 ,  681279174);
		    d = md5_hh(d, a, b, c, x[i+ 0], 11, -358537222);
		    c = md5_hh(c, d, a, b, x[i+ 3], 16, -722521979);
		    b = md5_hh(b, c, d, a, x[i+ 6], 23,  76029189);
		    a = md5_hh(a, b, c, d, x[i+ 9], 4 , -640364487);
		    d = md5_hh(d, a, b, c, x[i+12], 11, -421815835);
		    c = md5_hh(c, d, a, b, x[i+15], 16,  530742520);
		    b = md5_hh(b, c, d, a, x[i+ 2], 23, -995338651);
		
		    a = md5_ii(a, b, c, d, x[i+ 0], 6 , -198630844);
		    d = md5_ii(d, a, b, c, x[i+ 7], 10,  1126891415);
		    c = md5_ii(c, d, a, b, x[i+14], 15, -1416354905);
		    b = md5_ii(b, c, d, a, x[i+ 5], 21, -57434055);
		    a = md5_ii(a, b, c, d, x[i+12], 6 ,  1700485571);
		    d = md5_ii(d, a, b, c, x[i+ 3], 10, -1894986606);
		    c = md5_ii(c, d, a, b, x[i+10], 15, -1051523);
		    b = md5_ii(b, c, d, a, x[i+ 1], 21, -2054922799);
		    a = md5_ii(a, b, c, d, x[i+ 8], 6 ,  1873313359);
		    d = md5_ii(d, a, b, c, x[i+15], 10, -30611744);
		    c = md5_ii(c, d, a, b, x[i+ 6], 15, -1560198380);
		    b = md5_ii(b, c, d, a, x[i+13], 21,  1309151649);
		    a = md5_ii(a, b, c, d, x[i+ 4], 6 , -145523070);
		    d = md5_ii(d, a, b, c, x[i+11], 10, -1120210379);
		    c = md5_ii(c, d, a, b, x[i+ 2], 15,  718787259);
		    b = md5_ii(b, c, d, a, x[i+ 9], 21, -343485551);
		
		    a = md5_safe_add(a, olda);
		    b = md5_safe_add(b, oldb);
		    c = md5_safe_add(c, oldc);
		    d = md5_safe_add(d, oldd);
		}
	  
		return Array(a, b, c, d);
	},
	
	/**
	 * Calculate the HMAC-MD5, of a key and some data
	 *
	 * @method private static
	 */
	_ComputeHmac: function(key, data) {
	  var bkey = f_md5._Str2binl(key);
	  if(bkey.length > 16) {
	  	bkey = f_md5._ComputeMd5(bkey, key.length * f_md5._CHRSZ);
	  }
	
	  var ipad = Array(16);
	  var opad = Array(16);
	  for(var i = 0; i < 16; i++) {
	    ipad[i] = bkey[i] ^ 0x36363636;
	    opad[i] = bkey[i] ^ 0x5C5C5C5C;
	  }
	
	  var hash = f_md5._ComputeMd5(ipad.concat(f_md5._Str2binl(data)), 512 + data.length * f_md5._CHRSZ);
	  return f_md5._ComputeMd5(opad.concat(hash), 512 + 128);
	},

	
	/**
	 * @method private static final
	 */
	_CMN: function(q, a, b, x, s, t) {
		var sa = f_md5._Safe_add;
		var br = f_md5._Bit_rol; 
		return sa(
				br(
		  		sa(
		  			sa(a, q), 
		  			sa(x, t)),
		  		s),
	  		b);
	},
	
	/**
	 * @method private static final
	 */
	_FF: function(a, b, c, d, x, s, t) {
		return f_md5._CMN((b & c) | ((~b) & d), a, b, x, s, t);
	},
	
	/**
	 * @method private static final
	 */
	_GG: function (a, b, c, d, x, s, t) {
		return f_md5._CMN((b & d) | (c & (~d)), a, b, x, s, t);
	},
	
	/**
	 * @method private static final
	 */
	_HH: function(a, b, c, d, x, s, t) {
		return f_md5._CMN(b ^ c ^ d, a, b, x, s, t);
	},
	
	/**
	 * @method private static final
	 */
	_II: function(a, b, c, d, x, s, t) {
		return f_md5._CMN(c ^ (b | (~d)), a, b, x, s, t);
	},
		
	/**
	 * Add integers, wrapping at 2^32. This uses 16-bit operations internally
	 * to work around bugs in some JS interpreters.
	 *
	 *  @method private static final
	 */
	_Safe_add: function(x, y) {
		var lsw = (x & 0xFFFF) + (y & 0xFFFF);
		var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
		
		return (msw << 16) | (lsw & 0xFFFF);
	},
	
	/**
	 * Bitwise rotate a 32-bit number to the left.
	 *
	 * @method private static final
	 */
	_Bit_rol: function(num, cnt) {
		return (num << cnt) | (num >>> (32 - cnt));
	}
}

var __members = {
	/**
	 * hex output format. 0 - lowercase; 1 - uppercase
	 * 
	 * @field private Number
	 */
	_hexcase: undefined,
	
	/**
	 * base-64 pad character. "=" for strict RFC compliance
	 * 
	 * @field private String
	 */
	_b64pad: undefined,
	
	/**
	 * @field private String
	 */
	_buffer: undefined,

	/**
	 * @method public
	 * @param optional String s
	 */
	f_md5: function(s) {
		this.f_super(arguments);

		this.f_reset();
		
		if (typeof(s)=="string") {
			this.f_append(s);
		}
	},

	/*
	f_finalize: function() {
		this._buffer=undefined; // string

		this.f_super(arguments);
	},
	*/
	
	/**
	 * @method public
	 * @param String s 
	 * @return void
	 */
	f_append: function(s) {
		this._buffer+=s;
	},
	
	
	/**
	 * @method public
	 * @return void
	 */
	f_reset: function() {
		this._buffer="";
	},

	/**
	 * @method public
	 * @return String
	 */
	f_toHexMd5: function() {
		var s=this._buffer;
		
		return f_md5._Binl2hex(f_md5._ComputeMd5(f_md5._Str2binl(s), s.length * f_md5._CHRSZ), this._hexcase);
	},	

	/**
	 * @method public
	 * @return String
	 */
	f_toBase64Md5: function() { 
		var s=this._buffer;
		
		return f_md5._Binl2b64(f_md5._ComputeMd5(f_md5._Str2binl(s), s.length * f_md5._CHRSZ), this._b64pad);
	},
	
	/**
	 * @method public
	 * @return String
	 */
	f_toMd5: function() { 
		var s=this._buffer;
		
		return f_md5._Binl2str(f_md5._ComputeMd5(f_md5._Str2binl(s), s.length * f_md5._CHRSZ));
	},	

	/**
	 * @method public
	 * @param String key
	 * @return String
	 */
	f_toHexHmacMd5: function(key) {
		var data=this._buffer;
		
		return f_md5._Binl2hex(f_md5._ComputeHmac(key, data), this._hexcase); 
	},

	/**
	 * @method public
	 * @param String key
	 * @return String
	 */
	f_toBase64HmacMd5: function(key) {
		var data=this._buffer;
		
		return f_md5._Binl2b64(f_md5._ComputeHmac(key, data), this._b64pad); 
	},

	/**
	 * @method public
	 * @param String key
	 * @return String
	 */
	f_toHmacMd5: function(key) { 
		var data=this._buffer;
		
		return f_md5._Binl2str(f_md5._ComputeHmac(key, data)); 
	},

	/**
	 * Perform a simple self-test to see if the VM is working
	 * @method hidden
	 */
	f_test: function() {
		return this.f_toHexMd5("abc") == "900150983cd24fb0d6963f7d28e17f72";
	}
}

new f_class("f_md5", {
	extend: f_object, 
	statics: __statics,
	members: __members
});
