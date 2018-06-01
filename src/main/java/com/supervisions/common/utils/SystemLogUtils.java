package com.supervisions.common.utils;

import com.supervisions.common.constant.CommonConstant;
import com.supervisions.common.utils.security.ShiroUtils;
import com.supervisions.common.utils.spring.SpringUtils;
import com.supervisions.modules.sys.mapper.Logininfor;
import com.supervisions.modules.sys.service.impl.LogininforServiceImpl;
import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;
import java.util.Date;

/**
 * 记录用户日志信息
 */
public class SystemLogUtils
{

    private static final Logger sys_user_logger = LoggerFactory.getLogger("sys-user");

    /**
     * 记录格式 [ip][用户名][操作][错误消息]
     * <p/>
     * 注意操作如下： loginError 登录失败 loginSuccess 登录成功 passwordError 密码错误 changePassword 修改密码 changeStatus 修改状态
     *
     * @param username
     * @param status
     * @param msg
     * @param args
     */
    public static void log(String username, String status, String msg, Object... args)
    {
        StringBuilder s = new StringBuilder();
        s.append(LogUtils.getBlock(ShiroUtils.getIp()));
        //s.append("123");
        s.append(LogUtils.getBlock(username));
        s.append(LogUtils.getBlock(status));
        s.append(LogUtils.getBlock(msg));

        sys_user_logger.info(s.toString(), args);

        if (CommonConstant.LOGIN_SUCCESS.equals(status) || CommonConstant.LOGOUT.equals(status))
        {
            saveOpLog(username, msg, CommonConstant.SUCCESS);
        }
        else if (CommonConstant.LOGIN_FAIL.equals(status))
        {
            saveOpLog(username, msg, CommonConstant.FAIL);
        }
    }

    public static void saveOpLog(String username, String message, String status)
    {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getHttpServletRequest().getHeader("User-Agent"));
        // 获取客户端操作系统
        String os = userAgent.getOperatingSystem().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        LogininforServiceImpl logininforService = SpringUtils.getBean(LogininforServiceImpl.class);
        Logininfor logininfor = new Logininfor();
        logininfor.setCreateTime(new Date());
        logininfor.setCreateUser(username);
        logininfor.setStatus(status);
        logininfor.setIp(ShiroUtils.getIp());
        //logininfor.setIp("123");
        logininfor.setBrowser(browser);
        logininfor.setOs(os);
        logininfor.setMsg(message);
        logininforService.insertLogininfor(logininfor);
    }
}