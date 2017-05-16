/*
 * Copyright 2004-2016 ICEsoft Technologies Canada Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS
 * IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.icefaces.ace.model.colorEntry;

import java.io.Serializable;

public class SwatchEntry implements Serializable{

    /**
     * to use swatches/palette support, a list is made of these objects which contain a color name as well
     * as an int value for RGB for example 'red': {r:1, g:0, b:0} could be one such SwatchEntry entry
     * for a swatch.
     */
    private String colorName;
    public static final double DEFAULT_OPACITY = 0.9;

    private double rpercent;
    private double bpercent;
    private double gpercent;
    private double opacity;

    public SwatchEntry(String colorName, double red, double green, double blue){
        if (colorName==null || colorName.length() < 1){
              throw new IllegalArgumentException(" ColorName must not be null or empty");
        }
        this.colorName = colorName;
        if (red >=0 && red <= 1 && green >=0 && green <=1 && blue >=0 && blue <=1){
            this.rpercent= red;
            this.bpercent = blue;
            this.gpercent = green;
        }   else throw new IllegalArgumentException("Bad value in rgb input.  Values must be rgb percent between 0 and 1") ;
        this.opacity = DEFAULT_OPACITY;
    }

    public SwatchEntry(String colorName, double red, double green, double blue, double opacity){
         if (colorName==null || colorName.length() < 1){
               throw new IllegalArgumentException(" ColorName must not be null or empty");
         }
         this.colorName = colorName;
         if (red >=0 && red <= 1 && green >=0 && green <=1 && blue >=0 && blue <=1){
             this.rpercent= red;
             this.bpercent = blue;
             this.gpercent = green;
         }   else throw new IllegalArgumentException("Bad value in rgb input.  Values must be rgb percent between 0 and 1") ;
        this.opacity = opacity;
     }
    /*
       this is required to write to js file for plugin to read the values.
     */
    public String getWrittenEntry(){
        return  "{ name: '"+this.colorName+"', r: "+this.rpercent+", g: "+this.gpercent+", b: "+this.bpercent+"}";
    }

    /**
     *
     * @return String representing rgba percent with opacity for css
     */
    public String getRGBAPercentForCSS(){
        return "rgba(" + this.rpercent*100 + "%, " + this.gpercent*100 + "%, " + this.bpercent*100 + "%, " + this.opacity + ");";
    }
    /**
       *
       * @return String representing rgba percent for css
       */
    public String getRGBPercentForCSS(){
        return "rgb(" + this.rpercent*100 + "%, " + this.gpercent*100 + "%, " + this.bpercent*100+ "%); ";
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }


    public double getRpercent() {
        return rpercent;
    }

    public void setRpercent(double rpercent) {
        this.rpercent = rpercent;
    }

    public double getBpercent() {
        return bpercent;
    }

    public void setBpercent(double bpercent) {
        this.bpercent = bpercent;
    }

    public double getGpercent() {
        return gpercent;
    }

    public void setGpercent(double gpercent) {
        this.gpercent = gpercent;
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }
}
