package com.ifreeshare.user.controller.feign;

import com.ifreeshare.user.entity.IsUsers;
import com.ifreeshare.user.service.IsUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * (IsUsers)表控制层
 *
 * @author
 * @since 2023-10-15 11:47:05
 */
@RestController
@RequestMapping("/feign/isUsers")
public class IsUsersController {
    /**
     * 服务对象
     */
    @Autowired
    private IsUsersService isUsersService;

    public IsUsersController(IsUsersService isUsersService) {
        this.isUsersService = isUsersService;
    }

    /**
     * 分页查询
     *
     * @param isUsers 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @GetMapping
    public ResponseEntity<Page<IsUsers>> queryByPage(IsUsers isUsers, PageRequest pageRequest) {
        return ResponseEntity.ok(this.isUsersService.queryByPage(isUsers, pageRequest));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public ResponseEntity<IsUsers> queryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.isUsersService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param isUsers 实体
     * @return 新增结果
     */
    @PostMapping
    public ResponseEntity<IsUsers> add(IsUsers isUsers) {
        return ResponseEntity.ok(this.isUsersService.insert(isUsers));
    }

    /**
     * 编辑数据
     *
     * @param isUsers 实体
     * @return 编辑结果
     */
    @PutMapping
    public ResponseEntity<IsUsers> edit(IsUsers isUsers) {
        return ResponseEntity.ok(this.isUsersService.update(isUsers));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Long id) {
        return ResponseEntity.ok(this.isUsersService.deleteById(id));
    }

}

