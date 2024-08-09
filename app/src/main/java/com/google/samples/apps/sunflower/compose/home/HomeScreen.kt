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

package com.google.samples.apps.sunflower.compose.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.compose.garden.GardenScreen
import com.google.samples.apps.sunflower.compose.plantlist.PlantListScreen
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.ui.SunflowerTheme
import com.google.samples.apps.sunflower.viewmodels.PlantListViewModel
import kotlinx.coroutines.launch

// Tab页配置
// 枚举类定义要求有2个参数
enum class SunflowerPage(
    @StringRes val titleResId: Int,
    @DrawableRes val drawableResId: Int
) {
    // 以下定义2个TAB页，定义枚举常量时需要传递枚举类定义的2个参数
    MY_GARDEN(R.string.my_garden_title, R.drawable.ic_my_garden_active),
    PLANT_LIST(R.string.plant_list_title, R.drawable.ic_plant_list_active)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onPlantClick: (Plant) -> Unit = {},
    viewModel: PlantListViewModel = hiltViewModel(),
    pages: Array<SunflowerPage> = SunflowerPage.values()
) {
    // 页面状态
    val pagerState = rememberPagerState(pageCount = { pages.size })

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            // 顶部菜单
            HomeTopAppBar(
                pagerState = pagerState,
                onFilterClick = { viewModel.updateData() },
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        HomePagerScreen(
            onPlantClick = onPlantClick,
            pagerState = pagerState,
            pages = pages,
            Modifier.padding(top = contentPadding.calculateTopPadding())
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomePagerScreen(
    onPlantClick: (Plant) -> Unit,
    pagerState: PagerState,
    pages: Array<SunflowerPage>,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        val coroutineScope = rememberCoroutineScope()

        // Tab Row
        // Tab 页
        TabRow(
            // 当前选中TAB标签，当pagerState发生改变时，选中的TAB页也会发生改变
            selectedTabIndex = pagerState.currentPage
        ) {
            // 遍历 pages 创建 Tab菜单
            pages.forEachIndexed { index, page ->
                val title = stringResource(id = page.titleResId)
                Tab(
                    // 初始化设置选中的TAB页
                    selected = pagerState.currentPage == index,
                    // 点击TAB页时，以动画形式，滚动到指定TAB页
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(text = title) },
                    icon = {
                        Icon(
                            painter = painterResource(id = page.drawableResId),
                            contentDescription = title
                        )
                    },
                    unselectedContentColor = MaterialTheme.colorScheme.secondary
                )
            }
        }

        // Pages，水平分页器
        HorizontalPager(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) {
            // 当水平滚动时，页面索引发生变化时会触发 pageContent 的改变
            index ->
            when (pages[index]) {
                // 我的花园
                SunflowerPage.MY_GARDEN -> {
                    GardenScreen(
                        Modifier.fillMaxSize(),
                        onAddPlantClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(SunflowerPage.PLANT_LIST.ordinal)
                            }
                        },
                        onPlantClick = {
                            onPlantClick(it.plant)
                        })
                }
                // 植物列表
                SunflowerPage.PLANT_LIST -> {
                    PlantListScreen(
                        onPlantClick = onPlantClick,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(
    pagerState: PagerState,
    onFilterClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    // 顶部菜单
    CenterAlignedTopAppBar(
        title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall
                )
        },
        modifier = modifier,
        // 可以加动作按钮，默认靠右边布局
        actions = {
            //  SunflowerPage.PLANT_LIST.ordinal 返回枚举常量对应的序号
            if (pagerState.currentPage == SunflowerPage.PLANT_LIST.ordinal) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter_list_24dp),
                        contentDescription = stringResource(
                            id = R.string.menu_filter_by_grow_zone
                        )
                    )
                }
            }

        },
        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun HomeScreenPreview() {
    SunflowerTheme {
        val pages = SunflowerPage.values()
        HomePagerScreen(
            onPlantClick = {},
            pagerState = rememberPagerState(pageCount = { pages.size }),
            pages = pages
        )
    }
}
