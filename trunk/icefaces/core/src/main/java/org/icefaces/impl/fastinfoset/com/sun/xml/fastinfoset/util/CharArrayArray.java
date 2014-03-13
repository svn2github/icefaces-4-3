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

import org.icefaces.impl.fastinfoset.com.sun.xml.fastinfoset.CommonResourceBundle;

public class CharArrayArray extends ValueArray {
    
    private CharArray[] _array;
    
    private CharArrayArray _readOnlyArray;
    
    public CharArrayArray(int initialCapacity, int maximumCapacity) {
        _array = new CharArray[initialCapacity];
        _maximumCapacity = maximumCapacity;
    }

    public CharArrayArray() {
        this(DEFAULT_CAPACITY, MAXIMUM_CAPACITY);
    }
    
    public final void clear() {
        for (int i = 0; i < _size; i++) {
            _array[i] = null;
        }
        _size = 0;
    }

    public final CharArray[] getArray() {
        return _array;
    }
    
    public final void setReadOnlyArray(ValueArray readOnlyArray, boolean clear) {
        if (!(readOnlyArray instanceof CharArrayArray)) {
            throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.illegalClass", new Object[]{readOnlyArray}));
        }       
        
        setReadOnlyArray((CharArrayArray)readOnlyArray, clear);
    }

    public final void setReadOnlyArray(CharArrayArray readOnlyArray, boolean clear) {
        if (readOnlyArray != null) {
            _readOnlyArray = readOnlyArray;
            _readOnlyArraySize = readOnlyArray.getSize();
                        
            if (clear) {
                clear();
            }
        }
    }
    
    public final CharArray get(int i) {
        if (_readOnlyArray == null) {
            return _array[i];
        } else {
            if (i < _readOnlyArraySize) {
               return _readOnlyArray.get(i); 
            } else {
                return _array[i - _readOnlyArraySize];
            }
        }
   }
    
    public final void add(CharArray s) {
        if (_size == _array.length) {
            resize();
        }
            
       _array[_size++] = s;
    }
    
    protected final void resize() {
        if (_size == _maximumCapacity) {
            throw new ValueArrayResourceException(CommonResourceBundle.getInstance().getString("message.arrayMaxCapacity"));
        }

        int newSize = _size * 3 / 2 + 1;
        if (newSize > _maximumCapacity) {
            newSize = _maximumCapacity;
        }

        final CharArray[] newArray = new CharArray[newSize];
        System.arraycopy(_array, 0, newArray, 0, _size);
        _array = newArray;
    }
}
