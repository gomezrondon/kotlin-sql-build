

import SQLEx.static.ORDER_DESC
import SQLEx.static.group
import com.gomezrondon.RegEx
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName


class Test {

    @Test
    @DisplayName("Test insert into")
    fun testInsert() {
        val table1 = Table("Table1").add("field1   field2      field3") //testing with Tabs and multiple spaces

        val build = SQLEx().insertInto(table1)
                .values("'MY Name'   '2017-01-01'    563")//testing with Tabs and multiple spaces
                .b()

        assertEquals("""INSERT INTO Table1 (field1, field2, field3) VALUES ('MY Name', '2017-01-01', 563)""", build.trim())

    }

    @Test
    @DisplayName("Test multiple Delete tables statements")
    fun testMultipleDeletes() {
        val tNames = "#temp1 #temp2"
        val build = SQLEx().deleteFrom(tNames)
                .b()

        assertEquals("""DELETE FROM #temp1; DELETE FROM #temp2;""", build.trim())

    }

    @Test
    @DisplayName("Test Delete statements")
    fun testDelete() {
        val table1 = Table("Table1", "A").add("field1")

        val build = SQLEx().deleteFrom(table1)
                .where(table1.f(0) eql  "0")
                .b()

        assertEquals("""DELETE FROM Table1 AS A WHERE A.field1 = 0""", build.trim())

    }

    @Test
    @DisplayName("Test multiple select counts queries")
    fun testMultipleSelectCounts() {
        val tNames = "#temp1 #temp2 #temp3"
        val build = SQLEx().selectCountFrom(tNames)
                .b()

        assertEquals("""SELECT count(*) FROM #temp1 ; SELECT count(*) FROM #temp2 ; SELECT count(*) FROM #temp3 ;""", build.trim())

    }



    @Test
    @DisplayName("Test sql with formatting")
    fun testPrettyPrint() {
        val table1 = Table("Table1", "A")

        val build = SQLEx().selectAllFrom(table1)
                .prettyPrint()

        assertEquals("""
            SELECT
                    * 
                FROM
                    Table1 AS A
        """.trimIndent(), build.trim())

    }


    @Test
    @DisplayName("Test query with Having")
    fun testHaving() {
        val table1 = Table("Table1", "A").add("field1 field2")

        val build = SQLEx().s()
                .fieldList(table1.f(0), table1.f(1))
                .c()
                .frm(table1)
                .groupBy(table1.f(0), table1.f(1))
                .having("count(*)" grThan  "1")
                .b()

        assertEquals("""SELECT A.field1, A.field2 , count(*) FROM Table1 AS A GROUP BY A.field1, A.field2 HAVING count(*) > 1""", build.trim())

    }

    @Test
    @DisplayName("Test with group by")
    fun testGroupBy() {
        val table1 = Table("Table1", "A").add("field1 field2")

        val build = SQLEx().s()
                .fieldList(table1.f(0), table1.f(1))
                .c()
                .frm(table1)
                .groupBy(table1.f(0), table1.f(1))
                .oBy(table1.f(0), table1.f(1), order = ORDER_DESC)
                .b()

        assertEquals("""SELECT A.field1, A.field2 , count(*) FROM Table1 AS A GROUP BY A.field1, A.field2 ORDER BY A.field1, A.field2 DESC""", build.trim())

    }


    @Test
    @DisplayName("Test with order by")
    fun testOrderBy() {
        val table1 = Table("Table1", "A").add("field1", "field2")

        val build = SQLEx().s()
                .limit(10)
                .everything()
                .frm(table1)
                .orderBy(table1.f(0), table1.f(1), order = ORDER_DESC)
                .b()

        assertEquals("""SELECT TOP 10 * FROM Table1 AS A ORDER BY A.field1, A.field2 DESC""", build.trim())

    }


    @Test
    @DisplayName("Test Adding fields to a table first")
    fun testAddingFieldsToTable() {
        val table1 = Table("Table1", "A").add("field1", "field2")

        val build = SQLEx().s()
                .limit(10)
                .everything()
                .frm(table1)
                .w(SQLEx(table1.f(0) eql "1526") and (table1.f(1) grThan  "'abd'"))
                .b()

        assertEquals("""SELECT TOP 10 * FROM Table1 AS A WHERE A.field1 = 1526 AND A.field2 > 'abd'""", build.trim())

    }


    @Test
    @DisplayName("Select with where")
    fun selectWithWhere() {
        val table1 = Table("Table1", "A")

        var build = SQLEx().s()
                .limit(10)
                .everything()
                .frm(table1)
                .w(table1.f("""field1""") eql "1256")
                .b()


        assertEquals("""SELECT TOP 10 * FROM Table1 AS A WHERE A.field1 = 1256""", build.trim())

        val table2 = Table("Table2")
        build = SQLEx().s()
                .limit(10)
                .everything()
                .frm(table2)
                .w(table2.field("""field1""") eql "1256")
                .b()

        assertEquals("""SELECT TOP 10 * FROM Table2 WHERE Table2.field1 = 1256""", build.trim())
    }


        @Test
        @DisplayName("query with limit")
    fun selectWithLimit() {
        val table = Table("Table1", "A")

        var build = SQLEx().s()
                .fieldList("field1")
                .frm(table)
                .limit(10)
                .b()

        assertEquals("""SELECT TOP 10 field1 FROM Table1 AS A""", build.trim())

        build = SQLEx().s()
                .limit(10)
                .everything()
                 .frm(table)
                .b()

        assertEquals("""SELECT TOP 10 * FROM Table1 AS A""", build.trim())

    }


    @Test
    @DisplayName("Test joining two tables")
    fun joiningTwoTables() {

        val table1 = Table("Table1", "A").add("field1", "field2")
        val table2 = Table("Table2", "B").add("field3")

        val build = SQLEx().s()
                .fieldList(table1.f(0),table1.f(1) , table2.f(0))
                .frm(table1)
                .join(table2).on( table1.f(0) eql table2.f(0) )
                .b()

        assertEquals("""SELECT A.field1, A.field2, B.field3 FROM Table1 AS A INNER JOIN Table2 AS B ON A.field1 = B.field3""", build.trim())

    }


    @Test
    @DisplayName("query with some fields")
    fun selectOfSomeFields() {
        val build = SQLEx().s()
                .fieldList("field1", "field2", "field3")
                .frm("Table1")
                .b()

        assertEquals("""SELECT field1, field2, field3 FROM Table1""", build.trim())

    }

    @Test
    @DisplayName("query grouping logic conditions")
    fun whereWithGroup() {
        val build = SQLEx().s().c().frm("Table1")
            .w(group(SQLEx("field1" eql "1526") and ("field2" grThan  "'abd'") )
                        or group(SQLEx("field1" diff  "20") and ("field1" diff  "20"))
            )
            .b()

        assertEquals("""SELECT count(*) FROM Table1 WHERE (field1 = 1526 AND field2 > 'abd' ) OR (field1 != 20 AND field1 != 20 )""", build.trim())

    }


    @Test
    @DisplayName("testing compact query")
    fun miniSelect() {
        val build = SQLEx().s().c().frm("Table1")
            .w(SQLEx("field1" eql "1526")
                .AND("field2" grThan  "'abd'")
            )
            .b()

        assertEquals("""SELECT count(*) FROM Table1 WHERE field1 = 1526 AND field2 > 'abd'""", build.trim())

    }

    @Test
    @DisplayName("Test select count")
    fun selectCount() {
        val build = SQLEx().select().count().from(Table("Table1")).build()

        assertEquals("""SELECT count(*) FROM Table1""", build.trim())

    }

    @Test
    @DisplayName("Test selectAllFrom query")
    fun compactSelect() {
        val build = SQLEx().selectAllFrom(Table("Table1","a")).build()

        assertEquals("""SELECT * FROM Table1 AS a""", build.trim())

    }

    @Test
    @DisplayName("Test simple select")
    fun simpleSelect() {
        val build = SQLEx().select().everything().from(Table("Table1","a"))
            .build()

        assertEquals("""SELECT * FROM Table1 AS a""", build.trim())

    }

    @Test
    @DisplayName("Test select with where")
    fun simpleWhere() {

        val build = SQLEx().select().everything().from(Table("Table1","a") )
            .where(SQLEx("a.field1" eql "1526")
                    .AND("a.field2" eql """ 'abd' """)
            )
            .build()

        assertEquals("""SELECT * FROM Table1 AS a WHERE a.field1 = 1526 AND a.field2 = 'abd'""", build.trim())

    }


}







