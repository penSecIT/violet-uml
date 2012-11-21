package com.horstmann.violet;

import eu.webtoolkit.jwt.WApplication;
import eu.webtoolkit.jwt.WEnvironment;
import eu.webtoolkit.jwt.WtServlet;

public class UMLEditorWebServlet extends WtServlet {
	
	public UMLEditorWebServlet() {
		super();
	}

	@Override
	public WApplication createApplication(WEnvironment env) {
		/*
         * You could read information from the environment to decide whether the
         * user has permission to start a new application
         */
        return new UMLEditorWebApplication(env);
	}

}
