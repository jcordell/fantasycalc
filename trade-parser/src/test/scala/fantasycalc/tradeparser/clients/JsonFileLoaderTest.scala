package fantasycalc.tradeparser.clients

import fantasycalc.tradeparser.models.api.mfl.MflPositionRules._
import fantasycalc.tradeparser.models.api.mfl.{RulesApiResponse, _}
import fantasycalc.tradeparser.testhelpers.JsonUtils
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.TableFor2

import scala.util.{Success, Try}

class JsonFileLoaderTest extends AnyFunSpec with Matchers {
  private val apiEndpoints: TableFor2[String, String => RulesApiResponse] =
    Table(
      ("directory", "File loader"),
      ("mfl/rules", JsonUtils.load[RulesApiResponse])
    )

  describe("Can parse JSON api responses") {
    it("should parse JSON api responses") {
      forAll(apiEndpoints) { (path, jsonLoader) =>
        {
          val fileNames =
            JsonUtils.getFiles(s"src/test/resources/api-responses/$path")
          fileNames.map(
            fileName =>
              Try(jsonLoader(s"$path/${fileName.getName}")) shouldBe a[Success[_]]
          )
        }
      }

    }

  }

}
