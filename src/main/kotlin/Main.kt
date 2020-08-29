import SQLEx.static.ORDER_DESC
import Table.static.createTableList
import com.gomezrondon.RegEx
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl

//
//fun main() {
//
//    val build = SQLEx().select().everything()
//        .from(Table("Table1"), "a")
//        .build()
//    println(build)
//
//}

infix fun String.eql(str: String): SQLEx {
    return SQLEx("$this = $str")
}

infix fun String.grThan(str: String): SQLEx {
    return SQLEx("$this > $str")
}

infix fun SQLEx.grThan(str: String): SQLEx {
    return SQLEx("${this.build()} > $str")
}


infix fun String.lessThan(str: String):String{
    return "$this < $str"
}

infix fun String.diff(str: String): SQLEx {
    return SQLEx("$this != $str")
}

infix fun SQLEx.or(group: SQLEx): SQLEx {
    return this.OR(group.build())
}

infix fun SQLEx.and(s: SQLEx): SQLEx {
    return this.AND(SQLEx(s))
}

infix fun SQLEx.diff(str: String): SQLEx {
    return SQLEx("${this.build()} != $str")
}

class SQLEx(value: String = ""){


    constructor(value2: SQLEx) : this(value2.sqlExp)
    private var sqlExp = value
    var sqlTables:MutableList<Table> = mutableListOf()


    object static{

        const val ORDER_DESC = "DESC"
        const val ORDER_ASC = "ASC"

        fun sqlEqual(value: String): SQLEx {
            val sqlEx = """$value """
            return SQLEx(sqlEx)
        }

        fun group(chain: SQLEx): SQLEx {
             return SQLEx(""" (${chain.sqlExp}) """)
        }


    }

    fun sqlEqual(value: String): SQLEx {
        sqlExp += """ = $value """
        return  this
    }

    fun AND(value: SQLEx): SQLEx {
        sqlExp += """ AND ${value.sqlExp} """
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


    fun having(sqlEx: SQLEx): SQLEx {
        sqlExp +=  """HAVING """ + sqlEx.sqlExp
        return this
    }

    fun having(sqlEx: String): SQLEx {
        return having(SQLEx(sqlEx))
    }


    fun selectAllFrom(table: Table): SQLEx  {
        sqlExp += """SELECT * """
        from(table)
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

    fun fieldList(vararg fields: SQLEx): SQLEx {
        val list = fields.map { it.sqlExp }.joinToString(", ")
        sqlExp += "$list "
        return this
    }


    fun s(): SQLEx {
        return select()
    }

    fun count(): SQLEx {
        if (sqlExp.trim() == "SELECT") {
            sqlExp += """count(*) """
        } else {
            sqlExp += """, count(*) """
        }

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

    fun frm(table: String): SQLEx {

        return from(Table(table))
    }

    fun frm(table: Table): SQLEx {
        return from(table)
    }

    fun prettyPrint(): String {
        val formattedSQL = BasicFormatterImpl().format(sqlExp)
        return formattedSQL
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

    fun orderBy(vararg fields: SQLEx, order: String = ORDER_DESC): SQLEx {
        val list = fields.map { it.sqlExp }.joinToString(", ")
        sqlExp += " ORDER BY $list $order"
        return this
    }

    fun oBy(vararg fields: SQLEx, order: String = ORDER_DESC): SQLEx {
        return orderBy(*fields, order = order)
    }

    fun groupBy(vararg fields: SQLEx): SQLEx {
        val list = fields.map { it.sqlExp }.joinToString(", ")
        sqlExp += " GROUP BY $list "
        return this
    }

    fun selectCountFrom(tables: MutableList<Table>): SQLEx {
        tables.forEach { tbl ->
            sqlExp += SQLEx().s().c().frm(tbl.name).b() + "; \n"
        }
        return this
    }

    fun selectCountFrom(tables: String): SQLEx {
        val tables: MutableList<Table> = createTableList(tables)
        return selectCountFrom(tables)
    }


    fun deleteFrom(tables: MutableList<Table>): SQLEx {
        tables.forEach { tbl ->
            sqlExp += SQLEx().deleteFrom(tbl).b() + "; \n"
        }
        return this
    }

    fun deleteFrom(table: Table): SQLEx {
        if (table.alias.isNotEmpty()) {
            sqlExp += """DELETE FROM ${table.name} AS ${table.alias} """
        } else {
            sqlExp += """DELETE FROM ${table.name}"""
        }

        return this
    }

    fun deleteFrom(table: String): SQLEx {
        val tables: MutableList<Table> = createTableList(table)
        return deleteFrom(tables)
    }


    fun insertInto(table: Table): SQLEx {
        sqlExp += """INSERT INTO ${table.name} ("""
        sqlExp += table.fields.joinToString(", ") + ")"

        return this
    }

    fun values(vararg values: String): SQLEx {
        sqlExp += " VALUES ("+values.joinToString(", ") + ")"
        return this
    }

    fun values(values: String): SQLEx {
        sqlExp += " VALUES ("+insertFormattedValues(values) + ")"
        return this
    }


    private fun insertFormattedValues(strTemp: String): String {
        val range = RegEx().startWith(RegEx("'"))

        val regEx: RegEx = RegEx()
                .letter("'")
                .group(RegEx.static.range(RegEx.static.addToRange(range)).oneOrMore())
                .letter("'")

        val strVariables = regEx.findAll(strTemp) // find the string variables
        val withPlaceHolders = regEx.replaceAll(strTemp, "@%@") // replace the string variables

        var withComa = RegEx()
                .group(RegEx().space().oneOrMore()) // remove spaces and add coma separator
                .replaceAll(withPlaceHolders, ", ")

        //----------- re inject the string variables from step 1)
        strVariables.forEach { variable ->
            withComa = withComa.replaceFirst("@%@", variable)
        }
        return withComa
    }


}// fin de clase


infix fun SQLEx.eql(str: String): SQLEx {
    return this.sqlEqual(str )
}

infix fun SQLEx.eql(str: SQLEx): SQLEx {
     return this.sqlEqual(str.build())
}

data class Table(val name: String, var alias: String = ""){

    private val spaces = RegEx()
            .group(RegEx().space().oneOrMore())
            .buildRegExp()
    val fields:MutableList<String> = mutableListOf()


    object static{
        fun createTableList(tNames: String): MutableList<Table> {
            val regExp = RegEx()
                    .group(RegEx().space().oneOrMore())
                    .buildRegExp()

            return tNames.split(regExp).map { Table(it) }.toMutableList()
        }
    }

    fun f(field: String): SQLEx {
        return field(field)
    }

    fun f(i: Int): SQLEx {
        val field = fields[i]
        return field(field)
    }

    fun field(field: String): SQLEx {
        fields.add(field)
        if (this.alias.isNotEmpty()) {
            val str = "${this.alias}.$field"
            return SQLEx(str)
        } else {
            val str = "${this.name}.$field"
            return SQLEx(str)
        }

    }

    fun add(vararg values: String): Table {

        values.flatMap { it.split(spaces) }.forEach { field ->
            fields.add(field)
        }

        return this
    }



}// end of table
