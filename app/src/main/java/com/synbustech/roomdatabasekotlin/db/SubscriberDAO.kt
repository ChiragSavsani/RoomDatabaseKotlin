package com.synbustech.roomdatabasekotlin.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SubscriberDAO {

    @Insert
    suspend fun insertSubscriber(subscriber: Subscriber): Long

    @Update
    suspend fun updateSubscriber(subscriber: Subscriber)

    @Delete
    suspend fun deleteSubscriber(subscriber: Subscriber)

    @Query("DELETE FROM subscriber_data_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM subscriber_data_table")
    fun getAllSubscribers(): LiveData<List<Subscriber>>

    /*//We can create different versions of insert method like below
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubscriber2(subscriber: Subscriber): Long

    @Insert
    fun insertSubscriber3(subscriber1: Subscriber, subscriber2: Subscriber, subscriber3: Subscriber): List<Long>

    @Insert
    fun insertSubscriber4(subscribers: List<Subscriber>): List<Long>

    @Insert
    fun insertSubscriber5(subscriber1: Subscriber, subscribers: List<Subscriber>): List<Long>*/

}