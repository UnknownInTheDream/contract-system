package cn.newangels.system.dto;

/**
 * AssigneeEntity
 *
 * @author mwd 2021-04-21
 */
public class AssigneeEntity {

    private String userCode;
    private String posiCode;
    private String userName;

    public AssigneeEntity(String usercode, String posicode) {
        this.userCode = usercode;
        this.posiCode = posicode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getPosiCode() {
        return posiCode;
    }

    public void setPosiCode(String posiCode) {
        this.posiCode = posiCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return String.format("EMP[%s]POSI[%s]", userCode, posiCode);
    }
}
