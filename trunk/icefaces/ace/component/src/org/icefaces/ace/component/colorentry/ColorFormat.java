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

package org.icefaces.ace.component.colorentry;

public enum ColorFormat {

    /**
     * HEX is hexadecimal display in input field eg #112233 (#RRGGBB)
     * HEX3 is hexadecimal display using 3 characters only if possible (#RGB). If HEX3 is
     * unavailable, then the control falls back to a HEX value.
     * HEXA is #RRBBGGAA for example #11223344
     * HSL is (hue, saturation, lightness) designation of color if opaque
     * HSLA eg hsl(123,45,67,0.123%)
     * HSL% eg hsla(12%,34%,56%) if opaque
     * HSLA% eg hsla(123,45,67,0.123%)
     * RGB is  (red, green, blue) of color if opaque--will not be returned if transparent
     * RBGA   for example rgb(123,45, 67, 0.123%) includes transparency
     * RGB% ie rgb(12%,34%,56%)
     * RGBA% ie rgb(12%,34%,56%,0.123%)
     * NAME specifies the color by name when selecting color and will fall back to hex
     * EXACT name color may not always be available and will fall back to NAME value
     */
     HEX("HEX"), HEX3("HEX3"), HEXA("HEXA"), RGB("RGB"), RGBA("RGBA"), RGBPERCENT("RGB%"),
         HSL("HSL"), HSLA("HSLA"), HSLPERCENT("HSL%"),
         HSLAPERCENT("HSLA%"),NAME("NAME"), EXACT("EXACT");
     private String value;
     private ColorFormat(String value){
        this.value=value;
    }
     public String getValue() {
        return this.value;
    }
}
