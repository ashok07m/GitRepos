package com.gitrepos.android.ui.home.model

import com.gitrepos.android.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.view_repo_item.view.*


/**
 * @author Created by kuashok on 2020-02-16
 */

class RepoItem(private val repo: Repo) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.txtTitle.text = repo.title
        viewHolder.itemView.txtDesc.text = repo.description
        viewHolder.itemView.txtLang.text = repo.language
        viewHolder.itemView.txtStarCount.text = repo.starCount.toString()
        viewHolder.itemView.txtLastUpdated.text = repo.lastUpdated
    }

    override fun getLayout(): Int = R.layout.view_repo_item

}
