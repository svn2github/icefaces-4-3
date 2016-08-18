package org.icefaces.ace.component.colorentry;

import org.icefaces.ace.component.clientValidator.Validateable;

/**
 * Created by jguglielmin on 2016-07-25.
 */
public class ColorEntry extends ColorEntryBase implements Validateable {
    public final static String THEME_INPUT_CLASS = "ui-inputfield ui-textentry ui-widget ui-state-default ui-corner-all";
    public final static String PLAIN_INPUT_CLASS = "ui-textentry";
    public String getValidatedElementId() {
         return getClientId() + "_input";
     }
}
