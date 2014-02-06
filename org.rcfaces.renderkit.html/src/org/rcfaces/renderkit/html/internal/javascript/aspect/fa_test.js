var __members = {
	fa_test: { 
		after: function() {
			alert("AFTER cons !");
		},
		before: function() {
			alert("BEFORE cons !");
		}
	},
	f_finalize: {
		before: function() {
			alert("BEFORE dest");
		},
		after: function() {
			alert("AFTER dest !");
		}
	}
}

var __abstract = [
]

var fa_disabled=new f_aspect("fa_test", null, __members, __abstract);
