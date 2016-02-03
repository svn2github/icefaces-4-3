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

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2004-2011 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.icefaces.impl.fastinfoset.com.sun.xml.fastinfoset.util;

public abstract class ValueArray {
    public static final int DEFAULT_CAPACITY = 10;
    public static final int MAXIMUM_CAPACITY = Integer.MAX_VALUE;

    protected int _size;
    
    protected int _readOnlyArraySize;  
    
    protected int _maximumCapacity;
            
    public int getSize() {
        return _size;
    }

    public int getMaximumCapacity() {
        return _maximumCapacity;
    }
    
    public void setMaximumCapacity(int maximumCapacity) {
        _maximumCapacity = maximumCapacity;
    }
    
    public abstract void setReadOnlyArray(ValueArray array, boolean clear);
    
    public abstract void clear();
}
