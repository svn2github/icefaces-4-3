/*
 * Copyright 2004-2014 ICEsoft Technologies Canada Corp.
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

package org.icefaces.ace.component.videoplayer;


import org.icefaces.ace.meta.annotation.*;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import org.icefaces.ace.meta.baseMeta.UIComponentBaseMeta;

@Component(
        tagName = "videoPlayer",
        componentClass = "org.icefaces.ace.component.videoplayer.VideoPlayer",
        rendererClass = "org.icefaces.ace.component.videoplayer.VideoPlayerRenderer",
        generatedClass = "org.icefaces.ace.component.videoplayer.VideoPlayerBase",
        componentType = "org.icefaces.VideoPlayer",
        rendererType = "org.icefaces.VideoPlayerRenderer",
        extendsClass = "javax.faces.component.UIComponentBase",
        componentFamily = "org.icefaces.VideoPlayer",
        tlddoc = "videoPlayer renders a video player to play back videos. " +
                "The component can be bound to a variety of source types. "
)
@ResourceDependencies({
        @ResourceDependency(library = "org.icefaces.component.util", name = "component.js")
})

public class VideoPlayerMeta extends UIComponentBaseMeta {

    @Property(tlddoc = "Sets the CSS style definition to be applied to this component.")
     private String style;

    @Property(tlddoc = "Sets the CSS class to apply to this component.")
    private String styleClass;

    @Property(defaultValue = "auto", tlddoc = "Options for preloading the resource. Accepted values are " +
            "\"auto\" (allow the browser to decide), \"none\" or \"metadata\" (preload only the metadata). ")
    private String preload;

    @Property(defaultValue = "true", tlddoc = "If \"true\", will render the media controls. ")
    private boolean controls;

    @Property(tlddoc = "The media source. The value may resolve to a byte array, the String of a file name, " +
            "or a JSF Resource, if used in JSF project ")
    private Object value;

    @Property(defaultValue = "false", tlddoc = "If \"true\", will play the the audio file repeatedly. ")
    private boolean loop;

    @Property (defaultValue="false",
               tlddoc="Will cause the media to automatically play when the page is load. This may be required " +
                    "for some older devices to play. ")
    private boolean autoplay;

    @Property(defaultValue = "session", tlddoc="The JSF Resource scope of the object resolving from the \"value\" " +
            "attribute. It is only applicable when using a byte array or an instance of IceOutputResource as the value. Possible values are \"flash\", \"request\", \"view\", \"session\", and \"application\". ")
    private String scope;

    @Property(tlddoc = "The name is used for JSF Resource registration. ")
    private String name;

    @Property(tlddoc = "The library used for JSF Resource registration. ")
    private String library;

    @Property(tlddoc = "The URL or src of the media file. If the \"value\" attribute is empty, the \"url\" attribute will" +
            " be used. ")
    private String url;

    @Property(tlddoc = "The mime type of media file. ")
    private String type;

    @Property( tlddoc = "For certain devices, this adds the ability for a link to launch a system media player. If " +
            "this attribute is present thelink will appear with this text as the label.")
    private String linkLabel;

    @Property(defaultValue = "false", tlddoc=" audio will be muted if true. Default is false")
    private boolean muted;

    @Field
    private String srcAttribute;

    @Property(tlddoc = "Allows webkit supported browsers to play back the video inline rather than a new window.")
    private boolean playsinline;

    @Property(tlddoc = "display a static photo until the video is played")
    private String poster;

    @Property(defaultValue = "Integer.MIN_VALUE", tlddoc = "The maximum width of video area.")
    private int width;

    @Property(defaultValue = "Integer.MIN_VALUE", tlddoc = "The maximum height of video area.")
    private int height;
 }

