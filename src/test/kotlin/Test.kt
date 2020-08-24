import SQLEx.Companion.sqlEqual
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals


class Test {


    @Test
    fun simpleSelect() {
        val build = SQLEx().select().everything().from("pepe", "a").build()

        assertEquals("""SELECT * FROM pepe AS a""", build.trim())

    }

    @Test
    fun simpleWhere() {
        val build = SQLEx().select().everything().from("pepe", "a")
            .where(sqlEqual("a.field1", "1526")
         //       .and("a.field2",""" 'abd' """)
            )
            .build()

        assertEquals("""SELECT * FROM pepe AS a WHERE a.field1 = 1526""", build.trim())

    }


}