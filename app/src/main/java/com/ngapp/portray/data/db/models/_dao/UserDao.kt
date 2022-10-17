package com.ngapp.portray.data.db.models._dao

import androidx.room.*
import com.ngapp.portray.data.db.models.user.User
import com.ngapp.portray.data.db.models.user.UserContract

@Dao
interface UserDao {

    @Query("SELECT * FROM ${UserContract.TABLE_NAME} WHERE ${UserContract.Columns.ID} = :userId")
    suspend fun getUserById(userId: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("UPDATE ${UserContract.TABLE_NAME} SET ${UserContract.Columns.LOGGED_USER} = 1 WHERE ${UserContract.Columns.USERNAME} = :username")
    suspend fun updateLoggedUser(username: String)

    @Query("SELECT * FROM ${UserContract.TABLE_NAME} WHERE ${UserContract.Columns.LOGGED_USER} = 1")
    suspend fun findLoggedUser(): User?

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT MAX(${UserContract.Columns.MOD_DATE}) from ${UserContract.TABLE_NAME}")
    suspend fun lastUpdated(): Long?

    @Query("DELETE FROM ${UserContract.TABLE_NAME} WHERE ${UserContract.Columns.LOGGED_USER} = 1")
    suspend fun deleteLoggedUserData()
}