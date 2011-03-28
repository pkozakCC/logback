package ch.qos.logback.core.time;

import ch.qos.logback.core.contention.RunnableWithCounterAndDone;
import ch.qos.logback.core.contention.ThreadedThroughputCalculator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Ignore;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;


@Ignore
public class DateFormattingThroughputTest {


  ThreadedThroughputCalculator t;

  static int THREAD_COUNT = 30;
  static long OVERALL_DURATION_IN_MILLIS = 2000;

  static String PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

  @Test
  public void sdf() throws InterruptedException {
    ThreadedThroughputCalculator tp = new ThreadedThroughputCalculator(
            OVERALL_DURATION_IN_MILLIS);

    SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);
    RunnableWithCounterAndDone[] sdfRunnables = buildSDFRunnables(sdf);
    tp.execute(sdfRunnables);
    tp.printThroughput("SDF with synchronization:   ", true);
  }

  RunnableWithCounterAndDone[] buildSDFRunnables(SimpleDateFormat sdf) {
    RunnableWithCounterAndDone[] runnables = new RunnableWithCounterAndDone[THREAD_COUNT];
    for (int i = 0; i < THREAD_COUNT; i++) {
      runnables[i] = new SDFRunnabable(sdf);
    }
    return runnables;
  }

  @Test
  public void yoda() throws InterruptedException {
    ThreadedThroughputCalculator tp = new ThreadedThroughputCalculator(
            OVERALL_DURATION_IN_MILLIS);

    DateTimeFormatter fmt = DateTimeFormat.forPattern(PATTERN);
    RunnableWithCounterAndDone[] yodaRunnables = buildYodaRunnables(fmt);
    tp.execute(yodaRunnables);
    tp.printThroughput("Yoda:   ", true);
  }

  RunnableWithCounterAndDone[] buildYodaRunnables(DateTimeFormatter fmt) {
    RunnableWithCounterAndDone[] runnables = new RunnableWithCounterAndDone[THREAD_COUNT];
    for (int i = 0; i < THREAD_COUNT; i++) {
      runnables[i] = new YodaTimeRunnable(fmt);
    }
    return runnables;
  }


  class SDFRunnabable extends RunnableWithCounterAndDone {

    SimpleDateFormat sdf;

    SDFRunnabable(SimpleDateFormat sdf) {
      this.sdf = sdf;
    }

    public void run() {
      while (!done) {
        synchronized (sdf) {
          sdf.format(new Date());
        }
        counter++;
      }
    }
  }

  class YodaTimeRunnable extends RunnableWithCounterAndDone {

    DateTimeFormatter yodaDTFt;

    YodaTimeRunnable(DateTimeFormatter dtf) {
      this.yodaDTFt = dtf;
    }

    public void run() {
      while (!done) {
        long now = System.currentTimeMillis();
        yodaDTFt.print(now);
        counter++;
      }
    }
  }

}