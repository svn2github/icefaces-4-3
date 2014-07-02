package org.icefaces.panel;

import java.io.Serializable;
import java.lang.String;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class AttributeBean implements Serializable
{
    private static final long serialVersionUID = 1L;

	private boolean closable,
					collapsed,
					rendered,
					toggleable,
					visible;
	
	private String	footer = "I'm the footer text";
    private String header = "I'm the header text";
    private String style;
    private String styleClass;
    private String widgetVar;
    private String changeHeader;
    private String changeFooter;
    private String value;
    private String value2;
    private String value3;
	
	private int closeSpeed=2000,
				toggleSpeed=500;

	public boolean isClosable()
	{
		return closable;
	}

	public void setClosable(boolean closable)
	{
		this.closable = closable;
	}

	public boolean isCollapsed()
	{
		return collapsed;
	}

	public void setCollapsed(boolean collapsed)
	{
		this.collapsed = collapsed;
	}

	public boolean isRendered()
	{
		return rendered;
	}

	public void setRendered(boolean rendered)
	{
		this.rendered = rendered;
	}

	public boolean isToggleable()
	{
		return toggleable;
	}

	public void setToggleable(boolean toggleable)
	{
		this.toggleable = toggleable;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	public String getFooter()
	{
		return footer;
	}

	public void setFooter(String footer)
	{
		this.footer = footer;
	}

	public String getHeader()
	{
		return header;
	}

	public void setHeader(String header)
	{
		this.header = header;
	}

	public String getStyle()
	{
		return style;
	}

	public void setStyle(String style)
	{
		this.style = style;
	}

	public String getStyleClass()
	{
		return styleClass;
	}

	public void setStyleClass(String styleClass)
	{
		this.styleClass = styleClass;
	}

	public String getWidgetVar() 
	{
		return widgetVar;
	}

	public void setWidgetVar(String widgetVar)
	{
		this.widgetVar = widgetVar;
	}

	public int getCloseSpeed()
	{
		return closeSpeed;
	}

	public void setCloseSpeed(int closeSpeed)
	{
		this.closeSpeed = closeSpeed;
	}

	public int getToggleSpeed()
	{
		return toggleSpeed;
	}

	public void setToggleSpeed(int toggleSpeed)
	{
		this.toggleSpeed = toggleSpeed;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getValue3()
	{
		return value3;
	}

	public void setValue3(String value3)
	{
		this.value3 = value3;
	}

    public String getChangeHeader() {
        return changeHeader;
    }

    public void setChangeHeader(String changeHeader) {
        this.changeHeader = changeHeader;
    }

    public String getChangeFooter() {
        return changeFooter;
    }

    public void setChangeFooter(String changeFooter) {
        this.changeFooter = changeFooter;
    }



}
