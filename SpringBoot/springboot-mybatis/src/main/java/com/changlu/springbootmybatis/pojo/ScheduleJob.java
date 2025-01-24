package com.changlu.springbootmybatis.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScheduleJob implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer projectId;
    private Integer dtuicTenantId;
    private Integer appType;
    private String jobId;
    private String cycTime;
    private Boolean isDeleted;
    private String flowJobId;
}