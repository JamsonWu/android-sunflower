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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.samples.apps.sunflower.utilities.DATABASE_NAME
import com.google.samples.apps.sunflower.utilities.PLANT_DATA_FILENAME
import com.google.samples.apps.sunflower.workers.SeedDatabaseWorker
import com.google.samples.apps.sunflower.workers.SeedDatabaseWorker.Companion.KEY_FILENAME

/**
 * The Room database for this app
 * Room 管理 Sqlite 数据库
 * entities 指定数据库有哪些表
 */
@Database(entities = [GardenPlanting::class, Plant::class], version = 1, exportSchema = false)
// 指定类型转换器
@TypeConverters(Converters::class)
// RoomDatabase是Room数据库的抽象类
abstract class AppDatabase : RoomDatabase() {
    // 提供操作花园植物表的Dao
    abstract fun gardenPlantingDao(): GardenPlantingDao
    // 提供操作植物表的Dao
    abstract fun plantDao(): PlantDao

    // 内联对象
    companion object {

        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {

            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // 在数据库构建后的回调中添加 WorkManger
                            // 添加一个作业任务，读取本地assets下的json文件数据保存到本地数据库中
                            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
                                    .setInputData(workDataOf(KEY_FILENAME to PLANT_DATA_FILENAME))
                                    .build()
                            WorkManager.getInstance(context).enqueue(request)
                        }
                    }
                )
                .build()
        }
    }
}
