/*
 * $Id: f_columnSortDialog.js,v 1.2 2013/11/13 12:53:28 jbmeslin Exp $
 */

/**
 * <p><strong>f_columnSortDialog</strong> represents columns Sort popup modal window.
 *
 * @class public final f_columnSortDialog extends f_dialog
 * @author Fred Lefevere-Laoide Lefevere-Laoide (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:28 $
 */
var __statics = {
	/**
	 * @field private static final
	 */
	_EVENTS: {
		selection: f_event.SELECTION
	},
		
	/**
	 * @field private static final
	 */
	_DEFAULT_FEATURES: {
		width: 250,
		height: 272,
		dialogPriority: 0,
		styleClass: "f_columnSortDialog",
		backgroundMode: f_shell.LIGHT_GREYED_BACKGROUND_MODE
	},
		
	/**
	 * Style constant for application modal behavior
	 * 
	 * @field public static final Number
	 */
	APPLY_BUTTON_STYLE: 1<<20,
	
	/**
	 * @field private static final String
	 */
	LIB_ASCENDANT: "ascendant",
	
	/**
	 * @field private static final String
	 */
	LIB_DESCENDANT: "descendant",
	
	/**
	 * @field private static final String
	 */
	_SORT_MANAGER_NAME: "dialog",

    /*
     * <p>js listener example</p>
     * dans le tag : SelectionListener="return ListenerExample(event);"
     *
     * @method public static
     * @param f_event evt
     * @return Boolean
     *
    ListenerExample: function(evt) {
    	var value = evt.f_getValue();
    	return true;
    },
    */
 
	/**
	 * @method public static
	 * @return void
	 */
	Initializer: function() {
		f_grid.RegisterSortManager(f_columnSortDialog._SORT_MANAGER_NAME, function(event) {
			var grid=event.f_getComponent();
			
			var dialog=f_columnSortDialog.f_newInstance(grid);
			
			dialog.f_open();
		});
	}   
};

var __members = {
	
	/**
	 * @field private f_columnSortConfigurator
	 */
	_configurator: undefined,
	
	/**
	 * @field private f_grid
	 */
	_grid: undefined,

	/**
	 * <p>Construct a new <code>f_columnSortDialog</code> with the specified
     * initial values.</p>
	 *
	 * @method public
	 * @param f_grid grid
	 */
	f_columnSortDialog: function(grid, style) {
		this.f_super(arguments, (style)?style:(f_shell.PRIMARY_MODAL_STYLE | 
			f_shell.TITLE_STYLE | 
			f_shell.CLOSE_STYLE | 
			f_shell.COPY_STYLESHEET));

		this._grid=grid;
		this._configurator=f_columnSortConfigurator.f_newInstance(this, grid);
		this.f_setShellFeatures();	
	},
	
	/**
	 * @method protected
	 * @return void
	 */
	f_setShellFeatures: function() {
		this.f_setTitle(f_resourceBundle.Get(f_columnSortDialog).f_get("TITLE"));
	},

	/**
	 * <p>Destruct a  <code>f_columnSortDialog</code>.</p>
	 *
	 * @method public
	 */
	f_finalize: function() {		
		// this._title=undefined; // String
		this._grid=undefined; // f_grid

		var configurator=this._configurator;
		if (configurator) {
			this._configurator=undefined;
						
			f_classLoader.Destroy(configurator);
		}
		
		this.f_super(arguments);
	},
	/**
	 * @method protected
	 * @return Object
	 */
	f_getDefaultFeatures: function() {
		return f_columnSortDialog._DEFAULT_FEATURES;
	},
	/**
	 *  <p>draw a message box.
	 *  </p>
	 *
	 * @method private 
	 * @param HTMLElement the base html element to construct the dialog
	 * @return void
	 */
	f_fillBody: function(base) {
		this.f_super(arguments, base);
		
     	f_core.Debug(f_columnSortDialog, "f_fillBody: entering ("+base+")");
		
		var cssClassBase = "f_columnSortDialog";

		var docBase = base.ownerDocument;
		var grid = this._grid;
		
		// form to catch the return
		var actForm = docBase.createElement("form");
		actForm.className="f_columnSortDialog_form";
		actForm.style.width=this.f_getWidth()+"px";
		actForm.style.height=this.f_getHeight()+"px";
		
		base.appendChild(actForm);
		//actForm._button=button;
		
		// Creation de la table
		var table = docBase.createElement("ul");
		actForm.appendChild(table);

		table.className = "f_columnSortDialog_dialog";
		
		//set size and pos
		table.style.width=this.f_getWidth()+"px";
		table.style.height=this.f_getHeight()+"px";
		table.width=this.f_getWidth();
		
		// Creation du corps de la popup
		var ligne = f_core.CreateElement(table, "li", {	
			className: cssClassBase+"_corps_tr"
		});
		
		var tableCorps = docBase.createElement("table");
		ligne.appendChild(tableCorps);
		tableCorps.cellPadding=0;
		tableCorps.cellSpacing=0;
		tableCorps.width="100%";
	
		var tbodCorps = docBase.createElement("tbody");
		tableCorps.appendChild(tbodCorps);
		
		// Corps de la popup : 3 (max) combos et des radios

		var _radios = new Array;
		var _selects = new Array;

		// var sortedCols = grid.f_getSortedColumns();
		var cols = f_columnSortConfigurator.GetColumns(grid);
		var nbCols = cols.length;
		if (nbCols > 3) {
			nbCols = 3;
		}

		for(var j=0;j<nbCols;j++) {
			// Creation de la ligne de libellé Trier par

			var ligneCorps = docBase.createElement("tr");
			tbodCorps.appendChild(ligneCorps);

			var cellCorps = docBase.createElement("td");
			ligneCorps.appendChild(cellCorps);
			cellCorps.width=20;
			
			var cellCorps = docBase.createElement("td");
			ligneCorps.appendChild(cellCorps);
			cellCorps.colSpan=2;
	
			var zone = docBase.createElement("label");
			cellCorps.appendChild(zone);
			zone.className = "f_columnSortDialog_text";
			
			var key=(j==0)?"SORT_BY":"NEXT_SORT_BY";			
			var sortBy=f_resourceBundle.Get(f_columnSortDialog).f_get(key);		
			f_core.SetTextNode(zone, sortBy);
	
			// ligne 1er combo et radios
			ligneCorps = docBase.createElement("tr");
			tbodCorps.appendChild(ligneCorps);

			var cellCorps = docBase.createElement("td");
			ligneCorps.appendChild(cellCorps);
			cellCorps.width=20;

			cellCorps = docBase.createElement("td");			
			ligneCorps.appendChild(cellCorps);
			cellCorps.width=120;
	
			var selectComp = docBase.createElement("select");
			cellCorps.appendChild(selectComp);

			selectComp.className = "f_columnSortDialog_select";
	
			_selects.push(selectComp);

			cellCorps = docBase.createElement("td");
			ligneCorps.appendChild(cellCorps);
			
			this._createTableRadio(cellCorps, "sort"+j, selectComp, _radios);			
	
			if (j+1<nbCols) {
				var ligneCorps = docBase.createElement("tr");
				tbodCorps.appendChild(ligneCorps);
				ligneCorps.style.height="20px";
				ligneCorps.className = "f_columnSortDialog_hr";
				
				var cellCorps = docBase.createElement("td");
				ligneCorps.appendChild(cellCorps);
				cellCorps.colSpan=3;
			
				var hr = docBase.createElement("div");
				cellCorps.appendChild(hr);
				
				f_core.SetTextNode(hr, " ");
			}
		}

		//fin de la table de corps

		// Creation de la ligne de boutons
		var ligne = f_core.CreateElement(table, "li", {	
			className: "f_shellDecorator_body_buttons"
		});
				
		// Bouton OK
		var okButton = f_core.CreateElement(ligne, "input", {
			type: "submit",
			className: "f_columnSortDialog_button",
			value: f_resourceBundle.Get(f_shell).f_get("VALID_BUTTON")
		});
		
		var applyButton=undefined;

		// Bouton Apply
		if (this._style & f_columnSortDialog.APPLY_BUTTON) {			
			applyButton = f_core.CreateElement(ligne, "input", {
				type: "button",
				className: "f_columnSortDialog_button",
				value: f_resourceBundle.Get(f_shell).f_get("APPLY_BUTTON")
			});
		}

		// Bouton Annuler
		var cancelButton = f_core.CreateElement(ligne, "input", {
			type: "button",
			className: "f_columnSortDialog_button",
			value: f_resourceBundle.Get(f_shell).f_get("CANCEL_BUTTON")
		});

		this._configurator.f_fillBody(_selects, _radios, okButton, cancelButton, applyButton);
	},
	
	/**
	 * @method private
	 * @param HTMLElement parent
	 * @param String name
	 * @param Number sort
	 * @return HTMLElement table with radios
	 */
	_createTableRadio: function(parent, name, selectComp, _radios) {

		var resourceBundle = f_resourceBundle.Get(f_columnSortDialog);
 
		var tableRadio =  f_core.CreateElement(parent, "table", {
			cellPadding: 0,
			cellSpacing: 2
		});
		
		var tbodyRadio = f_core.CreateElement(tableRadio, "tbody");

		var rowRadio=f_core.CreateElement(tbodyRadio, "tr");
		rowRadio.verticalAlign="middle";
		rowRadio.style.height="20px";

		var cellRadio = f_core.CreateElement(rowRadio, "td");
  
        var label=f_core.CreateElement(cellRadio, "label", {
        	className: "f_columnSortDialog_radio_text"
        });
 		
		var radioComp=f_core.CreateElement(label, "input", {
			type: "radio",
			name: name,
			id: name+"_asc",
			value: f_columnSortDialog.LIB_ASCENDANT,
			className: "f_columnSortDialog_radio"		
		});
		_radios.push(radioComp);
		
		label.appendChild(label.ownerDocument.createTextNode(resourceBundle.f_get("ASCENDANT")));
		
        
		var rowRadio=f_core.CreateElement(tbodyRadio, "tr");
		rowRadio.verticalAlign="middle";
		rowRadio.style.height="20px";
		
		var cellRadio = f_core.CreateElement(rowRadio, "td");
          
       	var label= f_core.CreateElement(cellRadio, "label", {
        	className: "f_columnSortDialog_radio_text"
        });
 		
		var radioComp=f_core.CreateElement(label, "input", {
			type: "radio",
			name: name,
			id: name+"_desc",
			value: f_columnSortDialog.LIB_DESCENDANT,
			className: "f_columnSortDialog_radio"
		});
		_radios.push(radioComp);
		
		label.appendChild(label.ownerDocument.createTextNode(resourceBundle.f_get("DESCENDANT")));

  		
		return tableRadio;
	},

	/**
	 * @method protected
	 */
	f_preDestruction: function() {
		var configurator=this._configurator;
		if (configurator) {
			this._configurator=undefined;
			
			f_classLoader.Destroy(configurator);
		}
		

		this.f_super(arguments);		
	},
	
	/**
	 * @method public
	 * @return String
	 */
	_toString: function() {
		var ts = this.f_super(arguments);
		ts += "\n[f_columnSortDialog title='"+this._title+"' text='"+this._text+"' defaultValue='"+this._defaultValue+"']";
		return ts;
	}
};

new f_class("f_columnSortDialog", {
	extend: f_dialog,
	members: __members,
	statics: __statics
});
