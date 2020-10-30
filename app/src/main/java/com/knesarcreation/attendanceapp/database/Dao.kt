package com.knesarcreation.attendanceapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @Insert
    fun insertAttendanceSheet(attendanceSheet: AttendanceSheet?): Long

    @Insert
    fun insertStudentDetails(studentDetails: StudentDetails?)

    @Insert
    fun insertStudentList(studentListClass: StudentListClass?)

    @Insert
    fun insertAttendanceHistory(attendanceHistory: AttendanceHistory?): Long

    @Insert
    fun insertAttendanceDatesTime(attendanceDateTimes: AttendanceDateTimes?): Long

    @Insert
    fun insertStudentHistory(studentPastAttendance: StudentPastAttendance?)


    @Query("DELETE FROM AttendanceSheet WHERE sheetNo =:sheetNo")
    fun deleteAttendanceSheet(sheetNo: Int)

    @Query("DELETE FROM AttendanceHistory WHERE hisID =:hisId")
    fun deleteAttendanceHistory(hisId: Int)

    @Query("DELETE FROM AttendanceDateTimes WHERE attendHisId =:hisID")
    fun deleteAttendanceHistoryDate(hisID: Int)

    @Query("DELETE FROM StudentDetails WHERE sheetNo = :sheetNo")
    fun deleteStdDetails(sheetNo: Int)

    @Query("DELETE FROM StudentPastAttendance WHERE stdId= :stdId")
    fun deleteStudentFromStdPastAttendance(stdId: Int)

    @Query("DELETE FROM StudentDetails WHERE id = :stdId")
    fun deleteStudentFromStdDetails(stdId: Int)

    @Query("DELETE FROM StudentListClass WHERE stdId= :stdId")
    fun deleteStudentFromStdList(stdId: Int)

    @Query("DELETE FROM AttendanceDateTimes WHERE id =:id")
    fun deleteSingleAttendanceHistoryDate(id: Int)

    @Query("UPDATE AttendanceHistory SET  subName = :subName , subCode = :subCode ,  profName =:profName , classYear = :classYear WHERE hisID=:hisId ")
    fun editAttendanceHistory(
        subName: String?,
        subCode: String?,
        profName: String?,
        classYear: String?,
        hisId: Int
    )

    @Query("UPDATE AttendanceSheet SET subName = :subName , subCode = :subCode ,  profName =:profName , classYear = :classYear WHERE sheetNo=:sheetNo ")
    fun editAttendanceSheet(
        subName: String?,
        subCode: String?,
        profName: String?,
        classYear: String?,
        sheetNo: Int
    )

    @Query("SELECT * FROM AttendanceSheet")
    fun getAllAttendanceSheets(): MutableList<AttendanceSheet>

    @Query("SELECT * FROM AttendanceSheet WHERE sheetNo =:id")
    fun getAllStudents(id: Int): List<AttendanceSheetAndStudentList>

    @Query("SELECT * FROM StudentDetails WHERE studUsn =:stdUsn")
    fun getStudentDetails(stdUsn: String): List<StudentDetails>

    @Query("SELECT * FROM AttendanceHistory")
    fun getAllAttendanceHistory(): MutableList<AttendanceHistory>

    @Query("SELECT * FROM AttendanceHistory WHERE hisID = :id")
    fun getHistoryAndDatesTimes(id: Int): List<AttendanceHistoryAndAttendanceDatesTime>

    @Query("SELECT * FROM AttendanceDateTimes WHERE id = :id")
    fun getDatesTimesAndStudentHist(id: Int): List<AttendanceDatesTimeAndStudentPastAttendance>


    /* Update student details*/
    @Query("UPDATE StudentDetails SET studAddress =:address WHERE id = :id")
    fun updateAddressStudentDetails(id: Int, address: String)

    @Query("UPDATE StudentDetails SET studContact =:contact WHERE id = :id")
    fun updateContactStudentDetails(id: Int, contact: String)

    @Query("UPDATE StudentDetails SET studCourse =:course WHERE id = :id")
    fun updateCourseStudentDetails(id: Int, course: String)

    @Query("UPDATE StudentDetails SET studName =:stdName WHERE id = :id")
    fun updateNameStudentDetails(id: Int, stdName: String)

    @Query("UPDATE StudentDetails SET studUsn =:stdUsn WHERE id = :id")
    fun updateUSNStudentDetails(id: Int, stdUsn: String)

    @Query("UPDATE StudentDetails SET absent =:abstStatus WHERE studUsn = :stdUsn")
    fun updateStudentAbsentStatus(abstStatus: Int, stdUsn: String)

    @Query("UPDATE StudentDetails SET present =:prsetStatus WHERE  studUsn = :stdUsn")
    fun updateStudentPresentStatus(prsetStatus: Int, stdUsn: String)

    @Query("UPDATE StudentDetails SET attendancePercentage =:percentage WHERE studUsn = :stdUsn")
    fun updateStudentPercentageStatus(percentage: Double, stdUsn: String)


    @Query("UPDATE AttendanceHistory SET  totalStud =:totalStudent WHERE subName = :subName")
    fun updateTotalStudInAttendanceHistory(subName: String, totalStudent: Int)

    @Query("UPDATE AttendanceSheet SET totalStud =:totalStudent WHERE subName = :subName")
    fun updateTotalStudInAttendanceSheet(subName: String, totalStudent: Int)


    @Query("UPDATE StudentListClass SET isChecked =:isChecked WHERE stdUsn = :usn")
    fun updateStudentList(isChecked: Boolean, usn: String)

    @Query("UPDATE StudentPastAttendance SET isChecked =:isChecked WHERE id= :id")
    fun updateStudentPastAttendance(isChecked: Boolean, id: Int)


    @Query("UPDATE StudentPastAttendance SET stdName =:stdName WHERE stdId = :id")
    fun updateStudNameInStudPastAttend(id: Int, stdName: String?)

    @Query("UPDATE StudentPastAttendance SET stdUsn =:stdUsn WHERE stdId = :id")
    fun updateUSNStudentAttendanceHistory(id: Int, stdUsn: String?)

    @Query("UPDATE StudentListClass SET stdName =:stdName WHERE stdId =:id")
    fun updateNameStudentLists(id: Int, stdName: String?)

    @Query("UPDATE StudentListClass SET stdUsn =:stdUsn WHERE stdId =:id")
    fun updateUSNStudentListsFromID(id: Int, stdUsn: String?)

    @Query("SELECT COUNT() FROM StudentPastAttendance WHERE stdHistId=:id ")
    fun getItemCount(id: Int): Int
}