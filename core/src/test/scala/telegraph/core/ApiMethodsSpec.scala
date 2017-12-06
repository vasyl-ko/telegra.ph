package telegraph.core

import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.{ExecutionContext, Future}

class ApiMethodsSpec extends AsyncFlatSpec with ApiMethods with Matchers {
  implicit override val executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  override protected def sendGetRequest(url: String) = url match {
    case "https://api.telegra.ph/createAccount?short_name=Sandbox&author_name=Anonymous" =>
      Future.successful(
        """{"ok":true,"result":{"short_name":"Sandbox","author_name":"Anonymous","author_url":"",
          |"access_token":"a55e85f1ca0186d79019140f63c804d0669075800f90973f1398ec6509d4",
          |"auth_url":"https:\/\/edit.telegra.ph\/auth\/ElWgU3604Q5T1oWCg1JN9YACxp7i9PfN9913c53gV6"}}""".stripMargin)
    case "https://api.telegra.ph/getViews/Sample-Page-12-15?year=2016&month=12" =>
      Future.successful("""{"ok":true,"result":{"views":40}}""")
    case "https://api.telegra.ph/editAccountInfo?access_token=b968da509bb76866c35425099bc0989a5ec3b32997d55286c657e6994bbb&short_name=Sandbox&author_name=Anonymous" =>
      Future.successful(
        """{"ok":true,"result":{"short_name":"Sandbox","author_name":"Anonymous",
          |"author_url":"https:\/\/telegra.ph\/"}}""".stripMargin)
    case "https://api.telegra.ph/getAccountInfo?access_token=b968da509bb76866c35425099bc0989a5ec3b32997d55286c657e6994bbb&fields=[\"short_name\",\"author_name\",\"author_url\"]" =>
      Future.successful("""{"ok":true,"result":{"short_name":"Sandbox","author_name":"Anonymous","author_url":"https:\/\/telegra.ph\/"}}""")
    case "https://api.telegra.ph/revokeAccessToken?access_token=b968da509bb76866c35425099bc0989a5ec3b32997d55286c657e6994bbb" =>
      Future.successful("""{"ok":true,"result":{"short_name":"Sandbox","author_name":"Anonymous","author_url":"https:\/\/telegra.ph\/"}}""")
//    case "https://api.telegra.ph/createPage?access_token=b968da509bb76866c35425099bc0989a5ec3b32997d55286c657e6994bbb&title=Sample Page&author_name=Anonymous&content=[{\"tag\":\"p\",\"children\":[\"Hello,world!\"]}]&return_content=true"
//    case "https://api.telegra.ph/getPageList?access_token=b968da509bb76866c35425099bc0989a5ec3b32997d55286c657e6994bbb&limit=3" =>
//      Future.successful("""""")
    case _ => Future.successful("""{"ok":false,"error":"URL not found"}""")
  }

  "createAccount" should "return valid Response[Account] object" in {
    createAccount("Sandbox", "Anonymous").map {
      case Response.Result(account) =>
        account.shortName should be("Sandbox")
        account.authorName should be("Anonymous")
        account.accessToken should be(Some("a55e85f1ca0186d79019140f63c804d0669075800f90973f1398ec6509d4"))
      case Response.Error(error) => fail(error)
    }
  }

  it should "fail on wrong url" in {
    createAccount("", "").map {
      case Response.Result(_) => fail("this request should fail")
      case Response.Error(_) => succeed
    }
  }

  "editAccountInfo" should "return valid Response[Account] object" in {
    editAccountInfo("b968da509bb76866c35425099bc0989a5ec3b32997d55286c657e6994bbb")("Sandbox", "Anonymous").map {
      case Response.Result(account) =>
        account.shortName should be("Sandbox")
        account.authorName should be("Anonymous")
      case Response.Error(error) => fail(error)
    }
  }

  it should "fail on wrong url" in {
    editAccountInfo("")("", "").map {
      case Response.Result(_) => fail("this request should fail")
      case Response.Error(_) => succeed
    }
  }

  "getAccountInfo" should "return valid Response[Account] object" in {
    getAccountInfo("b968da509bb76866c35425099bc0989a5ec3b32997d55286c657e6994bbb")().map {
      case Response.Result(account) =>
        account.shortName should be("Sandbox")
        account.authorName should be("Anonymous")
      case Response.Error(error) => fail(error)
    }
  }

  it should "fail on wrong url" in {
    getAccountInfo("")().map {
      case Response.Result(_) => fail("this request should fail")
      case Response.Error(_) => succeed
    }
  }

  "revokeAccessToken" should "return valid Response[Account] object" in {
    revokeAccessToken("b968da509bb76866c35425099bc0989a5ec3b32997d55286c657e6994bbb").map {
      case Response.Result(account) =>
        account.shortName should be("Sandbox")
        account.authorName should be("Anonymous")
      case Response.Error(error) => fail(error)
    }
  }

  it should "fail on wrong url" in {
    revokeAccessToken("").map {
      case Response.Result(_) => fail("this request should fail")
      case Response.Error(_) => succeed
    }
  }

  //  "createPage" should "return valid Response[Page] object" in {
  //    createPage("b968da509bb76866c35425099bc0989a5ec3b32997d55286c657e6994bbb")("Sample Page", "Anonymous", "", List(Node.Element(Tag.p, List(Node.Text("Hello,+world!"))))).map {
  //      case Response.Result(page) =>
  //        page.authorName should be("Anonymous")
  //        page.title should be("Sample Page")
  //      case Response.Error(error) => fail(error)
  //    }
  //  }

  //  it should "fail on wrong url" in {
  //    createPage("")("", "", "", Nil).map {
  //      case Response.Result(_) => fail("this request should fail")
  //      case Response.Error(_) => succeed
  //    }
  //  }
  //
  //  "editPage" should "return valid Response[Page] object" in {
  //
  //  }
  //
  //  it should "fail on wrong url" in {
  //    .map {
  //      case Response.Result(_) => fail("this request should fail")
  //      case Response.Error(_) => succeed
  //    }
  //  }
  //
  //  "getPage" should "return valid Response[Page] object" in {
  //
  //  }
  //
  //  it should "fail on wrong url" in {
  //    .map {
  //      case Response.Result(_) => fail("this request should fail")
  //      case Response.Error(_) => succeed
  //    }
  //  }
  //
  //  "getPageList" should "return valid Response[PageList] object" in {
  //
  //  }
  //
  //  it should "fail on wrong url" in {
  //    .map {
  //      case Response.Result(_) => fail("this request should fail")
  //      case Response.Error(_) => succeed
  //    }
  //  }

  "getViews" should "return valid Response[PageViews] object" in {
    getViews("Sample-Page-12-15")(Some(2016), Some(12)).map {
      case Response.Result(pageViews) =>
        pageViews.views should be(40)
      case Response.Error(error) => fail(error)
    }
  }

  it should "fail on wrong url" in {
    getViews("")().map {
      case Response.Result(_) => fail("this request should fail")
      case Response.Error(_) => succeed
    }
  }
}
