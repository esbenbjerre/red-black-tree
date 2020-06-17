# red-black-tree

Efficient immutable red-black trees written in Scala. Adapted from [Okasaki (1998)](#1) and [Germane and Might (2014)](#1).

## Constructing

A `RedBlackTree` can be constructed either by calling the `apply` or `from` method on the companion object. The tree supports any type with an implicit ordering.

## Functions
```scala
def contains[B >: A](x: B)(implicit ord: Ordering[B]): Boolean
def +[B >: A](x: B)(implicit ord: Ordering[B]): Boolean
def updated[B >: A](x: B)(implicit ord: Ordering[B]): RedBlackTree[B]
def -[B >: A](x: B)(implicit ord: Ordering[B]): Boolean
def deleted[B >: A](x: B)(implicit ord: Ordering[B]): RedBlackTree[A]
def height: Int
def size: Int
def foldLeft[B](z: B, f: (B, A) => B): B
def foldRight[B](z: B, f: (A, B) => B): B
def findLeft(f: A => Boolean): Option[A]
def findRight(f: A => Boolean): Option[A]
def reduceLeft[B >: A](f: (B, A) => B): Option[B]
def reduceRight[B >: A](f: (A, B) => B): Option[B]
def toString: String
```

## Examples

```scala
val t0 = RedBlackTree(0, 1, 2)
val t1 = RedBlackTree('A', 'B', 'C')
val t2 = RedBlackTree from Range(0, 3)
val t3 = RedBlackTree from List(0x0, 0x1, 0x2)
(t0 + 3).size // 4
t1.foldLeft("", (acc: String, x) => acc + x) // ABC
t2.reduceLeft(_ + _) // Some(3)
t3.findRight(_ > 0) // 0x2
```

## References
<a id="germane-matthew-2014">Germane, K. and Might, M. (July 2014)</a>. *"Deletion: The curse of the red-black tree"*. In: Journal of Functional Programming 24, pp. 423–433.

<a id="okasaki-1998">Okasaki, C. (1998)</a>. *Purely Functional Data Structures*. Cambridge University Press.