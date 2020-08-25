

import SQLEx.static.group
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals


class Test {



    @Test
    fun whereWithGroup() {
        val build = SQLEx().s().c().f("pepe")
            .w(group(SQLEx("field1" eql "1526") and ("field2" grThan  "'abd'"))
                        or SQLEx().group(SQLEx("field1" diff  "20") and ("field1" diff  "20"))
            )
            .b()

        assertEquals("""SELECT count(*) FROM pepe WHERE (field1 = 1526 AND field2 >= 'abd' ) OR (field1 != 20 AND field1 != 20 )""", build.trim())

    }


    @Test
    fun miniSelect() {
        val build = SQLEx().s().c().f("pepe")
            .w(SQLEx("field1" eql "1526")
                .AND("field2" grThan  "'abd'")
            )
            .b()

        assertEquals("""SELECT count(*) FROM pepe WHERE field1 = 1526 AND field2 >= 'abd'""", build.trim())

    }

    @Test
    fun selectCount() {
        val build = SQLEx().select().count().from("pepe").build()

        assertEquals("""SELECT count(*) FROM pepe""", build.trim())

    }

    @Test
    fun compactSelect() {
        val build = SQLEx().selectAllFrom("pepe", "a").build()

        assertEquals("""SELECT * FROM pepe AS a""", build.trim())

    }

    @Test
    fun simpleSelect() {
        val build = SQLEx().select().everything().from("pepe", "a")
            .build()

        assertEquals("""SELECT * FROM pepe AS a""", build.trim())

    }

    @Test
    fun simpleWhere() {
        println("a.field2" eql "hola")
        val build = SQLEx().select().everything().from("pepe", "a")
            .where(SQLEx("a.field1" eql "1526")
                    .AND("a.field2" eql """ 'abd' """)
            )
            .build()

        assertEquals("""SELECT * FROM pepe AS a WHERE a.field1 = 1526 AND a.field2 = 'abd'""", build.trim())

    }


}




