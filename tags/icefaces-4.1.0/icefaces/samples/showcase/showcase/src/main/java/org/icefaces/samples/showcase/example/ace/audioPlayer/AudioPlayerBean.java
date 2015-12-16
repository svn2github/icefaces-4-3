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

package org.icefaces.samples.showcase.example.ace.audioPlayer;

import java.io.Serializable;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name = AudioPlayerBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class AudioPlayerBean implements Serializable {
    public static final String BEAN_NAME = "audioPlayerBean";
	public String getBeanName() { return BEAN_NAME; }

    private boolean autoplay = false;
    private boolean newWindow = false;
    private boolean loop = false;
    private boolean muted = false;

    private String linkLabel = null;
    private String value = "./resources/media/canary.mp3";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isAutoplay() {
        return autoplay;
    }

    public void setAutoplay(boolean autoplay) {
        this.autoplay = autoplay;
    }

    public boolean isNewWindow() {
        return newWindow;
    }

    public void setNewWindow(boolean newWindow) {
        this.newWindow = newWindow;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public String getLinkLabel() {
        return linkLabel;
    }

    public void setLinkLabel(String linkLabel) {
        this.linkLabel = linkLabel;
    }
}
