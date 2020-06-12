package com.example.baseproject

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_spinner.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivitySpinner : AppCompatActivity(),AdapterView.OnItemSelectedListener {
    lateinit var Pbar: ProgressBar
    var stat = arrayOf("AN","AP","AR","AS","BR","CH","CT","DL","DN","GA","GJ","HP","HR","JH","JK","KA","KL","LA","LD","MH","ML","MN","MP","MZ","NL","OR","PB","PY","RJ","SK","TG","TN","TR","TT",
    "UN","UP","UT","WB","ALL")
    var spinner: Spinner? = null
    var textView_msg:TextView? = null
    lateinit var confirmdata:TextView
    lateinit var recoverdata:TextView
    lateinit var  testtedata:TextView
    lateinit var deathdata:TextView
    lateinit  var datadate:TextView
    lateinit var headcovid:LinearLayout
    lateinit var headcovidempty:LinearLayout
    lateinit var datacovid:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_spinner)
        Pbar = findViewById(R.id.Pbar)
        datacovid=findViewById(R.id.datacovid)
        headcovid=findViewById(R.id.headcovid)
        headcovidempty=findViewById(R.id.headcovidempty)
        confirmdata=findViewById(R.id.confirmdata)
        recoverdata=findViewById(R.id.recoverdata)
        testtedata=findViewById(R.id.testtedata)
        datadate=findViewById(R.id.datadate)
        deathdata=findViewById(R.id.deathdata)
        confirmdata=this.confirmdata
        recoverdata=this.recoverdata
        testtedata=this.testtedata
        deathdata=this.deathdata
        datadate=this.datadate
        spinner = this.spinner_sample
        spinner!!.setOnItemSelectedListener(this@MainActivitySpinner)
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, stat)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner!!.setAdapter(aa)
    }
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
       // textView_msg!!.text = "Selected : "+stat[position]
        if (this@MainActivitySpinner.isConnectedToNetwork()) {
            if(stat[position].equals("ALL")){
                datacovid.visibility=View.GONE
                headcovidempty.visibility=View.VISIBLE
                Toast.makeText(this@MainActivitySpinner, "TRY IT AGAIN", Toast.LENGTH_SHORT).show()

            }else{
                datacovid.visibility=View.VISIBLE
                headcovidempty.visibility=View.GONE
                var url="https://api.covid19india.org/v3/data.json"
              GetJsonWithOkHttpClient(url,stat[position],confirmdata,recoverdata,testtedata,deathdata,datadate,Pbar).execute()

       }
        }else{
            datacovid.visibility=View.GONE
            headcovidempty.visibility=View.VISIBLE
            Toast.makeText(this@MainActivitySpinner, "INTERNET OFF,TRY IT AGAIN IF INTERNET PRESENT", Toast.LENGTH_SHORT).show()
        }

    }
    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
    fun Context.isConnectedToNetwork(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting() ?: false
    }
   open class GetJsonWithOkHttpClient(textView: String,tvlocation: String,confirmdata:TextView,recoverdata:TextView,testtedata:TextView,deathdata:TextView,datadate:TextView,   Pbar:ProgressBar) : AsyncTask<Unit, Unit, String>() {
      val Pbar=Pbar
       override fun onPreExecute() {
           super.onPreExecute()
           Pbar.setVisibility(View.VISIBLE)
       }

        val mInnerTextView = textView
        val tvlocations=tvlocation
       val confirmdata=confirmdata
       val recoverdata=recoverdata
       val testtedata=testtedata
       val deathdata=deathdata
       val datadate=datadate
        override fun doInBackground(vararg params: Unit?): String? {
            android.util.Log.e("finalllurl", mInnerTextView)
            val networkClient = NetworkClient()
            val stream = BufferedInputStream(
                networkClient.get(mInnerTextView))
            return readStream(stream)
        }
       override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
           Pbar.setVisibility(View.GONE)
            Log.e("finalllresponse", result)
            val res=JSONObject(result)
            Log.e("finalllresponse", result)
            val stat=res.getJSONObject(tvlocations)
            Log.e("finalllresponseTN", stat.toString())
            val  tot=stat.getJSONObject("total")
            Log.e("TNconfirmes", stat.toString())
            val conf=tot.getString("confirmed")
            Log.e("dc",  conf.toString())
            val met=stat.getJSONObject("meta")
            val lastup=met.getString("last_updated")
            Log.e("lastupdated",lastup.toString())
           confirmdata.text=conf

           datadate.text="Last Updated:"+lastup
           try{
               val teste=tot.getString("tested")
               if (teste != "0" ||teste != null) {
                   testtedata.text=teste
               }
           }catch (e:JSONException){
               testtedata.text="0"
           }
           try{
               val reco=tot.getString("recovered")
               if (reco != "0" || reco != null) {
                   recoverdata.text = reco
               }
           }catch (e:JSONException){
               recoverdata.text="0"
           }
           try {
               val dea = tot.getString("deceased")
               if (dea != "0" || dea != null) {
                   deathdata.text = dea
               } else {
                   deathdata.text = "0"
               }
           }catch (e:JSONException){
               deathdata.text = "0"
           }




        }

        fun readStream(inputStream: BufferedInputStream): String {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { stringBuilder.append(it) }
            return stringBuilder.toString()
        }
    }



}

private fun JSONArray.getJSONArray(s: String) {

}
