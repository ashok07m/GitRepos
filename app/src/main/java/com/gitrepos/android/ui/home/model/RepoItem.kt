package com.gitrepos.android.ui.home.model

import android.view.View
import com.gitrepos.android.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.view_repo_item.view.*


/**
 * @author Created by kuashok on 2020-02-16
 */

class RepoItem(val repo: Repo) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtTitle.text = repo.title
        viewHolder.itemView.txtDesc.visibility = View.GONE
        repo.description?.let {
            viewHolder.itemView.txtDesc.visibility = View.VISIBLE
            viewHolder.itemView.txtDesc.text = repo.description
        }
        viewHolder.itemView.txtLangValue.text = repo.languageUrl
        viewHolder.itemView.txtStarCount.text = repo.starCount.toString()
        viewHolder.itemView.txtLastUpdated.text = repo.lastUpdated

    }

    override fun getLayout(): Int = R.layout.view_repo_item

}
