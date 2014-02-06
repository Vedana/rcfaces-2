
var f_vx = {

  /**
   * @method F_date
   * @decl public
   */
  Checker_date: function(validator, inVal) {
    // Handle empty string
    if (inVal == "") {
      return inVal;
    }

    var pattern = validator.f_getParameter("date.input");

    if (!pattern) {
      pattern = "ddMMyyyy";
    }


    var sdf = new F_SimpleDateFormat(pattern);
    if (sdf.f_getStatus() != F_SimpleDateFormat.NO_ERROR) {
      validator.f_setLastError(
			       "VALIDATION DATE",
			       "Le format de saisie de date est invalide:\n" + pattern
			       );
      validator.f_setObject(null);
      return null;
    }
    var date = sdf.f_parse(inVal);
    var error = "";
    switch (sdf.f_getStatus()) {
    case F_SimpleDateFormat.NO_ERROR: 
      break;

    case F_SimpleDateFormat.PARSE_ERROR:
      error = "La date ne correspond pas au format de saisie:\n" + pattern;
      break;
    
    case F_SimpleDateFormat.DATE_ERROR:
      error = "La date saisie est invalide.";
      break;
    
    default:
      error = "Erreur non répertoriée.";
      break;
    
    }
    if (error != "") {
      validator.f_setLastError("VALIDATION DATE",error);
      date = null;
    }
    validator.f_setObject(date);
    //var msg = (date == null)? "null":date.toString();
    //alert("date: " + msg);

    return sdf.f_format(date);
  },

  /**
   * @method F_number
   * @decl public
   */
  Checker_number: function(validator, inVal) {
    // Handle empty string
    if (inVal == "") {
      return inVal;
    }

    var pattern = validator.f_getParameter("number.input");
    var locale = validator.f_getParameter("number.locale");

    if (!pattern) {
      pattern = "# ###,#E#;-# ###,#E#";
    }
    var localized = (locale !== undefined);

    // Create a new format instance
    var df = new F_DecimalFormat("");
    df.f_applyLocalizedPattern(pattern, localized);
    if (df.f_getStatus() != F_DecimalFormat.NO_ERROR) {
      validator.f_setLastError(
			       "VALIDATION NOMBRE",
			       "Le format de saisie de nombre est invalide:\n" + pattern
			       );
      validator.f_setObject(null);
      return null;
    }
    var pos = new F_ParsePosition();
    var wrapper = df.f_parse(inVal, pos);
    var error = "";
    switch (df.f_getStatus()) {
    case F_DecimalFormat.NO_ERROR:
      break;
    case F_DecimalFormat.PARSE_ERROR:
    case F_DecimalFormat.PARSE_NO_DIGIT:
    case F_DecimalFormat.PARSE_AMBIG:
    case F_DecimalFormat.PARSE_UNCOMPLETE:
      error = "Le nombre saisi est invalide.";
      break;
    default: {
      error = "Erreur non répertoriée";
      break;
    }
    }
    if (error != "") {
      validator.f_setLastError( "VALIDATION NOMBRE", error);
      wrapper = null;
    }
    validator.f_setObject(wrapper);
    //alert(wrapper.f_value);
    if (wrapper == null) {
      return null;
    }
    var result = new F_StringBuffer();
    if (wrapper.f_type == "double") {
      df.f_formatDouble(wrapper.f_value, result);
    } else {
      df.f_formatLong(wrapper.f_value, result);
    }
    return result.toString();
  },

  /**
   * @method F_time
   * @decl public
   */
  Checker_time: function(validator, inVal) {
    // Handle empty string
    if (inVal == "") return inVal;

    var pattern = validator.f_getParameter("time.input");
    if (!pattern) pattern = "HHmmss";

    var sdf = new F_SimpleDateFormat(pattern);
    if (sdf.f_getStatus() != F_SimpleDateFormat.NO_ERROR) {
      validator.f_setLastError(
			       "VALIDATION HEURE",
			       "Le format de saisie d'heure est invalide:\n" + pattern
			       );
      validator.f_setObject(null);
      return null;
    }
    var date = sdf.f_parse(inVal);
    var error = "";
    switch (sdf.f_getStatus()) {
    case F_SimpleDateFormat.NO_ERROR: 
      break;
    case F_SimpleDateFormat.PARSE_ERROR:
      error = "L'heure ne correspond pas au format de saisie:\n" + pattern;
      break;
    
    case F_SimpleDateFormat.DATE_ERROR:
      error = "L'heure saisie est invalide.";
      break;
    
    default:
      error = "Erreur non répertoriée.";
      break;
    }
    if (error != "") {
      validator.f_setLastError("VALIDATION HEURE",error);
      date = null;
    }
    validator.f_setObject(date);

    return sdf.f_format(date);
  },


  /**
   * Cette fonction est utilisée pour le formatage de date dans la section
   * "formatter". Elle utilise le descripteur "date.output" dont la valeur est
   * un format de date. Ce dernier est compatible avec les formats supportés
   * par la classe Java SimpleDateFormat.
   *
   * @method F_date
   * @decl public
   * @param validator Objet de type validateur étendu
   * @param inVal La chaîne de caractères à formater
   * @return Retourne une chaîne de caractères au format de sortie
   * @see f_formatter_time, f_formatter_number
   */
  Formatter_date: function(validator, inVal) {
    var outVal = null;
    var pattern = validator.f_getParameter("date.output");
    if (!pattern) {
      pattern = "dd/MM/yyyy";
    }

    // Create a new format
    var sdf = new F_SimpleDateFormat(pattern);
    if (sdf.f_getStatus() != F_SimpleDateFormat.NO_ERROR) {
      validator.f_setLastError(
			       "FORMATAGE DATE",
			       "Le format d'affichage de la date est invalide:\n" + pattern
			       );
      return null;
    }
    // Get formatted date
    outVal = sdf.f_format(validator.f_getObject());
    var error = "";
    switch (sdf.f_getStatus()) {
    case F_SimpleDateFormat.NO_ERROR:
      break;
    default: {
      error = "Erreur non répertoriée.";
      break;
    }
    }
    if (error != "") {
      validator.f_setLastError("FORMATAGE DATE",error);
      return null;
    }
	
    return outVal;
  },

  /**
   * Cette fonction est utilisée pour le formatage de nombre dans la section
   * "formatter". Elle utilise le descripteur "number.output" dont la valeur est
   * un format de nombre. Ce dernier est compatible avec les formats supportés
   * par la classe Java DecimalFormat.
   *
   * @method F_number
   * @decl public
   * @param validator Objet de type validateur étendu
   * @param inVal La chaîne de caractères à formater
   * @return Retourne une chaîne de caractères au format de sortie
   * @see f_formatter_time, f_formatter_date
   */
  Formatter_number: function(validator, inVal) {
    var outVal = null;
    var pattern = validator.f_getParameter("number.output");
    var locale = validator.f_getParameter("number.locale");

    if (!pattern) {
      pattern = "# ###,00";
    }
    var localized = (locale !== undefined);

    // Create an empty pattern
    var df = new F_DecimalFormat("");
    // Apply localized pattern
    df.f_applyLocalizedPattern(pattern, localized);
    if (df.f_getStatus() != F_DecimalFormat.NO_ERROR) {
      validator.f_setLastError(
			       "FORMATAGE NOMBRE",
			       "Le format d'affichage du nombre est invalide:\n" + pattern
			       );
      return null;
    }
    var result = new F_StringBuffer();
    var wrapper = validator.f_getObject();
    if (wrapper.f_type == "double") {
      df.f_formatDouble(wrapper.f_value, result);
    } else {
      df.f_formatLong(wrapper.f_value, result);
    }
    outVal = result.toString();
    var error = "";
    switch (df.f_getStatus()) {
    case F_DecimalFormat.NO_ERROR: 
      break;
    default: {
      error = "Erreur non répertoriée.";
      break;
    }
    }
    if (error != "") {
      validator.f_setLastError( "FORMATAGE NOMBRE", error);
      return null;
    }
    return outVal;
  },

  /**
   * Cette fonction est utilisée pour le formatage d'heure dans la section
   * "formatter". Elle utilise le descripteur "time.output" dont la valeur est
   * un format d'heure. Ce dernier est compatible avec les formats supportés
   * par la classe Java SimpleDateFormat.
   *
   * @method F_time
   * @decl public
   * @param validator Objet de type validateur étendu
   * @param inVal La chaîne de caractères à formater
   * @return Retourne une chaîne de caractères au format de sortie
   * @see f_formatter_time, f_formatter_number
   */
  Formatter_time: function(validator, inVal) {
    var outVal = null;
    var pattern = validator.f_getParameter("time.output");
    if (!pattern) {
      pattern = "HH:mm:ss";
    }

    var sdf = new F_SimpleDateFormat(pattern);
    if (sdf.f_getStatus() != F_SimpleDateFormat.NO_ERROR) {
      validator.f_setLastError(
			       "FORMATAGE HEURE",
			       "Le format d'affichage de l'heure est invalide:\n" + pattern
			       );
      return null;
    }
    outVal = sdf.f_format(validator.f_getObject());
    var error = "";
    switch (sdf.f_getStatus()) {
    case F_SimpleDateFormat.NO_ERROR: 
      break;
    default: {
      error = "Erreur non répertoriée.";
      break;
    }
    }
    if (error != "") {
      validator.f_setLastError("FORMATAGE HEURE",error);
      return null;
    }
    return outVal;
  }
}
