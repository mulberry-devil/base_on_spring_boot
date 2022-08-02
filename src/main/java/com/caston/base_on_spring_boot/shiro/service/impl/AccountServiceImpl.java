package com.caston.base_on_spring_boot.shiro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caston.base_on_spring_boot.shiro.entity.Account;
import com.caston.base_on_spring_boot.shiro.mapper.AccountMapper;
import com.caston.base_on_spring_boot.shiro.service.AccountService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author caston
 * @since 2022-08-01
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

}
