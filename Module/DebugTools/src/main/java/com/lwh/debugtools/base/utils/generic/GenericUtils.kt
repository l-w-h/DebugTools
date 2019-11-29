package com.lwh.debugtools.base.utils.generic

import java.lang.reflect.ParameterizedType

/**
 * @author lwh
 * @Date 2019/9/11 16:12
 * @description 泛型工具类
 */
object GenericUtils {

    /**
     * 获取类的泛型实例
     * @param obj 类
     * @param defaultClazz 当泛型获取失败时，需要的默认返回泛型实例
     * @param index 泛型所在的下标 默认0
     */
    fun <T> getGeneric(obj: Any, defaultClazz: Class<T>, index: Int = 0): T {
        return getGenericClass(obj, defaultClazz, index).getDeclaredConstructor().newInstance()
    }


    /**
     * 获取类的泛型Class
     * @param obj 类
     * @param defaultClazz 当泛型获取失败时，需要的默认返回泛型实例
     * @param index 泛型所在的下标 默认0
     */
    fun <T> getGenericClass(obj: Any, defaultClazz: Class<T>, index: Int = 0): Class<T> {
        val type = obj.javaClass.genericSuperclass
        val clazz: Class<T> = if (type is ParameterizedType) {
            type.actualTypeArguments[index] as Class<T>
        } else {
            defaultClazz
        }
        return clazz
    }

}
