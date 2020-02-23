package com.gitrepos.android.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.signature.ObjectKey
import com.gitrepos.android.R
import kotlinx.android.synthetic.main.fragment_details.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class DetailsFragment : Fragment() {

    private val detailsViewModel: DetailsViewModel by viewModel()
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = args.RepoDetailsArgs
        detailsViewModel.langLiveData.observe(this, languageObserver)
        detailsViewModel.fetchLanguage(repo.owner, repo.title)

        if (!repo.avatarUrl.isNullOrEmpty()) {
            loadImage(repo.avatarUrl)
        }

        txtOwner.text = repo.owner
        txtDescValue.text = repo.description
        txtTitleValue.text = repo.title
    }


    /**
     * Observes repository language
     */
    private val languageObserver = Observer<String> {
        txtLangValue.text = it ?: "NA"
    }

    /**
     * Loads image in image view
     */
    private fun loadImage(uri: String) {
        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.ic_photo_24dp)
            .circleCrop()
            .error(R.drawable.ic_photo_24dp)
            .signature(ObjectKey(File(uri).lastModified()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(imgOwnerAvatar)
    }

    companion object {
        fun newInstance() = DetailsFragment()
    }
}

