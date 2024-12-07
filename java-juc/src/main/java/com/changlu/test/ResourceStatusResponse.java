package com.changlu.test;

import java.util.List;

public class ResourceStatusResponse {

    private String msg;

    private Integer code;

    private DataResponse data;

    public static class DataResponse {

        private Integer concurrency_free_total;

        private String men_free_total;

        private String cpu_free_total;

        private List<SidecarInfo> sidecar_list_info;

        public static class SidecarInfo {
            private String id;
            private String local_ip;
            private Integer concurrency_free;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLocal_ip() {
                return local_ip;
            }

            public void setLocal_ip(String local_ip) {
                this.local_ip = local_ip;
            }

            public Integer getConcurrency_free() {
                return concurrency_free;
            }

            public void setConcurrency_free(Integer concurrency_free) {
                this.concurrency_free = concurrency_free;
            }

            @Override
            public String toString() {
                return "SidecarInfo{" +
                        "id='" + id + '\'' +
                        ", local_ip='" + local_ip + '\'' +
                        ", concurrency_free=" + concurrency_free +
                        '}';
            }
        }

        public List<SidecarInfo> getSidecar_list_info() {
            return sidecar_list_info;
        }

        public void setSidecar_list_info(List<SidecarInfo> sidecar_list_info) {
            this.sidecar_list_info = sidecar_list_info;
        }

        public Integer getConcurrency_free_total() {
            return concurrency_free_total;
        }

        public void setConcurrency_free_total(Integer concurrency_free_total) {
            this.concurrency_free_total = concurrency_free_total;
        }

        public String getMen_free_total() {
            return men_free_total;
        }

        public void setMen_free_total(String men_free_total) {
            this.men_free_total = men_free_total;
        }

        public String getCpu_free_total() {
            return cpu_free_total;
        }

        public void setCpu_free_total(String cpu_free_total) {
            this.cpu_free_total = cpu_free_total;
        }
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public DataResponse getData() {
        return data;
    }

    public void setData(DataResponse data) {
        this.data = data;
    }
}
