
package com.project.enjoyit.entity;

import java.util.List;

import com.baidu.location.Poi;

public class MyLocation {
	private String time;//时间
	private int locType;//获得erro code 得知定位现状 GPS或网络定位
	private double latitude;//获得纬度  重要！！
	private double longitude;//获得经度  重要！！
	private float radius;//定位半径
	private String countryCode;//国家代码
	private String country;//国家
	private String province;//省份
	private String cityCode;//城市代码
	private String city;//城市
	private String district;//地区
	private String streetNumber;//街道代码
	private String street;//街道
	private String addrStr;//详细地址   重要！！
	private String locationDescribe;//地理位置描述
	private float direction;//方向
	private List<Poi> poiList;//POI
	private String buildingID;
	private String buildingName;//
	//限gps
	private float speed;//获得速度 单位：km/h
	private int satelliteNumber;//卫星数量
	private double altitude;//海拔  单位：米
	//限网络
	private int operators;// 运营商信息
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getLocType() {
		return locType;
	}
	public void setLocType(int locType) {
		this.locType = locType;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public float getRadius() {
		return radius;
	}
	public void setRadius(float radius) {
		this.radius = radius;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getAddrStr() {
		return addrStr;
	}
	public void setAddrStr(String addrStr) {
		this.addrStr = addrStr;
	}
	public String getLocationDescribe() {
		return locationDescribe;
	}
	public void setLocationDescribe(String locationDescribe) {
		this.locationDescribe = locationDescribe;
	}
	public float getDirection() {
		return direction;
	}
	public void setDirection(float direction) {
		this.direction = direction;
	}
	public List<Poi> getPoiList() {
		return poiList;
	}
	public void setPoiList(List<Poi> poiList) {
		this.poiList = poiList;
	}
	public String getBuildingID() {
		return buildingID;
	}
	public void setBuildingID(String buildingID) {
		this.buildingID = buildingID;
	}
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	public int getSatelliteNumber() {
		return satelliteNumber;
	}
	public void setSatelliteNumber(int satelliteNumber) {
		this.satelliteNumber = satelliteNumber;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public int getOperators() {
		return operators;
	}
	public void setOperators(int operators) {
		this.operators = operators;
	}
	
	
	
}
/***  locType
 *61 ： GPS定位结果，GPS定位成功。
 *62 ： 无法获取有效定位依据，定位失败，请检查运营商网络或者wifi网络是否正常开启，尝试重新请求定位。
 *63 ： 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。
 *65 ： 定位缓存的结果。
 *66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。
 *67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。
 *68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
 *161： 网络定位结果，网络定位定位成功。
 *162： 请求串密文解析失败。
 *167： 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。
 *502： key参数错误，请按照说明文档重新申请KEY。
 *505： key不存在或者非法，请按照说明文档重新申请KEY。
 *601： key服务被开发者自己禁用，请按照说明文档重新申请KEY。
 *602： key mcode不匹配，您的ak配置过程中安全码设置有问题，请确保：sha1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请KEY。
 *501～700：key验证失败，请按照说明文档重新申请KEY。
 */