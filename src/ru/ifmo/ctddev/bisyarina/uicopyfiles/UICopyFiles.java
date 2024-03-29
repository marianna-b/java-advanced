package ru.ifmo.ctddev.bisyarina.uicopyfiles;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class provides functionality for file and directory copying
 */

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
            form.timer = new Timer(1000, a -> {
                if (!form.isCanceled.get()) {
                    status.update(currRead.getAndSet(0));
                    form.upd(status);
                }
            });
            SwingUtilities.invokeLater(form.timer::start);
            try {
                copy(from, to);
                SwingUtilities.invokeLater(form.timer::stop);
                form.isCanceled.set(true);
                SwingUtilities.invokeLater(() -> {
                    form.buttonCancel.setText("OK");
                    form.progress.setValue(100);
                });
            } catch (IOException e) {
                System.err.println(e.toString());
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }


    /**
     * Starts copying from src to dst
     * @param args [src, dst] file paths
     */
    public static void main(String[] args) {
        if (args == null) {
            System.err.println("Null args array");
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
