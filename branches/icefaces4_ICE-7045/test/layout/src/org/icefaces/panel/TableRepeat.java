package org.icefaces.panel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ViewScoped
@ManagedBean
public class TableRepeat implements Serializable
{
    private static final long serialVersionUID = 1L;

	private List<Employee> employees;
	private List<Note> notes;
	
	public TableRepeat()
	{
		employees = new ArrayList<Employee>();
		notes = new ArrayList<Note>();
		
        notes.add(new Note("one"));
        notes.add(new Note("two"));
        notes.add(new Note("three"));
        Employee emp1 = new Employee("Markus",notes);
        employees.add(emp1);
 
        List<Note> notes2 = new ArrayList<Note>();
        notes2.add(new Note("four"));
        notes2.add(new Note("five"));
        notes2.add(new Note("six"));
        Employee emp2 = new Employee("Gareth",notes2);
        employees.add(emp2);

        List<Note> notes3 = new ArrayList<Note>();
        notes3.add(new Note("seven"));
        notes3.add(new Note("eight"));
        notes3.add(new Note("nine"));
        Employee emp3 = new Employee("Sander",notes3);
        employees.add(emp3);
	}

	public List<Employee> getEmployees()
	{
        return employees;
    }

    public void setEmployees(List<Employee> employees)
    {
        this.employees = employees;
    }
}
