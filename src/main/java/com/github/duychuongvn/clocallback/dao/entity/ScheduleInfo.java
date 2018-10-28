package com.github.duychuongvn.clocallback.dao.entity;

import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

@Document
@TypeAlias("ScheduleInfo")
@EnableMongoAuditing
public class ScheduleInfo implements Serializable{
    @Id
    private String id;
    private Integer timestamp;
    private String contractAddress;
    private BigInteger gasLimit;
    private Boolean finished;
    private String callbackHash;
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();
    @LastModifiedDate
    private LocalDateTime updatedAt = LocalDateTime.now();
    @Version
    private Integer versionNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduleInfo that = (ScheduleInfo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ScheduleInfo{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", contractAddress='" + contractAddress + '\'' +
                ", gasLimit=" + gasLimit +
                ", finished=" + finished +
                ", callbackHash='" + callbackHash + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", versionNo=" + versionNo +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getVersionNo() {
        return versionNo;
    }

    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    public String getCallbackHash() {
        return callbackHash;
    }

    public void setCallbackHash(String callbackHash) {
        this.callbackHash = callbackHash;
    }
}
