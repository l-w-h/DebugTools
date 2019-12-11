package com.lwh.debugtoolsdemo.api

import com.lwh.debugtools.DebugTools
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * @author lwh
 * @Date 2019/8/26 20:41
 * @description ServiceUtils
 */
object ServiceUtils {

    val BASE_URL = "https://m.weibo.cn/";
    private val HTTP_CLIENT = initOkHttpClient(BASE_URL)

    fun <T> getApi(clazz:Class<T>): T{
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(HTTP_CLIENT)
            .build()
            .create(clazz)
    }

    private fun initOkHttpClient(url: String): OkHttpClient {
        val httpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        httpClientBuilder.addNetworkInterceptor(DebugTools.getInstance().getRecordInterceptor())
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)

        if (url.startsWith("https://")) {
            //配置HTTPS证书
            val trustManager = getTrustManager()
            val sslSocketFactory = getSSLSocketFactory(trustManager)
            httpClientBuilder.sslSocketFactory(sslSocketFactory!!, trustManager)
            httpClientBuilder.hostnameVerifier { _, _ -> true }
            httpClientBuilder.connectionSpecs(
                listOf(
                    ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_1)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
                        )
                        .build()
                )
            )
        }

        return httpClientBuilder.build()
    }


    private fun getSSLSocketFactory(trustManager: TrustManager): SSLSocketFactory? {
        var sslContext: SSLContext?
        var sslSocketFactory: SSLSocketFactory? = null
        try {
            sslContext = SSLContext.getInstance("SSL")
            sslContext!!.init(null, arrayOf(trustManager), null)
            sslSocketFactory = sslContext.socketFactory
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }

        return sslSocketFactory
    }

    private fun getTrustManager(): X509TrustManager {
        val trustManagers = getTrustManagers()
        if (trustManagers.size != 1 || trustManagers[0] !is X509TrustManager) {
            throw IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers))
        }
        return trustManagers[0] as X509TrustManager
    }

    /**
     * 获取TrustManagers
     */
    private fun getTrustManagers(): Array<TrustManager> {
        return arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })
    }


}