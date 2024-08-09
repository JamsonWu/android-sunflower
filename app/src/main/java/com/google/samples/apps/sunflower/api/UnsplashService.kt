/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.api

import com.google.samples.apps.sunflower.BuildConfig
import com.google.samples.apps.sunflower.data.UnsplashSearchResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Used to connect to the Unsplash API to fetch photos
 * Api接口定义
 */
interface UnsplashService {

    // 接口上下文地址
    @GET("search/photos")
    suspend fun searchPhotos(
        // query 查询条件
        @Query("query") query: String,
        // 第几页
        @Query("page") page: Int,
        // 每页多少条
        @Query("per_page") perPage: Int,

        @Query("client_id") clientId: String = BuildConfig.UNSPLASH_ACCESS_KEY
    ): UnsplashSearchResponse

    // 内联对象
    companion object {
        // 接口地址
        // private const val BASE_URL = "https://api.unsplash.com/"
        // 创建接口
        fun create(): UnsplashService {
            // 日志
            //    val logger = HttpLoggingInterceptor().apply { level = Level.BASIC }
            //    // 这里是client
            //    val client = OkHttpClient.Builder()
            //        .addInterceptor(logger)
            //        .build()
            //
            //    // 实例化接口请求实例
            //    return Retrofit.Builder()
            //        .baseUrl(BASE_URL)
            //        .client(client)
            //        .addConverterFactory(GsonConverterFactory.create())
            //        .build()
            //        .create(UnsplashService::class.java)
            return Request<UnsplashService>().create(UnsplashService::class.java)
        }
    }
}