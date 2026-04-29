package com.yudev.wisatakebumen

import androidx.room.*

@Dao
interface WisataDao {

    @Query("SELECT * FROM wisata")
    fun getAll(): List<Wisata>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(w: Wisata)

    @Update
    fun update(w: Wisata): Int

    @Delete
    fun delete(w: Wisata)
}