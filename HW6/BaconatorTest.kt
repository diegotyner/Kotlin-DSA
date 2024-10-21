import edu.ucdavis.cs.ecs36c.Baconator
import org.junit.jupiter.api.Test

class BaconatorTest {

    @Test
    fun executeBasicTest() {
        val b = Baconator("moviedata.csv")
        /*
         * This is FAR from comprehensive on the testing...
         * But it will check that you have the format right
         */
        val testpath = b.getBaconpath("Neil Fingleton")
        assert(testpath.size == 3)
        assert(testpath[0] == "Neil Fingleton")
        assert(testpath[1] == "X-Men: First Class")
        assert(testpath[2] == "Kevin Bacon")
        println("$testpath")
    }

    @Test
    fun actorTesting() {
        val b = Baconator("moviedata.csv")
        /*
         * This is FAR from comprehensive on the testing...
         * But it will check that you have the format right
         */

        var testpath = b.getBaconpath("Kevin Bacon")
        assert(testpath.size == 1)
        assert(testpath[0] == "Kevin Bacon")
        println("$testpath")

        testpath = b.getBaconpath("Tom Holland")
        assert(testpath.size == 5)
        assert(testpath[0] == "Tom Holland")
        assert(testpath[4] == "Kevin Bacon")
        println("$testpath")

        testpath = b.getBaconpath("Bob Odenkirk")
        assert(testpath.size == 5)
        assert(testpath[4] == "Kevin Bacon")
        println("$testpath")

        testpath = b.getBaconpath("Keanu Reeves")
        assert(testpath.size == 5)
        assert(testpath[4] == "Kevin Bacon")
        println("$testpath")

        testpath = b.getBaconpath("Kevin Dillon")
        assert(testpath.size == 5)
        assert(testpath[4] == "Kevin Bacon")
        println("$testpath")

        testpath = b.getBaconpath("Christine Adams")
        assert(testpath.size == 5)
        assert(testpath[0] == "Christine Adams")
        assert(testpath[4] == "Kevin Bacon")
        println("$testpath")

    }
}