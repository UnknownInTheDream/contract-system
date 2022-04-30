package cn.newangels.system.dto;

import java.util.Map;


/**
 * ActivityEntity
 *
 * @author mwd 2021-04-21
 */
public class ActivityEntity implements Comparable {
    /**
     * 活动Id
     */
    private String id;
    /**
     * 活动名称
     */
    private String name;
    /**
     * X坐标
     */
    private Integer x;
    /**
     * Y坐标
     */
    private Integer y;
    /**
     * 宽
     */
    private int width;
    /**
     * 高
     */
    private int height;
    /**
     * 当前是否运行
     */
    private boolean running;
    /**
     * 其他属性
     */
    private Map<String, Object> properties;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }


    @Override
    public int compareTo(Object o) {
        return this.getY().compareTo(((ActivityEntity) o).getY());
    }
}
