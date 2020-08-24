



fun main() {

    val build = SQLEx().select().everything()
        .from("pepe", "a")
        .build()
    println(build)

}

class SQLEx(value: String="" ){

    private var sqlExp=""

    companion object {

        fun sqlEqual(field: String, value: String): String {
            return """$field = $value """
        }

    }

//    fun and(field: String, value: String): SQLEx {
//        return sqlEqual(field, value)
//    }

    fun where(sqlEx: String): SQLEx {
        sqlExp += """WHERE """ + sqlEx
        return this
    }

    fun select(): SQLEx {
        sqlExp += """SELECT """
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
        return sqlExp
    }

}