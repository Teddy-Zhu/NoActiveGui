package com.v2dawn.noactivegui.utils;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class SuTool {

    public static boolean mkdir(String path) {
        return executeCmd("mkdir " + path);
    }

    public static boolean createFile(String file) {
        return executeCmd("touch " + file);
    }

    public static boolean existDir(String file) {
        return exist(file, "d");
    }

    public static boolean copyFile(String source, String destFile) {
        return executeCmd("cp -f " + source + " " + destFile);
    }

    public static boolean createLink(String source, String destFile) {
        return executeCmd("ln -s " + source + " " + destFile);
    }

    public static boolean setFileRead(String file) {
        return executeCmd("chmod +r " + file);
    }

    public static boolean existFile(String file) {
        return exist(file, "f");
    }

    public static boolean exist(String file, String mark) {
        return executeCmd("[ -" + mark + " \"" + file + "\" ]");
    }

    public static boolean writeFile(String file, String content) {
        return writeFile(file, content, false);
    }

    private static boolean writeFile(String file, String content, Boolean append) {
        return executeCmd("echo '" + content + "' " + (append ? ">>" : ">") + " " + file);
    }

    private static boolean formatResultStr(String result) {
        String[] resultArr = result.split(System.lineSeparator());
        return resultArr.length > 0 && "0".equals(resultArr[resultArr.length - 1]);
    }

    public static Set<String> readFile(String file) {
        return Observable.create((ObservableOnSubscribe<Set<String>>) emitter -> {
                    emitter.onNext(_readFile(file));
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .blockingFirst();
    }

    public static Set<String> _readFile(String file) {

        String result = runCmdWithShardProcessWithResponse("cat " + file + "; echo $?" + END_STR);

        String[] resultArr = result.split(System.lineSeparator());
        Set<String> set = new HashSet<>();

        boolean ret = formatResultStr(resultArr[resultArr.length - 1]);
        if (ret) {
            for (int i = 0; i < resultArr.length - 1; i++) {
                String line = resultArr[i];
                if ("".equals(line.trim()) || line.trim().startsWith("#")) continue;
                set.add(line);
            }
        }
        return set;
    }


    private static boolean executeCmd(String cmd) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                    Boolean ret = _executeCmd(cmd);
                    emitter.onNext(ret);
                    emitter.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .blockingFirst();

    }

    private static boolean _executeCmd(String cmd) {
        String ret = runCmdWithShardProcessWithResponse(cmd + "; echo $?" + END_STR);
        return formatResultStr(ret);
    }

    public static int getPid(Process p) {
        int pid = -1;

        try {
            Field f = p.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            pid = f.getInt(p);
            f.setAccessible(false);
        } catch (Throwable e) {
            pid = -1;
        }
        return pid;
    }

    public static boolean removeFile(String filePath) {
        return executeCmd("rm -f " + filePath);
    }

    static Process holderProcess;
    static DataOutputStream os;
    static BufferedReader bufferedReader;
    public static final String END_STR = "#NOA#";

    public static void createSuProcess() {
        try {
            holderProcess.destroy();
            IoUtil.close(os);
            IoUtil.close(bufferedReader);
        } catch (Exception e) {
            //ignore
        }
        try {

            holderProcess = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(holderProcess.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(holderProcess.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public static void requireSharedProcess() {
        if (holderProcess == null || !holderProcess.isAlive()) {
            createSuProcess();
        }
    }

    public static String runCmdWithShardProcessWithResponse(String cmd) {
        requireSharedProcess();
        String ret = "";
        try {
            os.write(cmd.getBytes());
            os.writeBytes(System.lineSeparator());
            os.flush();
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                line = bufferedReader.readLine();
                if (line.endsWith(END_STR)) {
                    line = StrUtil.removeSuffix(line, END_STR) + System.lineSeparator();
                    stringBuilder.append(line).append(System.lineSeparator());
                    break;
                } else {
                    stringBuilder.append(line).append(System.lineSeparator());
                }
            }
            ret = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return ret;
    }

    public static void releaseSharedProcess() {
        IoUtil.close(bufferedReader);
        IoUtil.close(os);
        if (holderProcess != null) {
            holderProcess.destroy();
            holderProcess = null;
        }
    }

    public static void main(String[] args) {

    }
}
