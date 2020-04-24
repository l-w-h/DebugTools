# DebugTools
> Android DebugTools
> 可以保存调试log、可以拦截网络请求、拦截崩溃错误并保存,方便查看

# 使用方法

### 1.添加依赖

* 在项目build.gradle中添加
    ```
    allprojects {
        repositories {
            maven{ url 'https://dl.bintray.com/liwh/PublicMaven'}
        }
    }
    ```
* 在Module build.gradle中添加
    ```
    debugImplementation "com.lwh.debug:DebugTools:DebugTools:2.2.1@aar"
    releaseImplementation "com.lwh.debug:DebugTools:DebugToolsNoOp:2.2.1@aar"
    ```

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
        
                }).addIgnoreUrl("app/system/downApk")
    ```
    
* 在初始化网络请求时添加如下代码
    
    ```
        val httpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        httpClientBuilder.addNetworkInterceptor(DebugTools.getInstance().getRecordInterceptor(object :RecordInterceptor.OnDecryptCallback{
                    override fun onRequestBodyDecrypt(body: String): String {
                        return "request body 解密数据"
                    }

                    override fun onResponseBodyDecrypt(body: String?): String? {
                        return "response body 解密数据"
                    }
                }))
    ```

### 3. DebugTools API

* **API使用说明** 

  |方法|参数|说明|
  |:---:|:---|:---:|
  |getInstance|无|获取DebugTools实例|
  |init|autoAdd：自动添加DebugView，默认自动添加<br>magnetViewListener：DebugView点击移除监听，默认实现|初始化|
  |initCrash|enabled：是否启动全局异常捕获，默认启动<br>showErrorDetails:是否显示错误详细信息，默认显示<br>showRestartButton：是否显示重启按钮，默认显示<br>trackActivities：是否跟踪Activity，默认跟踪<br>minTimeBetweenCrashesMs：崩溃的间隔时间(毫秒)，默认2000ms<br>errorDrawable：崩溃后默认图标<br>restartActivityClass：重新启动后的activity<br>errorActivityClass：崩溃后的错误activity<br>eventListener：崩溃后的错误监听|初始化异常捕获|
  |startWhitecrashIntercept|interceptAll：拦截所有<br>onCrashListener：白名单崩溃通知|开启白名单拦截|
  |stopWhitecrashIntercept|无|关闭白名单崩溃拦截|
  |attachDebugView|activity：当前显示页面|手动添加DebugView|
  |detachDebugView|activity：当前显示页面|手动移除DebugView|
  |setDebugViewListener|magnetViewListener：DebugView点击移除监听，默认实现|设置DebugView点击移除监听|
  |getRecordInterceptor|callback：提供解密body方法，默认无|获取网络拦截器|
  |addIgnoreUrl|url:需要忽略拦截的url，例如下载文件的url需要忽略，否在可能卡死|添加忽略url|
  |logV，logD，logI<br>logW，logE|tag：tag<br>content：内容<br>jumpStack：储存打印数据时，获取当前调用任务堆栈信息，需要跳过的堆栈数|将调试log保存到log列表|
