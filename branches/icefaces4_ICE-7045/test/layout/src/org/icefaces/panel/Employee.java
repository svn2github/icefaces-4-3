package org.icefaces.panel;

import java.util.List;
import java.io.Serializable;

public class Employee implements Serializable
{
    private static final long serialVersionUID = 1L;

	private String firstName, value;
	private List<Note> notes;
	
	//Constructor for PushBean, TableBean, UpdateTblBtt, UpdateTblTop
	public Employee(String firstName)
	{
		setFirstName(firstName);
	}
	
	//Constructor for TableRepeat
	public Employee(String firstName, List<Note> notes)
	{
		setFirstName(firstName);
		setNotes(notes);
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	public void setNotes(List<Note> notes)
	{
		this.notes = notes;
	}

	public List<Note> getNotes()
	{
		return notes;
	}
}