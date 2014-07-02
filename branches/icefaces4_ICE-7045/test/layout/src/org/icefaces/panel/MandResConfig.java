package org.icefaces.panel;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class MandResConfig implements Serializable
{
    private static final long serialVersionUID = 1L;

	private boolean	rendered=false;
	
	private String value="include-txt.xhtml",
				   txt;
	

	public boolean isRendered()
	{
		return rendered;
	}

	public void setRendered(boolean rendered)
	{
		if (rendered)
			setValue("include-comp.xhtml");
		else if (!rendered)
			setValue("include-txt.xhtml");
			
		this.rendered = rendered;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
	
	public void setTxt(String txt)
	{
		this.txt = txt;
	}

	public String getTxt()
	{
		return txt;
	}	
}
