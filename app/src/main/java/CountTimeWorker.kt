import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class CountTimeWorker(val context: Context, val params: WorkerParameters) : Worker(context,params) {

    val TAG = "CountTimeWorker"


    override fun doWork(): Result {

      /*Guarda la ultima fecha de Activacion*/
      val localDateTime = LocalDateTime.now()
      saveLastActivationDate("LastActivation",localDateTime.toString(),context)
        Log.d(TAG,"Fecha de activacion:${LocalDateTime.now()}")


      /*Guarda el conteo de las veces que ya se activo*/
      saveActivationTime("ActivationCount", getValue("ActivationCount") + 1 ,context)

      /*Establece el nuevo tiempo faltante*/
      if (getValue("TimeToCompletePeriod") != 0){
          saveValue("TimeToCompletePeriod",getValue("TimeToCompletePeriod") -2,context)
          scheduleWorkerToreview(context)
          Log.d(TAG,"Nuevo valor:${getValue("TimeToCompletePeriod")}")
          return Result.success()
      } else{
          Log.d(TAG,"Sin valor recuperado")
          return Result.failure()
      }


    }

    private fun saveValue(key: String, value: Int, context: Context) {
        val sharedPref = context.getSharedPreferences("workTest", Context.MODE_PRIVATE)
        try {
            val editor = sharedPref!!.edit()
            editor.putInt(key, value)
            editor.apply()
        } catch (ex: Exception) {

        }
    }

    fun getValue(key:String):Int {
        val sharedPref = context.getSharedPreferences("workTest", Context.MODE_PRIVATE)
        try {
            val defaultVlue = 0
            return sharedPref!!.getInt(key,defaultVlue)
        } catch (ex: Exception) {
           return 0
        }
    }

    private fun saveLastActivationDate(key: String, value: String, context: Context) {
        val sharedPref = context.getSharedPreferences("workTest", Context.MODE_PRIVATE)
        try {
            val editor = sharedPref!!.edit()
            editor.putString(key, value)
            editor.apply()
        } catch (ex: Exception) {

        }
    }

    private fun saveActivationTime(key: String, value: Int, context: Context){
        val sharedPref = context.getSharedPreferences("workTest", Context.MODE_PRIVATE)
        val editor = sharedPref!!.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    private fun scheduleWorkerToreview(context:Context){
        try {
            val rebootDeviceWorkRequest: WorkRequest = OneTimeWorkRequest.Builder(CountTimeWorker::class.java)
                .setInitialDelay(2, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueue(rebootDeviceWorkRequest)
        }catch(ex:Exception){

        }
    }
}