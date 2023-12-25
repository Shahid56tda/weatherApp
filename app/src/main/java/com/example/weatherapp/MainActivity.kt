package com.example.weatherapp

import android.content.ContentValues.TAG
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.locks.Condition

//6ac5ac99c01c7ec2b1e45ccf9aacd3c1

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        feathWeatherData("Bhakkar")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    feathWeatherData(query)
                }
                return  true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun feathWeatherData(cityName:String) {
        val reteofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response=reteofit.getWeaterData(cityName,"6ac5ac99c01c7ec2b1e45ccf9aacd3c1","metric")
        response.enqueue(object :Callback<weatherapp>{
            override fun onResponse(call: Call<weatherapp>, response: Response<weatherapp>) {
                val responsebody=response.body()
                if(response.isSuccessful && responsebody!=null){
                    val temprature=responsebody.main.temp.toString()
                   // Log.d("TAG","onResponce:$temprature")
                    binding.temp.text="$temprature ℃"
                    val humdity=responsebody.main.humidity
                    val windspeed=responsebody.wind.speed
                    val sunries=responsebody.sys.sunrise.toLong()
                    val sunset=responsebody.sys.sunset.toLong()
                    val sealevel=responsebody.main.pressure
                    val condition=responsebody.weather.firstOrNull()?.main?:"unknow"
                    val max=responsebody.main.temp_max
                    val min=responsebody.main.temp_min

                    binding.weather.text=condition
                    binding.maxterm.text="Max Tem $max"
                    binding.minterm.text="Max Tem $min"
                    binding.humadity.text="$humdity %"
                    binding.wind.text="$windspeed m/s"
                    binding.sunries.text="${time(sunries)}"
                    binding.sunset.text="${time(sunset)}"
                    binding.sea.text="$sealevel hpa"
                    binding.condtion.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityname.text="$cityName"
                    changeImageAccordingtoweather(condition)

                }
            }

            override fun onFailure(call: Call<weatherapp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeImageAccordingtoweather(condition: String) {
        when (condition){
            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "Light Rain","Drizzle","Moderate Rain" ,"Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Partly Clouds","Clouds","Overcst" ,"Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            " Light Snow","Moderate Snow","Heavy Snow" ,"Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }else->{
            binding.root.setBackgroundResource(R.drawable.sunny_background)
            binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()

    }

    private fun date(): CharSequence? {
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun time(timestemp:Long): CharSequence? {
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestemp*1000)))

    }

    fun dayName(timestemp:Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}