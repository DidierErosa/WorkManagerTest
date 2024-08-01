package com.example.workmanegertest

import CountTimeWorker
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.workmanegertest.ui.theme.WorkManegerTestTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkManegerTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Schedule(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        this.applicationContext
                    )
                }
            }
        }
    }


}

    fun startWorkerCheckDeviceAdminService(context:Context){
    try {
        val rebootDeviceWorkRequest: WorkRequest = OneTimeWorkRequest.Builder(CountTimeWorker::class.java)
            .setInitialDelay(2, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueue(rebootDeviceWorkRequest)
    }catch(ex:Exception){

        }
    }

    fun saveValue(key: String, value: Int, context: Context) {
        val sharedPref = context.getSharedPreferences("workTest", Context.MODE_PRIVATE)
        try {
            val editor = sharedPref!!.edit()
            editor.putInt(key, value)
            editor.apply()
        } catch (ex: Exception) {

        }
    }

fun getValue(context: Context,key: String):Int {
    val sharedPref = context.getSharedPreferences("workTest", Context.MODE_PRIVATE)
    try {
        val defaultVlue = 0
        return sharedPref!!.getInt(key,defaultVlue)
    } catch (ex: Exception) {
        return 0
    }
}

fun getStringValue(context: Context,key: String):String {
    val sharedPref = context.getSharedPreferences("workTest", Context.MODE_PRIVATE)
    try {
        val defaultVlue = ""
        return sharedPref!!.getString(key,defaultVlue) ?: ""
    } catch (ex: Exception) {
        return ""
    }
}

@Composable
fun Schedule(name: String, modifier: Modifier = Modifier, context: Context, ) {
    var inputValue by rememberSaveable { mutableStateOf("") }
    val value  by rememberSaveable { mutableIntStateOf(getValue(context,"TimeToCompletePeriod")) }
    val dateValue  by rememberSaveable { mutableStateOf(getStringValue(context,"LastActivation")) }
    val countValue  by rememberSaveable { mutableIntStateOf(getValue(context,"ActivationCount")) }
    Column {
        Text(
            text = "Schedule a time!",
            modifier = modifier
        )

        OutlinedTextField(value = inputValue, onValueChange = {inputValue = it }  )
        Button(onClick = {
            saveValue("TimeToCompletePeriod",inputValue.toInt(),context)
            startWorkerCheckDeviceAdminService(context)
        }) {
            Text(text = "Schedule")
        }
        Text(
            text = "Tiempo faltante: $value",
            modifier = modifier
        )
        Text(
            text = "Ultima fecha de Activacion: $dateValue",
            modifier = modifier
        )
        Text(
            text = "Conteo de activaciones: $countValue",
            modifier = modifier
        )
    }

}
