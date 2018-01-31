package me.thenightmancodeth.garlidroid

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import io.realm.Realm

import kotlinx.android.synthetic.main.activity_wallets.*
import kotlinx.android.synthetic.main.content_wallets.*
import kotlinx.android.synthetic.main.wallet_dialog.view.*
import me.thenightmancodeth.garlidroid.Model.Wallet
import java.util.*

class WalletsActivity : AppCompatActivity() {



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
                                if (view.wallet_input.text.toString().length == 34) {
                                    realm.executeTransaction {
                                        val newWallet: Wallet = realm.createObject(Wallet::class.java, UUID.randomUUID().toString())
                                        newWallet.title = view.wallet_title.text.toString()
                                        newWallet.address = view.wallet_input.text.toString()
                                    }
                                } else {
                                    Snackbar.make(
                                            coordinator,
                                            "Invalid address",
                                            Snackbar.LENGTH_LONG)
                                            .show()
                                }
                            })
            builder.create().show()
        }

        setupRecycler()

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            setupRecycler()
        }
    }

    private fun setupRecycler() {
        wallets_recycler.layoutManager = LinearLayoutManager(this)
        val adapter = RealmRVAdapter(WalletsActivity@this,
                realm.where(Wallet::class.java).findAllAsync(),
                { wallet ->
                    //We need to hold the data until the undo snackbar is dismissed
                    val tempWallet = Wallet()
                    tempWallet.title = wallet.title
                    tempWallet.address = wallet.address
                    tempWallet.balance = wallet.balance
                    AlertDialog.Builder(this)
                            .setTitle("Delete Wallet?")
                            .setCancelable(false)
                            .setNegativeButton("Cancel", null) //null closes dialog
                            .setPositiveButton("Delete", {_,_ ->
                                Snackbar.make(coordinator, "Wallet deleted from realm", Snackbar.LENGTH_LONG)
                                        .setAction("UNDO", {
                                            realm.executeTransaction {
                                                val newWallet: Wallet = realm.createObject(Wallet::class.java, UUID.randomUUID().toString())
                                                newWallet.title = tempWallet.title
                                                newWallet.address = tempWallet.address
                                                newWallet.balance = tempWallet.balance
                                            }
                                        })
                                        .show()
                                realm.executeTransaction {
                                    wallet.deleteFromRealm()
                                }
                            })
                            .setMessage("Are you sure you want to delete ${wallet.title}?")
                            .show()
                })
        wallets_recycler.adapter = adapter
        wallets_recycler.setHasFixedSize(true)
        swipeRefreshLayout.isRefreshing = false
    }

}
