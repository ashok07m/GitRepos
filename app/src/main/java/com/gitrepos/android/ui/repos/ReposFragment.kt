package com.gitrepos.android.ui.repos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gitrepos.android.R
import com.gitrepos.android.ui.CommonViewModel
import com.gitrepos.android.ui.home.model.RepoItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_repos.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReposFragment : Fragment() {

    private val reposViewModel: ReposViewModel by viewModel()
    private val commonViewModel: CommonViewModel by sharedViewModel()
    private val groupAdapter by lazy { GroupAdapter<GroupieViewHolder>() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_repos, container, false)

        reposViewModel.reposLiveData.observe(viewLifecycleOwner, reposObserver)
        reposViewModel.fetchRepositories(commonViewModel.getBioAuthManager())

        groupAdapter.setOnItemClickListener(onItemClickListener)
        root.rvRepos.apply {
            adapter = groupAdapter
        }
        return root
    }

    /**
     * Observer to observe the repos list
     */
    private val reposObserver: Observer<List<RepoItem>> = Observer {
        groupAdapter.clear()
        groupAdapter.addAll(it)
    }

    /**
     * Item click listener
     */
    private val onItemClickListener = OnItemClickListener { item, _ ->
        if (item is RepoItem) {
            val repoInfo = item.repo
            val action =
                ReposFragmentDirections.actionNavigationHomeToDetailsFragment(repoInfo)
            findNavController().navigate(action)
        }
    }
}