package com.example.potikorn.twitterwithweb

import java.text.SimpleDateFormat
import java.util.*

class DateUtility() {

    companion object {
        private lateinit var dateFormat: SimpleDateFormat

        val SIMPLE_DATE = "ddMMyyHHmmss"


        fun getcurrentDate(strDateFormat: String): String? {
            dateFormat = SimpleDateFormat(strDateFormat, Locale.getDefault())
            return dateFormat.format(Date())
        }

        fun getCurrentDateTime(): String {
            val dateFormat = SimpleDateFormat("ddMMyyHHmmss", Locale.getDefault())
            val date = Date()
            return dateFormat.format(date)
        }

        fun getDayTimeFormat(date: String): String {
            val dateFormat = SimpleDateFormat("ddMMyyHHmmss", Locale.getDefault())
            val dayHourMinuteFormat = SimpleDateFormat("EEEE HH:mm", Locale.getDefault())
            val dateFormatted = dateFormat.parse(date)
            return dayHourMinuteFormat.format(dateFormatted)
        }
    }

}