package org.shl.vikramsamvat

typealias Converter<T> = (T) -> String

abstract class LocaleConverter<T : NamedCalendarObject> : Converter<T> { // there should be an i18n supertype for this
    abstract val mapping:Map<T, String>

    override fun invoke(p1: T): String = mapping[p1]!!

}

abstract class DayConverter : LocaleConverter<Day>()

object Sanskrit {
    class DayConverter : org.shl.vikramsamvat.DayConverter() {
        override val mapping: Map<Day, String> = mapOf(
            Days.Ravivaasara to "रविवासर",
            Days.Somavaasara to "-",
            Days.Mangalaavasara to "-",
            Days.Budhavaasara to "-",
            Days.Guruvaasara to "-",
            Days.Shukravaasara to "-",
            Days.Shanivaasara to "-"
            )
    }
}

//TODO: add a date formatter

//TODO: maybe I should move these to a data file...

