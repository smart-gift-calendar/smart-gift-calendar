package com.smartgiftcalendar.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smartgiftcalendar.app.data.local.dao.DiscountCodeDao
import com.smartgiftcalendar.app.data.local.dao.EmployeeDao
import com.smartgiftcalendar.app.data.local.dao.GiftDao
import com.smartgiftcalendar.app.data.local.dao.InviteLinkDao
import com.smartgiftcalendar.app.data.local.dao.OrderDao
import com.smartgiftcalendar.app.data.local.dao.OrganizationDao
import com.smartgiftcalendar.app.data.local.dao.PersonDao
import com.smartgiftcalendar.app.data.local.dao.ReminderDao
import com.smartgiftcalendar.app.data.local.dao.UserDao
import com.smartgiftcalendar.app.data.local.entity.DiscountCode
import com.smartgiftcalendar.app.data.local.entity.Employee
import com.smartgiftcalendar.app.data.local.entity.Gift
import com.smartgiftcalendar.app.data.local.entity.InviteLink
import com.smartgiftcalendar.app.data.local.entity.Order
import com.smartgiftcalendar.app.data.local.entity.Organization
import com.smartgiftcalendar.app.data.local.entity.Person
import com.smartgiftcalendar.app.data.local.entity.Reminder
import com.smartgiftcalendar.app.data.local.entity.User

/** The app's Room database. Created once per process through [getInstance]. */
@Database(
    entities = [
        User::class,
        Person::class,
        Reminder::class,
        Gift::class,
        Order::class,
        DiscountCode::class,
        Organization::class,
        Employee::class,
        InviteLink::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun personDao(): PersonDao
    abstract fun reminderDao(): ReminderDao
    abstract fun giftDao(): GiftDao
    abstract fun orderDao(): OrderDao
    abstract fun discountCodeDao(): DiscountCodeDao
    abstract fun organizationDao(): OrganizationDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun inviteLinkDao(): InviteLinkDao

    companion object {

        private const val DATABASE_NAME = "smart_gift_calendar.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /** Returns the shared database, building it on first use. */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .addCallback(SeedCallback())
                    .build()
                    .also { INSTANCE = it }
            }
        }

        /**
         * Inserts the demo data the first time the database file is created.
         *
         * Room runs [onCreate] inside the creation transaction, so the seeder writes rows directly
         * through [SupportSQLiteDatabase] rather than through the DAOs, which would deadlock while
         * waiting for that transaction to commit.
         */
        private class SeedCallback : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                DatabaseSeeder.seed(db)
            }
        }
    }
}
