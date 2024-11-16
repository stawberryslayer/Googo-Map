package com.cs407.map_application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.lifecycleScope;
import androidx.room.Room;
import com.cs407.map_application.data.AppDatabase
import com.cs407.map_application.data.Location
import com.cs407.map_application.data.Route
import com.cs407.map_application.data.RouteDao
import com.cs407.map_application.model.DirectionsResponse
import kotlinx.coroutines.CoroutineScope
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

public class MapFragment : Fragment(){
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

        return inflater.inflate(R.layout.activity_map, container, false)

    }
    // Mapping function to convert DirectionsResponse to RouteEntity
    private fun mapToRoute(response: DirectionsResponse, startId: Int, endId: Int): Route {
        val route = response.routes.firstOrNull()
        val leg = route?.legs?.firstOrNull()

        return Route(
            startId = startId,
            endId = endId,
            distance = leg?.distance?.value?.toDouble() ?: 0.0,
            duration = leg?.duration?.value ?: 0,
            transitInfo = "",  // Optionally, you can serialize detailed transit info here
            transportMode = "driving"
        )
    }

    fun getDirections(
        origin: String,
        destination: String,
        coroutineScope: CoroutineScope,
        onResult: (Boolean, String) -> Unit
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                // Fetch directions from Google Maps API
                val response = googleMapsApiService.getDirections(origin, destination, "driving", apiKey)
                if (response.isSuccessful && response.body() != null) {
                    val directionsResponse = response.body()!!

                    // Assuming you have already inserted the locations into your database:
                    val startLocation = Location(name = origin, latitude = 0.0, longitude = 0.0)  // Replace with real values
                    val endLocation = Location(name = destination, latitude = 0.0, longitude = 0.0)  // Replace with real values
                    //val startId = routeDao.insertDestination(startLocation).toInt()
                    //val endId = routeDao.insertDestination(endLocation).toInt()

                    // Map response to Route entity
                    //val route = mapToRoute(directionsResponse, startId, endId)

                    // Insert the Route entity into Room database
                    //routeDao.insertRoute(route)

                    withContext(Dispatchers.Main) {
                        onResult(true, "Route saved successfully.")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onResult(false, "Failed to get directions.")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(false, "An error occurred: ${e.message}")
                }
            }
        }
    }
}
