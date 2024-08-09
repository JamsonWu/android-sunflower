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
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Calendar

/**
 * [GardenPlanting] represents when a user adds a [Plant] to their garden, with useful metadata.
 * Properties such as [lastWateringDate] are used for notifications (such as when to water the
 * plant).
 *
 * Declaring the column info allows for the renaming of variables without implementing a
 * database migration, as the column name would not change.
 */
@Entity(
    tableName = "garden_plantings",
    // 定义父子表关系，外键
    foreignKeys = [
        // 外键首先指定子表，然后指定父字段与子字段
        ForeignKey(entity = Plant::class, parentColumns = ["id"], childColumns = ["plant_id"])
    ],
    indices = [Index("plant_id")]
)
data class GardenPlanting(
    // @ColumnInfo用于列映射，表字段为 plant_id映射为 plantId
    @ColumnInfo(name = "plant_id") val plantId: String,

    /**
     * Indicates when the [Plant] was planted. Used for showing notification when it's time
     * to harvest the plant.
     * 种植日期
     */
    @ColumnInfo(name = "plant_date") val plantDate: Calendar = Calendar.getInstance(),

    /**
     * Indicates when the [Plant] was last watered. Used for showing notification when it's
     * time to water the plant.
     * 最后浇水日期
     */
    @ColumnInfo(name = "last_watering_date")
    val lastWateringDate: Calendar = Calendar.getInstance()
) {
    // 定义主键字段为自增
    @PrimaryKey(autoGenerate = true)
    // 表字段名为id
    @ColumnInfo(name = "id")
    // UI访问字段别名为gardenPlantingId，Long类型
    var gardenPlantingId: Long = 0
}
