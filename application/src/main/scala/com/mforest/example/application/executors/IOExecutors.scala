package com.mforest.example.application.executors

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{Executors, ThreadFactory}

import cats.Functor.ops.toAllFunctorOps
import cats.effect.{Resource, SyncIO}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

object IOExecutors {

  private val poolSize: Int = {
    sys.env
      .get("MAIN_EXECUTOR_POOL_SIZE")
      .map(_.toInt)
      .getOrElse(Runtime.getRuntime.availableProcessors())
  }

  def initializeNonBlocking: Resource[SyncIO, ExecutionContext] = {
    Resource.make(context)(shutdown)
  }

  private def context: SyncIO[ExecutionContextExecutorService] = SyncIO.suspend {
    Slf4jLogger
      .create[SyncIO]
      .flatMap(_.info(s"Starting non blocking pool with size: $poolSize"))
      .as {
        ExecutionContext.fromExecutorService(
          Executors.newFixedThreadPool(poolSize, threadFactory)
        )
      }
  }

  private def shutdown(ex: ExecutionContextExecutorService): SyncIO[Unit] = SyncIO.suspend {
    Slf4jLogger
      .create[SyncIO]
      .flatMap(_.info(s"Closing non blocking pool with size: $poolSize"))
      .as(ex.shutdown())
  }

  private def threadFactory: ThreadFactory = new ThreadFactory {

    val ctr = new AtomicInteger(0)

    override def newThread(r: Runnable): Thread = {
      val back = new Thread(r, s"non-blocking-${ctr.getAndIncrement()}")
      back.setDaemon(true)
      back
    }
  }
}
