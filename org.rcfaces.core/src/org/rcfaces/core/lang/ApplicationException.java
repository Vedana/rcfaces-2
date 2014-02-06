/*
 * $Id: ApplicationException.java,v 1.3 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.lang;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:26 $
 */
public class ApplicationException extends RuntimeException {

	private static final long serialVersionUID = -1121873372303775679L;

	private final int errorCode;

	private final String errorMessage;

	public ApplicationException(String message, Throwable cause, int errorCode,
			String errorMessage) {
		super(message, cause);

		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public ApplicationException(int errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public final int getErrorCode() {
		return errorCode;
	}

	public final String getErrorMessage() {
		return errorMessage;
	}

}
