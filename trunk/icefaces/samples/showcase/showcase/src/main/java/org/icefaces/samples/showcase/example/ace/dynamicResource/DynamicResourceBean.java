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

package org.icefaces.samples.showcase.example.ace.dynamicResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.Resource;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@ManagedBean(name= DynamicResourceBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class DynamicResourceBean implements Serializable {
    public static final String BEAN_NAME = "dynamicResourceBean";
	public String getBeanName() { return BEAN_NAME; }
    
    @PostConstruct
    public void initMetaData() {
		String resourcePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/media/icemobile.pdf");
		File file = new File(resourcePath);
		try {
			this.resource = new MyResource(readIntoByteArray(new FileInputStream(file)));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			this.resource = new MyResource(new byte[0]);
		}
    }

	private Resource resource;
	private String label = "Click here to download";
	private String type = "link";

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    private static byte[] readIntoByteArray(InputStream in) throws IOException {
        byte[] buffer = new byte[4096];
        int bytesRead;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();

        return out.toByteArray();
    }
	
	public class MyResource extends Resource implements java.io.Serializable {

		private String path = "";
		private HashMap<String, String> headers;
		private byte[] bytes;
		
		public MyResource(byte[] bytes) {
			this.bytes = bytes;
			this.headers = new HashMap<String, String>();
		}
		
		public InputStream getInputStream() {
			return new ByteArrayInputStream(this.bytes);
		}

		public String getRequestPath() {
			return path;
		}
		
		public void setRequestPath(String path) {
			this.path = path;
		}

		public Map<String, String> getResponseHeaders() {
			return headers;
		}

		public URL	getURL() {
			return null;
		}

		public boolean userAgentNeedsUpdate(FacesContext context) {
			return false;
		}
	}
}
