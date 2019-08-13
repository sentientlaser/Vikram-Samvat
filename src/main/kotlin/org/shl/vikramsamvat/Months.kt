package org.shl.vikramsamvat


interface Month: NamedCalendarObject {
    val monthOfYear:Int
}

interface LunarMonth: Month

enum class LunarMonths(override val monthOfYear:Int) : LunarMonth {
    Vaisakha(1),
    Jyeshtha(2),
    Ashadha(3),
    Shraavana(4),
    Bhadra(5),
    Ashvin(6),
    Kartik(7),
    Agahana(8),
    Pausha(9),
    Magha(10),
    Phalguna(11),
    Chaitra(12);
}
