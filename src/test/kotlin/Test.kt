

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals


class Test {


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
        val build = SQLEx().select().everything().from("pepe", "a").build()

        assertEquals("""SELECT * FROM pepe AS a""", build.trim())

    }

    @Test
    fun simpleWhere() {
        println("a.field2" eql "hola")
        val build = SQLEx().select().everything().from("pepe", "a")
            .where(SQLEx("a.field1" eql "1526")
                    .sqlAnd("a.field2" eql """ 'abd' """)
            )
            .build()

        assertEquals("""SELECT * FROM pepe AS a WHERE a.field1 = 1526 AND a.field2 = 'abd'""", build.trim())

    }


}