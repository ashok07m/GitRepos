package com.gitrepos.android.ui.home

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.gitrepos.android.R
import com.gitrepos.android.internal.hideKeyboard
import com.gitrepos.android.internal.showToast
import com.gitrepos.android.ui.home.model.RepoItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_home.*
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

        groupAdapter.setOnItemClickListener(onItemClickListener)
        root.rvHome.apply {
            adapter = groupAdapter
        }

        Log.e("TAG", "onCreateView() :$this")
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initSearch()
        setupScrollListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("TAG", "onCreate() :$this")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("TAG", "onDestroyView() :$this")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TAG", "onDestroy() :$this")
    }

    /**
     * Observer to observe the repos list
     */
    private val successObserver: Observer<List<RepoItem>> = Observer {
        groupAdapter.addAll(it)
        Log.e("TAG", "successObserver : size: ${groupAdapter.itemCount}")
    }

    /**
     * Observer to observe the error message
     */
    private val errorObserver: Observer<Int> = Observer {
        showToast(it)
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


    /**
     * Init search for search listeners
     */
    private fun initSearch() {
        etSearchRepo.apply {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    updateRepoListFromInput()
                    true
                } else {
                    false
                }
            }
            setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    updateRepoListFromInput()
                    true
                } else {
                    false
                }
            }
        }
    }

    /**
     * Fires query to fetch repos based on input string
     */
    private fun updateRepoListFromInput() {
        etSearchRepo.text?.trim().let {
            it?.let {
                if (it.isNotEmpty()) {
                    rvHome.scrollToPosition(0)
                    homeViewModel.searchRepositories(it.toString())
                    groupAdapter.clear()
                    activity?.hideKeyboard(etSearchRepo)
                }
            }
        }
    }

    /**
     * Scroll listener for list
     */
    private fun setupScrollListener() {
        val layoutManager =
            rvHome.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
        rvHome.addOnScrollListener(object :
            androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(
                recyclerView: androidx.recyclerview.widget.RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                homeViewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount)
            }
        })
    }


}