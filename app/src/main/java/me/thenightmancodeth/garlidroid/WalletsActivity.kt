package me.thenightmancodeth.garlidroid

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.activity_wallets.*
import me.thenightmancodeth.garlidroid.Retrofit.Address
import me.thenightmancodeth.garlidroid.Retrofit.GrlcService

class WalletsActivity : AppCompatActivity() {

    val grlcApiServe by lazy {
        GrlcService.create()
    }

    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            val builder = AlertDialog.Builder(this@WalletsActivity)
            builder.setTitle(getString(R.string.wallet_dialog_title))
                    .setCancelable(true)
                    .setPositiveButton("Add",
                            DialogInterface.OnClickListener { dialogInterface, i ->

                    })
        }

        getAddressData("GQjM4wvGKT1gRggRtSZ5r92tYdNwhKjDWP")
    }

    private fun getAddressData(address: String) {
        disposable = grlcApiServe.getAddressDetails(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result -> updateAddressInfo(result)},
                        { _ -> Toast.makeText(applicationContext, "Error communicating with blockchain", Toast.LENGTH_LONG).show() }
                )
    }

    private fun updateAddressInfo(result: Address) {
        addrText.text = result.address
        addrBalance.text = result.balance
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }

}
