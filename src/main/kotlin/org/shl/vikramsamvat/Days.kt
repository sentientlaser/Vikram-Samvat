package org.shl.vikramsamvat

import org.shl.util.clean


interface Day: NamedCalendarObject {
    val dayOfWeek:Int
}

enum class Days(override val dayOfWeek:Int) :Day {
    Ravivaasara (1), // sunday
    Somavaasara (2), // monday
    Mangalaavasara (3), // tuesday
    Budhavaasara  (4), // wednesday
    Guruvaasara (5), // thursday
    Shukravaasara (6), // friday
    Shanivaasara (7); // saturday

    override fun toString() = name.clean()
}