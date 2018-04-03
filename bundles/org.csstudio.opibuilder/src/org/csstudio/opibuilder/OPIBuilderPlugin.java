/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder;

import java.util.logging.Logger;

import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.script.ScriptService;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.GUIRefreshThread;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.SchemaService;
import org.csstudio.utility.singlesource.ResourceHelper;
import org.csstudio.utility.singlesource.UIHelper;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author Xihui Chen
 *
 */
@SuppressWarnings("deprecation")
public class OPIBuilderPlugin extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.csstudio.opibuilder"; //$NON-NLS-1$

    /**
     * The ID of the widget extension point.
     */
    public static final String EXTPOINT_WIDGET = PLUGIN_ID + ".widget"; //$NON-NLS-1$

    /**
     * The ID of the widget extension point.
     */
    public static final String EXTPOINT_FEEDBACK_FACTORY = PLUGIN_ID + ".graphicalFeedbackFactory"; //$NON-NLS-1$

    /** File extension used for OPI files */
    public static final String OPI_FILE_EXTENSION = "opi"; //$NON-NLS-1$

    public static final String KEY_IS_MOBILE = "org.csstudio.rap.isMobile"; //$NON-NLS-1$

    final private static Logger logger = Logger.getLogger(PLUGIN_ID);

    // The shared instance
    private static OPIBuilderPlugin plugin;

    private static ResourceHelper resources;

    private static UIHelper ui;

    private IPropertyChangeListener preferenceLisener;

    /**
     * The constructor
     */
    public OPIBuilderPlugin() {
        plugin = this;
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static OPIBuilderPlugin getDefault() {
        return plugin;
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        resources = new ResourceHelper();
        ui = new UIHelper();

        // set this to resolve Xincludes in XMLs
        System.setProperty("org.apache.xerces.xni.parser.XMLParserConfiguration",
                "org.apache.xerces.parsers.XIncludeParserConfiguration");

        ScriptService.getInstance();

        if (PreferencesHelper.isDisplaySystemOutput()) {
            ConsoleService.getInstance().turnOnSystemOutput();
        }

        preferenceLisener = new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty()
                        .equals(PreferencesHelper.COLOR_FILE))
                    MediaService.getInstance().reloadColorFile();
                else if (event.getProperty().equals(
                        PreferencesHelper.FONT_FILE))
                    MediaService.getInstance().reloadFontFile();
                else if (event.getProperty().equals(
                        PreferencesHelper.OPI_GUI_REFRESH_CYCLE))
                    GUIRefreshThread.getInstance(true).reLoadGUIRefreshCycle();
                else if (event.getProperty().equals(
                        PreferencesHelper.DISABLE_ADVANCED_GRAPHICS)) {
                    String disabled = PreferencesHelper.isAdvancedGraphicsDisabled() ? "true" : "false";//$NON-NLS-1$ //$NON-NLS-2$
                    // for swt.widgets
                    System.setProperty(
                            "org.csstudio.swt.widget.prohibit_advanced_graphics", disabled);//$NON-NLS-1$
                    // for XYGraph
                    System.setProperty("prohibit_advanced_graphics", disabled); //$NON-NLS-1$

                } else if (event.getProperty().equals(
                        PreferencesHelper.URL_FILE_LOADING_TIMEOUT))
                    System.setProperty(
                            "org.csstudio.swt.widget.url_file_load_timeout", //$NON-NLS-1$
                            Integer.toString(PreferencesHelper
                                    .getURLFileLoadingTimeout()));
                else if (event.getProperty().equals(
                        PreferencesHelper.SCHEMA_OPI)) {
                    SchemaService.getInstance().reLoad();
                } else if (event.getProperty().equals(
                        PreferencesHelper.DISPLAY_SYSTEM_OUTPUT)) {
                    if (PreferencesHelper.isDisplaySystemOutput())
                        ConsoleService.getInstance().turnOnSystemOutput();
                    else
                        ConsoleService.getInstance().turnOffSystemOutput();
                }
            }

        };

        getPluginPreferences().addPropertyChangeListener(preferenceLisener);

        @SuppressWarnings("serial")
        // need to run preferenceListener at startup
        // A hack to make protected constructor public.
        class HackPropertyChangeEvent extends PropertyChangeEvent {
            public HackPropertyChangeEvent(Object source, String property, Object oldValue, Object newValue) {
                super(source, property, oldValue, newValue);
            }
        }
        preferenceLisener.propertyChange(
                new HackPropertyChangeEvent(
                        this, PreferencesHelper.DISABLE_ADVANCED_GRAPHICS, null, null));
        preferenceLisener.propertyChange(
                new HackPropertyChangeEvent(
                        this, PreferencesHelper.URL_FILE_LOADING_TIMEOUT, null, null));
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        ScriptService.getInstance().exit();
        getPluginPreferences().removePropertyChangeListener(preferenceLisener);
    }

    public static Logger getLogger() {
        return logger;
    }

    public static ResourceHelper getResourceHelper() {
        return resources;
    }

    public static UIHelper getUIHelper() {
        return ui;
    }
}
