package com.gitrepos.android.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.gitrepos.android.R
import com.gitrepos.android.data.database.entity.ReposEntity
import com.gitrepos.android.internal.showToast
import com.gitrepos.android.ui.CommonViewModel
import com.gitrepos.android.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_details.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsFragment : Fragment() {

    private val detailsViewModel: DetailsViewModel by viewModel()
    private val commonViewModel: CommonViewModel by sharedViewModel()
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (!commonViewModel.isBioAuthenticated()) {
            activity?.run {
                (this as MainActivity).doBioAUth()
            }
        }

        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val repo = args.RepoDetailsArgs
        val isShowSavedDetails = args.showSavedDetails

        txtNameValue.text = repo.name
        txtFullName.text = repo.fullName
        txtLangValue.text = repo.language ?: commonViewModel.emptyData
        txtDescValue.text = repo.description ?: commonViewModel.emptyData
        txtStarsValue.text = repo.starsCount
        txtForksValue.text = repo.forksCount
        txtHomePageValue.text = repo.homePage ?: commonViewModel.emptyData

        btnSave.setOnClickListener {
            val reposEntity = ReposEntity(
                name = repo.name,
                fullName = repo.fullName,
                language = repo.language,
                description = repo.description,
                starsCount = repo.starsCount,
                forksCount = repo.forksCount,
                homepage = repo.homePage.orEmpty()
            )

            detailsViewModel.saveRepoDetails(reposEntity, commonViewModel.getBioAuthManager())
                .observe(viewLifecycleOwner, saveResultObserver)
        }
    }

    /**
     * Observes repository save result
     */
    private val saveResultObserver = Observer<Boolean> {
        val message = if (it) {
            R.string.msg_repo_saved
        } else {
            R.string.msg_unable_to_save_repo
        }
        showToast(message)
    }
}

