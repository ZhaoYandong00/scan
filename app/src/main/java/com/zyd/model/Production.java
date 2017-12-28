package com.zyd.model;

/**
 * 产品类
 * Created by Administrator on 2017/12/26.
 */
public class Production {
    /**
     * id
     */
    private Long id;
    /**
     * 名字
     */
    private String name;
    /**
     * 规格型号
     */
    private String specification;
    /**
     * 生产编号
     */
    private String productNumber;
    /**
     * 生产者
     */
    private String producer;
    /**
     * 生产日期
     */
    private String productionDate;
    /**
     * 检验者
     */
    private String inspector;
    /**
     * 备注
     */
    private String remark;

    /**
     * 查询生产日期终点
     */
    private String endProductionDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(String productionDate) {
        this.productionDate = productionDate;
    }

    public String getInspector() {
        return inspector;
    }

    public void setInspector(String inspector) {
        this.inspector = inspector;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEndProductionDate() {
        return endProductionDate;
    }

    public void setEndProductionDate(String endProductionDate) {
        this.endProductionDate = endProductionDate;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name=");
        sb.append(name);
        sb.append("&specification=");
        sb.append(specification);
        sb.append("&productNumber=");
        sb.append(productNumber);
        sb.append("&productionDate=");
        sb.append(productionDate);
        sb.append("&producer=");
        sb.append(producer);
        sb.append("&inspector=");
        sb.append(inspector);
        return sb.toString();
    }
}
