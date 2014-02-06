/*
 * $Id: fa_compositeNumEntry.js,v 1.3 2013/11/13 12:53:29 jbmeslin Exp $
 */
 
/**
 * Aspect Composite number entry
 *
 * @aspect hidden fa_compositeNumEntry extends fa_compositeEntry
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:29 $
 */
var __statics={	

	/**
	 * @field protected static final String
	 */
	DEFAULT_TYPE: "default"
	
};

var __members={

	fa_initializeInput: function(input) {
		input._min=f_core.GetNumberAttributeNS(input,"min");
		input._max=f_core.GetNumberAttributeNS(input,"max");
		input._default=f_core.GetNumberAttributeNS(input,"default");

		input._cycle=f_core.GetBooleanAttributeNS(input,"cycle");
				
		input._autoCompletion=f_core.GetBooleanAttributeNS(input,"auto");
		
		var step=f_core.GetAttributeNS(input,"step");
		if (step) {
			input._step=step;
		}
	},
	/*
	fa_finalizeInput: function(input) {
		// input._min=undefined; // number
		// input._max=undefined; // number
		// input._default=undefined; // number
		// input._step=undefined; // string
		// input._cycle=undefined; // boolean
		// input._autoCompletion=undefined; // boolean
	},
	*/
	/**
	 * @method protected
	 */
	fa_performStep: function(input, unit, limitMin, limitMax, step) {
		var inputValue=input.value;

		var fv=parseInt(inputValue, 10);
		if (isNaN(fv)) {
			if (input._default) {
				fv=input._default;

			} else if (unit>0) {
				fv=(limitMin)?limitMin:0;
				
			} else {
				if (isNaN(limitMax)) {
					return false;
				}
				
				fv=limitMax;
			}
			
			
			if (isNaN(fv)) {
				return;
			}
			return this.fa_performSet(input, fv, limitMin, limitMax, step);
		}
		
		var next=this._computeNextValue(fv, unit, limitMin, limitMax, step, input._cycle);
		
		input.value=this.fa_formatNumber(input, next);
		f_core.SelectText(input, 0, parseInt(input.maxLength, 10));
	
		return false;
	},
	/**
	 * @method protected
	 */
	fa_performSet: function(input, newValue, limitMin, limitMax, step) {
		var inputValue=input.value;

		var fv=parseInt(inputValue, 10);
		
		f_core.Debug(fa_compositeNumEntry, "fa_performSet:  fv="+fv+" newValue="+newValue+" step="+step);
		if (fv==newValue) {
			return false;
		}
		
		var next=this._computeNextValue(newValue, 0, limitMin, limitMax, step, input._cycle);
		
		input.value=this.fa_formatNumber(input, next);
		f_core.SelectText(input, 0, parseInt(input.maxLength, 10));
	
		return false;
	},
	/**
	 * @method private
	 */
	_computeNextValue: function(fv, unit, limitMin, limitMax, step, cycle) {
		var next=fv;
		
		if (step) {
			if (step.charAt(0)=="%") {
				var s=parseInt(step.substring(1), 10);
				if (!isNaN(s)) {
					if ((next % s)==0) {
						next+=s*unit;
												
					} else  {
						next+=((unit>0)?s:0)-(next % s);
					}
					
	//				alert("fv="+fv+" next="+next+" s="+s+" unit="+unit+" limit="+limit);
				}
			
			} else {
				var s=parseInt(step, 10);
				if (!isNaN(s)) {
					next+=s*unit;
				}
			}
		} else {
			next+=unit;
		}
		
		if (limitMin!==undefined && next<limitMin) {
			if (cycle) {
				next=this._computeNextValue(limitMax, 0, limitMin, limitMax, step, false);
				
			} else {
				next=limitMin;
			}
		}
		
		if (limitMax!==undefined && next>limitMax) {
			if (cycle) {
				next=this._computeNextValue(limitMin, 0, limitMin, limitMax, step, false);
				
			} else {
				next=limitMax;
			}
		}
		
		return next;
	},
	/**
	 * @method protected
	 */
	fa_keyPressed: function(input, keyChar, jsEvent, fill) {
		f_core.Debug(fa_compositeNumEntry, "fa_keyPressed: on input '"+input.id+"' keyChar="+keyChar);

		if (keyChar<'0' || keyChar>'9') {
			return null;
		}
			
		var sel=f_core.GetTextSelection(input);

		var inputValue=input.value;
		var maxLength=parseInt(input.maxLength, 10);
		
		f_core.Debug(fa_compositeNumEntry, "fa_keyPressed: inputValue='"+inputValue+"' maxLength='"+maxLength+"' sel0="+sel[0]+" sel1="+sel[1]+" autocompletion="+input._autoCompletion);
		
		if (inputValue.length==maxLength) {
			if (sel[0]==maxLength) {
				// Balance le chiffre sur l'autre champ !
				
				var nextInput=input._nextInput;
				if (nextInput) {
					// On efface le champ suivant
					nextInput.value="";			
					
					// On lui donne le focus		
					nextInput.focus();

					f_core.Debug(fa_compositeNumEntry, "fa_keyPressed: next field '"+nextInput+"'");
					
					// On simule l'appuie de la touche
					this.fa_keyPressed(nextInput, keyChar, jsEvent, true);
					
					return false;
				}
				
				// On est à la fin ... y a plus rien à saisir
				return false;
			}
			
			if (sel[0]!=0 || sel[1]!=maxLength) {
				// On est au milieu du champ: Normalement ca bloque !
				return true;
			}
		}

		var futureValue=inputValue.substring(0, sel[0])+keyChar+inputValue.substring(sel[1]);
		if (input._autoCompletion) {
			// Si on ajoute à la fin !
			var fv=parseInt(futureValue, 10);
			
			var min=input._min;
			var max=input._max;
		
			f_core.Debug(fa_compositeNumEntry, "fa_keyPressed: Supposed value '"+futureValue+"' int="+fv+" min="+min+" max="+max);
	
			if ((fv || futureValue.length==maxLength) 
					&& ((min!==undefined && min>fv && futureValue.length==maxLength)  // On ne peut pas determiner le min si le champ n'est pas complet !
						|| (max!==undefined && max<fv))) {
				if (sel[1]!=inputValue.length) {
					// On insere au milieu et y a un probleme: on refuse la touche
					return false;
				}
				
				// probleme !
				// Si le champ est vide : on prend le defaut !
				if (!inputValue.length) {
					var defaultValue=input._default;
					if (defaultValue===undefined) {
						// meme pas de valeur par defaut
						
						return false; // on refuse
					}
				
					futureValue=defaultValue;
					fill=true;
					
				} else if (!fv) {
					// Le champ est rempli de zero !
					return false;
					
				} else if (min===undefined || min<fv) {
					// Le champ n'est pas vide .. mais la valeur précédente etait bonne !
					// On la conserve ... (on formate au passage)
					
					var v=this.fa_formatNumber(input, inputValue, maxLength);
					if (v!=input.value) {
						input.value=v;
					}
					
					// ... et on passe la touche à l'input suivant !
					
					var nextInput=input._nextInput;
					if (nextInput) {
						// On efface le champ suivant
						nextInput.value="";			
						
						// On lui donne le focus		
						nextInput.focus();
						
						// On simule l'appuie de la touche
						this.fa_keyPressed(nextInput, keyChar, jsEvent, true);
						
						return false;
					}
					
					// On est à la fin ... y a plus rien à saisir
					return false;
							
				} else {
					// Le nombre précédent n'est pas acceptable !
					return false;
				}
			}
			
			// Maintenant on recherche si on peut predire les valeurs suivantes !
			// On ne fait ca que si le curseur est à la fin !
			if (sel[1]==inputValue.length) {
				fv=parseInt(futureValue, 10); // On recalcule, car futureValue a pu changer !
				
				var diff=maxLength-futureValue.length;
				
				if (max!==undefined) {
					for(;diff;diff--) {
						if (max>=(fv*10)) {
							// Le chiffre suivant est possible !		
							break;
						}
						
						fv*=10;
						futureValue="0"+futureValue;
						fill=true;
					}
				}
				
				f_core.Debug(fa_compositeNumEntry, "fa_keyPressed: Diff="+diff+" fv="+fv);
				
				if (!diff) {
					// Il est complet !
					// On passe au suivant si possible !
					input.value=futureValue;
	
					var nextInput=input._nextInput;
					if (nextInput) {
						// On lui donne le focus		
						nextInput.focus();
						f_core.SelectText(nextInput, 0, nextInput.value.length);
							
						return false;
					}
					
					// On est à la fin ... y a plus rien à saisir
					return false;				
				}
			}
		}
		
		if (fill) {
			input.value=futureValue;
		}
		
		return true;
	},
	/**
	 * @method protected
	 */
	fa_formatInput: function(input, onBlur) {
		var inputValue=input.value;
		if (!inputValue.length) {
			// Aucune saisie
			
			var defaultValue=input._default;
			if (defaultValue===undefined) {
				// meme pas de valeur par defaut
				
				return false; // on refuse
			}
			
			inputValue=defaultValue;
		}
		
		var v=this.fa_formatNumber(input, inputValue);
		if (v!=input.value) {
			input.value=v;
		}
		
		if (onBlur) {
			return true;
		}
		
		var separators=input._separators;
		if (separators) {
			var nextInput=input._nextInput;
			if (nextInput) {
				nextInput.focus();
			}

			// De toute facon on refuse la saisie du séparateur
			return false;
		}
		
		// C'est un TAB, on laisse faire ....
		return true;
	},
	/**
	 * @method protected
	 */
	fa_formatNumber: function(input, number, size) {
		if (size===undefined) {
			size=parseInt(input.maxLength, 10);
		}
		
		var s=String(number);
		
		for(size-=s.length;size>0;size--) {
			s="0"+s;
		}

		return s;
	}
	
};
 
new f_aspect("fa_compositeNumEntry", {
	extend: [ fa_compositeEntry ],
	members: __members,
	statics: __statics 
});
