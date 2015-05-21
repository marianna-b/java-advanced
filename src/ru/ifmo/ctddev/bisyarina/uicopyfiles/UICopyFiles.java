package ru.ifmo.ctddev.bisyarina.uicopyfiles;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicLong;

public class UICopyFiles {
    CopyWindow form;
    Status status;
    AtomicLong currRead;

    void copyFile(File from, File to) {
        try (FileInputStream inputStream = new FileInputStream(from);
             FileOutputStream outputStream = new FileOutputStream(to)) {
            byte[] buf = new byte[4096];
            int readRes;
            while (!form.isCanceled.get() && (readRes = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, readRes);
                currRead.addAndGet(readRes);
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }

    }

    void copy(File from, File to) throws IOException {
        if (from.isDirectory()) {
            if (!to.exists()) {
                Files.createDirectories(to.toPath());
            }
            String files[] = from.list();
            for (String file : files) {
                File srcFile = new File(from, file);
                File destFile = new File(to, file);
                copy(srcFile, destFile);
            }
        } else {
            copyFile(from, to);
        }
    }

    long getFileSize(File from) throws IOException {
        if (from.isDirectory()) {
            long ans = 0;
            String files[] = from.list();
            for (String file : files) {
                ans += getFileSize(new File(from, file));
            }
            return ans;
        } else {
            return from.length();
        }
    }

    UICopyFiles(File from, File to) {
        currRead = new AtomicLong(0);
        try {
            status = new Status(getFileSize(from));
            form = new CopyWindow();

            Runnable update = () -> {
                while (!form.isCanceled.get()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.err.println(e.toString());
                    }
                    status.update(currRead.getAndSet(0));
                    form.upd(status);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.err.println(e.toString());
                }
            };
            Thread thread = new Thread(update);
            thread.start();
            try {
                copy(from, to);
                form.isCanceled.set(true);
                SwingUtilities.invokeLater(() -> form.buttonCancel.setText("OK"));
                thread.join();
            } catch (IOException | InterruptedException e) {
                System.err.println(e.toString());
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }



    public static void main(String[] args) {
        if (args == null) {
            System.err.println();
            return;
        }

        if (args.length < 2) {
            System.err.println("Not enough parameters: " + args.length);
            System.err.println("Need what to copy and where to copy.");
            return;
        }

        File from = new File(args[0]);
        File to = new File(args[1]);
        new UICopyFiles(from, to);
    }
}
