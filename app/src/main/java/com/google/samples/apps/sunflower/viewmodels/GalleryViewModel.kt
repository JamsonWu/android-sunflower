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

package com.google.samples.apps.sunflower.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.samples.apps.sunflower.data.UnsplashPhoto
import com.google.samples.apps.sunflower.data.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// 注入分页数据仓库
@HiltViewModel
class GalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: UnsplashRepository
) : ViewModel() {

    // 查询字符串，是植物名称
    private var queryString: String? = savedStateHandle["plantName"]
    //
    private val _plantPictures = MutableStateFlow<PagingData<UnsplashPhoto>?>(null)
    // 使用Flow数据源
    val plantPictures: Flow<PagingData<UnsplashPhoto>>
        // 返回非空流
        get() = _plantPictures.filterNotNull()

    init {
        refreshData()
    }

    // 刷新数据
    fun refreshData() {
        viewModelScope.launch {
            try {
                // 更新StateFlow数据
                _plantPictures.value = repository.getSearchResultStream(queryString ?: "")
                    .cachedIn(viewModelScope)
                    .first()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}