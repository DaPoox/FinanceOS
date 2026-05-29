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

@Database(
    entities = [
        EnvelopeEntity::class,
        TransactionEntity::class,
        MonthEntity::class,
        MonthAllocationEntity::class,
        AccountEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class FinanceDatabase : RoomDatabase() {
    abstract fun envelopeDao(): EnvelopeDao
    abstract fun transactionDao(): TransactionDao
    abstract fun monthDao(): MonthDao
    abstract fun monthAllocationDao(): MonthAllocationDao
    abstract fun accountDao(): AccountDao

    companion object {
        fun create(context: Context): FinanceDatabase {
            var instance: FinanceDatabase? = null
            val isDebug = context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
            instance = Room.databaseBuilder(context, FinanceDatabase::class.java, "finance_os.db")
                .fallbackToDestructiveMigration()
                .apply {
                    if (isDebug) {
                        addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                // Seeding disabled — run with fresh DB for real usage
                                // CoroutineScope(Dispatchers.IO).launch { DatabaseSeeder.seed(instance!!) }
                            }
                        })
                    }
                }
                .build()
            return instance
        }
    }
}
