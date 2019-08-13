package org.shl.util


fun String.clean() =  this.replace("_+", " ").trim().toLowerCase().capitalize()
