package com.smartgiftcalendar.app.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.time.LocalDate

/**
 * Fills a newly created database with the demo accounts, gift catalogue and discount codes.
 *
 * Seeding writes plain rows through [SupportSQLiteDatabase] so it can run synchronously inside
 * `RoomDatabase.Callback.onCreate`, which Room already executes inside the database-creation
 * transaction. Going back through the DAOs there would wait for that outer transaction and block.
 *
 * Seeding is idempotent: it does nothing when accounts already exist, so a second call never
 * duplicates rows.
 */
object DatabaseSeeder {

    /** Inserts the demo data into [db] unless accounts are already present. */
    fun seed(db: SupportSQLiteDatabase) {
        if (hasUsers(db)) return

        val now = System.currentTimeMillis()

        val adminId = insertUser(db, "admin", "admin@smartgift.app", "admin123", "admin", now)
        val userId = insertUser(db, "user", "user@smartgift.app", "1234", "user", now)
        insertUser(db, "company", "company@smartgift.app", "1234", "company", now)

        sampleGifts().forEach { insertGift(db, it) }

        insertDiscountCode(
            db = db,
            code = "FRIEND10",
            percent = 10,
            expiresAt = LocalDate.now().plusMonths(6).toString(),
            maxUses = 100,
            ownerId = userId
        )
        insertDiscountCode(
            db = db,
            code = "WELCOME5",
            percent = 5,
            expiresAt = LocalDate.now().plusMonths(3).toString(),
            maxUses = 50,
            ownerId = null
        )
        insertDiscountCode(
            db = db,
            code = "SPRING15",
            percent = 15,
            expiresAt = LocalDate.now().plusMonths(1).toString(),
            maxUses = 25,
            ownerId = adminId
        )
    }

    private fun hasUsers(db: SupportSQLiteDatabase): Boolean =
        db.query("SELECT COUNT(*) FROM users").use { cursor ->
            cursor.moveToFirst() && cursor.getInt(0) > 0
        }

    /** Inserts one account and returns its new row id. */
    private fun insertUser(
        db: SupportSQLiteDatabase,
        name: String,
        email: String,
        password: String,
        role: String,
        createdAt: Long
    ): Long {
        val hashed = PasswordHasher.hash(password)
        val values = ContentValues().apply {
            put("name", name)
            put("email", email)
            put("password_hash", hashed.hash)
            put("salt", hashed.salt)
            put("role", role)
            put("created_at", createdAt)
        }
        return db.insert("users", SQLiteDatabase.CONFLICT_ABORT, values)
    }

    private fun insertGift(db: SupportSQLiteDatabase, gift: DemoGift) {
        val values = ContentValues().apply {
            put("name", gift.name)
            put("description", gift.description)
            put("price", gift.price)
            put("image_url", gift.imageUrl)
            put("category", gift.category)
            put("active", 1)
        }
        db.insert("gifts", SQLiteDatabase.CONFLICT_ABORT, values)
    }

    private fun insertDiscountCode(
        db: SupportSQLiteDatabase,
        code: String,
        percent: Int,
        expiresAt: String,
        maxUses: Int,
        ownerId: Long?
    ) {
        val values = ContentValues().apply {
            put("code", code)
            put("percent", percent)
            put("expires_at", expiresAt)
            put("max_uses", maxUses)
            put("used_count", 0)
            if (ownerId != null) put("owner_id", ownerId) else putNull("owner_id")
        }
        db.insert("discount_codes", SQLiteDatabase.CONFLICT_ABORT, values)
    }

    /** A gift row for the seeded catalogue; [price] is in toman. */
    private data class DemoGift(
        val name: String,
        val description: String,
        val price: Long,
        val imageUrl: String,
        val category: String
    )

    /** Six gifts for the shop catalogue, priced in toman. */
    private fun sampleGifts(): List<DemoGift> = listOf(
        DemoGift(
            name = "Flower Bouquet",
            description = "Fresh seasonal flowers, hand tied",
            price = 3_500_000,
            imageUrl = "",
            category = "flowers"
        ),
        DemoGift(
            name = "Chocolate Box",
            description = "Premium assorted chocolates",
            price = 2_400_000,
            imageUrl = "",
            category = "sweets"
        ),
        DemoGift(
            name = "Smart Watch",
            description = "Fitness tracking and notifications",
            price = 12_000_000,
            imageUrl = "",
            category = "electronics"
        ),
        DemoGift(
            name = "Book Gift Set",
            description = "A bundle of bestselling novels",
            price = 4_500_000,
            imageUrl = "",
            category = "books"
        ),
        DemoGift(
            name = "Perfume Gift Set",
            description = "Eau de parfum with a matching body lotion",
            price = 6_800_000,
            imageUrl = "",
            category = "beauty"
        ),
        DemoGift(
            name = "Gift Card",
            description = "Let them choose their own gift",
            price = 5_000_000,
            imageUrl = "",
            category = "voucher"
        )
    )
}
