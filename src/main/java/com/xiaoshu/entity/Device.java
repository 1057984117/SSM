package com.xiaoshu.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

public class Device implements Serializable {
    @Id
    @Column(name = "deviceId")
    private Integer deviceid;

    @Column(name = "devicetypeId")
    private Integer devicetypeid;

    private String devicename;

    private String deviceram;

    private String color;

    private Double price;

    private Integer status;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    
    @Transient
    private Devicetype devicetype;
    
    @Transient
    private String devicename1;
    
    @Transient
    private Integer devicetypeid1;
    
    @Transient
    private String bmm;
    
    @Transient
    private Integer cmm;
    
    public String getBmm() {
		return bmm;
	}

	public void setBmm(String bmm) {
		this.bmm = bmm;
	}

	public Integer getCmm() {
		return cmm;
	}

	public void setCmm(Integer cmm) {
		this.cmm = cmm;
	}

	public String getDevicename1() {
		return devicename1;
	}

	public void setDevicename1(String devicename1) {
		this.devicename1 = devicename1;
	}

	public Integer getDevicetypeid1() {
		return devicetypeid1;
	}

	public void setDevicetypeid1(Integer devicetypeid1) {
		this.devicetypeid1 = devicetypeid1;
	}

	public Devicetype getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(Devicetype devicetype) {
		this.devicetype = devicetype;
	}

	private static final long serialVersionUID = 1L;

    /**
     * @return deviceId
     */
    public Integer getDeviceid() {
        return deviceid;
    }

    /**
     * @param deviceid
     */
    public void setDeviceid(Integer deviceid) {
        this.deviceid = deviceid;
    }

    /**
     * @return devicetypeId
     */
    public Integer getDevicetypeid() {
        return devicetypeid;
    }

    /**
     * @param devicetypeid
     */
    public void setDevicetypeid(Integer devicetypeid) {
        this.devicetypeid = devicetypeid;
    }

    /**
     * @return devicename
     */
    public String getDevicename() {
        return devicename;
    }

    /**
     * @param devicename
     */
    public void setDevicename(String devicename) {
        this.devicename = devicename == null ? null : devicename.trim();
    }

    /**
     * @return deviceram
     */
    public String getDeviceram() {
        return deviceram;
    }

    /**
     * @param deviceram
     */
    public void setDeviceram(String deviceram) {
        this.deviceram = deviceram == null ? null : deviceram.trim();
    }

    /**
     * @return color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color
     */
    public void setColor(String color) {
        this.color = color == null ? null : color.trim();
    }

    /**
     * @return price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * @return status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return createtime
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * @param createtime
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", deviceid=").append(deviceid);
        sb.append(", devicetypeid=").append(devicetypeid);
        sb.append(", devicename=").append(devicename);
        sb.append(", deviceram=").append(deviceram);
        sb.append(", color=").append(color);
        sb.append(", price=").append(price);
        sb.append(", status=").append(status);
        sb.append(", createtime=").append(createtime);
        sb.append("]");
        return sb.toString();
    }
}