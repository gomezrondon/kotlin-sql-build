

import SQLEx.static.group
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals


class Test {


    @Test
    fun selectWithLimit() {
        val table = Table("Table1", "A")

        var build = SQLEx().s()
                .fieldList("field1")
                .f(table)
                .limit(10)
                .b()

        assertEquals("""SELECT TOP 10 field1 FROM Table1 AS A""", build.trim())

        build = SQLEx().s()
                .limit(10)
                .everything()
                 .f(table)
                .b()

        assertEquals("""SELECT TOP 10 * FROM Table1 AS A""", build.trim())

    }


    @Test
    fun joiningTwoTables() {
        val build = SQLEx().s()
                .fieldList("field1", "field2", "field3")
                .f(Table("Table1","A") )
                .join(Table("Table2","B") ).on( "a.field1" eql "b.field3" )
                .b()

        assertEquals("""SELECT field1, field2, field3 FROM Table1 AS A INNER JOIN Table2 AS B ON a.field1 = b.field3""", build.trim())

    }


    @Test
    fun selectOfSomeFields() {
        val build = SQLEx().s()
                .fieldList("field1", "field2", "field3")
                .f("Table1")
                .b()

        assertEquals("""SELECT field1, field2, field3 FROM Table1""", build.trim())

    }

        @Test
    fun whereWithGroup() {
        val build = SQLEx().s().c().f("Table1")
            .w(group(SQLEx("field1" eql "1526") and ("field2" grThan  "'abd'") )
                        or group(SQLEx("field1" diff  "20") and ("field1" diff  "20"))
            )
            .b()

        assertEquals("""SELECT count(*) FROM Table1 WHERE (field1 = 1526 AND field2 >= 'abd' ) OR (field1 != 20 AND field1 != 20 )""", build.trim())

    }


    @Test
    fun miniSelect() {
        val build = SQLEx().s().c().f("Table1")
            .w(SQLEx("field1" eql "1526")
                .AND("field2" grThan  "'abd'")
            )
            .b()

        assertEquals("""SELECT count(*) FROM Table1 WHERE field1 = 1526 AND field2 >= 'abd'""", build.trim())

    }

    @Test
    fun selectCount() {
        val build = SQLEx().select().count().from(Table("Table1")).build()

        assertEquals("""SELECT count(*) FROM Table1""", build.trim())

    }

    @Test
    fun compactSelect() {
        val build = SQLEx().selectAllFrom("Table1", "a").build()

        assertEquals("""SELECT * FROM Table1 AS a""", build.trim())

    }

    @Test
    fun simpleSelect() {
        val build = SQLEx().select().everything().from(Table("Table1","a"))
            .build()

        assertEquals("""SELECT * FROM Table1 AS a""", build.trim())

    }

    @Test
    fun simpleWhere() {
        println("a.field2" eql "hola")
        val build = SQLEx().select().everything().from(Table("Table1","a") )
            .where(SQLEx("a.field1" eql "1526")
                    .AND("a.field2" eql """ 'abd' """)
            )
            .build()

        assertEquals("""SELECT * FROM Table1 AS a WHERE a.field1 = 1526 AND a.field2 = 'abd'""", build.trim())

    }


}






