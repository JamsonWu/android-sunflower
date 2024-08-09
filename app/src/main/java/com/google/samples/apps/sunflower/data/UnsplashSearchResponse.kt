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

package com.google.samples.apps.sunflower.data

import com.google.gson.annotations.SerializedName

/**
 * Data class that represents a photo search response from Unsplash.
 *
 * Not all of the fields returned from the API are represented here; only the ones used in this
 * project are listed below. For a full list of fields, consult the API documentation
 * [here](https://unsplash.com/documentation#search-photos).
 */
data class UnsplashSearchResponse(
    // 注解标识对应哪个字段：results是JSON中的字段
    // 接口JSON中的字段与代码字段进行映射，如果相同可以不用映射
    @field:SerializedName("results")
    val results: List<UnsplashPhoto>,
    // 注解标识对应哪个字段，total_pages 总页数也是JSON字段
    @field:SerializedName("total_pages")
    val totalPages: Int
)
