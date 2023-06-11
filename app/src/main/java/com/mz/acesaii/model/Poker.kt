package com.mz.acesaii.model

import com.mz.acesaii.util.toRealmInstant
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId
import java.time.Instant

open class Poker: RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var date: RealmInstant = Instant.now().toRealmInstant()
    var description: String = ""
    var gameRecord: String = GameRecord.StartUpDialog.name
    var ownerId: String = ""
    var title: String = ""
}


