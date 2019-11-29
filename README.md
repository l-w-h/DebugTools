# DebugTools
Android debug tools

# 使用方法

### 1.添加依赖
    debugImplementation project("com.lwh.debug:DebugTools:DebugTools:2.1.0@aar")
    releaseImplementation project("com.lwh.debug:DebugTools:DebugToolsNoOp:2.1.0@aar")

### 2.添加代码

* 在Application中的onCreate方法中添加如下代码
    ```
        DebugTools.getInstance(this).init(true).initCrash(
                    MainActivity::class.java
                ).startWhitecrashIntercept(onCrashListener = object :OnCrashListener{
                    override fun onCrash(throwable: Throwable) {
                        Toast.makeText(this@App,"""
                            |捕获到崩溃信息：
                            |${throwable.javaClass.simpleName}
                            |${throwable.message}""".trimMargin(),Toast.LENGTH_SHORT).show()
                    }
        
                })
    ```
    
* 在初始化网络请求时添加如下代码
    
    ```
        val httpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        httpClientBuilder.addInterceptor(DebugTools.getInstance().getRecordInterceptor())
    ```
    