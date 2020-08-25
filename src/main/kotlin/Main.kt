import com.gomezrondon.RegEx


fun main() {

    val build = SQLEx().select().everything()
        .from("pepe", "a")
        .build()
    println(build)

}

infix fun String.eql(str:String):String{
    return "$this = $str"
}

infix fun String.grThan(str:String):String{
    return "$this >= $str"
}

infix fun String.lessThan(str:String):String{
    return "$this <= $str"
}

infix fun String.diff(str:String):String{
    return "$this != $str"
}



class SQLEx(value: String="" ){


    constructor(value2: SQLEx) : this(value2.sqlExp )
    private var sqlExp = value
    companion object {

        fun sqlEqual(value: String): SQLEx {
            val sqlEx = """$value """
            return SQLEx(sqlEx)
        }

    }

    fun sqlEqual(value: String): SQLEx {
        sqlExp += """$value """
        return  this
    }

    fun sqlAnd(value: String): SQLEx {
        sqlExp += """ AND $value """
        return this
    }

//    fun and(field: String, value: String): SQLEx {
//        return sqlEqual(field, value)
//    }

    fun where(sqlEx: SQLEx): SQLEx {
        sqlExp +=  """WHERE """ + sqlEx.sqlExp
        return this
    }

    fun selectAllFrom(table:String, alias:String=""): SQLEx  {
        val prefix = """SELECT * FROM """
        if (alias.isNotEmpty()) {
            sqlExp += """$prefix$table AS $alias """
        } else {
            sqlExp += """$prefix$table"""
        }
        return this
    }

    fun select(): SQLEx {
        sqlExp += """SELECT """
        return this
    }

    fun count(): SQLEx {
        sqlExp += """count(*) """
        return this
    }

    fun everything(): SQLEx {
        sqlExp += """* """
        return this
    }


    fun from(table:String, alias:String=""): SQLEx {
        if (alias.isNotEmpty()) {
            sqlExp += """FROM $table AS $alias """
        } else {
            sqlExp += """FROM $table """
        }
        return this
    }



    fun build(): String {
        //(\s+)
        val replaceAll = RegEx()
                .group(RegEx().space().oneOrMore())
                .replaceAll(sqlExp, " ")
        return replaceAll
    }



}