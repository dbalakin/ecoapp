package com.example.myapplication.data.sql_helper

import androidx.room.*

@Dao
interface ContentDao {

    @Query("SELECT * FROM favoriteInfo")
    fun getAll(): MutableList<FavoriteInfo>


    @Query("SELECT * FROM favoriteInfo WHERE lat LIKE :lat AND lng LIKE :lng LIMIT 1")
    fun findByLatLng(lat: Double, lng: Double): FavoriteInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(favoriteInfo: FavoriteInfo)

    @Delete
    fun delete(favoriteInfo: FavoriteInfo)
}


@Entity
data class FavoriteInfo(
        @PrimaryKey @ColumnInfo(name = "address") val address: String,
        @ColumnInfo(name = "place") val place: String,
        @ColumnInfo(name = "corruption_info") val corruptionInfo: String?,
        @ColumnInfo(name = "lat") val lat: Double,
        @ColumnInfo(name = "lng") val lng: Double,
        @ColumnInfo(name = "output_text") val outputText: String = "",
) {
}


@Database(entities = [FavoriteInfo::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): ContentDao
}

