package com.xander.performance;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class PERF {

  static String TAG = "pTool";

  public static class Builder {
    /**
     * logLevel ，设置可以打印的 log 等级
     */
    int logLevel = Log.DEBUG;
    /**
     * 是否开启检测 UI 线程
     */
    boolean mCheckUI = false;
    /**
     * UI 线程的检测触发时间间隔，超过时间间隔，会被认为发生了 block
     */
    long mUIBlockIntervalTime = Config.UI_BLOCK_INTERVAL_TIME;
    /**
     * 检测线程的 start 方法调用栈
     */
    boolean mCheckThread = false;
    /**
     * 是否检测 fps
     */
    boolean mCheckFPS = false;
    /**
     * 是否需要检测 ipc， 也就是进程间通讯
     */
    boolean mCheckIPC = false;

    IssueSupplier issueSupplier = null;

    /**
     * 上下文，用于获取保存文件夹路径
     */
    @Deprecated
    Context appContext = null;

    String globalTag = TAG;

    public Builder checkUI(boolean check) {
      return checkUI(check, Config.UI_BLOCK_INTERVAL_TIME);
    }

    public Builder checkUI(boolean check, long blockIntervalTime) {
      mCheckUI = check;
      mUIBlockIntervalTime = blockIntervalTime;
      return this;
    }

    public Builder checkThread(boolean check) {
      mCheckThread = check;
      return this;
    }

    public Builder checkFps(boolean check) {
      mCheckFPS = check;
      return this;
    }

    public Builder checkIPC(boolean check) {
      mCheckIPC = check;
      return this;
    }

    public Builder globalTag(String tag) {
      globalTag = tag;
      return this;
    }

    public Builder issueSupplier(IssueSupplier supplier) {
      issueSupplier = supplier;
      return this;
    }

    public Builder logLevel(int level) {
      logLevel = level;
      return this;
    }

    public Builder build() {
      return this;
    }

  }

  public interface IssueSupplier {
    /**
     * 最大的磁盘缓存空间
     *
     * @return
     */
    long maxCacheSize();

    /**
     * 缓存根目录
     *
     * @return
     */
    File cacheRootDir();

    /**
     * 开始上传
     *
     * @param issueFile
     * @return true 表示上传成功 false 表示失败
     */
    boolean upLoad(File issueFile);
  }

  public static void init(Builder builder) {
    if (builder == null) {
      builder = new Builder();
    }
    xLog.setLogLevel(builder.logLevel);
    // Logger.setLogLevel(builder.logLevel);
    TAG = builder.globalTag;
    ThreadTool.resetTag(TAG);
    DumpTool.resetTag(TAG);
    FPSTool.resetTag(TAG);
    IPCTool.resetTag(TAG);
    UIBlockTool.resetTag(TAG);
    PerformanceHandler.resetTag(TAG);
    Issue.resetTag(TAG);
    Issue.init(builder.issueSupplier);
    if (builder.mCheckThread) {
      ThreadTool.init();
    }
    if (builder.mCheckUI) {
      Config.UI_BLOCK_INTERVAL_TIME = builder.mUIBlockIntervalTime;
      UIBlockTool.start();
    }
    if (builder.mCheckFPS) {
      FPSTool.start();
    }
    if (builder.mCheckIPC) {
      IPCTool.start();
    }
  }

}