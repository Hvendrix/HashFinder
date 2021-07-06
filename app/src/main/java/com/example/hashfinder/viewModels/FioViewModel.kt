package com.example.hashfinder.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.example.hashfinder.Hash

class FioViewModel(application: Application) : AndroidViewModel(application) {

    //Вообще я не планировал делать два viewModel такими похожими,
    // но как-то в итоге вышло, возможно чуть позже переделаю
    // через фабричный метод или вообще один класс оставлю
    private var _strHashList = MutableLiveData<String>()
    val strShow: LiveData<String>
        get() = _strHashList

    private var _hashSetOfHashes = MutableLiveData<HashSet<String>>()


    private var _strStatus = MutableLiveData<String>()
    val strStatus: LiveData<String>
        get() = _strStatus


    // Как-то нравится отдельно сеттеры писать, если скажите так не писать, то больше не буду
    fun setStrHashListForDisplay() {
        var strHash = ""
        getOrCreateHashList().forEach { strHash += it + "\n" }
        _strHashList.value = strHash
    }

    fun setHashSetOfHashes(hash: Hash) {
        _hashSetOfHashes.value = hash.hashSet
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
            if (modelClass.isAssignableFrom(FioViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FioViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}
