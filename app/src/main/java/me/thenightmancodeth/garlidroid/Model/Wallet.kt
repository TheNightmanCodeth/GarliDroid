package me.thenightmancodeth.garlidroid.Model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by joe on 1/28/18.
 */
open class Wallet(
        @PrimaryKey var id: String = "",
        var address: String = "",
        var title: String = "",
        var balance: String = ""
) : RealmObject() {/** Leave this nakey **/}