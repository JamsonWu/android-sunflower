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

package com.google.samples.apps.sunflower.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.Calendar.DAY_OF_YEAR

// 使用数据类定义植物表
// 一个实体对应的是表中的一行记录，实体对象
@Entity(tableName = "plants")
data class Plant(
    // 定义主键字段，存在映射
    @PrimaryKey @ColumnInfo(name = "id") val plantId: String,
    // 以下字段不需要映射，即表字段名与UI使用字段名一致
    val name: String,
    val description: String,
    val growZoneNumber: Int,
    // 7 代表这个字段默认值
    val wateringInterval: Int = 7, // how often the plant should be watered, in days
    val imageUrl: String = ""
) {

    /**
     * 在实体对象中添加方法，方法可以直接访问实体的属性
     * Determines if the plant should be watered.  Returns true if [since]'s date > date of last
     * watering + watering Interval; false otherwise.
     * 判断植物是否应浇水了
     */
    fun shouldBeWatered(since: Calendar, lastWateringDate: Calendar) =
        since >
                // 日历添加多少天后的日期
                lastWateringDate.apply { add(DAY_OF_YEAR, wateringInterval) }

    override fun toString() = name
}
