package com.gitrepos.android.ui.home.model

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.signature.ObjectKey
import com.gitrepos.android.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.view_repo_item.view.*
import java.io.File


/**
 * Class to bind the data to repositories list item
 */

class RepoItem(val repo: Repo) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val context = viewHolder.itemView.txtTitle.context
        viewHolder.itemView.txtTitle.text = repo.fullName
        viewHolder.itemView.txtDesc.visibility = View.GONE
        repo.description?.let {
            viewHolder.itemView.txtDesc.visibility = View.VISIBLE
            viewHolder.itemView.txtDesc.text = repo.description
        }

        // load profile thumbnail
        loadImage(context, repo.avatarUrl, viewHolder.itemView.imgOwnerAvatar)
    }

    override fun getLayout(): Int = R.layout.view_repo_item


    /**
     * Loads image in image view
     */
    private fun loadImage(context: Context, uri: String, imgOwnerAvatar: ImageView) {
        Glide.with(context)
            .load(uri)
            .placeholder(R.drawable.ic_photo_24dp)
            .circleCrop()
            .error(R.drawable.ic_photo_24dp)
            .signature(ObjectKey(File(uri).lastModified()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imgOwnerAvatar)
    }
}
