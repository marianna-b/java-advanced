package ru.ifmo.ctddev.bisyarina.uicopyfiles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.atomic.AtomicBoolean;

class CopyWindow extends JFrame {
    JPanel rootPanel, infoPanel;
    JButton buttonCancel;
    JProgressBar progress;
    JLabel currentSpeed, speed, timeEnd, time, timeSpent, averSpeed, timeLeft, cuSpeed;
    JLabel labels[] = {currentSpeed, speed, timeEnd, time, timeSpent, averSpeed, timeLeft, cuSpeed};
    AtomicBoolean isCanceled;
    Timer timer;

    void upd(Status status) {
        progress.setValue(status.getPercentSize());
        currentSpeed.setText(status.getCurrentSpeed());
        speed.setText(status.getSpeed());
        time.setText(status.getTime());
        timeEnd.setText(status.getTimeLeft());
        resizeLabels();
    }

    CopyWindow() {
        super("Copy files");
        isCanceled = new AtomicBoolean(false);
        setContentPane(rootPanel);
        setVisible(true);

        progress.setMaximum(100);
        buttonCancel.setAction(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                isCanceled.set(true);
                timer.stop();
                dispose();
            }
        });
        buttonCancel.setText("Cancel");
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                resizeLabels();
            }

            @Override
            public void componentMoved(ComponentEvent componentEvent) {
            }

            @Override
            public void componentShown(ComponentEvent componentEvent) {
            }

            @Override
            public void componentHidden(ComponentEvent componentEvent) {
            }
        });
        resizeLabels();
        pack();
    }

    void resizeLabels() {
        int stringWidth = 0;
        for (JLabel label : labels) {
            stringWidth = Math.max(label.getFontMetrics(label.getFont()).stringWidth(label.getText()), stringWidth);
        }
        int componentWidth = infoPanel.getWidth() / 2;

        Font font = averSpeed.getFont();
        double widthRatio = (double) componentWidth / (double) stringWidth;
        int newFontSize = (int) (font.getSize() * widthRatio);

        for (JLabel label : labels) {
            label.setFont(new Font(font.getName(), Font.PLAIN, newFontSize));
        }
    }
}
