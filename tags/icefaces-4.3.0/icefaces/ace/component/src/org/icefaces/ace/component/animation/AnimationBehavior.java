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

package org.icefaces.ace.component.animation;

import org.icefaces.resources.ICEResourceDependencies;
import org.icefaces.resources.ICEResourceDependency;
import org.icefaces.resources.ICEResourceLibrary;
import org.icefaces.ace.resources.ACEResourceNames;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.component.behavior.FacesBehavior;
import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.Map;

@ICEResourceLibrary(ACEResourceNames.ACE_LIBRARY)
@ICEResourceDependencies({
        @ICEResourceDependency(name = "util/ace-core.js"),
        @ICEResourceDependency(name = "jquery/jquery.js"),
        @ICEResourceDependency(name = "util/ace-jquery-ui.js"),
        @ICEResourceDependency(name = "animation/animation.js")
})
@FacesBehavior("org.icefaces.ace.animation.Animation")
public class AnimationBehavior extends BehaviorBase {
    public final static String BEHAVIOR_ID = "org.icefaces.ace.animation.Animation";
    private String effectsLib = "ice.ace.animation.";
    private boolean usingStyleClass;
    private boolean run;
    Effect effect = new Anim();
    private String style;
    private String to;
    private String from;
    private String easing;
    private Integer iterations;
    private Double duration;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getEasing() {
        return easing;
    }

    public void setEasing(String easing) {
        this.easing = easing;
    }

    public Integer getIterations() {
        return iterations;
    }

    public void setIterations(Integer iterations) {
        this.iterations = iterations;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setEffectObject(Effect effect) {
        this.effect = effect;
        clearInitialState();
    }

    public Effect getEffect() {
        return (Effect) eval("effectObject", effect);

    }

    public void setName(String name) {
        use(name);
        clearInitialState();
    }

    public String getName() {
        return effect.getClass().getSimpleName();

    }

    public void setRun(boolean run) {
        ValueExpression expression = getValueExpression("run");

        if (expression != null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            expression.setValue(ctx.getELContext(), run);
        } else {

            this.run = run;
        }
        clearInitialState();
    }

    public boolean isRun() {
        return (Boolean) eval("run", run);
    }

    private void use(String name) {
        if ("Appear".equalsIgnoreCase(name)) {
            effect = new Appear();
        } else if ("Blind".equalsIgnoreCase(name)) {
            effect = new Blind();
        } else if ("Clip".equalsIgnoreCase(name)) {
            effect = new Clip();
        } else if ("Drop".equalsIgnoreCase(name)) {
            effect = new Drop();
        } else if ("Explode".equalsIgnoreCase(name)) {
            effect = new Explode();
        } else if ("Fade".equalsIgnoreCase(name)) {
            effect = new Fade();
        } else if ("Fold".equalsIgnoreCase(name)) {
            effect = new Fold();
        } else if ("Puff".equalsIgnoreCase(name)) {
            effect = new Puff();
        } else if ("Slide".equalsIgnoreCase(name)) {
            effect = new Slide();
        } else if ("Scale".equalsIgnoreCase(name)) {
            effect = new Scale();
        } else if ("Bounce".equalsIgnoreCase(name)) {
            effect = new Bounce();
        } else if ("Highlight".equalsIgnoreCase(name)) {
            effect = new Highlight();
        } else if ("Pulsate".equalsIgnoreCase(name)) {
            effect = new Pulsate();
        } else if ("Shake".equalsIgnoreCase(name)) {
            effect = new Shake();
        } else if ("Size".equalsIgnoreCase(name)) {
            effect = new Size();
        } else if ("Transfer".equalsIgnoreCase(name)) {
            effect = new Transfer();
        } else if ("Anim".equalsIgnoreCase(name)) {
            effect = new Anim();
        }
    }

    protected String getEffectsLib() {
        return effectsLib;
    }

    protected void setEffectsLib(String effectsLib) {
        this.effectsLib = effectsLib;
    }

    private String styleClass;

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }


    public AnimationBehavior() {
        // System.out.println("EffectBehavior initialized ");
    }

    public void setUsingStyleClass(boolean usingStyleClass) {
        this.usingStyleClass = usingStyleClass;
    }

    public boolean isUsingStyleClass() {
        return usingStyleClass;

    }

    public String getScript(ClientBehaviorContext behaviorContext) {
        return getScript(behaviorContext, true);
    }

    public String getScript(ClientBehaviorContext behaviorContext, boolean run) {
        if ("Anim".equals(effect.getName())) {
            if (getTo() != null && !effect.getProperties().containsKey("to")) {
                effect.getProperties().put("to", getTo());
            }
            if (getFrom() != null && !effect.getProperties().containsKey("from")) {
                effect.getProperties().put("from", getFrom());
            }
        }
        if (getIterations() != null && !effect.getProperties().containsKey("iterations")) {
            effect.getProperties().put("iterations", getIterations());
        }
        if (getDuration() != null && !effect.getProperties().containsKey("duration")) {
            effect.getProperties().put("duration", getDuration());
        }
        if (getEasing() != null && !effect.getProperties().containsKey("easing")) {
            effect.getProperties().put("easing", getEasing());
        }
        if (!effect.getProperties().containsKey("event")) {
            effect.getProperties().put("event", behaviorContext.getEventName());
        }
        if (!effect.getProperties().containsKey("name")) {
            effect.getProperties().put("name", getName());
        }
        effect.setSourceElement(behaviorContext.getComponent().getClientId());
        if (behaviorContext.getComponent().getAttributes().get("styleClass") != null) {
            effect.getProperties().put("componentStyleClass", behaviorContext.getComponent().getAttributes().get("styleClass"));
        }
        StringBuilder call = new StringBuilder();
        call.append(getEffectsLib());

        if (run) {
            call.append("run");
        } else {
            call.append("register");
        }
        call.append("(");
        call.append(effect.getPropertiesAsJSON());
        call.append(")");

        return call.toString();
    }


    public void decode(FacesContext context, UIComponent component) {
        super.decode(context, component);
        Map map = context.getExternalContext().getRequestParameterMap();
        String id = "effect_style" + component.getClientId();
        if (map.containsKey(id)) {
            setStyle(map.get(id).toString());
        }
    }

    protected Object eval(String propertyName, Object value) {
        ValueExpression expression = getValueExpression(propertyName);

        if (expression != null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            return expression.getValue(ctx.getELContext());
        }

        if (value != null) {
            return value;
        }

        return null;
    }

    public ValueExpression getValueExpression(String name) {
        if (name == null) {
            throw new NullPointerException();
        }

        return ((bindings == null) ? null : bindings.get(name));
    }

    public void setValueExpression(String name, ValueExpression binding) {
        if (name == null) {
            throw new NullPointerException();
        }

        if (binding != null) {
            if (binding.isLiteralText()) {
                setLiteralValue(name, binding);
            } else {
                if (bindings == null) {
                    bindings = new HashMap<String, ValueExpression>();
                }

                bindings.put(name, binding);
                if ("effectObject".equals(name)) {
                    effect = ((Effect) binding.getValue(FacesContext.getCurrentInstance().getELContext()));
                    effect.setEffectBehavior(this);
                } else if ("name".equals(name)) {
                    String effectName = ((String) binding.getValue(FacesContext.getCurrentInstance().getELContext()));
                    use(effectName);
                } else if ("to".equals(name)) {
                    String to = ((String) binding.getValue(FacesContext.getCurrentInstance().getELContext()));
                    setTo(to);
                } else if ("from".equals(name)) {
                    String from = ((String) binding.getValue(FacesContext.getCurrentInstance().getELContext()));
                    setFrom(from);
                } else if ("easing".equals(name)) {
                    String easing = ((String) binding.getValue(FacesContext.getCurrentInstance().getELContext()));
                    setEasing(easing);
                } else if ("iterations".equals(name)) {
                    Integer iterations = ((Integer) binding.getValue(FacesContext.getCurrentInstance().getELContext()));
                    setIterations(iterations);
                } else if ("duration".equals(name)) {
                    Double duration = ((Double) binding.getValue(FacesContext.getCurrentInstance().getELContext()));
                    setDuration(duration);
                }
            }
        } else {
            if (bindings != null) {
                bindings.remove(name);
                if (bindings.isEmpty()) {
                    bindings = null;
                }
            }
        }

        clearInitialState();
    }

    protected void setLiteralValue(String propertyName, ValueExpression expression) {

        assert (expression.isLiteralText());

        Object value;
        javax.el.ELContext context = FacesContext.getCurrentInstance().getELContext();

        try {
            value = expression.getValue(context);
        } catch (javax.el.ELException ele) {
            throw new FacesException(ele);
        }
        if ("run".equals(propertyName)) {
            run = (Boolean) value;
        } else if ("name".equals(propertyName)) {
            setName((String) value);
        } else if ("to".equals(propertyName)) {
            setTo((String) value);
        } else if ("from".equals(propertyName)) {
            setFrom((String) value);
        } else if ("easing".equals(propertyName)) {
            setEasing((String) value);
        } else if ("iterations".equals(propertyName)) {
            setIterations((Integer) value);
        } else if ("duration".equals(propertyName)) {
            setDuration((Double) value);
        }
    }

    public Object saveState(FacesContext context) {
        Object[] state = new Object[9];
        state[0] = usingStyleClass;
        state[1] = run;
        state[2] = effect;
        state[3] = style;
        state[4] = to;
        state[5] = from;
        state[6] = easing;
        state[7] = iterations;
        state[8] = duration;
        return state;
    }

    public void restoreState(FacesContext context, Object state) {
        Object[] s = (Object[]) state;
        usingStyleClass = ((Boolean) s[0]).booleanValue();
        run = ((Boolean) s[1]).booleanValue();
        effect = (Effect) s[2];
        style = (String) s[3];
        to = (String) s[4];
        from = (String) s[5];
        easing = (String) s[6];
        iterations = (Integer) s[7];
        duration = (Double) s[8];
    }

    public interface IIterator {
        void next(String name, AnimationBehavior effectBehavior);
    }

    public static abstract class Iterator implements IIterator {
        private UIComponent uiComponent;

        public Iterator(UIComponent uiComponent) {
            this.uiComponent = uiComponent;
        }

        public UIComponent getUIComponent() {
            return this.uiComponent;
        }
    }
}