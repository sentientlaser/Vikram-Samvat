package org.shl.vikramsamvat

import java.lang.RuntimeException
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.Calendar
import java.util.concurrent.TimeUnit


fun main() {
    var amt = 2L
    val field = Calendar.MINUTE
    println(amt)
    if (field <= Calendar.MILLISECOND) amt *= 1
    println(amt)
    if (field <= Calendar.SECOND) amt *= 1000
    println(amt)
    if (field <= Calendar.MINUTE) amt *= 60
    println(amt)
    if (field <= Calendar.HOUR_OF_DAY) amt *= 60
    println(amt)

}


class VikramSamvatCalendarException(msg:String) : RuntimeException(msg)

/**
 * Most of the logic is taken from https://github.com/medic/bikram-sambat
 */
class VikramSamvatCalendar :Calendar(){


    private fun countDaysforMonths(startYear:Int, startMonth:Int, months:Long):Long {
        var days = 0L
        var month = startMonth
        var year = startYear
        for (monthCounter in months .. 0) { // fuck, doesn't handle negatives
            month++
            if (month == 0) {
                month = 12
                year ++
            }
            days += daysInMonth(year, month)
        }
        return days
    }

    override fun computeFields() {
        fun computeFields(epochOffset:Long, gregorian:GregorianCalendar) : VikramSamvatCalendar{

            fun setFields(year:Int, month:Int, days:Int, gregorian: GregorianCalendar): VikramSamvatCalendar {
                set(YEAR, year)
                set(MONTH, month)
                set(DAY_OF_MONTH, month)
                fieldIterator.forEach { set(it, gregorian.get(it)) }
                return this
            }

            var year = BS_YEAR_ZERO
            var days  = Math.floor((epochOffset - BS_EPOCH_TS ) as Double / TimeUnit.DAYS.toMillis(1)).toInt() + 1

            while (days > 0) {
                for (month in 1..12) {
                    val dM = daysInMonth(year, month)
                    if (days <= dM) return setFields(year, month, days, gregorian)
                    days -= dM
                }
                ++year
            }

            throw VikramSamvatCalendarException("Date outside supported range: $epochOffset epoch offset")
        }


        val instant = Instant.ofEpochMilli(time)
        computeFields(time, GregorianCalendar.from(ZonedDateTime.ofInstant(instant, ZoneId.of("UTC"))))
    }

    override fun computeTime() {

        var year = get(YEAR)
        var month = get(MONTH)
        val day = get(DAY_OF_MONTH)

        if (month < 1) throw VikramSamvatCalendarException("Invalid month value $month")
        if (year < BS_YEAR_ZERO) throw VikramSamvatCalendarException("Invalid year value $year")
        if (day < 1 || day > daysInMonth(year, month)) throw VikramSamvatCalendarException("Invalid day value $day")

        var timestamp = BS_EPOCH_TS + TimeUnit.DAYS.toMillis(1) * day
        month--

        while (year >= BS_YEAR_ZERO) {
            while (month > 0) {
                timestamp += TimeUnit.DAYS.toMillis(1) * daysInMonth(year, month)
                month--
            }
            month = 12
            year--
        }

        time = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"))
            .apply{
                timeInMillis = timestamp
            }
            .toInstant()
            .epochSecond
    }

    fun getSizeOfField(field:Int):Long {
        return when(field) {
            MILLISECOND -> 1L
            SECOND -> TimeUnit.SECONDS.toMillis(1L)
            MINUTE -> TimeUnit.MINUTES.toMillis(1L)
            HOUR, HOUR_OF_DAY -> TimeUnit.HOURS.toMillis(1L)
            DAY_OF_YEAR, DAY_OF_MONTH, DAY_OF_WEEK, DAY_OF_WEEK_IN_MONTH -> TimeUnit.DAYS.toMillis(1L)
            MONTH -> TimeUnit.DAYS.toMillis(countDaysforMonths(get(YEAR), get(MONTH), 1L))
            YEAR -> TimeUnit.DAYS.toMillis(countDaysforMonths(get(YEAR), get(MONTH), 12L))
            else -> throw VikramSamvatCalendarException("Unsupported Field: $field")
        }
    }
    override fun add(field: Int, amount: Int) {
        if (amount == 0) return

        time += getSizeOfField(field) * amount

        computeFields()
    }

    override fun roll(field: Int, up: Boolean) {
        if (get(field) >= getSizeOfField(field)) {
            set(field, 0)
        } else {
            set(field, get(field) + if (up) 1 else -1)
        }
    }

    override fun getMaximum(field: Int): Int {
        return when (field) {
            MONTH -> 24
            DAY_OF_WEEK -> 7
            DAY_OF_YEAR -> 365 //?
            else -> throw VikramSamvatCalendarException("Unsupported Field: $field")
        }
    }

    override fun getMinimum(field: Int): Int = 1

    override fun getLeastMaximum(field: Int): Int = getMaximum(field)

    override fun getGreatestMinimum(field: Int): Int = 1


    companion object {


        private fun daysInMonth(year: Int, month: Int): Int {
            if (month < 1 || month > 12) throw VikramSamvatCalendarException("Month does not exist: $month")
            try {
                return 29 + (ENCODED_MONTH_LENGTHS[year - BS_YEAR_ZERO].ushr(month - 1 shl 1) and 3) as Int
            } catch (ex: ArrayIndexOutOfBoundsException) {
                throw VikramSamvatCalendarException("Unsupported year/month combination: $year/$month")
            }
        }


        private val fieldIterator = arrayOf(
            HOUR_OF_DAY,
            MINUTE,
            SECOND,
            MILLISECOND
        )

        private val BS_EPOCH_TS = -1789990200000L // 1913-4-13 AD
        private val BS_YEAR_ZERO = 1970


        private val ENCODED_MONTH_LENGTHS = longArrayOf(
            5315258L,
            5314490L,
            9459438L,
            8673005L,
            5315258L,
            5315066L,
            9459438L,
            8673005L,
            5315258L,
            5314298L,
            9459438L,
            5327594L,
            5315258L,
            5314298L,
            9459438L,
            5327594L,
            5315258L,
            5314286L,
            9459438L,
            5315306L,
            5315258L,
            5314286L,
            8673006L,
            5315306L,
            5315258L,
            5265134L,
            8673006L,
            5315258L,
            5315258L,
            9459438L,
            8673005L,
            5315258L,
            5314298L,
            9459438L,
            8673005L,
            5315258L,
            5314298L,
            9459438L,
            8473322L,
            5315258L,
            5314298L,
            9459438L,
            5327594L,
            5315258L,
            5314298L,
            9459438L,
            5327594L,
            5315258L,
            5314286L,
            8673006L,
            5315306L,
            5315258L,
            5265134L,
            8673006L,
            5315306L,
            5315258L,
            9459438L,
            8673005L,
            5315258L,
            5314490L,
            9459438L,
            8673005L,
            5315258L,
            5314298L,
            9459438L,
            8473325L,
            5315258L,
            5314298L,
            9459438L,
            5327594L,
            5315258L,
            5314298L,
            9459438L,
            5327594L,
            5315258L,
            5314286L,
            9459438L,
            5315306L,
            5315258L,
            5265134L,
            8673006L,
            5315306L,
            5315258L,
            5265134L,
            8673006L,
            5315258L,
            5314490L,
            9459438L,
            8673005L,
            5315258L,
            5314298L,
            9459438L,
            8669933L,
            5315258L,
            5314298L,
            9459438L,
            8473322L,
            5315258L,
            5314298L,
            9459438L,
            5327594L,
            5315258L,
            5314286L,
            9459438L,
            5315306L,
            5315258L,
            5265134L,
            8673006L,
            5315306L,
            5315258L,
            5265134L,
            5527290L,
            5527277L,
            5527226L,
            5527226L,
            5528046L,
            5527277L,
            5528250L,
            5528057L,
            5527277L,
            5527277L
        )
    }

}
