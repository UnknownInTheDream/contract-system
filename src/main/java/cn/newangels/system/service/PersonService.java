package cn.newangels.system.service;

import java.util.List;
import java.util.Map;

/**
 * 人员管理
 *
 * @author: TangLiang
 * @date: 2022/1/25 11:11
 * @since: 1.0
 */
public interface PersonService {

    /**
     * 加载人员信息
     *
     * @param I_ID id
     */
    Map<String, Object> loadPerson(String I_ID);

    /**
     * 根据编码加载人员信息
     *
     * @param V_PERCODE 人员编码
     */
    Map<String, Object> loadPersonByCode(String V_PERCODE);

    /**
     * 查询人员
     *
     * @param V_PERCODE 人员编码
     * @param V_PERNAME 人员姓名
     * @param V_STATUS  状态
     * @param current   当前页数
     * @param pageSize  每次显示数量
     */
    List<Map<String, Object>> selectPerson(String V_PERCODE, String V_PERNAME, String V_STATUS, Integer current, Integer pageSize);

    /**
     * 根据登陆账户查询用户
     *
     * @param V_LOGINNAME 登录账户
     */
    List<Map<String, Object>> selectPersonByLoginName(String V_LOGINNAME);

    /**
     * 查询数量
     *
     * @param V_PERCODE 人员编码
     * @param V_PERNAME 人员姓名
     * @param V_STATUS  状态
     */
    int countPerson(String V_PERCODE, String V_PERNAME, String V_STATUS);

    /**
     * 新增人员
     *
     * @param I_ID        id
     * @param V_PERCODE   人员编码
     * @param V_PERNAME   人员姓名
     * @param V_LOGINNAME 人员登陆名
     * @param V_TEL       单位电话
     * @param V_LXDH_CLF  联系电话
     * @param V_SFZH      身份证号码
     * @param V_SAPPER    sap账号
     * @param V_YGCODE    员工号
     * @param V_TOAM      是否接收即时通(是/否)
     * @param V_AM        即时通号
     * @param V_ZJ        职级
     * @param V_ZW        职务
     * @param I_ORDER     人员显示排序
     * @param V_PER_EDIT  最后修改人
     */
    int insertPerson(String I_ID, String V_PERCODE, String V_PERNAME, String V_LOGINNAME, String V_TEL, String V_LXDH_CLF, String V_SFZH, String V_SAPPER, String V_YGCODE, String V_TOAM, String V_AM, String V_ZJ, String V_ZW, Integer I_ORDER, String V_PER_EDIT);

    /**
     * 修改人员
     *
     * @param I_ID        id
     * @param V_PERCODE   人员编码
     * @param V_PERNAME   人员姓名
     * @param V_LOGINNAME 人员登陆名
     * @param V_TEL       单位电话
     * @param V_LXDH_CLF  联系电话
     * @param V_SFZH      身份证号码
     * @param V_SAPPER    sap账号
     * @param V_YGCODE    员工号
     * @param V_TOAM      是否接收即时通(是/否)
     * @param V_AM        即时通号
     * @param V_ZJ        职级
     * @param V_ZW        职务
     * @param I_ORDER     人员显示排序
     * @param V_PER_EDIT  最后修改人
     */
    int updatePerson(String I_ID, String V_PERCODE, String V_PERNAME, String V_LOGINNAME, String V_TEL, String V_LXDH_CLF, String V_SFZH, String V_SAPPER, String V_YGCODE, String V_TOAM, String V_AM, String V_ZJ, String V_ZW, Integer I_ORDER, String V_PER_EDIT);

    /**
     * 修改人员状态
     *
     * @param I_ID       id
     * @param V_STATUS   状态1/0
     * @param V_PER_EDIT 最后修改人
     */
    int updatePersonStatus(String I_ID, String V_STATUS, String V_PER_EDIT);

    /**
     * 修改密码
     *
     * @param I_ID       id
     * @param V_PASSWORD 密码
     * @param V_PER_EDIT 最后修改人
     */
    int initPersonPassWord(String I_ID, String V_PASSWORD, String V_PER_EDIT);

    /**
     * 修改密码
     *
     * @param V_OLDPASSWORD 旧密码
     * @param V_NEWPASSWORD 新密码
     * @param V_PERCODE     人员编码
     */
    int updatePersonPassWord(String V_OLDPASSWORD, String V_NEWPASSWORD, String V_PERCODE);

    /**
     * 修改最后密码
     *
     * @param I_ID id
     */
    void updatePersonLastLogin(String I_ID);

    /**
     * 删除人员
     *
     * @param I_ID id
     */
    int deletePerson(String I_ID);

    /**
     * 待办 根据人员编码查信息
     *
     * @param PERCODE_LIST
     * @return
     */
    List<Map<String, Object>> selectPersonByPerCodeList(List<String> PERCODE_LIST);
}
