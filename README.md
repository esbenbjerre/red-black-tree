# red-black-tree

Efficient immutable red-black trees written in Scala. Adapted from [Okasaki (1998)](#references) and [Germane and Might (2014)](#references).

## Constructing

A `RedBlackTree` can be constructed either by calling the `apply` or `from` method on the companion object. The elements of the tree must have an (implicit) `Ordering`.

## Examples

```scala
val t0 = RedBlackTree(0, 1, 2)
val t1 = RedBlackTree('A', 'B', 'C')
val t2 = RedBlackTree from Range(0, 3)
val t3 = RedBlackTree from List(0x0, 0x1, 0x2)
(t0 + 3).size // 4
t1.foldLeft("")((acc: String, x) => acc + x) // ABC
```

## References
Germane, K. and Might, M. (July 2014). *"Deletion: The curse of the red-black tree"*. In: Journal of Functional Programming 24, pp. 423–433.

Okasaki, C. (1998). *Purely Functional Data Structures*. Cambridge University Press.