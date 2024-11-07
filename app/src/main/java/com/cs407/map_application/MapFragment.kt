package com.cs407.map_application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.lifecycleScope;
import androidx.room.Room;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.launch;
import kotlinx.coroutines.withContext;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface GoogleMapsApiService {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
            @Query("origin") origin: String,
            @Query("destination") destination: String,
            @Query("mode") mode: String,
            @Query("key") apiKey: String
    ): Response<DirectionsResponse>
}
public class MapFragment{
    private lateinit var googleMapsApiService: GoogleMapsApiService;
    private lateinit var routeDao: RouteDao
    private val apiKey = "YOUR_GOOGLE_MAPS_API_KEY"

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        googleMapsApiService = retrofit.create(GoogleMapsApiService::class.java)

        // Initialize Room database
        val db = Room.databaseBuilder(
                requireContext(),
                AppDatabase::class.java, "app_database"
        ).build()
        routeDao = db.routeDao()

        return inflater.inflate(R.layout.fragment_map, container, false)

}
