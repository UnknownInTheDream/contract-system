package cn.newangels.system.controller;

import cn.newangels.common.annotation.Log;
import cn.newangels.common.base.BaseUtils;
import cn.newangels.common.util.ListToTreeUtil;
import cn.newangels.common.util.SnowflakeIdWorker;
import cn.newangels.system.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 岗位
 *
 * @author: TangLiang
 * @date: 2022/01/28 11:09
 * @since: 1.0
 */
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 加载岗位
     */
    @GetMapping("loadPost")
    @Log(title = "岗位管理", operateType = "加载岗位")
    public Map<String, Object> loadPost(String I_ID) {
        return BaseUtils.loadSuccess(postService.loadPost(I_ID));
    }

    /**
     * 查询岗位
     */
    @GetMapping("selectPost")
    @Log(title = "岗位管理", operateType = "查询岗位")
    public Map<String, Object> selectPost(String V_POSTCODE, String V_POSTNAME, String V_POSTCODE_UP, String V_ISADMIN, Integer current, Integer pageSize) {
        List<Map<String, Object>> list = postService.selectPost(V_POSTCODE, V_POSTNAME, V_POSTCODE_UP, V_ISADMIN, current, pageSize);
        int total = 0;
        if (pageSize != null && pageSize > 0) {
            total = postService.countPost(V_POSTCODE, V_POSTNAME, V_POSTCODE_UP, V_ISADMIN);
        }
        return BaseUtils.success(list, total);
    }

    /**
     * 加载岗位树
     */
    @GetMapping("selectPostTree")
    @Log(title = "岗位管理", operateType = "加载岗位树")
    public Map<String, Object> selectPostTree() {
        List<Map<String, Object>> list = postService.selectPost(null, null, null, null, null, null);
        return BaseUtils.success(ListToTreeUtil.listToTree(list, (m) -> "-1".equals(m.get("V_POSTCODE_UP")), (m) -> m.get("V_POSTCODE"), (n) -> n.get("V_POSTCODE_UP"), "isLeaf"));
    }

    /**
     * 新增岗位
     */
    @PostMapping("insertPost")
    @Log(title = "岗位管理", operateType = "新增岗位")
    public Map<String, Object> insertPost(String V_POSTCODE, String V_POSTNAME, String V_POSTCODE_UP, Integer I_ORDER, String V_ISADMIN, String V_PERCODE) {
        if (postService.insertPost(String.valueOf(snowflakeIdWorker.nextId()), V_POSTCODE, V_POSTNAME, V_POSTCODE_UP, I_ORDER, V_ISADMIN, V_PERCODE) == 0) {
            return BaseUtils.failed("新增岗位失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改岗位
     */
    @PostMapping("updatePost")
    @Log(title = "岗位管理", operateType = "修改岗位")
    public Map<String, Object> updatePost(String I_ID, String V_POSTCODE, String V_POSTNAME, Integer I_ORDER, String V_ISADMIN, String V_PERCODE) {
        if (postService.updatePost(I_ID, V_POSTCODE, V_POSTNAME, I_ORDER, V_ISADMIN, V_PERCODE) == 0) {
            return BaseUtils.failed("修改岗位失败");
        }
        return BaseUtils.success();
    }

    /**
     * 修改岗位状态
     */
    @PostMapping("updatePostStatus")
    @Log(title = "承揽方管理", operateType = "修改岗位状态")
    public Map<String, Object> updatePostStatus(String I_ID, String V_STATUS, String V_PERCODE) {
        if (postService.updatePostStatus(I_ID, V_STATUS, V_PERCODE) == 0) {
            return BaseUtils.failed("修改岗位状态失败");
        }
        return BaseUtils.success();
    }

    /**
     * 删除岗位
     */
    @PostMapping("deletePost")
    @Log(title = "岗位管理", operateType = "删除岗位")
    public Map<String, Object> deletePost(String I_ID) {
        if (postService.deletePost(I_ID) == 0) {
            return BaseUtils.failed("删除岗位失败");
        }
        return BaseUtils.success();
    }

}
