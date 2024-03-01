package org.kote.common.cache

import cats.Show
import cats.data.{EitherT, OptionT}
import cats.effect.Sync
import cats.implicits.{toFlatMapOps, toFunctorOps, toShow, toTraverseOps}
import org.kote.common.filename.FileName
import org.kote.common.filename.FileName.ToFileName
import org.kote.common.parser.Parser

import java.nio.charset.Charset
import java.nio.file.{Files, Path}
import scala.collection.concurrent
import scala.collection.concurrent.TrieMap
import scala.io.Source
import scala.util.Using

trait Cache[F[_], K, V] {
  def add(key: K, value: V): F[Unit]
  def values: F[List[V]]
  def get(key: K): F[Option[V]]
  def remove(key: K): F[Option[V]]
  def update(key: K, value: V): F[Unit]
}

object Cache {
  private class InMemory[F[_]: Sync, K, V](map: concurrent.Map[K, V]) extends Cache[F, K, V] {
    override def add(key: K, value: V): F[Unit] =
      Sync[F]
        .delay(map += key -> value)

    override def values: F[List[V]] =
      Sync[F].delay(map.values.toList)

    override def get(key: K): F[Option[V]] =
      Sync[F].delay(map.get(key))

    override def remove(key: K): F[Option[V]] =
      Sync[F].delay(map.remove(key))

    override def update(key: K, value: V): F[Unit] =
      Sync[F].delay(map.update(key, value))
  }

  private class OnDisk[F[_]: Sync, K: FileName, V: Show: Parser[Throwable, *]](
      directory: String,
      charset: Charset,
      map: concurrent.Map[K, Path],
  ) extends Cache[F, K, V] {
    override def add(key: K, value: V): F[Unit] =
      for {
        path <- Sync[F].delay(Path.of(s"$directory/${key.getFileName}.data"))
        _ <- Sync[F].delay(map += key -> path)
        res <- serialize(path, charset, value).as(()).rethrowT
      } yield res

    override def values: F[List[V]] =
      map.values.toList.traverse(path => parse(path).rethrowT)

    override def get(key: K): F[Option[V]] =
      (for {
        path <- OptionT(Sync[F].delay(map.get(key)))
        res <- parse(path).toOption
      } yield res).value

    override def remove(key: K): F[Option[V]] =
      (for {
        path <- OptionT(Sync[F].delay(map.get(key)))
        res <- parse(path).toOption
        _ <- OptionT(Sync[F].delay(Option(Files.delete(path))))
      } yield res).value

    override def update(key: K, value: V): F[Unit] =
      (for {
        path <- OptionT(Sync[F].delay(map.get(key)))
        res <- OptionT.liftF(serialize(path, charset, value).rethrowT)
      } yield res).value.as(())

    private def parse(path: Path): EitherT[F, Throwable, V] =
      EitherT(Sync[F].delay(Using(Source.fromFile(path.toFile)) { source =>
        Parser[Throwable, V].parse(source.getLines().to(LazyList)).toTry
      }.flatten.toEither))

    private def serialize(path: Path, charset: Charset, value: V): EitherT[F, Throwable, Unit] =
      EitherT(Sync[F].delay(Using(Files.newBufferedWriter(path, charset)) { writer =>
        writer.write(value.show)
      }.toEither))
  }

  // Аллоцируем изменяемое состояние, поэтому без F снаружи будет не RT
  def ram[F[_]: Sync, K, V]: F[Cache[F, K, V]] =
    Sync[F].delay(new InMemory[F, K, V](new TrieMap[K, V]))

  def disk[F[_]: Sync, K: FileName, V: Show: Parser[Throwable, *]](
      directory: String,
      charset: Charset,
  ): F[Cache[F, K, V]] =
    Sync[F].delay(new OnDisk[F, K, V](directory, charset, new TrieMap[K, Path]))
}
