package com.programacion.avanzada.listas

import scala.annotation.tailrec

object Lista {
  sealed trait Lista[+T] {
    def head: T
    def tail: Lista[T]
    def isEmpty: Boolean

    def count: Int = 1 + tail.count
    def prepend[U >: T](elem: U): Lista[U] = Lista.cons(elem, this)
    def append[U >: T](elem: U): Lista[U] = Lista.cons(head, tail.append(elem))

    def insert[U >: T](index: Int, elem: U): Lista[U] =
      if (index == 0) Lista.cons(elem, this)
      else Lista.cons(head, tail.insert(index - 1, elem))

    def mapFold[U](fn: T => U): Lista[U] =
      foldLeft(Lista.empty[U])((ls, t) => ls.append(fn(t)))

    def map[U](fn: T => U): Lista[U] =
      if (isEmpty) Lista.empty
      else Lista.cons(fn(head), tail.map(fn))

    def foldLeft[U](identity: U)(fn: (U, T) => U): U = {
      var acc = identity
      var tmp = this
      while (!tmp.isEmpty) {
        acc = fn(acc, tmp.head)
        tmp = tmp.tail
      }
      acc
    }

    def reduce[U >: T](identity: U)(fn: (U, T) => U): U = foldLeft(identity)(fn)

    def foldRight[U](identity: U)(fn: (T, U) => U): U =
      if (isEmpty) identity
      else fn(head, tail.foldRight(identity)(fn))

    def reduceFoldLeft[U >: T](identity: U)(fn: (U, U) => U): U =
      foldLeft(identity)(fn)

    def appendFold[U >: T](elem: U): Lista[U] =
      foldRight(Lista.cons(elem, Lista.empty[U]))((t, ls) => ls.prepend(t))

    def concat[U >: T](other: Lista[U]): Lista[U] =
      if (isEmpty) other
      else Lista.cons(head, tail.concat(other))

    def countFold: Int = foldLeft(0)((i, _) => i + 1)

    def takeFold(n: Int): Lista[T] = {
      val vi: Lista[T] = Lista.empty
      val fn = (ls: Lista[T], t: T) =>
        if (ls.count < n) ls.append(t) else ls
      foldLeft(vi)(fn)
    }

    def dropFold(n: Int): Lista[T] =
      if (n <= 0 || isEmpty) this else tail.dropFold(n - 1)

    def invertFold: Lista[T] = foldLeft(Lista.empty[T])((ls, t) => ls.prepend(t))

    def takeWhileFold(p: T => Boolean): Lista[T] = {
      val vi = (Lista.empty[T], true)
      val ret = foldLeft(vi) { case ((ls, cont), t) =>
        if (!cont) (ls, cont)
        else if (p(t)) (ls.append(t), true)
        else (ls, false)
      }
      ret._1
    }
  }

  case object Empty extends Lista[Nothing] {
    override def head: Nothing = throw new NoSuchElementException("Lista vacía")
    override def tail: Lista[Nothing] = throw new NoSuchElementException("Lista vacía")
    override def isEmpty: Boolean = true
    override def toString: String = "Empty"
    override def count: Int = 0
    override def append[U >: Nothing](elem: U): Lista[U] = Lista.cons(elem, Empty)
  }

  case class Cons[+T](head: T, tail: Lista[T]) extends Lista[T] {
    override def isEmpty: Boolean = false
    override def toString: String = s"[$head, $tail]"
  }

  def empty[T]: Lista[T] = Empty

  def cons[T](head: T, tail: Lista[T]): Lista[T] = Cons(head, tail)

  def apply[T](elems: T*): Lista[T] = {
    var tmp: Lista[T] = empty
    for (elem <- elems.reverse) {
      tmp = cons(elem, tmp)
    }
    tmp
  }

  @tailrec
  def rangeAux(tmp: Lista[Int], start: Int, end: Int): Lista[Int] =
    if (start < end) rangeAux(tmp.prepend(start), start + 1, end) else tmp

  def range(start: Int, end: Int): Lista[Int] =
    rangeAux(empty[Int], start, end).invertFold

  @tailrec
  private def unfoldAux[T](tmp: Lista[T], start: T, fn: T => T, p: T => Boolean): Lista[T] =
    if (p(start)) unfoldAux(tmp.prepend(start), fn(start), fn, p) else tmp

  def unfold[T](start: T, fn: T => T, p: T => Boolean): Lista[T] =
    unfoldAux(empty[T], start, fn, p).invertFold
}