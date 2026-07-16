package org.openedx.app.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database Migrations
 *
 * This file contains all database migrations for the app.
 * When adding new migrations:
 * 1. Create a new MIGRATION_X_Y object
 * 2. Add it to the ALL_MIGRATIONS array
 * 3. Update DATABASE_VERSION in AppDatabase.kt
 *
 * Best Practices:
 * - Never remove migrations that have been released
 * - Keep migrations in chronological order
 * - Use descriptive comments
 * - Test migrations thoroughly before release
 */

private const val COURSE_DISCOVERY_TABLE = "course_discovery_table"

/**
 * Migration from a "sumac" version 3 database straight to version 8.
 */
val MIGRATION_3_8 = object : Migration(3, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.createUlmoTablesIfMissing()
        db.addSherabDiscoveryColumnsIfMissing()
    }
}

/**
 * Migration from version 6 to version 7
 *
 * Changes:
 * - Adds new fields to course_discovery_table:
 *   - duration: Course duration
 *   - courseRequirement: Course prerequisites/requirements
 *   - description: Detailed course description
 *   - learningOutcomes: Expected learning outcomes
 *   - instructors: JSON string of instructor information
 */
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.addSherabDiscoveryColumnsIfMissing()
    }
}

/**
 * Migration from version 7 to version 8
 *
 * Changes:
 * - Adds new field to course_discovery_table:
 *   - purchaseLink: URL to purchase paid courses
 */
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.addColumnIfMissing(COURSE_DISCOVERY_TABLE, "purchaseLink", "TEXT DEFAULT NULL")
    }
}

/**
 * All migrations in chronological order.
 * This array is used by the database builder to apply migrations automatically.
 *
 * To add a new migration:
 * - Create a new MIGRATION_X_Y object above
 * - Add it to this array: arrayOf(MIGRATION_6_7, MIGRATION_7_8, ...)
 */
val ALL_MIGRATIONS = arrayOf(
    MIGRATION_3_8,
    MIGRATION_6_7,
    MIGRATION_7_8
    // Add future migrations here
)

private fun SupportSQLiteDatabase.createUlmoTablesIfMissing() {
    execSQL("CREATE TABLE IF NOT EXISTS `download_course_preview_table` (`course_id` TEXT NOT NULL, `course_name` TEXT, `course_image` TEXT, `total_size` INTEGER, PRIMARY KEY(`course_id`))")
    execSQL("CREATE TABLE IF NOT EXISTS `course_enrollment_details_table` (`id` TEXT NOT NULL, `courseUpdates` TEXT NOT NULL, `courseHandouts` TEXT NOT NULL, `discussionUrl` TEXT NOT NULL, `hasUnmetPrerequisites` INTEGER NOT NULL, `isTooEarly` INTEGER NOT NULL, `isStaff` INTEGER NOT NULL, `auditAccessExpires` TEXT, `hasAccess` INTEGER, `errorCode` TEXT, `developerMessage` TEXT, `userMessage` TEXT, `additionalContextUserMessage` TEXT, `userFragment` TEXT, `certificateURL` TEXT, `created` TEXT, `mode` TEXT, `isActive` INTEGER NOT NULL, `upgradeDeadline` TEXT, `name` TEXT NOT NULL, `number` TEXT NOT NULL, `org` TEXT NOT NULL, `start` INTEGER, `startDisplay` TEXT NOT NULL, `startType` TEXT NOT NULL, `end` INTEGER, `isSelfPaced` INTEGER NOT NULL, `courseAbout` TEXT NOT NULL, `bannerImage` TEXT, `courseImage` TEXT, `courseVideo` TEXT, `image` TEXT, `facebook` TEXT NOT NULL, `twitter` TEXT NOT NULL, PRIMARY KEY(`id`))")
    execSQL("CREATE TABLE IF NOT EXISTS `course_dates_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `first_component_block_id` TEXT, `course_id` TEXT NOT NULL, `due_date` TEXT, `assignment_title` TEXT, `learner_has_access` INTEGER, `relative` INTEGER, `course_name` TEXT)")
    execSQL("CREATE TABLE IF NOT EXISTS `video_progress_table` (`block_id` TEXT NOT NULL, `video_url` TEXT NOT NULL, `video_time` INTEGER, `duration` INTEGER, PRIMARY KEY(`block_id`))")
    execSQL("CREATE TABLE IF NOT EXISTS `course_progress_table` (`courseId` TEXT NOT NULL, `verifiedMode` TEXT NOT NULL, `accessExpiration` TEXT NOT NULL, `creditCourseRequirements` TEXT NOT NULL, `end` TEXT NOT NULL, `enrollmentMode` TEXT NOT NULL, `hasScheduledContent` INTEGER NOT NULL, `sectionScores` TEXT NOT NULL, `studioUrl` TEXT NOT NULL, `username` TEXT NOT NULL, `userHasPassingGrade` INTEGER NOT NULL, `disableProgressGraph` INTEGER NOT NULL, `certificate_certStatus` TEXT, `certificate_certWebViewUrl` TEXT, `certificate_downloadUrl` TEXT, `certificate_certificateAvailableDate` TEXT, `completion_completeCount` INTEGER, `completion_incompleteCount` INTEGER, `completion_lockedCount` INTEGER, `grade_letterGrade` TEXT, `grade_percent` REAL, `grade_isPassing` INTEGER, `grading_assignmentPolicies` TEXT, `grading_gradeRange` TEXT, `grading_assignmentColors` TEXT, `verification_link` TEXT, `verification_status` TEXT, `verification_statusDate` TEXT, PRIMARY KEY(`courseId`))")
}

private fun SupportSQLiteDatabase.addSherabDiscoveryColumnsIfMissing() {
    addColumnIfMissing(COURSE_DISCOVERY_TABLE, "duration", "TEXT NOT NULL DEFAULT ''")
    addColumnIfMissing(COURSE_DISCOVERY_TABLE, "courseRequirement", "TEXT NOT NULL DEFAULT ''")
    addColumnIfMissing(COURSE_DISCOVERY_TABLE, "description", "TEXT NOT NULL DEFAULT ''")
    addColumnIfMissing(COURSE_DISCOVERY_TABLE, "learningOutcomes", "TEXT NOT NULL DEFAULT ''")
    addColumnIfMissing(COURSE_DISCOVERY_TABLE, "instructors", "TEXT NOT NULL DEFAULT ''")
}

private fun SupportSQLiteDatabase.addColumnIfMissing(
    tableName: String,
    columnName: String,
    definition: String
) {
    query("PRAGMA table_info($tableName)").use { cursor ->
        val nameIndex = cursor.getColumnIndex("name")
        while (cursor.moveToNext()) {
            if (cursor.getString(nameIndex) == columnName) return
        }
    }
    execSQL("ALTER TABLE $tableName ADD COLUMN $columnName $definition")
}
