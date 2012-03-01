package com.horstmann.violet.framework.plugin.extensionpoint;

import java.beans.PersistenceDelegate;
import java.util.Map;

/**
 * Extension point to interact on the XML content produced by the software to
 * save and reload diagrams
 * 
 * @author Alexandre de Pellegrin
 * 
 */
public interface IFilePersistenceServiceExtentionPoint {

	/**
	 * Use this if want need to have specific XML content to serialize INode and IEdge instances of your diagram <br/>
	 * You can extend DefaultPersistenceDelegate if you want.
	 * 
	 * @return l
	 */
	public Map<Class<?>, PersistenceDelegate> getMapOfSpecificPersistenceDelegate();

}
