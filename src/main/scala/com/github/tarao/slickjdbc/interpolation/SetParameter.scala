package com.github.tarao
package slickjdbc
package interpolation

import scala.annotation.implicitNotFound
import slick.jdbc.{SetParameter => SP, PositionedParameters}
import com.github.tarao.nonempty.NonEmpty

trait ListParameter {
  @inline implicit
  def createSetList[T](implicit c: SP[T]): SetList[T, NonEmpty[T]] =
    new SetList[T, NonEmpty[T]](c)
}
object ListParameter extends ListParameter

/** SetParameter for non-empty list types. */
class SetList[S, -T <: NonEmpty[S]](val c: SP[S]) extends SP[T] {
  def apply(param: T, pp: PositionedParameters): Unit = {
    param.foreach { item => c.asInstanceOf[SP[Any]](item, pp) }
  }
}

@implicitNotFound(msg = "Unsupported parameter type: ${T}.\n" +
  "[NOTE] You need an implicit of slick.jdbc.SetParameter[${T}] to pass a value of the type.")
sealed trait CheckParameter[-T]
object CheckParameter {
  implicit def valid[T](implicit c: SP[T]): CheckParameter[T] =
    new CheckParameter[T] {}
}

@implicitNotFound(msg = "Illegal parameter type: ${T}.\n" +
  "[NOTE] A list is not allowed since it may be empty and breaks the query.\n" +
  "[NOTE] Pass a com.github.tarao.nonempty.NonEmpty[] if you know that it is not empty.")
sealed trait CheckList[-T]
object CheckList {
  implicit def valid[T](implicit c: SP[T]): CheckList[T] =
    new CheckList[T] {}
}

@implicitNotFound(msg = "Non-empty list is passed.\n" +
  "[NOTE] Use interpolation.ListParameter trait to enable passing a non-empty list.")
sealed trait CheckNonEmpty[-T]
object CheckNonEmpty {
  implicit def valid[T](implicit c: SP[T]): CheckNonEmpty[T] =
    new CheckNonEmpty[T] {}
}

@implicitNotFound(msg = "Maybe-non-empty list is passed.\n" +
  "[NOTE] Break it into Some(_) or None to confirm that it is not empty.")
sealed trait CheckOptionNonEmpty[-T]
object CheckOptionNonEmpty {
  implicit def valid[T](implicit c: SP[T]): CheckOptionNonEmpty[T] =
    new CheckOptionNonEmpty[T] {}
}
