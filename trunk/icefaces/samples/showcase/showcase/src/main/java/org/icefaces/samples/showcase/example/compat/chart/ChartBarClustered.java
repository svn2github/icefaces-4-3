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
        title = "example.compat.chart.barClustered.title",
        description = "example.compat.chart.barClustered.description",
        example = "/resources/examples/compat/chart/chartBarClustered.xhtml"
)
@ExampleResources(
        resources ={
            // xhtml
            @ExampleResource(type = ResourceType.xhtml,
                    title="chartBarClustered.xhtml",
                    resource = "/resources/examples/compat/"+
                               "chart/chartBarClustered.xhtml"),
            // Java Source
            @ExampleResource(type = ResourceType.java,
                    title="ChartBarClustered.java",
                    resource = "/WEB-INF/classes/org/icefaces/samples/"+
                               "showcase/example/compat/chart/ChartBarClustered.java")
        }
)
@ManagedBean(name= ChartBarClustered.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class ChartBarClustered extends ComponentExampleImpl<ChartBarClustered> implements Serializable {
	
	public static final String BEAN_NAME = "chartBarClustered";
	
	private ChartModelAxial model = new ChartModelAxial("Bar Clustered", true, true, true, true);
	
	public ChartBarClustered() {
		super(ChartBarClustered.class);
	}
	
    @PostConstruct
    public void initMetaData() {
        super.initMetaData();
    }

	public String getType() { return OutputChart.BAR_CLUSTERED_CHART_TYPE; }
	public ChartModelAxial getModel() { return model; }
	
	public void setModel(ChartModelAxial model) { this.model = model; }
}
