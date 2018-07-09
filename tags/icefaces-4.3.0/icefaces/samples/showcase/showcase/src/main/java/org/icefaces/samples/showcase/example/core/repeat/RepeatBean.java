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

package org.icefaces.samples.showcase.example.core.repeat;

import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;

@ManagedBean(name = RepeatBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class RepeatBean implements Serializable {
    private Person[] people = new Person[]{
            new Person("John","Doe"),
            new Person("Huckleberry","Finn"),
            new Person("Louis", "Armstrong"),
            new Person("Catherine", "Deneuve")
    };

    public static final String BEAN_NAME = "repeatBean";
	public String getBeanName() { return BEAN_NAME; }

    public Person[] getPeople() {
        return people;
    }

    public static class Person implements Serializable {
	    private String firstName;
	    private String lastName;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }
    }
}
