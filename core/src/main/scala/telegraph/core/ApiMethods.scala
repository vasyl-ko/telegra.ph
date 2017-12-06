package telegraph.core

import io.circe.{Decoder, Encoder, Json}
import io.circe.parser._

import scala.language.postfixOps

import scala.concurrent.{ExecutionContext, Future}

trait ApiMethods {
  implicit val executionContext: ExecutionContext
  private val apiRoot = "https://api.telegra.ph"

  protected def sendGetRequest(url: String): Future[String]

  protected def executeRequest[T](url: String)(implicit decoder: Decoder[Response[T]]): Future[Response[T]] =
    sendGetRequest(url)
      .map(parse(_) flatMap decoder.decodeJson)
      .collect {
        case Right(response) => response
        case Left(error) => throw error
      }

  protected def executeMethodWithPath[T](method: String, path: String)(params: Map[String, String])
                                        (implicit decoder: Decoder[Response[T]]): Future[Response[T]] =
    executeRequest(apiRoot + s"/$method${if (path.nonEmpty) s"/$path" else ""}?${params.map { case (key, value) => s"$key=$value" }.mkString("&")}")

  protected def executeMethod[T](method: String)(params: Map[String, String])(implicit decoder: Decoder[Response[T]]): Future[Response[T]] =
    executeMethodWithPath[T](method, "")(params)

  def createAccount(shortName: String, authorName: String, authorUrl: Option[String] = None)
                   (implicit decoder: Decoder[Response[Account]]): Future[Response[Account]] =
    executeMethod("createAccount")(Map(
      "short_name" -> shortName,
      "author_name" -> authorName) ++
      authorUrl.map("author_url" -> _)
    )

  def editAccountInfo(accessToken: String)(shortName: String, authorName: String, authorUrl: Option[String] = None)
                     (implicit decoder: Decoder[Response[Account]]): Future[Response[Account]] =
    executeMethod("editAccountInfo")(Map(
      "access_token" -> accessToken,
      "short_name" -> shortName,
      "author_name" -> authorName) ++
      authorUrl.map("author_url" -> _)
    )

  def getAccountInfo(accessToken: String)(withAuthUrl: Boolean = false, withPageCount: Boolean = false)
                    (implicit decoder: Decoder[Response[Account]]): Future[Response[Account]] =
    executeMethod("getAccountInfo")(Map(
      "access_token" -> accessToken,
      "fields" -> s"""["short_name","author_name","author_url"${if (withAuthUrl) ",\"auth_url\"" else ""}${if (withPageCount) ",\"page_count\"" else ""}]"""
    ))

  def revokeAccessToken(accessToken: String)
                       (implicit decoder: Decoder[Response[Account]]): Future[Response[Account]] =
    executeMethod("revokeAccessToken")(Map(
      "access_token" -> accessToken
    ))

  def createPage(accessToken: String)(title: String, authorName: String, authorUrl: String,
                                      content: List[Node], returnContent: Boolean = false)
                (implicit decoder: Decoder[Response[Page]], encoder: Encoder[Node]): Future[Response[Page]] =
    executeMethod("createPage")(Map(
      "access_token" -> accessToken,
      "title" -> title,
      "author_name" -> authorName,
      "author_url" -> authorUrl,
      "content" -> s"[${content.map(encoder apply _ noSpaces).mkString(",")}]",
      "return_content" -> returnContent.toString
    ))

  def editPage(accessToken: String)(path: String, title: String, authorName: String, authorUrl: String, content: List[Node], returnContent: Boolean = false)
              (implicit decoder: Decoder[Response[Page]], encoder: Encoder[Node]): Future[Response[Page]] =
    executeMethodWithPath("editPage", path)(Map(
      "access_token" -> accessToken,
      "title" -> title,
      "author_name" -> authorName,
      "author_url" -> authorUrl,
      "content" -> s"[${content.map(encoder apply _ noSpaces).mkString(",")}]",
      "return_content" -> returnContent.toString
    ))

  def getPage(path: String, returnContent: Boolean = false)
             (implicit decoder: Decoder[Response[Page]]): Future[Response[Page]] =
    executeMethodWithPath("getPage", path)(Map(
      "return_content" -> returnContent.toString
    ))

  def getPageList(accessToken: String)(offset: Int, limit: Int = 50)
                 (implicit decoder: Decoder[Response[PageList]]): Future[Response[PageList]] =
    executeMethod("getPageList")(Map(
      "access_token" -> accessToken,
      "offset" -> offset.toString,
      "limit" -> limit.toString
    ))

  def getViews(path: String)(year: Option[Int] = None, month: Option[Int] = None, day: Option[Int] = None, hour: Option[Int] = None)
              (implicit decoder: Decoder[Response[PageViews]]): Future[Response[PageViews]] =
    executeMethodWithPath("getViews", path)(Map() ++
      year.map("year" -> _.toString) ++
      month.map("month" -> _.toString) ++
      day.map("day" -> _.toString) ++
      hour.map("hour" -> _.toString)
    )
}
