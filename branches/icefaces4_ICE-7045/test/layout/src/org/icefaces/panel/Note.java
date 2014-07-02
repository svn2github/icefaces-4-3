package org.icefaces.panel;

import java.io.Serializable;

public class Note implements Serializable
{
    private static final long serialVersionUID = 1L;

	private String note, value;

	public Note(String note)
	{
		setNote(note);
	}

	public void setNote(String note)
	{
		this.note = note;
	}

	public String getNote()
	{
		return note;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}
}
