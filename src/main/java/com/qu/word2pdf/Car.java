package com.qu.word2pdf;

/**
 * @Description: demo
 * @author: qu
 * @date: 2021.06.18.15:56
 */
public class Car {

    private String evidenceNumber;

    private String plate;

    private String carType;

    private String company;

    private String place;

    private String people;

    private String phone;

    private Integer axle;

    private Float totalWeight;

    private Float limitWeight;

    private Float overWeight;

    private String checkTime;

    private String checkPlace;

    private String direction;

    private String lane;

    private String description;

    public Car() {
        this.evidenceNumber = "acacacacaca1213";
        this.plate ="川A123450";
        this.carType = "大件运输车";
        this.company = "山东省叉叉物流有限公司";
        this.place = "山东省青岛市";
        this.people="王武";
        this.phone = "17358540214";
        this.axle = 3;
        this.totalWeight=70.55f;
        this.limitWeight=49f;
        this.overWeight = 14.45f;
        this.checkTime = "2021年 6月 18日 下午 18:57";
        this.checkPlace = "滕州市某个站点";
        this.direction = "一路向北";
        this.lane = "日落大道";
        this.description="车辆在公路上超限行驶\n" +
                "证据材料附后：\n" +
                "车辆前、后、侧45度现场抓拍图片\n" +
                "\n" +
                "备注：超限吨数=车货总重-（1-10%）-整车限重";
    }

    public String getEvidenceNumber() {
        return evidenceNumber;
    }

    public void setEvidenceNumber(String evidenceNumber) {
        this.evidenceNumber = evidenceNumber;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getAxle() {
        return axle;
    }

    public void setAxle(Integer axle) {
        this.axle = axle;
    }

    public Float getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Float totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Float getLimitWeight() {
        return limitWeight;
    }

    public void setLimitWeight(Float limitWeight) {
        this.limitWeight = limitWeight;
    }

    public Float getOverWeight() {
        return overWeight;
    }

    public void setOverWeight(Float overWeight) {
        this.overWeight = overWeight;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getCheckPlace() {
        return checkPlace;
    }

    public void setCheckPlace(String checkPlace) {
        this.checkPlace = checkPlace;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getLane() {
        return lane;
    }

    public void setLane(String lane) {
        this.lane = lane;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
