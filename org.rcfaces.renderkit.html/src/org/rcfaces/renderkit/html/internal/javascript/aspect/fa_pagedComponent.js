/*
 * $Id: fa_pagedComponent.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect PagedComponent
 *
 * @aspect public abstract fa_pagedComponent extends fa_filterProperties
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
 
var __statics = {
	/** 
	 * @field private static
	 */
	_DataPagers: undefined,
	 
	/**
	 * @method hidden static final 
	 * @param String componentId
	 * @param fa_pager pager
	 * @return void
	 */
	RegisterPager: function(componentId, pager) {
	
		var dgp=fa_pagedComponent._DataPagers;
		if (!dgp) {
			dgp=new Object;
			
			fa_pagedComponent._DataPagers=dgp;
		}
		
		componentId=fa_namingContainer.ComputeComponentId(pager, componentId);
		
		var lst=dgp[componentId];
		if (!lst) {
			lst=new Array;
			dgp[componentId]=lst;
		}

		if (!pager.id) {
			pager.id=f_core.AllocateAnoIdentifier();
		}

		// On enregistre l'ID pour eviter les leaks !
		lst.push(pager.id);

		var dp=pager.ownerDocument.getElementById(componentId);
		// Le dataGrid n'existe pas forcement lors de son enregistrement !		
		if (dp && f_classLoader.IsObjectInitialized(dp)) {

			f_core.Debug(fa_pagedComponent, "RegisterPager: Register fa_pager ("+pager.id+"/"+pager+") to component '"+componentId+"': Initialize now ! ");
			try {		
				pager.fa_pagedComponentInitialized(dp);
		
			} catch (x) {
				f_core.Error(fa_pagedComponent, "RegisterPager: Call of fa_pagedComponentInitialized() throws an exception ! (pager="+pager.id+")", x);
			}
							
			return;
		}

		f_core.Debug(fa_pagedComponent, "RegisterPager: Register fa_pager ("+pager.id+"/"+pager+") to component '"+componentId+"': Waiting initialization ! ");
	},
	/**
	 * @method hidden static final 
	 * @param fa_pager pager
	 * @return void
	 */
	UnregisterPager: function(pager) {	
		var dgp=fa_pagedComponent._DataPagers;
		if (!dgp) {
			return;
		}
		
		var pagerId=pager.id;
		
		for(var componentId in dgp) {
			dgp[componentId].f_removeElement(pagerId);
		}
	},
	/**
	 * @method protected static
	 * @return void
	 */
	Finalizer: function() {
		fa_pagedComponent._DataPagers=undefined;
	}
};
 
var __members = {
	fa_pagedComponent: function() {
		this._interactive=f_core.GetBooleanAttributeNS(this, "asyncRender");
		
		this._interactiveShow=f_core.GetBooleanAttributeNS(this, "interactiveShow");
		if (this._interactiveShow && !this.f_isVisible()) {
			this.f_getClass().f_getClassLoader().f_addVisibleComponentListener(this);
		}
		
		this._rows=f_core.GetNumberAttributeNS(this, "rows", 0); // Nombre ligne a afficher
		
		this._first=f_core.GetNumberAttributeNS(this, "first", 0);  // La premiere ligne

		this._paged=f_core.GetBooleanAttributeNS(this, "paged", true);

		this._rowCount=f_core.GetNumberAttributeNS(this, "rowCount", -1); // Nombre ligne au total
		if (this._rowCount<0) {
			this._maxRows=this._first+this._rows;
		}
	},
	/*
	f_finalize: function() {
		// this._rows=undefined;  // number
		// this._maxRows=undefined; // number
		// this._rowCount=undefined; // number
		// this._first=undefined; // number
		// this._interactive=undefined; // boolean
		// this._paged=undefined; // boolean

		// this._interactiveShow=undefined; // boolean
	},
	*/
	
	/**
	 * @method protected
	 */
	f_performPagedComponentInitialized: function() {
		var dps=fa_pagedComponent._DataPagers;

		if (!dps) {
			f_core.Debug(fa_pagedComponent, "f_performPagedComponentInitialized: Perform page component initialized:  NO pagers !");

			return;
		}
		
		var lst=dps[this.id];
		f_core.Debug(fa_pagedComponent, "f_performPagedComponentInitialized: Perform page component initialized ("+this.id+") list="+lst);
		
		if (!lst) {
			return;
		}
		
		var doc=this.ownerDocument;
		
		for(var i=0;i<lst.length;i++) {
			var pId=lst[i];
			if (!pId) {
				continue;
			}
			
			var p=f_core.GetElementByClientId(pId, doc);
			if (!p) {
				lst[i]=undefined; // N'existe plus  (AJAX ???)
				continue;
			}

			if (!p.fa_pagedComponentInitialized) {
				f_core.Error(fa_pagedComponent, "f_performPagedComponentInitialized: Can not call fa_pagedComponentInitialized on pager='"+p.id+"'.");
				
				lst[i]=undefined;
				continue;
			}
			
			try {
				p.fa_pagedComponentInitialized(this);

			} catch (x) {
				f_core.Error(fa_pagedComponent, "f_performPagedComponentInitialized: Call of fa_pagedComponentInitialized() throws an exception ! (pager="+p.id+")", x);
				
				lst[i]=undefined;
			}
		}
	},

	/**
	 * @method hidden
	 */
	f_setInteractiveShow: function(interactiveComponentId) {		
		this._interactiveShow=true;
	},
	
	/**
	 * Returns index of first row.
	 *
	 * @method public
	 * @return Number
	 * @javaReturnType int
	 */
	f_getFirst: function() {
		return this._first;
	},
	/**
	 * Returns number of row.
	 *
	 * @method public
	 * @return Number
	 * @javaReturnType int
	 */
	f_getMaxRows: function() {
		return this._maxRows;
	},
	/**
	 * Get number of rows by page
	 *
	 * @method public
	 * @return Number
	 * @javaReturnType int
	 */
	f_getRows: function() {
		return this._rows;
	},
	/**
	 * Returns number of dowloaded rows.
	 *
	 * @method public
	 * @return Number
	 * @javaReturnType int
	 */
	f_getRowCount: function() {
		return this._rowCount;
	},
	/**
	 * Returns <code>true</code> if the component is paged
	 *
	 * @method public
	 * @return Boolean <code>true</code> if the component is paged.
	 */
	f_isPaged: function() {
		return this._paged;
	},
	
	/**
	 * @method protected abstract
	 * @return void
	 */
	f_setFirst: f_class.ABSTRACT
};

new f_aspect("fa_pagedComponent", {
	extend: [ fa_filterProperties ],
	statics: __statics,
	members: __members
});


