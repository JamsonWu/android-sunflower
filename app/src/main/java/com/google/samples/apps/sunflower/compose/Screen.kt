/*
 * Copyright 2023 Google LLC
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

package com.google.samples.apps.sunflower.compose

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

// 路由配置，内部类
sealed class Screen(
    val route: String,
    // 导航命名参数列表
    val navArguments: List<NamedNavArgument> = emptyList()
) {
    // 首页，Home又实现Screen
    data object Home : Screen("home")
    // 明细页
    data object PlantDetail : Screen(
        // 动态路由，动态参数用{}包装
        route = "plantDetail/{plantId}",
        // 导航参数，指定动态参数字段名，类型为字符串
        navArguments = listOf(navArgument("plantId") {
            type = NavType.StringType
        })
    ) {
        // 创建动态路由
        fun createRoute(plantId: String) = "plantDetail/${plantId}"
    }

    data object Gallery : Screen(
        route = "gallery/{plantName}",
        navArguments = listOf(navArgument("plantName") {
            type = NavType.StringType
        })
    ) {
        fun createRoute(plantName: String) = "gallery/${plantName}"

    }
}