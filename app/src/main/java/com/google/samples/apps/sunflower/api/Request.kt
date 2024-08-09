package com.google.samples.apps.sunflower.api

import com.google.samples.apps.sunflower.api.UnsplashService.Companion
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 接口请求通用封装
class Request<T> {
    private val BASE_URL = "https://api.unsplash.com/"

    // 创建接口
    fun create(serviceClass: Class<T>): T {
        // 日志
        val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }
        // 这里是client
        val client = OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()

        // 实例化接口请求实例
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(serviceClass)
    }
}