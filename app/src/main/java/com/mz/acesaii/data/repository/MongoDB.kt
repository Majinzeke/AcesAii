package com.mz.acesaii.data.repository

import android.security.keystore.UserNotAuthenticatedException
import com.mz.acesaii.model.Poker
import com.mz.acesaii.model.RequestState
import com.mz.acesaii.util.Constants.APP_ID
import com.mz.acesaii.util.toInstant
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.time.ZoneId

object MongoDB : MongoRepository {

    private val app = App.create(APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(user, setOf(Poker::class))
                .initialSubscriptions { sub ->
                    add(
                        query = sub.query<Poker>(query = "ownerId == $0", user.id),
                        name = "User's Poker Entries"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    override fun getAllPokerEntries(): Flow<Entries> {
        return if (user != null) {
            try {
                realm.query<Poker>(query = "ownerId == $0", user.id)
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        RequestState.Success(
                            data = result.list.groupBy {
                                it.date.toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                        )
                    }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }


    }

    override suspend fun getSelectedPokerEntry(pokerId: ObjectId): RequestState<Poker> {
        return if (user != null){
            try {
                val entry = realm.query<Poker>(query = "_id == $0", pokerId).find().first()
                RequestState.Success(data = entry)
            }catch (e: Exception){
                RequestState.Error(e)
            }
        }else{
            RequestState.Error(UserNotAuthenticatedException())
        }
    }


    override suspend fun addNewPokerEntry(poker: Poker): RequestState<Poker> {
        return if (user != null){
           realm.write {
               try {
                   val addedEntry = copyToRealm(poker.apply { ownerId = user.id })
                   RequestState.Success(data = addedEntry)
               }catch (e: Exception){
                   RequestState.Error(e)
               }
           }
        }else{
            RequestState.Error(UserNotAuthenticatedException())
        }
    }
}