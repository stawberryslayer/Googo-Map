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

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
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
    fun insertDestination(location: Location): Long


    @Query("SELECT * FROM locations WHERE id = :id")
    fun getLocationById(id: Int): Location?

}

@Dao
interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRoute(route: Route): Long

    @Query("SELECT * FROM routes WHERE startId = :startId AND endId = :endId")
    fun getRoutesBetweenDestinations(startId: Int, endId: Int): List<Route>


}

@Dao
interface DeleteDao{
    @Query("DELETE FROM locations WHERE id = :id")
     fun deleteLocationById(id: Int)

    @Query("DELETE FROM routes WHERE id = :id")
     fun deleteRouteById(id: Int)
}




