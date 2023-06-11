package com.mz.acesaii.data.repository

import com.mz.acesaii.model.Poker
import com.mz.acesaii.model.RequestState
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

typealias Entries = RequestState<Map<LocalDate, List<Poker>>>

interface MongoRepository {
    fun configureTheRealm()
    fun getAllPokerEntries(): Flow<Entries>
    suspend fun getSelectedPokerEntry(pokerId: ObjectId): RequestState<Poker>
    suspend fun addNewPokerEntry(poker: Poker): RequestState<Poker>
}