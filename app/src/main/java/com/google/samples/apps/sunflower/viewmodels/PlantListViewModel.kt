/*
 * Copyright 2018 Google LLC
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

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.data.PlantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for plant list.
 * plantRepository 可以依赖注入
 * savedStateHandle是如何注入进来的？是由Hilt自动处理的
 * 因为它是一个由 Jetpack Navigation Compose 插件和 Hilt 集成提供的特殊依赖项
 * 依赖注入的对象只能在ViewModel函数体内使用，在ViewModel中定义的外部方法是无法访问的
 * 好奇怪！！！
 */
@HiltViewModel
class PlantListViewModel @Inject internal constructor(
    // 这里没有声明作用域（private,protected,public），那么只能在构造函数体内可见，ViewModel中定义的方法是不可见的，说明是构造函数的局部变量
    private val plantRepository: PlantRepository,
    // ViewModel生命周期内保存状态的处理器
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    // 创建可观察状态字段，Int类型，区分是否为种植区
    private val growZone: MutableStateFlow<Int> = MutableStateFlow(
        savedStateHandle.get(GROW_ZONE_SAVED_STATE_KEY) ?: NO_GROW_ZONE
    )

    // 将流数据转为LiveData，LiveData是生命周期内可观察对象
    // UI组件如何使用呢？
    // 当growZone发生变化时，flatMapLatest会返回一个新流
    // 整个处理过程好复杂呀！
    // 为了更新一个流列表数据，需要定义一个StateFlow状态字段来进行处理
    // 为啥不用重新赋值方式呢？ 直接赋值方式发现无法更新LiveData状态
    // 以下更新方式，可能是生成一个新的对象，但UI还是连接之前旧的对象
    // plants = plantRepository.getPlantsWithGrowZoneNumber(9).asLiveData() 这种赋值方式无效
    // LiveData是只读的，不可变对象
    // Flow数据转为LiveData或StateFlow
    val plants: LiveData<List<Plant>> = growZone.flatMapLatest { zone ->
        if (zone == NO_GROW_ZONE) {
            plantRepository.getPlants()
        } else {
            // zone=9 哪里传参过来的
            plantRepository.getPlantsWithGrowZoneNumber(zone)
        }
    }.asLiveData()



    init {
        /**
         * When `growZone` changes, store the new value in `savedStateHandle`.
         *
         * There are a few ways to write this; all of these are equivalent. (This info is from
         * https://github.com/android/sunflower/pull/671#pullrequestreview-548900174)
         *
         * 1) A verbose version:
         *
         *    viewModelScope.launch {
         *        growZone.onEach { newGrowZone ->
         *            savedStateHandle.set(GROW_ZONE_SAVED_STATE_KEY, newGrowZone)
         *        }
         *    }.collect()
         *
         * 2) A simpler version of 1). Since we're calling `collect`, we can consume
         *    the elements in the `collect`'s lambda block instead of using the `onEach` operator.
         *    This is the version that's used in the live code below.
         *
         * 3) We can avoid creating a new coroutine using the `launchIn` terminal operator. In this
         *    case, `onEach` is needed because `launchIn` doesn't take a lambda to consume the new
         *    element in the Flow; it takes a `CoroutineScope` that's used to create a coroutine
         *    internally.
         *
         *    growZone.onEach { newGrowZone ->
         *        savedStateHandle.set(GROW_ZONE_SAVED_STATE_KEY, newGrowZone)
         *    }.launchIn(viewModelScope)
         */

        viewModelScope.launch {
            // 当growZone值发生变化时将会重新搜集状态数据
            growZone.collect { newGrowZone ->
                // 将收到的新状态值保存到savedStateHandle中
                savedStateHandle.set(GROW_ZONE_SAVED_STATE_KEY, newGrowZone)
            }
        }
    }

    fun updateData() {
        if (isFiltered()) {
            clearGrowZoneNumber()
        } else {
            setGrowZoneNumber(9)
        }
    }


    fun setGrowZoneNumber(num: Int) {
        growZone.value = num
    }


    fun clearGrowZoneNumber() {
        growZone.value = NO_GROW_ZONE
    }

    fun isFiltered() = growZone.value != NO_GROW_ZONE

    companion object {
        private const val NO_GROW_ZONE = -1
        private const val GROW_ZONE_SAVED_STATE_KEY = "GROW_ZONE_SAVED_STATE_KEY"
    }
}