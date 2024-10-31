import android.content.Context
import androidx.room.*
import androidx.room.RoomDatabase
import java.util.*


@Database(entities = [Location::class, Route::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun routeDao(): RouteDao
    abstract fun deleteDao(): DeleteDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

//        fun getDatabase(context: Context): AppDatabase{
//            return INSTANCE?: synchronized(this){
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase:: class.java,
//                    context.getString(R.string.note_database),
//
//                    ).build()
//                INSTANCE = instance
//                instance
//            }
//
//        }
    }
}

@Entity(
    tableName = "locations",
    indices = [Index(value = ["name"], unique = true)]
)
data class Location(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)


@Entity(tableName = "routes")
data class Route(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startId: Int,
    val endId: Int,
    val transitInfo: String,     // Serialized JSON for transit details
    val distance: Double,
    val duration: Int,
    val transportMode: String
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value:Long): Date {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long{
        return date.time
    }
}

// DAO Interface
@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDestination(location: Location): Long

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getLocationById(id: Int): Location?

}

@Dao
interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: Route): Long

    @Query("SELECT * FROM routes WHERE startId = :startId AND endId = :endId")
    suspend fun getRoutesBetweenDestinations(startId: Int, endId: Int): List<Route>
}

@Dao
interface DeleteDao{

}




