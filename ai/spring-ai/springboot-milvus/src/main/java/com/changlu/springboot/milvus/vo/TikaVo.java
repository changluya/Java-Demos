package com.changlu.springboot.milvus.vo;
 
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
 
import java.io.Serializable;
import java.util.List;
 
/**
 * @description  tika返回对象
 * @author changlu
 * @date 2025/6/7 17:06
 */
@Accessors(chain = true)
@Schema(description = "tika返回对象")
@Data
public class TikaVo implements Serializable {
 
    @Schema(description = "文本内容")
    private List<String> text;
 
    @Schema(description = "元数据")
    private List<String> metadata;
}