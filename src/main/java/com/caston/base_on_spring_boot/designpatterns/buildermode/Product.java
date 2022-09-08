package com.caston.base_on_spring_boot.designpatterns.buildermode;

public class Product {
    private String host;
    private String port;

    private Product(Builder builder) {
        this.host = builder.host;
        this.port = builder.port;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public static class Builder {
        private String host;
        private String port;

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(String port) {
            this.port = port;
            return this;
        }

        public Product build() {
            // 添加自己的生成规则判断
            return new Product(this);
        }
    }
}
