package org.shl.vikramsamvat

typealias Converter<T> = (T) -> String

abstract class LocaleConverter<T : NamedCalendarObject> : Converter<T> { // there should be an i18n supertype for this
    abstract val mapping:Map<T, String>

    override fun invoke(p1: T): String = mapping[p1]!!

}

abstract class DayConverter : LocaleConverter<Day>()
abstract class MonthConverter: LocaleConverter<Month>()

//TODO: add a date formatter

//TODO: maybe I should move these to a data file...

