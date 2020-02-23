package com.gitrepos.android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gitrepos.android.R
import com.gitrepos.android.data.utils.AppUtils
import com.gitrepos.android.ui.home.model.RepoItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()
    private val groupAdapter by lazy { GroupAdapter<GroupieViewHolder>() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel.successLiveData.observe(viewLifecycleOwner, successObserver)
        homeViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)

        homeViewModel.fetchRepositories()

        groupAdapter.setOnItemClickListener(onItemClickListener)
        root.rvHome.apply {
            adapter = groupAdapter
        }
        return root
    }

    /**
     * Observer to observe the repos list
     */
    private val successObserver: Observer<List<RepoItem>> = Observer {
        groupAdapter.addAll(it)
    }

    /**
     * Observer to observe the error message
     */
    private val errorObserver: Observer<Int> = Observer {
        AppUtils.showToast(context!!, it)
    }

    /**
     * Item click listener
     */
    private val onItemClickListener = OnItemClickListener { item, _ ->
        if (item is RepoItem) {
            val repoInfo = item.repo
            val action = HomeFragmentDirections.actionNavigationHomeToDetailsFragment(repoInfo)
            findNavController().navigate(action)
        }
    }

}