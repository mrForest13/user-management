package com.mforest.example.core.executors

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.{LinkedBlockingQueue, SynchronousQueue, ThreadFactory, ThreadPoolExecutor}

import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

object ExecutorFactory {

  def fixedThreadPool(size: Int, prefix: String, timeout: Duration): ExecutionContextExecutorService = {
    ExecutionContext.fromExecutorService(
      new ThreadPoolExecutor(
        size,
        size,
        timeout.length,
        timeout.unit,
        new LinkedBlockingQueue(),
        threadFactory(prefix)
      )
    )
  }

  def newCachedThreadPool(size: Int, prefix: String, timeout: Duration): ExecutionContextExecutorService = {
    ExecutionContext.fromExecutorService(
      new ThreadPoolExecutor(
        size,
        Int.MaxValue,
        timeout.length,
        timeout.unit,
        new SynchronousQueue(),
        threadFactory(prefix)
      )
    )
  }

  private def threadFactory(prefix: String): ThreadFactory = new ThreadFactory {

    val ctr = new AtomicInteger(0)

    override def newThread(r: Runnable): Thread = {
      val back = new Thread(r, s"$prefix-${ctr.getAndIncrement()}")
      back.setDaemon(true)
      back
    }
  }
}
