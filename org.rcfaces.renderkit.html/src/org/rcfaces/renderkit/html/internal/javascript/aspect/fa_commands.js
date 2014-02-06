/*
 * $Id: fa_commands.js,v 1.3 2013/11/13 12:53:31 jbmeslin Exp $
 */
 
/**
 * Aspect Commands
 *
 * @aspect hidden fa_commands
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:31 $
 */
var __members = {

	/**
	 * @field private Function
	 */
	_nextCommand: undefined,

	/**
	 * @field private Number
	 */
	_nextCommandDate: undefined,

	f_finalize: function() {
		this._nextCommand=undefined; // function
	},
	/**
	 * @method protected
	 * @param Function callback
	 * @return void
	 */
	f_appendCommand: function(callBack) {
		f_core.Assert(typeof(callBack)=="function", "fa_commands.f_appendCommand: Invalid callback parameter ("+callBack+")");
		
		var now=new Date().getTime();
		
		if (this._nextCommandDate && now-this._nextCommandDate>1000*10) {
			this._nextCommandDate=undefined;
			this._nextCommand=undefined;
		}
		
		var nextCommand = this._nextCommand;		
		
		if (!this._nextCommandDate && !nextCommand) {
			this._nextCommandDate=now;
			
			f_core.Info(fa_commands, "f_appendCommand: Call immediatly the callback !");
			try {
				callBack.call(this, this);
				
			} catch (ex) {
				f_core.Error(fa_commands, "f_appendCommand: Call of callback: "+callBack+" throws exception.", ex);
				this._nextCommandDate=undefined;
				return;
			}

			return;
		}
		
		if (f_core.IsInfoEnabled(fa_commands)) {
			if (nextCommand) {
				f_core.Info(fa_commands, "f_appendCommand: Replace an other callback !");
	
			} else  {
				f_core.Info(fa_commands, "f_appendCommand: Set the next callback !");
			}
		}
		
		this._nextCommand=callBack;
	},
	/**
	 * @method protected
	 * @return Boolean
	 */
	f_processNextCommand: function() {
		var nextCommand=this._nextCommand;
		if (!nextCommand) {
			this._nextCommandDate=undefined;
			
			f_core.Debug(fa_commands, "f_processNextCommand: no more commands");
			return false;
		}
	
		f_core.Info(fa_commands, "f_processNextCommand: Process callback !");
		
		var now=new Date().getTime();
		
		this._nextCommand=undefined;
		this._nextCommandDate=now;
		
		try {
			nextCommand.call(this, this);
			
		} catch (ex) {
			f_core.Error(fa_commands, "f_processNextCommand: Call of callback: "+nextCommand+" throws exception.", ex);
			this._nextCommandDate=undefined;
			return false;
		}
		
		return true;
	},
	/**
	 * @method protected
	 * @return void
	 */
	f_clearCommands: function() {
		f_core.Debug(fa_commands, "f_clearCommands: clear commands");
		this._nextCommand=undefined;
	}
};

new f_aspect("fa_commands", {
	members: __members
});
