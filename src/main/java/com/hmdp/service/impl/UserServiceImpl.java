package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;
import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author pony
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 校验手机号
        if(RegexUtils.isPhoneInvalid(phone)){
            // 手机号码不对返回错误
            return Result.fail("手机格式错误");
        }
        // 手机号码正确生成验证码
        String code = RandomUtil.randomNumbers(6);
//        // 将验证码存到Session中
//        session.setAttribute("code",code);
        // 将验证码存到Redis中
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY +phone,code,LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // 发送验证码
        log.debug("发送短信验证成功，验证码:{}",code);
        //返回ok
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        //1.校验手机号
        String phone = loginForm.getPhone();
        if(RegexUtils.isPhoneInvalid(phone)){
            //2.如果不符合，返回错误信息
            return Result.fail("手机格式错误");
        }
        //3.从Session中获取验证码并校验
//        String cacheCode = loginForm.getCode();
//        Object code = session.getAttribute("code");
//        if (cacheCode == null || !cacheCode.equals(code)){
//            //不一致则返回
//            return Result.fail("验证码错误");
//        }
        // 从Redis中获取验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        String code = loginForm.getCode();
        if (cacheCode == null || !cacheCode.equals(code)){
            //不一致则返回
            return Result.fail("验证码错误");
        }
        //4.一致则根据手机号码查询用户 select * from tb_user where phone=?
        User user = query().eq("phone", phone).one();
        //5.判断用户是否存在
        if(user == null){
            //6.用户不存在则创建新用户并保存
            user = createUserWithPhone(phone);
        }
        // 7.保存用户到redis中
        // 7.1随机生成token,作为登录令牌
        String token = UUID.randomUUID().toString(true);
        // 7.2将User对象转为Hash存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO,new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName,fieldValue)->fieldValue.toString()));
        // 7.3存储
        String tokenKey = LOGIN_USER_KEY+token;
        stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);
        //7.4设置token有效期
        stringRedisTemplate.expire(tokenKey,LOGIN_USER_TTL,TimeUnit.MINUTES);
//        //6.保存用户到数据库和seeion，并返回登录成功
//        session.setAttribute("user", BeanUtil.copyProperties(user, UserDTO.class));
        // 8.放回token
        return Result.ok(token);
    }

    private User createUserWithPhone(String phone) {
        //创建用户
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        //保存用户
        save(user);
        return user;
    }

    //登出
    @Override
    public Result logout(HttpSession session) {
        //TODO 删除redis中的tokenKey
//        String tokenKey = LOGIN_USER_KEY+token;
//        stringRedisTemplate.delete(tokenKey);
        session.invalidate();
        return Result.ok("成功退出");
    }
}
