package com.example.hashfinder.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.example.hashfinder.Hash

class MainViewModel(application: Application) : AndroidViewModel(application) {


    private var _strHashList = MutableLiveData<String>()
    val strHashList: LiveData<String>
        get() = _strHashList


    private var _hashSetOfHashes = MutableLiveData<HashSet<String>>()
    // геттер выписал отдельно getOrCreateHashList

    private var _strStatus = MutableLiveData<String>()
    val strStatus: LiveData<String>
        get() = _strStatus

    init {
        _strHashList.value = ""
    }


    fun addToHashSet(strHash: String) {
        _hashSetOfHashes.value?.add(strHash)
    }


    fun addToHashSet(hashSet: HashSet<String>) {
        hashSet.forEach { _hashSetOfHashes.value?.add(it) }
    }


    fun setHashSetOfHashes(hash: Hash) {
        _hashSetOfHashes.value = hash.hashSet
    }


    fun setStrHashListForDisplay() {
        var strHash = ""
        _hashSetOfHashes.value?.forEach { strHash += it + "\n" }
        _strHashList.value = strHash
    }


    fun setStatus(strStatus: String) {
        _strStatus.value = strStatus
    }


    fun getOrCreateHashList(): HashSet<String> {
        return if (_hashSetOfHashes.value != null) {
            _hashSetOfHashes.value!!
        } else {
            _hashSetOfHashes.value = hashSetOf()
            _hashSetOfHashes.value!!
        }
    }


    // Можно вынести класс в отдельный файл, но лично мне так удобнее
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}