package com.example.smartherd.classes;
import com.example.smartherd.controllers.LoginController;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
public class CodeTimer {

    private static final int INTERVAL = 5 * 60 * 1000; // 5 minutes in milliseconds
    private static final int MIN_VALUE = 100000; // minimum 6-digit number
    private static final int MAX_VALUE = 999999; // maximum 6-digit number
    private static final Random random = new Random();
    private static int count = 0;
    private int randomNumber;
    private Timer timer = null;
    public void codeTimer(LoginController loginController) {

        if(timer != null){

            System.out.println("i am checked null yes");
            randomNumber = random.nextInt(MAX_VALUE - MIN_VALUE + 1) + MIN_VALUE;
            loginController.setCode(randomNumber);
            System.out.println("the code is : "+ loginController.getCode());
            count = 0;
            timer.cancel();
            timer = null;
            System.out.println("count as been reset to : "+ count);

        }else{
            System.out.println("started successfully");
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (count < 2 ) {
                        randomNumber = random.nextInt(MAX_VALUE - MIN_VALUE + 1) + MIN_VALUE;
                        loginController.setCode(randomNumber);
                        System.out.println("the code is : "+ loginController.getCode());
                        count++;
                    } else {
                        System.out.println("i am cancelling");
                        count=0;
                        timer.cancel();
                    }
                }
            }, 0, INTERVAL);
        }


    }
}
