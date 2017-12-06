package telegraph

import io.circe.generic.extras._
import io.circe.{Decoder, HCursor}

package object core {
  implicit val config: Configuration = Configuration.default.withSnakeCaseKeys

  @ConfiguredJsonCodec
  case class Account(shortName: String, authorName: String, authorUrl: String,
                     accessToken: Option[String], authUrl: Option[String], pageCount: Option[Int])

  @ConfiguredJsonCodec
  case class PageList(totalCount: Int, pages: List[Page])

  @ConfiguredJsonCodec
  case class Page(path: String, url: String, title: String, description: String, authorName: Option[String],
                  authorUrl: Option[String], imageUrl: Option[String], content: List[Node], canEdit: Boolean)

  @ConfiguredJsonCodec
  case class PageViews(views: Int)

  @ConfiguredJsonCodec
  sealed trait Node

  object Node {

    case class Text(value: String) extends Node

    case class Element(tag: Tag, innerNodes: List[Node], attrs: List[Attr] = Nil) extends Node

  }

  type Tag = String
  type Attr = String

  object Tag {
    val a: Tag = "a"
    val aside: Tag = "aside"
    val b: Tag = "b"
    val blockquote: Tag = "blockquote"
    val br: Tag = "br"
    val code: Tag = "code"
    val em: Tag = "em"
    val figcaption: Tag = "figcaption"
    val figure: Tag = "figure"
    val h3: Tag = "h3"
    val h4: Tag = "h4"
    val hr: Tag = "hr"
    val i: Tag = "i"
    val iframe: Tag = "iframe"
    val img: Tag = "img"
    val li: Tag = "li"
    val ol: Tag = "ol"
    val p: Tag = "p"
    val pre: Tag = "pre"
    val s: Tag = "s"
    val strong: Tag = "strong"
    val u: Tag = "u"
    val ul: Tag = "ul"
    val video: Tag = "video"
  }

  object Attr {
    val href: Attr = "href"
    val src: Attr = "src"
  }

  sealed trait Response[T] {
    val ok: Boolean
  }

  object Response {

    case class Result[T](result: T) extends Response[T] {
      val ok = true
    }

    case class Error[T](error: String) extends Response[T] {
      val ok = false
    }

    implicit def decoder[T](implicit tDecode: Decoder[T]) = new Decoder[Response[T]] {
      override def apply(c: HCursor) = c.get[Boolean]("ok") match {
        case Right(true) => c.get[T]("result").map(Result.apply)
        case Right(false) => c.get[String]("error").map(Error.apply)
        case Left(failure) => Left(failure)
      }
    }

  }

}
