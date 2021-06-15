package com.qu.word2pdf;

public class Employee {
    private String num;
    private String master;
    private String carNum;
    private String color;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getCarNum() {
        return carNum;
    }

    public void setCarNum(String carNum) {
        this.carNum = carNum;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Employee(String num, String master, String carNum, String color) {
        this.num = num;
        this.master = master;
        this.carNum = carNum;
        this.color = color;
    }
}
