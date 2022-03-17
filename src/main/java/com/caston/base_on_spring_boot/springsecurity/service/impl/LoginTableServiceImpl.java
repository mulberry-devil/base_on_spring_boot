package com.caston.base_on_spring_boot.springsecurity.service.impl;

import com.caston.base_on_spring_boot.springsecurity.entity.LoginTable;
import com.caston.base_on_spring_boot.springsecurity.mapper.LoginTableMapper;
import com.caston.base_on_spring_boot.springsecurity.service.LoginTableService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author caston
 * @since 2022-03-17
 */
@Service
public class LoginTableServiceImpl extends ServiceImpl<LoginTableMapper, LoginTable> implements LoginTableService {

}
