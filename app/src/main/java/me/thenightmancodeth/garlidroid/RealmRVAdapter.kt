package me.thenightmancodeth.garlidroid

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.wallet_list_item.view.*
import kotlinx.android.synthetic.main.wallet_list_item.view.list_root
import me.thenightmancodeth.garlidroid.Model.Wallet
import me.thenightmancodeth.garlidroid.Retrofit.CoinMktCapService
import me.thenightmancodeth.garlidroid.Retrofit.GrlcService

/**
 * Created by joe on 1/28/18.
 */
class RealmRVAdapter(context: Context, wallets: OrderedRealmCollection<Wallet>, val deleteListener: (Wallet) -> Unit) : RealmRecyclerViewAdapter<Wallet, RealmRVAdapter.WalletListViewHolder>(context, wallets, true) {

    private val grlcApiServe by lazy {
        GrlcService.create()
    }

    private val mktCapServe by lazy {
        CoinMktCapService.create()
    }

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): WalletListViewHolder{
        val listItem: View = LayoutInflater.from(p0?.context).inflate(R.layout.wallet_list_item, p0, false)
        return WalletListViewHolder(listItem)
    }

    override fun onBindViewHolder(p0: WalletListViewHolder?, p1: Int) {
        val wallet = data!![p1]
        grlcApiServe.getAddressDetails(wallet.address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            p0!!.itemView.titleText.text = wallet.title
                            p0.itemView.addrText.text = wallet.address
                            val bal = result.balance
                            p0.itemView.addrBalance.text = "${result.balance} GRLC"
                            mktCapServe.getUSDPrice()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe { cmkt ->
                                        val usd: Float = cmkt[0].price_usd.toFloat() * bal.toFloat()
                                        p0.itemView.addrBalanceUSD.text = "$%.2f USD".format(usd)
                                    }
                        }
                )
        p0?.itemView?.list_root?.setOnLongClickListener {
            deleteListener(wallet)
            true
        }
    }

    class WalletListViewHolder(view: View) : RecyclerView.ViewHolder(view)
}