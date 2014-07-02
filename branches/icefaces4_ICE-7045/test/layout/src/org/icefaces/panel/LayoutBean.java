package org.icefaces.panel;

import org.icefaces.ace.model.borderlayout.PanelModel;
import org.icefaces.ace.model.borderlayout.PanelDefaultModel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;


@ManagedBean
@ViewScoped
public class LayoutBean implements Serializable
{
    private static final long serialVersionUID = 1L;

    private PanelModel northModel = new PanelModel();
    private PanelModel eastModel = new PanelModel();
    private PanelDefaultModel defaultModel = new PanelDefaultModel();

    //
    private boolean resizable = true;
    private boolean closable = true;
    private boolean slidable = true;

	public LayoutBean()
	{
        defaultModel.setResizable(Boolean.TRUE);
        defaultModel.setClosable(Boolean.TRUE);
        eastModel.setInitialSize(200);
        eastModel.setSpacing_closed(21);
        eastModel.setTogglerLength_closed(21);

	}

    public PanelModel getNorthModel() {
        return northModel;
    }

    public void setNorthModel(PanelModel northModel) {
        this.northModel = northModel;
    }

    public PanelDefaultModel getDefaultModel() {
        return defaultModel;
    }

    public void setDefaultModel(PanelDefaultModel defaultModel) {
        this.defaultModel = defaultModel;
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    public boolean isClosable() {
        return closable;
    }

    public void setClosable(boolean closable) {
        this.closable = closable;
    }

    public boolean isSlidable() {
        return slidable;
    }

    public void setSlidable(boolean slidable) {
        this.slidable = slidable;
    }

    public PanelModel getEastModel() {
        return eastModel;
    }

    public void setEastModel(PanelModel eastModel) {
        this.eastModel = eastModel;
    }
}
