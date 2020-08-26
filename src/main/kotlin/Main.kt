import com.gomezrondon.RegEx

//
//fun main() {
//
//    val build = SQLEx().select().everything()
//        .from(Table("Table1"), "a")
//        .build()
//    println(build)
//
//}

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

infix fun SQLEx.or(group: SQLEx): SQLEx {
    return this.OR(group.build())
}

infix fun SQLEx.and(s: String): SQLEx {
    return this.AND(s)
}

class SQLEx(value: String="" ){


    constructor(value2: SQLEx) : this(value2.sqlExp )
    private var sqlExp = value
    var sqlTables:MutableList<Table> = mutableListOf()


    object static{

        fun sqlEqual(value: String): SQLEx {
            val sqlEx = """$value """
            return SQLEx(sqlEx)
        }

        fun group(chain: SQLEx): SQLEx {
             return SQLEx(""" (${chain.sqlExp}) """)
        }


    }

    fun sqlEqual(value: String): SQLEx {
        sqlExp += """$value """
        return  this
    }

    fun AND(value: String): SQLEx {
        sqlExp += """ AND $value """
        return this
    }

    fun OR(value: String): SQLEx {
        sqlExp += """ OR $value """
        return this
    }

//    fun and(field: String, value: String): SQLEx {
//        return sqlEqual(field, value)
//    }

    fun group(chain: SQLEx): SQLEx {
        sqlExp +=  """ (${chain.sqlExp}) """
        return this
    }

    fun where(sqlEx: SQLEx): SQLEx {
        sqlExp +=  """WHERE """ + sqlEx.sqlExp
        return this
    }

    fun w(sqlEx: SQLEx): SQLEx {
        return where(sqlEx)
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

    fun fieldList(vararg strings: String): SQLEx  {
        val list = strings.joinToString(", ")
        sqlExp += list + " "
        return  this
    }

    fun s(): SQLEx {
        return select()
    }

    fun count(): SQLEx {
        sqlExp += """count(*) """
        return this
    }

    fun c(): SQLEx {
        return count()
    }

    fun everything(): SQLEx {
        sqlExp += """* """
        return this
    }


    fun join(table: Table): SQLEx {
        sqlTables.add(table)
        if (table.alias.isNotEmpty()) {
            sqlExp += """INNER JOIN ${table.name} AS ${table.alias} """
        } else {
            sqlExp += """INNER JOIN ${table.name} """
        }
        return this
    }

    fun on(value: SQLEx): SQLEx {
        sqlExp += " ON ${value.sqlExp}"
        return this
    }

    fun on(value: String): SQLEx {
        sqlExp += " ON ${value}"
        return this
    }


    fun from(table: Table): SQLEx {
        sqlTables.add(table)
        if (table.alias.isNotEmpty()) {
            sqlExp += """FROM ${table.name} AS ${table.alias} """
        } else {
            sqlExp += """FROM ${table.name} """
        }
        return this
    }

    fun f(table:String ): SQLEx {

        return from(Table(table) )
    }

    fun f(table:Table): SQLEx {
        return from(table )
    }


    fun build(): String {
        //(\s+)
        val replaceAll = RegEx()
                .group(RegEx().space().oneOrMore())
                .replaceAll(sqlExp, " ")
        return replaceAll
    }

    fun b() :String {
        return build()
    }

    fun limit(i: Int): SQLEx {
        val replace = this.sqlExp.replace("SELECT", "SELECT TOP $i ")
        return SQLEx(replace)
    }


}

data class Table(val name: String, var alias: String=""){

}
