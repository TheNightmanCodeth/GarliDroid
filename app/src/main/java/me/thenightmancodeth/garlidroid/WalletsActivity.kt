package me.thenightmancodeth.garlidroid

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_wallets.*
import kotlinx.android.synthetic.main.content_wallets.*
import kotlinx.android.synthetic.main.wallet_dialog.*
import kotlinx.android.synthetic.main.wallet_dialog.view.*
import me.thenightmancodeth.garlidroid.Model.Wallet
import me.thenightmancodeth.garlidroid.Retrofit.Address
import me.thenightmancodeth.garlidroid.Retrofit.GrlcService
import java.util.*

class WalletsActivity : AppCompatActivity() {

    private val grlcApiServe by lazy {
        GrlcService.create()
    }

    private var disposable: Disposable? = null

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallets)

        Realm.init(this@WalletsActivity)
        realm = Realm.getDefaultInstance()



        fab.setOnClickListener {
            val builder = AlertDialog.Builder(this@WalletsActivity)
            val view = layoutInflater.inflate(R.layout.wallet_dialog, null)
            builder.setTitle(getString(R.string.wallet_dialog_title))
                    .setCancelable(true)
                    .setView(view)
                    .setPositiveButton("Add",
                            { dialog, int ->
                                grlcApiServe.getAddressDetails(view.wallet_input.text.toString())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                { result ->
                                                    realm.executeTransaction {
                                                        val newWallet: Wallet = realm.createObject(Wallet::class.java, UUID.randomUUID().toString())
                                                        newWallet.title = view.wallet_title.text.toString()
                                                        newWallet.address = result.address
                                                        newWallet.balance = result.balance
                                                    }
                                                }
                                        )
                            })
            builder.create().show()
        }

        setupRecycler()
    }

    private fun setupRecycler() {
        wallets_recycler.layoutManager = LinearLayoutManager(this)
        val adapter = RealmRVAdapter(WalletsActivity@this,
                realm.where(Wallet::class.java).findAllAsync())
        wallets_recycler.adapter = adapter
        wallets_recycler.setHasFixedSize(true)
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

}
