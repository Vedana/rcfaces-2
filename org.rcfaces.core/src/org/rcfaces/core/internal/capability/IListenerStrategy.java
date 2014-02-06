package org.rcfaces.core.internal.capability;

/**
 * Listener Manager strategy constant
 * @author jbmeslin@vedana.com
 *
 */
public interface IListenerStrategy {
	/**
	 * DEFAULT : append listener without control
	 */
	int NORMAL = 0x00;
	
	/**
	 * ClEAN_ALL : remove all faces listener during the initialize phase of lyfe cycle. Used in facelet
	 */
	int CLEAN_ALL = 0x02;
	
	/**
	 * CLEAN_BYCLASS : clean listener before adding new.
	 */
	int CLEAN_BY_CLASS = 0x04;
	
	/**
	 *  CLEAN_BYCLASS : do not add faces listener if already one exist for this class 
	 */
	int ADD_IF_NEW = 0x08;

	int DEFAULT = CLEAN_ALL;
}
