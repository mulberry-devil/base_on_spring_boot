package com.caston.base_on_spring_boot.shiro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caston.base_on_spring_boot.shiro.entity.Account;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author caston
 * @since 2022-08-01
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {

}
