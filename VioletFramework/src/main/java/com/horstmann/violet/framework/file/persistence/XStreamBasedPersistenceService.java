package com.horstmann.violet.framework.file.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.horstmann.violet.framework.injection.bean.ManiocFramework.ManagedBean;
import com.horstmann.violet.product.diagram.abstracts.IGraph;
import com.thoughtworks.xstream.XStream;

@ManagedBean(registeredManually=true)
public class XStreamBasedPersistenceService implements IFilePersistenceService {

	@Override
	public IGraph read(InputStream in) throws IOException {
		XStream xStream = new XStream();
		Object fromXML = xStream.fromXML(in);
		IGraph graph = (IGraph) fromXML;
		return graph;
	}

	@Override
	public void write(IGraph graph, OutputStream out) {
		XStream xStream = new XStream();
		xStream.toXML(graph, out);
	}

}
