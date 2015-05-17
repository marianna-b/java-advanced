package ru.ifmo.ctddev.bisyarina.uicopyfiles;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicBoolean;

public class CopyWindow extends JFrame {
    JPanel rootPanel;
    JButton buttonCancel;
    JProgressBar progress;
    JLabel currentSpeed;
    JLabel speed;
    JLabel timeEnd;
    JLabel time;
    JPanel infoPanel;
    AtomicBoolean isCanceled;

    void upd(Status status) {
        SwingUtilities.invokeLater(() -> {
            progress.setValue(status.getPercentSize());
            currentSpeed.setText(status.getCurrentSpeed());
            speed.setText(status.getSpeed());
            time.setText(status.getTime());
            timeEnd.setText(status.getTimeLeft());
        });
    }

    public CopyWindow() {
        super("Copy files");
        isCanceled = new AtomicBoolean(false);
        setContentPane(rootPanel);
        setVisible(true);
        progress.setMaximum(100);
        buttonCancel.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                isCanceled.set(true);
                dispose();
            }
        });
        buttonCancel.setText("Cancel");
        pack();
    }
}
