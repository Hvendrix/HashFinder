package com.example.hashfinder.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.hashfinder.AdditionService
import com.example.hashfinder.Hash
import com.example.hashfinder.IAdditionService
import com.example.hashfinder.R
import com.example.hashfinder.databinding.ActivityMainBinding
import com.example.hashfinder.viewModels.MainViewModel

class MainActivity : AppCompatActivity() {

    companion object {
        const val HASH_SET = "hash_set"
    }

    lateinit var mService: IAdditionService


    // Сделал через viewModel,
    // потому что все-таки удобный способ следить за сменой состояния,
    // в частности из-за поворота и смены языка
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModel.Factory(this.application)
        ).get(MainViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        binding.button.setOnClickListener { checkHash(binding) }
        binding.btnFIOActivity.setOnClickListener { toFIOActivity() }


        viewModel.setStatus(getString(R.string.wait_user))


        getExtraFromFio()

        viewModel.setStrHashListForDisplay()


        createService()

    }

    private fun getExtraFromFio() {
        if (intent.getParcelableExtra<Hash>(HASH_SET) != null) {
            val hashSetOfHashes = intent.getParcelableExtra<Hash>(HASH_SET) ?: Hash(hashSetOf())
            //не нравится восклицательные знаки ставить, поэтому тут две одинаковые проверки на null
            viewModel.setHashSetOfHashes(hashSetOfHashes)
        } else {
            viewModel.setHashSetOfHashes(Hash(hashSetOf()))
        }
    }

    private fun createService() {
        val mConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                mService = IAdditionService.Stub.asInterface(service)
            }

            override fun onServiceDisconnected(name: ComponentName) {}
        }
        val intentService = Intent(this, AdditionService::class.java)
        bindService(intentService, mConnection, BIND_AUTO_CREATE)
    }

    private fun checkHash(binding: ActivityMainBinding) {
        val strHash = binding.edtHash.text.toString()
        val matchResult = Regex("""[0-9a-f]{16}""").matches(strHash.toLowerCase())
        val hashSetFromViewModel = viewModel.getOrCreateHashList()
        if (matchResult) {
            try {
                //Вот здесь происходит запуск поиска в службе, которая находится в отдельном процессе anotherprocess
                when (mService.add(Hash(hashSetFromViewModel), strHash.toUpperCase())) {
                    // Результат обрабатывается снова в основном процессе
                    1 -> viewModel.setStatus(getString(R.string.ok))
                    2 -> {
                        viewModel.addToHashSet(strHash.toUpperCase())
                        viewModel.setStrHashListForDisplay()
                        viewModel.setStatus(getString(R.string.hash_added))
                    }
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

        } else {
            viewModel.setStatus(getString(R.string.incorrect_value))
        }


    }

    private fun toFIOActivity() {
        val intent = Intent(this, FioActivity::class.java)
        intent.putExtra(FioActivity.HASH_SET, Hash(viewModel.getOrCreateHashList()))
        startActivity(intent)
    }

}