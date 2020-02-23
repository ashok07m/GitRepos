package com.gitrepos.android.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gitrepos.android.data.repositories.GitRepository
import kotlinx.coroutines.launch

class DetailsViewModel(private val gitRepository: GitRepository) : ViewModel() {

    private var langMutableLiveData = MutableLiveData<String>()
    val langLiveData: LiveData<String> = langMutableLiveData

    fun fetchLanguage(owner: String, repo: String) = viewModelScope.launch {
        val language = gitRepository.fetchGitLanguage(owner, repo)
        langMutableLiveData.value = language
    }

}
