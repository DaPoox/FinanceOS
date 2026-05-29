package com.daprox.financeos.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.daprox.financeos.data.db.dao.AccountDao
import com.daprox.financeos.data.db.dao.EnvelopeDao
import com.daprox.financeos.data.db.dao.MonthAllocationDao
import com.daprox.financeos.data.db.dao.MonthDao
import com.daprox.financeos.data.db.dao.TransactionDao
import com.daprox.financeos.data.db.entity.AccountEntity
import com.daprox.financeos.data.db.entity.EnvelopeEntity
import com.daprox.financeos.data.db.entity.MonthAllocationEntity
import com.daprox.financeos.data.db.entity.MonthEntity
import com.daprox.financeos.data.db.entity.TransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room database for FinanceOS.
 *
 * Manages all data persistence for the app including envelopes, accounts, months,
 * transactions, and monthly allocations.
 *
 * Database version: 2 with destructive migration enabled for development.
 * In production, migrations should be properly versioned.
 */
@Database(
  entities = [
    EnvelopeEntity::class,
    TransactionEntity::class,
    MonthEntity::class,
    MonthAllocationEntity::class,
    AccountEntity::class,
  ],
  version = 2,
  exportSchema = false,
)
abstract class FinanceDatabase : RoomDatabase() {
  /**
   * Provides access to envelope queries and mutations.
   *
   * @return The [EnvelopeDao] instance
   */
  abstract fun envelopeDao(): EnvelopeDao

  /**
   * Provides access to transaction queries and mutations.
   *
   * @return The [TransactionDao] instance
   */
  abstract fun transactionDao(): TransactionDao

  /**
   * Provides access to month queries and mutations.
   *
   * @return The [MonthDao] instance
   */
  abstract fun monthDao(): MonthDao

  /**
   * Provides access to month allocation queries and mutations.
   *
   * @return The [MonthAllocationDao] instance
   */
  abstract fun monthAllocationDao(): MonthAllocationDao

  /**
   * Provides access to account queries and mutations.
   *
   * @return The [AccountDao] instance
   */
  abstract fun accountDao(): AccountDao

  companion object {
    /**
     * Creates and initializes the FinanceDatabase instance.
     *
     * Uses destructive migration for development (version upgrades drop and recreate tables).
     * Debug builds can optionally seed the database with sample data via [DatabaseSeeder].
     *
     * @param context Android context for database file location
     * @return A configured [FinanceDatabase] instance
     */
    fun create(context: Context): FinanceDatabase {
      var instance: FinanceDatabase? = null
      val isDebug =
        context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
      instance =
        Room.databaseBuilder(context, FinanceDatabase::class.java, "finance_os.db")
          .fallbackToDestructiveMigration()
          .apply {
            if (isDebug) {
              addCallback(
                object : Callback() {
                  override fun onCreate(db: SupportSQLiteDatabase) {
                    // Seeding disabled — run with fresh DB for real usage
                    // CoroutineScope(Dispatchers.IO).launch { DatabaseSeeder.seed(instance!!) }
                  }
                }
              )
            }
          }
          .build()
      return instance
    }
  }
}
