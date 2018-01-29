package me.thenightmancodeth.garlidroid

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.wallet_list_item.view.*
import me.thenightmancodeth.garlidroid.Model.Wallet

/**
 * Created by joe on 1/28/18.
 */
class RealmRVAdapter(context: Context, wallets: OrderedRealmCollection<Wallet>) : RealmRecyclerViewAdapter<Wallet, RealmRVAdapter.WalletListViewHolder>(context, wallets, true) {

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): WalletListViewHolder{
        val listItem: View = LayoutInflater.from(p0?.context).inflate(R.layout.wallet_list_item, p0, false)
        return WalletListViewHolder(listItem)
    }

    override fun onBindViewHolder(p0: WalletListViewHolder?, p1: Int) {
        val wallet = data!![p1]
        p0!!.itemView.addrText.text = wallet.address
        p0.itemView.addrBalance.text = wallet.balance
    }

    class WalletListViewHolder(view: View) : RecyclerView.ViewHolder(view)
}