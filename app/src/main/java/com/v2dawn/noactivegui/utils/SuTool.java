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

    public static boolean existFile(String file) {
        return exist(file, "f");
    }

    public static boolean writeFile(String file, String content) {
        return writeFile(file, content, false);
    }

    private static boolean writeFile(String file, String content, Boolean append) {
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            emitter.onNext(_writeFile(file, content, append));
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .blockingFirst();
    }

    private static boolean _writeFile(String file, String content, Boolean append) {
        Boolean ret = false;
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.write(("echo '" + content + "' " + (append ? ">>" : ">") + " " + file).getBytes());
            os.writeBytes("\n");
            os.flush();
            os.close();
            os = null;
            int result = process.waitFor();
            if (result != 0) {
                int pid = getPid(process);
                if (pid != 0) {
                    try {
                        android.os.Process.killProcess(pid);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                ret = false;
            } else {
                ret = true;
            }
            process.destroy();
            process = null;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static Set<String> _readFile(String file) {
        return Observable.create((ObservableOnSubscribe<Set<String>>) emitter -> {
            emitter.onNext(_readFile(file));
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .blockingFirst();
    }

    public static Set<String> readFile(String file) {
        Set<String> set = new HashSet<>();
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.write(("cat " + file).getBytes());
            os.writeBytes("\n");
            os.flush();
            os.close();
            process.waitFor();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if ("".equals(line.trim()) || line.trim().startsWith("#")) continue;
                set.add(line.trim());
            }
            bufferedReader.close();

            process.destroy();
            process = null;
        } catch (FileNotFoundException fileNotFoundException) {
            Log.i(file + " file not found");
        } catch (IOException ioException) {
            Log.i(file + " file read filed");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return set;
    }

    public static boolean exist(String file, String mark) {
        return executeCmd("[ -" + mark + " \"" + file + "\" ]; exit $?");
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
        Boolean ret = false;
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.flush();
            os.close();
            os = null;
            int result = process.waitFor();
//            if (result != 0) {
////                int pid = getPid(process);
////                if (pid != 0) {
////                    try {
////                        android.os.Process.killProcess(pid);
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                }
//                ret = false;
//            } else {
//                ret = true;
//            }
            ret = result == 0;
            process.destroy();
            process = null;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return ret;
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
    static Process testprocess;
    static DataOutputStream testos;
    public static void createSuProcess() {
        try {
            testprocess = Runtime.getRuntime().exec("su");
            testos = new DataOutputStream(testprocess.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("NewApi")
    public static void adapterProcess() {
        if (testprocess == null || !testprocess.isAlive()) {
            createSuProcess();
        }
    }

    public static boolean testKeepProcess(String cmd) {
        Boolean ret = false;
        try {
            testos.write(cmd.getBytes());
            testos.writeBytes("\n");
            testos.flush();
            int result = testprocess.waitFor();
//            if (result != 0) {
////                int pid = getPid(process);
////                if (pid != 0) {
////                    try {
////                        android.os.Process.killProcess(pid);
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                }
//                ret = false;
//            } else {
//                ret = true;
//            }
            ret = result == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return ret;
    }

    public static void main(String[] args) {

    }
}
