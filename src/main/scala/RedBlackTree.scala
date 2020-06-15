sealed trait Color

case object R extends Color

case object B extends Color

case object BB extends Color

case object E extends RedBlackTree[Nothing]

case object EE extends RedBlackTree[Nothing]

case class T[A](color: Color, left: RedBlackTree[A], element: A, right: RedBlackTree[A]) extends RedBlackTree[A]

sealed trait RedBlackTree[+A] {

  def contains[B >: A](x: B)(implicit ord: Ordering[B]): Boolean = {
    import ord.mkOrderingOps
    this match {
      case E => false
      case T(_, a, y, _) if x < y => a.contains(x)
      case T(_, _, y, b) if x > y => b.contains(x)
      case _ => true
    }
  }

  def insert[B >: A](x: B)(implicit ord: Ordering[B]): RedBlackTree[B] = {

    import ord.mkOrderingOps

    def lbalance(t: RedBlackTree[B]): RedBlackTree[B] = t match {
      case T(B, T(R, T(R, a, x, b), y, c), z, d) => T(R, T(B, a, x, b), y, T(B, c, z, d))
      case T(B, T(R, a, x, T(R, b, y, c)), z, d) => T(R, T(B, a, x, b), y, T(B, c, z, d))
      case _ => t
    }

    def rbalance(t: RedBlackTree[B]): RedBlackTree[B] = t match {
      case T(B, a, x, T(R, T(R, b, y, c), z, d)) => T(R, T(B, a, x, b), y, T(B, c, z, d))
      case T(B, a, x, T(R, b, y, T(R, c, z, d))) => T(R, T(B, a, x, b), y, T(B, c, z, d))
      case _ => t
    }

    def ins(x: B, t: RedBlackTree[A]): RedBlackTree[B] = {
      t match {
        case E => T(R, E, x, E)
        case T(color, a, y, b) if x < y => lbalance(T(color, ins(x, a), y, b))
        case T(color, a, y, b) if x > y => rbalance(T(color, a, y, ins(x, b)))
        case _ => t
      }
    }

    def blacken(t: RedBlackTree[B]): RedBlackTree[B] = t match {
      case T(R, T(R, a, x, b), y, c) => T(B, T(R, a, x, b), y, c)
      case T(R, a, x, T(R, b, y, c)) => T(B, a, x, T(R, b, y, c))
      case _ => t
    }

    blacken(ins(x, this))

  }

  def delete[B >: A](x: B)(implicit ord: Ordering[B]): RedBlackTree[A] = {

    import ord.mkOrderingOps

    def balance(t: RedBlackTree[A]): RedBlackTree[A] = t match {
      case T(BB, a, x, T(R, T(R, b, y, c), z, d)) => T(B, T(B, a, x, b), y, T(B, c, z, d))
      case T(BB, T(R, a, x, T(R, b, y, c)), z, d) => T(B, T(B, a, x, b), y, T(B, c, z, d))
      case _ => t
    }

    def redden(t: RedBlackTree[A]): RedBlackTree[A] = t match {
      case T(B, T(B, a, x, b), y, T(B, c, z, d)) => T(R, T(B, a, x, b), y, T(B, c, z, d))
      case EE => E
      case _ => t
    }

    def rotate(t: RedBlackTree[A]): RedBlackTree[A] = t match {
      case T(R, T(BB, a, x, b), y, T(B, c, z, d)) => balance(T(B, T(R, T(B, a, x, b), y, c), z, d))
      case T(R, EE, y, T(B, c, z, d)) => balance(T(B, T(R, E, y, c), z, d))
      case T(R, T(B, a, x, b), y, T(BB, c, z, d)) => balance(T(B, a, x, T(R, b, y, T(B, c, z, d))))
      case T(R, T(B, a, x, b), y, EE) => balance(T(B, a, x, T(R, b, y, E)))
      case T(B, T(BB, a, x, b), y, T(B, c, z, d)) => balance(T(BB, T(R, T(B, a, x, b), y, c), z, d))
      case T(B, EE, y, T(B, c, z, d)) => balance(T(BB, T(R, E, y, c), z, d))
      case T(B, T(B, a, x, b), y, T(BB, c, z, d)) => balance(T(BB, a, x, T(R, b, y, T(B, c, z, d))))
      case T(B, T(B, a, x, b), y, EE) => balance(T(BB, a, x, T(R, b, y, E)))
      case T(B, T(BB, a, w, b), x, T(R, T(B, c, y, d), z, e)) => T(B, balance(T(B, T(R, T(B, a, w, b), x, c), y, d)), z, e)
      case T(B, EE, x, T(R, T(B, c, y, d), z, e)) => T(B, balance(T(B, T(R, E, x, c), y, d)), z, e)
      case T(B, T(R, a, w, T(B, b, x, c)), y, T(BB, d, z, e)) => T(B, a, w, balance(T(B, b, x, T(R, c, y, T(B, d, z, e)))))
      case T(B, T(R, a, w, T(B, b, x, c)), y, EE) => T(B, a, w, balance(T(B, b, x, T(R, c, y, E))))
      case _ => t
    }

    def minDel(t: RedBlackTree[A]): (A, RedBlackTree[A]) = t match {
      case T(R, E, x, E) => (x, E)
      case T(B, E, x, E) => (x, EE)
      case T(B, E, x, T(R, E, y, E)) => (x, T(B, E, y, E))
      case T(c, a, x, b) =>
        val (z, e) = minDel(a)
        (z, rotate(T(c, e, x, b)))
    }

    def del(t: RedBlackTree[A]): RedBlackTree[A] = t match {
      case E => E
      case T(R, E, y, E) if x == y => E
      case T(R, E, y, E) if x != y => t
      case T(B, E, y, E) if x == y => EE
      case T(B, E, y, E) if x != y => t
      case T(B, T(R, E, y, E), z, E) if x < z => T(B, del(T(R, E, y, E)), z, E)
      case T(B, T(R, E, y, E), z, E) if x == z => T(B, E, y, E)
      case T(B, T(R, E, y, E), z, E) if x > z => T(B, T(R, E, y, E), z, E)
      case T(c, a, y, b) if x < y => rotate(T(c, del(a), y, b))
      case T(c, a, y, b) if x == y =>
        val (z, e) = minDel(b)
        rotate(T(c, a, z, e))
      case T(c, a, y, b) if x > y => rotate(T(c, a, y, del(b)))
    }

    redden(del(this))

  }

  def height: Int = this match {
    case T(_, a, _, b) => 1 + (a.height max b.height)
    case _ => 0
  }

  def size: Int = this match {
    case T(_, a, _, b) => 1 + a.size + b.size
    case _ => 0
  }

  final def foldLeft[B](z: B, f: (B, A) => B): B = this match {
    case T(_, a, x, b) => b.foldLeft(f(a.foldLeft(z, f), x), f)
    case _ => z
  }

  def foldRight[B](z: B, f: (A, B) => B): B = this match {
    case T(_, a, x, b) => a.foldRight(f(x, b.foldRight(z, f)), f)
    case _ => z
  }

  def findLeft(f: A => Boolean): Option[A] = this match {
    case T(_, a, x, b) => a.findLeft(f) match {
      case None => if (f(x)) Some(x) else b.findLeft(f)
      case Some(y) => Some(y)
    }
    case _ => None
  }

  def findRight(f: A => Boolean): Option[A] = this match {
    case T(_, a, x, b) => b.findRight(f) match {
      case None => if (f(x)) Some(x) else a.findRight(f)
      case Some(y) => Some(y)
    }
    case _ => None
  }

  def reduceLeft[B >: A](f: (B, A) => B): Option[B] = foldLeft(None, (x: Option[B], y: A) => x match {
    case Some(x) => Some(f(x, y))
    case None => Some(y)
  })

  def reduceRight[B >: A](f: (A, B) => B): Option[B] = foldRight(None, (x: A, y: Option[B]) => y match {
    case Some(y) => Some(f(x, y))
    case None => Some(x)
  })

}

object RedBlackTree {

  def apply[A](elems: A*)(implicit ord: Ordering[A]): RedBlackTree[A] = elems.foldLeft(E: RedBlackTree[A])(_.insert(_))

  def from[A](it: IterableOnce[A])(implicit ord: Ordering[A]): RedBlackTree[A] = it.iterator.foldLeft(E: RedBlackTree[A])(_.insert(_))

}