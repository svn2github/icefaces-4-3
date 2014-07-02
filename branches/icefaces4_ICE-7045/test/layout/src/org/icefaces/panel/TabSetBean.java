package org.icefaces.panel;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class TabSetBean implements Serializable
{
    private static final long serialVersionUID = 1L;

	private String value;

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}
	
	
}
