/*
 * Copyright 2004-2013 ICEsoft Technologies Canada Corp.
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

package org.icefaces.samples.showcase.example.compat.chart;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;

import com.icesoft.faces.component.outputchart.OutputChart;

import org.icefaces.samples.showcase.metadata.annotation.ComponentExample;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResource;
import org.icefaces.samples.showcase.metadata.annotation.ExampleResources;
import org.icefaces.samples.showcase.metadata.annotation.ResourceType;
import org.icefaces.samples.showcase.metadata.context.ComponentExampleImpl;

@ComponentExample(
        parent = ChartBean.BEAN_NAME,
        title = "example.compat.chart.barStacked.title",
        description = "example.compat.chart.barStacked.description",
        example = "/resources/examples/compat/chart/chartBarStacked.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="chartBarStacked.xhtml",
                    resource = "/resources/examples/compat/"+
                               "chart/chartBarStacked.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="ChartBarStacked.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/"+
                               "showcase/example/compat/chart/ChartBarStacked.java")
        }
)
@ManagedBean(name= ChartBarStacked.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ChartBarStacked extends ComponentExampleImpl<ChartBarStacked> implements Serializable {
	
	public static final String BEAN_NAME = "chartBarStacked";
	
	private ChartModelAxial model = new ChartModelAxial("Bar Stacked", true, false, true, true, false);
	
	public ChartBarStacked() {
		super(ChartBarStacked.class);
	}
	
    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }

	public String getType() { return OutputChart.BAR_STACKED_CHART_TYPE; }
	public ChartModelAxial getModel() { return model; }
	
	public void setModel(ChartModelAxial model) { this.model = model; }
}
