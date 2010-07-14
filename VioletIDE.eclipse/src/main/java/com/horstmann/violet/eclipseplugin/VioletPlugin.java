/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2007 Cay S. Horstmann (http://horstmann.com)
                    Alexandre de Pellegrin (http://alexdp.free.fr);

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.horstmann.violet.eclipseplugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.horstmann.violet.framework.injection.bean.SpringDependencyInjector;
import com.horstmann.violet.framework.injection.bean.annotation.SpringBean;
import com.horstmann.violet.framework.plugin.PluginLoader;

/**
 * The main plugin class to be used in the desktop. This plugin embeds Violet in Eclipse
 * 
 * @author Alexandre de Pellegrin
 */
public class VioletPlugin extends AbstractUIPlugin
{

    // The shared instance.
    private static VioletPlugin plugin;
    
    @SpringBean(name = "pluginLoader")
    private PluginLoader pluginLoader;

    /**
     * The constructor.
     */
    public VioletPlugin()
    {
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
        ApplicationContext springContext = getApplicationContext();
        SpringDependencyInjector injector = (SpringDependencyInjector) springContext.getBean("springDependencyInjector");
        injector.inject(this);
        installPlugins();
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception
    {
        super.stop(context);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     */
    public static VioletPlugin getDefault()
    {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path.
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin("VioletPlugin", path);
    }
    
    /**
     * Install plugins
     */
    private void installPlugins()
    {

        this.pluginLoader.installPlugins();
    }
    
    /**
     * @return a new application context instance
     */
    private static ApplicationContext getApplicationContext()
    {
        String[] configLocations =
        {
                "classpath*:applicationContext*.xml",
                "classpath:applicationContext-framework.xml",
                "classpath:dedicatedApplicationContext-application.xml"
        };
        ApplicationContext context = new ClassPathXmlApplicationContext(configLocations);
        return context;
    }

}
