package com.example.hashfinder.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.hashfinder.Hash
import com.example.hashfinder.R
import com.example.hashfinder.databinding.ActivityFioBinding
import com.example.hashfinder.viewModels.FioViewModel
import kotlinx.coroutines.*
import java.io.*

class FioActivity : AppCompatActivity(),
    DialogExport.DialogExportListener,
    DialogImport.DialogImportListener {

    //Для передачи списка хешей через активити
    companion object {
        const val HASH_SET = "hash_set"
    }

    private val viewModel by lazy {
        ViewModelProvider(
            this,
            FioViewModel.Factory(this.application)
        ).get(FioViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fio)
        val binding: ActivityFioBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_fio
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        getExtraFromMain()


        viewModel.setStatus(getString(R.string.wait_user))
        viewModel.setStrHashListForDisplay()

        //Решил сделать привязку здесь
        binding.btnToMain.setOnClickListener { toMain() }
        binding.btnExport.setOnClickListener { showDialogExport() }
        binding.btnImport.setOnClickListener { showDialogImport() }


    }

    private fun getExtraFromMain() {
        val hashSetOfHashes = intent.getParcelableExtra(HASH_SET) ?: Hash(hashSetOf())
        viewModel.setHashSetOfHashes(hashSetOfHashes)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        toMain()
    }

    private fun toMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.HASH_SET, Hash(viewModel.getOrCreateHashList()))
        startActivity(intent)
    }


    private fun showDialogExport() {
        val dialogExport = DialogExport()
        dialogExport.show(supportFragmentManager, "test")
    }


    private fun showDialogImport() {
        val dialogImport = DialogImport()
        dialogImport.show(supportFragmentManager, "test")
    }


    // Далее обработчики для кнопок диалога реализованы здесь через интерфейс,
    // чтобы можно было связать с activity
    override fun onDialogPositiveClick(dialog: DialogExport, strPath: String) {
        MainScope().launch {
            withContext(Dispatchers.IO) {
                write(strPath)
            }
        }
        dialog.dismiss()
    }


    override fun onDialogPositiveClick(dialog: DialogImport, strPath: String) {
        MainScope().launch {
            withContext(Dispatchers.IO) {
                read(strPath)
            }
        }
        dialog.dismiss()
    }


    @Synchronized
    private suspend fun read(strPath: String) {
        try {
            val fileInputStream = openFileInput("$strPath.txt")
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuffer = StringBuffer()
            var lines: String?
            val hashFromImport = hashSetOf<String>()
            while ((bufferedReader.readLine().also { lines = it }) != null) {
                stringBuffer.append(lines).append("\n")
                lines?.let { hashFromImport.add(it) }
            }

            withContext(Dispatchers.Main) {
                viewModel.setHashSetOfHashes(Hash(hashFromImport))
                viewModel.setStrHashListForDisplay()
                viewModel.setStatus(getString(R.string.import_success))
            }

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) { viewModel.setStatus(getString(R.string.file_not_found)) }

        } catch (e: IOException) {
            withContext(Dispatchers.Main) { viewModel.setStatus(getString(R.string.fio_failed)) }

        }

    }


    @Synchronized
    private suspend fun write(strPath: String) {
        var strHash = ""
        viewModel.getOrCreateHashList().forEach { strHash += it + "\n" }
        try {
            val fileOutputStream = openFileOutput("$strPath.txt", MODE_PRIVATE)
            fileOutputStream.write(strHash.toByteArray())
            fileOutputStream.close()
            withContext(Dispatchers.Main) { viewModel.setStatus(getString(R.string.export_success)) }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            withContext(Dispatchers.IO) { viewModel.setStatus(getString(R.string.file_not_found)) }
        } catch (e: IOException) {
            e.printStackTrace()
            withContext(Dispatchers.Main) { viewModel.setStatus(getString(R.string.fio_failed)) }
        }
    }
}