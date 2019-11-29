/**
 * Copyright [2019] lwh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lwh.debug.delegate

import kotlin.reflect.KProperty

/**
 * @author lwh
 * @Date 2019/8/27 22:48
 * @description MapDelegate
 */
class MapDelegate<T>(val map: MutableMap<String, Any?>) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return map[property.name] as T;
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        map[property.name] = value;
    }
}