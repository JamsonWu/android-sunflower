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

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.samples.apps.sunflower.api.UnsplashService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
// 数据源封装为一个仓库
// @Inject指定在哪里需要依赖项，这里通过构造器依赖注入UnsplashService
class UnsplashRepository @Inject constructor(private val service: UnsplashService) {
    // 返回的数据流是分页，PagingData包含分页数据
    fun getSearchResultStream(query: String): Flow<PagingData<UnsplashPhoto>> {
        //  Pager 接口分页请求
        return Pager(
            // 分页配置
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = NETWORK_PAGE_SIZE),
            // 分页源
            pagingSourceFactory = {
                UnsplashPagingSource(service, query)
            }
        ).flow
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 25
    }
}
