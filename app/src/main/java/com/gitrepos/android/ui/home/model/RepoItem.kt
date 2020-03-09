package com.gitrepos.android.ui.home.model

import android.view.View
import com.gitrepos.android.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.view_repo_item.view.*


/**
 * Class to bind the data to repositories list item
 */

class RepoItem(val repo: Repo) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.txtFullName.text = repo.fullName

        viewHolder.itemView.txtDesc.visibility = View.GONE
        repo.description?.let {
            viewHolder.itemView.txtDesc.visibility = View.VISIBLE
            viewHolder.itemView.txtDesc.text = repo.description
        }

        viewHolder.itemView.txtLanguage.text = ""
        repo.language?.let {
            viewHolder.itemView.txtLanguage.text = repo.language
        }

        viewHolder.itemView.txtStars.text = repo.starsCount

    }

    override fun getLayout(): Int = R.layout.view_repo_item
}
