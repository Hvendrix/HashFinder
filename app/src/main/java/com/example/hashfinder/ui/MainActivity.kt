package com.example.hashfinder.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.hashfinder.*
import com.example.hashfinder.databinding.ActivityMainBinding
import com.example.hashfinder.viewModels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val HASH_SET = "hash_set"
    }

    lateinit var mService: IAdditionService


    lateinit var edtText: EditText


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


        binding.btnCheck.setOnClickListener { checkHash(binding) }
        binding.btnFIOActivity.setOnClickListener { toFIOActivity() }
        edtText = binding.edtHash


        viewModel.getOrCreateHashList()
        viewModel.setStatus(getString(R.string.wait_user))
        getExtraFromFio()
        viewModel.setStrHashListForDisplay()


        createService()


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.generateOne -> {
                edtText.setText(generateHash())
                return true
            }
            R.id.generateList -> {
                generateMultipleValues()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun generateMultipleValues() {
        // Если потом вдруг нужно будет генерировать больше значений,
        // почему бы заранее не разгрузить UI Thread
        MainScope().launch {
            withContext(Dispatchers.Default) {
                val tmpHashSet = hashSetOf<String>()
                for (i in 0..50) {
                    tmpHashSet.add(generateHash())
                }
                withContext(Dispatchers.Main) {
                    viewModel.addToHashSet(tmpHashSet)
                    viewModel.setStrHashListForDisplay()
                }

            }

        }
    }

    private fun generateHash(): String {
        var num = Consts.rand.nextInt(16)
        var generatedHash = ""
        for (i in 0..15) {
            generatedHash += Consts.acceptableValues[num]
            num = Consts.rand.nextInt(16)
        }
        return generatedHash
    }

    private fun getExtraFromFio() {
        if (intent.getParcelableExtra<Hash>(HASH_SET) != null) {
            val hashSetOfHashes = intent.getParcelableExtra<Hash>(HASH_SET) ?: Hash(hashSetOf())
            //не нравится восклицательные знаки ставить, поэтому тут две одинаковые проверки на null
            viewModel.setHashSetOfHashes(hashSetOfHashes)
        }
    }


    // Вообще обычно реализую переход через LiveData переменную во viewModel
    private fun toFIOActivity() {
        val intent = Intent(this, FioActivity::class.java)
        intent.putExtra(FioActivity.HASH_SET, Hash(viewModel.getOrCreateHashList()))
        startActivity(intent)
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
        val matchResult = Regex("""[0-9a-f]{16}""").matches(strHash.toLowerCase(Locale.ROOT))
        val hashSetFromViewModel = viewModel.getOrCreateHashList()
        if (matchResult) {
            try {
                //Вот здесь происходит запуск поиска в службе, которая находится в отдельном процессе anotherprocess
                when (mService.add(Hash(hashSetFromViewModel), strHash.toUpperCase(Locale.ROOT))) {
                    // Результат обрабатывается снова в основном процессе
                    1 -> viewModel.setStatus(getString(R.string.ok))
                    2 -> {
                        viewModel.addToHashSet(strHash.toUpperCase(Locale.ROOT))
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



}