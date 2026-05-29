package com.daprox.financeos.data.db

import com.daprox.financeos.data.db.entity.AccountEntity
import com.daprox.financeos.data.db.entity.EnvelopeEntity
import com.daprox.financeos.data.db.entity.MonthAllocationEntity
import com.daprox.financeos.data.db.entity.MonthEntity
import com.daprox.financeos.data.db.entity.TransactionEntity
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * Database seeder for development and testing.
 *
 * Provides sample data for all entities: envelopes, accounts, months, allocations, and transactions.
 * This seeder is only used in debug builds and must be explicitly enabled in [FinanceDatabase].
 *
 * Seeding is guarded by the FLAG_DEBUGGABLE check in [FinanceDatabase.create], ensuring
 * production builds never attempt to seed the database.
 */
object DatabaseSeeder {

  /**
   * Seeds the entire database with sample data.
   *
   * Populates all tables in a single transaction:
   * - Envelopes (budget categories)
   * - Accounts (bank/savings accounts)
   * - Months (monthly budget periods)
   * - Allocations (envelope allocations per month)
   * - Transactions (spending records)
   *
   * @param db The database instance to seed
   */
  suspend fun seed(db: FinanceDatabase) {
    seedEnvelopes(db)
    seedAccounts(db)
    seedMonths(db)
    seedAllocations(db)
    seedTransactions(db)
  }

  /**
   * Converts year, month, day to epoch milliseconds in UTC.
   *
   * Used to create date values for transaction timestamps.
   *
   * @param year The year
   * @param month The month (1-12)
   * @param day The day (1-31)
   * @return Epoch milliseconds in UTC
   */
  private fun epochMillis(year: Int, month: Int, day: Int): Long =
    LocalDate.of(year, month, day).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

  /**
   * Seeds the envelopes table with sample budget categories.
   *
   * Creates 14 sample envelopes covering fixed expenses, variable spending, goals, and investments.
   *
   * @param db The database instance
   */
  private suspend fun seedEnvelopes(db: FinanceDatabase) {
    val dao = db.envelopeDao()
    listOf(
      EnvelopeEntity(
        id = "rent",
        name = "Loyer",
        type = "FIXED",
        iconKey = "house",
        colorHex = "#8ba4be"
      ),
      EnvelopeEntity(
        id = "subs",
        name = "Abonnements",
        type = "FIXED",
        iconKey = "chart_bar",
        colorHex = "#8ba4be"
      ),
      EnvelopeEntity(
        id = "utils",
        name = "Énergie & Box",
        type = "FIXED",
        iconKey = "wallet",
        colorHex = "#8ba4be"
      ),
      EnvelopeEntity(
        id = "groceries",
        name = "Courses",
        type = "VARIABLE",
        iconKey = "shopping_cart",
        colorHex = "#e8eef5"
      ),
      EnvelopeEntity(
        id = "restos",
        name = "Restos & cafés",
        type = "VARIABLE",
        iconKey = "utensils",
        colorHex = "#f87171"
      ),
      EnvelopeEntity(
        id = "transport",
        name = "Transport",
        type = "VARIABLE",
        iconKey = "car",
        colorHex = "#e8eef5"
      ),
      EnvelopeEntity(
        id = "shopping",
        name = "Shopping",
        type = "VARIABLE",
        iconKey = "shopping_cart",
        colorHex = "#fb923c"
      ),
      EnvelopeEntity(
        id = "ams",
        name = "Amsterdam",
        type = "MONTHLY",
        iconKey = "plane",
        colorHex = "#e8eef5"
      ),
      EnvelopeEntity(
        id = "vacances",
        name = "Vacances",
        type = "PERMANENT",
        iconKey = "plane",
        colorHex = "#e8eef5"
      ),
      EnvelopeEntity(
        id = "cadeaux",
        name = "Cadeaux femme",
        type = "PERMANENT",
        iconKey = "gift",
        colorHex = "#e8eef5"
      ),
      EnvelopeEntity(
        id = "livretA",
        name = "Livret A",
        type = "SAVINGS",
        iconKey = "piggy_bank",
        colorHex = "#7eb8f7"
      ),
      EnvelopeEntity(
        id = "ldds",
        name = "LDDS",
        type = "SAVINGS",
        iconKey = "piggy_bank",
        colorHex = "#7eb8f7"
      ),
      EnvelopeEntity(
        id = "pea",
        name = "PEA",
        type = "INVESTMENT",
        iconKey = "trending_up",
        colorHex = "#a78bfa"
      ),
      EnvelopeEntity(
        id = "cto",
        name = "CTO",
        type = "INVESTMENT",
        iconKey = "trending_up",
        colorHex = "#a78bfa"
      ),
    )
      .forEach { dao.insert(it) }
  }

  /**
   * Seeds the accounts table with sample bank and savings accounts.
   *
   * Creates 5 sample accounts: checking, 2 savings accounts with legal caps, and 2 investment accounts.
   *
   * @param db The database instance
   */
  private suspend fun seedAccounts(db: FinanceDatabase) {
    val dao = db.accountDao()
    listOf(
      AccountEntity(
        id = "acc_boursorama",
        name = "Boursorama",
        type = "Compte courant",
        balance = 4820.0,
        colorHex = "#e8eef5"
      ),
      AccountEntity(
        id = "acc_livretA",
        name = "Livret A",
        type = "Épargne",
        balance = 18400.0,
        cap = 22950.0,
        colorHex = "#7eb8f7"
      ),
      AccountEntity(
        id = "acc_ldds",
        name = "LDDS",
        type = "Épargne",
        balance = 8200.0,
        cap = 12000.0,
        colorHex = "#7eb8f7"
      ),
      AccountEntity(
        id = "acc_pea",
        name = "PEA",
        type = "Investissement",
        balance = 22680.0,
        colorHex = "#a78bfa"
      ),
      AccountEntity(
        id = "acc_cto",
        name = "CTO Trade Rep.",
        type = "Investissement",
        balance = 6420.0,
        colorHex = "#a78bfa"
      ),
    )
      .forEach { dao.insert(it) }
  }

  /**
   * Seeds the months table with 8 months of historical budget data.
   *
   * Creates month records from October 2025 to May 2026 with various financial statuses.
   *
   * @param db The database instance
   */
  private suspend fun seedMonths(db: FinanceDatabase) {
    val dao = db.monthDao()
    listOf(
      MonthEntity(
        id = "2026-05",
        label = "Mai 2026",
        income = 4200.0,
        status = "best",
        isAllocated = true,
        spent = 2960.0,
        contrib = 1840.0
      ),
      MonthEntity(
        id = "2026-04",
        label = "Avr 2026",
        income = 4200.0,
        status = "good",
        isAllocated = true,
        spent = 3210.0,
        contrib = 1410.0
      ),
      MonthEntity(
        id = "2026-03",
        label = "Mar 2026",
        income = 3950.0,
        status = "mid",
        isAllocated = true,
        spent = 3380.0,
        contrib = 980.0
      ),
      MonthEntity(
        id = "2026-02",
        label = "Fév 2026",
        income = 4400.0,
        status = "good",
        isAllocated = true,
        spent = 3210.0,
        contrib = 1620.0
      ),
      MonthEntity(
        id = "2026-01",
        label = "Jan 2026",
        income = 4200.0,
        status = "good",
        isAllocated = true,
        spent = 3340.0,
        contrib = 1280.0
      ),
      MonthEntity(
        id = "2025-12",
        label = "Déc 2025",
        income = 4800.0,
        status = "hard",
        isAllocated = true,
        spent = 4520.0,
        contrib = 520.0
      ),
      MonthEntity(
        id = "2025-11",
        label = "Nov 2025",
        income = 4200.0,
        status = "good",
        isAllocated = true,
        spent = 3120.0,
        contrib = 1390.0
      ),
      MonthEntity(
        id = "2025-10",
        label = "Oct 2025",
        income = 4200.0,
        status = "good",
        isAllocated = true,
        spent = 3290.0,
        contrib = 1180.0
      ),
    )
      .forEach { dao.insert(it) }
  }

  /**
   * Seeds the month_allocations table with May 2026 budget allocations.
   *
   * Creates allocations for all 14 envelopes for May 2026. PERMANENT envelopes track
   * accumulated values across months.
   *
   * @param db The database instance
   */
  private suspend fun seedAllocations(db: FinanceDatabase) {
    val dao = db.monthAllocationDao()
    dao.insertAll(
      listOf(
        MonthAllocationEntity(monthId = "2026-05", envelopeId = "rent", allocated = 1180.0),
        MonthAllocationEntity(monthId = "2026-05", envelopeId = "subs", allocated = 84.0),
        MonthAllocationEntity(monthId = "2026-05", envelopeId = "utils", allocated = 96.0),
        MonthAllocationEntity(
          monthId = "2026-05",
          envelopeId = "groceries",
          allocated = 420.0
        ),
        MonthAllocationEntity(monthId = "2026-05", envelopeId = "restos", allocated = 220.0),
        MonthAllocationEntity(monthId = "2026-05", envelopeId = "transport", allocated = 90.0),
        MonthAllocationEntity(monthId = "2026-05", envelopeId = "shopping", allocated = 150.0),
        MonthAllocationEntity(monthId = "2026-05", envelopeId = "ams", allocated = 480.0),
        MonthAllocationEntity(
          monthId = "2026-05",
          envelopeId = "vacances",
          allocated = 150.0,
          accumulated = 1840.0
        ),
        MonthAllocationEntity(
          monthId = "2026-05",
          envelopeId = "cadeaux",
          allocated = 80.0,
          accumulated = 420.0
        ),
        MonthAllocationEntity(monthId = "2026-05", envelopeId = "livretA", allocated = 400.0),
        MonthAllocationEntity(monthId = "2026-05", envelopeId = "ldds", allocated = 200.0),
        MonthAllocationEntity(monthId = "2026-05", envelopeId = "pea", allocated = 500.0),
        MonthAllocationEntity(monthId = "2026-05", envelopeId = "cto", allocated = 150.0),
      )
    )
  }

  /**
   * Seeds the transactions table with sample spending records for May 2026.
   *
   * Creates 15 transactions across multiple envelopes (restaurants, groceries, Amsterdam trip).
   *
   * @param db The database instance
   */
  private suspend fun seedTransactions(db: FinanceDatabase) {
    val dao = db.transactionDao()
    listOf(
      TransactionEntity(
        id = "tx_restos_1",
        envelopeId = "restos",
        monthId = "2026-05",
        amount = 68.0,
        note = "Le Servan",
        date = epochMillis(2026, 5, 14)
      ),
      TransactionEntity(
        id = "tx_restos_2",
        envelopeId = "restos",
        monthId = "2026-05",
        amount = 14.0,
        note = "Café Méricourt",
        date = epochMillis(2026, 5, 12)
      ),
      TransactionEntity(
        id = "tx_restos_3",
        envelopeId = "restos",
        monthId = "2026-05",
        amount = 92.0,
        note = "Septime",
        date = epochMillis(2026, 5, 11)
      ),
      TransactionEntity(
        id = "tx_restos_4",
        envelopeId = "restos",
        monthId = "2026-05",
        amount = 41.0,
        note = "Le Mary Celeste",
        date = epochMillis(2026, 5, 8)
      ),
      TransactionEntity(
        id = "tx_restos_5",
        envelopeId = "restos",
        monthId = "2026-05",
        amount = 31.0,
        note = "Yard",
        date = epochMillis(2026, 5, 5)
      ),
      TransactionEntity(
        id = "tx_groceries_1",
        envelopeId = "groceries",
        monthId = "2026-05",
        amount = 38.0,
        note = "Franprix",
        date = epochMillis(2026, 5, 15)
      ),
      TransactionEntity(
        id = "tx_groceries_2",
        envelopeId = "groceries",
        monthId = "2026-05",
        amount = 62.0,
        note = "Carrefour Express",
        date = epochMillis(2026, 5, 13)
      ),
      TransactionEntity(
        id = "tx_groceries_3",
        envelopeId = "groceries",
        monthId = "2026-05",
        amount = 28.0,
        note = "Marché Aligre",
        date = epochMillis(2026, 5, 10)
      ),
      TransactionEntity(
        id = "tx_groceries_4",
        envelopeId = "groceries",
        monthId = "2026-05",
        amount = 47.0,
        note = "Naturalia",
        date = epochMillis(2026, 5, 8)
      ),
      TransactionEntity(
        id = "tx_groceries_5",
        envelopeId = "groceries",
        monthId = "2026-05",
        amount = 41.0,
        note = "Franprix",
        date = epochMillis(2026, 5, 4)
      ),
      TransactionEntity(
        id = "tx_groceries_6",
        envelopeId = "groceries",
        monthId = "2026-05",
        amount = 71.0,
        note = "Course semaine",
        date = epochMillis(2026, 5, 2)
      ),
      TransactionEntity(
        id = "tx_ams_1",
        envelopeId = "ams",
        monthId = "2026-05",
        amount = 184.0,
        note = "Hôtel 2 nuits",
        date = epochMillis(2026, 5, 6)
      ),
      TransactionEntity(
        id = "tx_ams_2",
        envelopeId = "ams",
        monthId = "2026-05",
        amount = 88.0,
        note = "Train Thalys",
        date = epochMillis(2026, 5, 6)
      ),
      TransactionEntity(
        id = "tx_ams_3",
        envelopeId = "ams",
        monthId = "2026-05",
        amount = 16.0,
        note = "Anne Frank Huis",
        date = epochMillis(2026, 5, 7)
      ),
      TransactionEntity(
        id = "tx_ams_4",
        envelopeId = "ams",
        monthId = "2026-05",
        amount = 24.0,
        note = "Restos",
        date = epochMillis(2026, 5, 8)
      ),
    )
      .forEach { dao.insert(it) }
  }
}
